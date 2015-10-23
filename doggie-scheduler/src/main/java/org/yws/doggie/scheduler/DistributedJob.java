package org.yws.doggie.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.yws.doggie.scheduler.models.*;
import org.yws.doggie.scheduler.service.JobService;
import org.yws.doggie.scheduler.service.WorkerService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author wangshu.yang
 */
public class DistributedJob implements Job {
	private static final Logger log = LoggerFactory
			.getLogger(DistributedJob.class);
	private RestTemplate restTemplate = new RestTemplate();

	private final String WORKER_URI = "/worker/job_receiver";

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		ApplicationContext appCtx = ApplicationContextUtil
				.getApplicationContext();
		WorkerService ws = appCtx.getBean(WorkerService.class);
		JobService jobService = appCtx.getBean(JobService.class);
		
		List<WorkerEntity> workers = ws.findAll();
		WorkerSelectStrategy workerSelectStrategy = appCtx
				.getBean(RandomWorkerSelectStrategy.class);

		Long jobId = (Long) context.getJobDetail().getJobDataMap()
				.get("JOB_ID");
		
		JobEntity jobEntity = jobService.getByJobId(jobId);
//		String script = (String) context.getJobDetail().getJobDataMap()
//				.get("SCRIPT");
//		JobType jobType = (JobType) context.getJobDetail().getJobDataMap()
//				.get("JOB_TYPE");
		String script = jobEntity.getScript();
		JobType jobType = jobEntity.getJobType();
		
		String cmdPrefix = "";
		String filePostfix = "";
		switch (jobType) {
		case HIVE:
			cmdPrefix = "hive -f";
			filePostfix = ".hive";
			break;
		case SHELL:
			cmdPrefix = "";
			if (System.getProperties().getProperty("os.name")
					.contains("Windows")) {
				filePostfix = ".cmd";
			} else {
				filePostfix = ".sh";
			}
			break;
		case PYTHON:
			cmdPrefix = "python";
			filePostfix = ".py";
			break;
		}
		TriggerType triggerType = (TriggerType) context.getJobDetail()
				.getJobDataMap().get("TRIGGER_TYPE");

		JobHistoryEntity historyEntity = new JobHistoryEntity();
		historyEntity.setStartTime(new Timestamp(new Date().getTime()));
		historyEntity.setTriggerType(triggerType);
		historyEntity.setResult(JobRunResult.RUNNING);
		StringBuilder sb = new StringBuilder("任务开始");
		JobService js = appCtx.getBean(JobService.class);
		historyEntity.setJob(js.getByJobId(jobId));
		js.saveLog(historyEntity);

		// 得到机器
		sb.append("正在分配Worker\n");
		WorkerEntity worker = workerSelectStrategy.select(workers);
		boolean jobSendSucceed = false;
		try {
			while (worker != null && !workers.isEmpty()) {
				// 给Worker发送任务信息
				JobInfoResponse rs = restTemplate.postForObject(
						worker.getWorkerUrl() + WORKER_URI, new JobInfoRequest(
								jobId, historyEntity.getId(), cmdPrefix,
								filePostfix, script, triggerType),
						JobInfoResponse.class);
				if (rs == null || !rs.isSucceed()) {
					sb.append(worker.getWorkerUrl());
					sb.append("无效,正在重试\n");
					workers.remove(worker);
					worker = workerSelectStrategy.select(workers);
				} else {
					sb.append(worker.getWorkerUrl());
					sb.append("有效\n");
					historyEntity.setExecutionMachine(worker.getWorkerUrl());
					jobSendSucceed = true;
					break;
				}
			}
		} catch (Exception e) {
			sb.append("任务失败,原因是:\n");
			sb.append(e.getMessage() + "\n");
			historyEntity.setContent(sb.toString());
			setFailed(historyEntity);
		}
		if (!jobSendSucceed) {
			sb.append("任务失败,原因是:\n");
			sb.append("无可用Worker,任务失败!\n");
			historyEntity.setContent(sb.toString());
			setFailed(historyEntity);
		} else {
			sb.append("任务已经发送,等待Worker执行!\n");
			historyEntity.setContent(sb.toString());
			js.saveLog(historyEntity);
			addToRunningList(historyEntity);
		}
	}

	private void setFailed(JobHistoryEntity history) {
		JobManager jobManager = ApplicationContextUtil.getApplicationContext()
				.getBean(JobManager.class);
		jobManager.setJobFailed(history);
	}

	private void addToRunningList(JobHistoryEntity history) {
		JobManager jobManager = ApplicationContextUtil.getApplicationContext()
				.getBean(JobManager.class);
		jobManager.addToRunningList(history);
	}

}
