package org.yws.doggie.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yws.doggie.scheduler.models.JobEntity;
import org.yws.doggie.scheduler.models.JobHistoryEntity;
import org.yws.doggie.scheduler.models.JobRunResult;
import org.yws.doggie.scheduler.models.LogStatus;
import org.yws.doggie.scheduler.models.ScheduleStatus;
import org.yws.doggie.scheduler.models.ScheduleType;
import org.yws.doggie.scheduler.models.TriggerType;
import org.yws.doggie.scheduler.service.JobService;
import org.yws.doggie.scheduler.service.MailService;

@Service
public class JobManager {
	private static final String TRIGGER_OF_JOB = "trigger_of_job_";
	private Scheduler scheduler;
	private Lock runningJobsLock = new ReentrantLock();
	private List<JobHistoryEntity> runningJobs = new LinkedList<JobHistoryEntity>();
	private Lock scheduledJobIdsLock = new ReentrantLock();
	private List<Long> scheduledJobIds = new LinkedList<Long>();
	@Autowired
	private MailService mailService;
	private Map<Long, String> logHolder = new HashMap<Long, String>();

	private Map<Long, List<Long>> dependencyInfoMap = new HashMap<Long, List<Long>>();
	private Lock workingDepMapLock = new ReentrantLock();
	private Map<Long, List<Long>> workingDepMap = new HashMap<Long, List<Long>>();

	@Autowired
	private JobService jobService;

	@PostConstruct
	void init() throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory();
		scheduler = sf.getScheduler();
		scheduler.start();

		initJobs();
		initMonitorOfJobTimeout();
	}

	private void initMonitorOfJobTimeout() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {

						List<JobHistoryEntity> cpy = Collections.EMPTY_LIST;
						runningJobsLock.lock();
						try {
							cpy = new ArrayList<JobHistoryEntity>();
							for (JobHistoryEntity je : runningJobs) {
								cpy.add(je);
							}
							System.out.println(cpy.size());
						} finally {
							runningJobsLock.unlock();
						}

						// 遍历CPY寻找过期的，然后去数据库比对
						for (JobHistoryEntity log : cpy) {
							long start = log.getStartTime().getTime();
							long cost = System.currentTimeMillis() - start;
							if (cost > 7200000) {
								// 超过2小时
								JobHistoryEntity dbLog = jobService
										.getJobHistory(log.getId());
								if (dbLog.getEndTime() == null) {
									killJob(dbLog);
									notifyFailedJob(dbLog);
								}
							}
						}

						Thread.sleep(60 * 1000);
					} catch (Exception e) {
						try {
							mailService.sendMail(
									new String[] { "wangshu.yang@mopote.com" },
									"服务异常告警",
									"超时任务监控功能失效,请注意排查原因," + e.getMessage());
						} catch (Exception ee) {

						}
						// break;
					}
				}
			}
		}).start();

	}

	private void initJobs() throws SchedulerException {
		for (JobEntity job : jobService.getAllJobs()) {
			if (ScheduleStatus.ON == job.getScheduleStatus()) {
				scheduleJob(job);
			}
		}
	}

	public boolean isScheduled(Long jobId) {
		scheduledJobIdsLock.lock();
		try {
			return this.scheduledJobIds.contains(jobId);
		} finally {
			scheduledJobIdsLock.unlock();
		}
	}

	public void scheduleJob(Long jobId) throws SchedulerException {
		JobEntity jobEntity = jobService.getByJobId(jobId);
		scheduleJob(jobEntity);
	}

	public void scheduleJob(JobEntity jobEntity) throws SchedulerException {
		JobDetail jobDetail = initJobDetail(jobEntity);

		if (ScheduleType.CRON == jobEntity.getScheduleType()) {
			CronTrigger trigger = newTrigger()
					.withIdentity(TRIGGER_OF_JOB + jobEntity.getId())
					.withSchedule(cronSchedule(jobEntity.getCron())).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// dependency
			String[] dpIds = jobEntity.getDependencies().split(",");
			dependencyInfoMap.put(jobEntity.getId(), new ArrayList<Long>(
					dpIds.length));
			for (String id : dpIds) {
				dependencyInfoMap.get(jobEntity.getId()).add(
						jobService.getByFileId(Long.valueOf(id)).getId());
			}
			Collections.sort(dependencyInfoMap.get(jobEntity.getId()));

			// scheduler.addJob(jobDetail, true, true);
		}

		scheduledJobIdsLock.lock();
		try {
			scheduledJobIds.add(jobEntity.getId());
		} finally {
			scheduledJobIdsLock.unlock();
		}

		jobEntity.setScheduleStatus(ScheduleStatus.ON);
		jobService.updateJob(jobEntity);
	}

	private JobDetail initJobDetail(JobEntity jobEntity) {
		JobDetail jobDetail = newJob(DistributedJob.class).withIdentity(
				String.valueOf(jobEntity.getId())).build();
		jobDetail.getJobDataMap().put("JOB_ID", jobEntity.getId());
//		jobDetail.getJobDataMap().put("SCRIPT", jobEntity.getScript());
//		jobDetail.getJobDataMap().put("JOB_TYPE", jobEntity.getJobType());
		jobDetail.getJobDataMap().put("TRIGGER_TYPE", TriggerType.AUTO);
		return jobDetail;
	}

	public void removeScheduledJob(Long jobId) throws SchedulerException {
		scheduler.deleteJob(new JobKey(jobId.toString()));
		dependencyInfoMap.get(jobId).clear();
		dependencyInfoMap.remove(jobId);
		workingDepMap.get(jobId).clear();
		workingDepMap.remove(jobId);
		scheduledJobIdsLock.lock();
		try {
			scheduledJobIds.remove(jobId);
		} finally {
			scheduledJobIdsLock.unlock();
		}

		JobEntity job = jobService.getByJobId(jobId);
		job.setScheduleStatus(ScheduleStatus.OFF);
		jobService.updateJob(job);
	}

	/**
	 * 恢复一个Job, 这个job和手工启动的job的不同之处是,它会触发依赖与他的job的执行
	 *
	 * @param jobId
	 *            这个job必须是已经存在于quartz中
	 * @throws SchedulerException
	 */
	public void resumeJob(Long jobId) throws SchedulerException {
		JobEntity jobEntity = jobService.getByJobId(jobId);
		JobDetail jobDetail = initJobDetail(jobEntity);
		scheduler.addJob(jobDetail, true, true);
		scheduler.triggerJob(jobDetail.getKey());
	}

	public void manualJob(Long jobId) throws SchedulerException {
		JobEntity jobEntity = jobService.getByJobId(jobId);

		JobDetail jobDetail = newJob(DistributedJob.class).withIdentity(
				"manual_job_" + jobId).build();
		jobDetail.getJobDataMap().put("JOB_ID", jobEntity.getId());
//		jobDetail.getJobDataMap().put("SCRIPT", jobEntity.getScript());
//		jobDetail.getJobDataMap().put("JOB_TYPE", jobEntity.getJobType());
		jobDetail.getJobDataMap().put("TRIGGER_TYPE", TriggerType.MANUAL);

		Trigger runNowTrigger = newTrigger()
				.withIdentity("manual_trigger_run_now_job_" + jobId).startNow()
				.withSchedule(simpleSchedule()).build();

		scheduler.scheduleJob(jobDetail, runNowTrigger);
	}

	public void setJobFailed(JobHistoryEntity historyEntity) {
		historyEntity.setEndTime(new Timestamp(new Date().getTime()));
		historyEntity.setResult(JobRunResult.FAILED);
		jobService.saveLog(historyEntity);

		logHolder.remove(historyEntity.getId());

		removeFromRunningList(historyEntity);
		notifyFailedJob(historyEntity);
	}

	public void setJobSucceed(JobHistoryEntity historyEntity,
			boolean isScheduledJob) {
		historyEntity.setEndTime(new Timestamp(new Date().getTime()));
		historyEntity.setResult(JobRunResult.SUCCESS);
		jobService.saveLog(historyEntity);

		logHolder.remove(historyEntity.getId());

		removeFromRunningList(historyEntity);

		// 检查任务依赖并触发
		if (isScheduledJob) {
			Long jobId = historyEntity.getJob().getId();

			for (Long id : dependencyInfoMap.keySet()) {
				if (dependencyInfoMap.get(id).contains(jobId)) {
					workingDepMapLock.lock();
					try {
						if (workingDepMap.containsKey(id)) {
							workingDepMap.get(id).add(jobId);
							Collections.sort(workingDepMap.get(id));
						} else {
							workingDepMap.put(id, new ArrayList<Long>());
							workingDepMap.get(id).add(jobId);
						}

						if (workingDepMap.get(id).equals(
								dependencyInfoMap.get(id))) {
							// 触发一个任务
							try {
								resumeJob(id);
							} catch (SchedulerException e) {

							}
							workingDepMap.get(id).clear();
							workingDepMap.remove(id);
						}
					} finally {
						workingDepMapLock.unlock();
					}
				}
			}
		}
	}

	public LogStatus getRunningJobLog(Long id) {
		LogStatus log = new LogStatus();
		boolean running = false;
		runningJobsLock.lock();
		try {
			if (runningJobs.contains(new JobHistoryEntity(id))) {
				running = true;
			}
		} finally {
			runningJobsLock.unlock();
		}

		if (!running) {
			JobHistoryEntity his = jobService.getJobHistory(id);
			log.setStatus(his.getResult() == JobRunResult.SUCCESS ? "SUCCESS"
					: "FAILED");
			log.setLog(his.getContent());
		} else {
			log.setStatus("RUNNING");
			log.setLog(logHolder.get(id));
		}
		return log;
	}

	public void addToRunningList(JobHistoryEntity log) {
		runningJobsLock.lock();
		try {
			runningJobs.add(log);
		} finally {
			runningJobsLock.unlock();
		}
	}

	public void removeFromRunningList(JobHistoryEntity log) {
		runningJobsLock.lock();
		try {
			runningJobs.remove(log);
		} finally {
			runningJobsLock.unlock();
		}
		log.setEndTime(new Timestamp(new Date().getTime()));
		jobService.saveLog(log);
	}

	public void notifyFailedJob(JobHistoryEntity historyEntity) {
		JobEntity job = jobService.getByJobId(historyEntity.getJob().getId());
		mailService.sendMail(new String[] { "wangshu.yang@mopote.com" },
				"任务运行失败", job.getId() + "\n" + job.getName() + "\n"
						+ historyEntity.getContent());
	}

	public void killJob(JobHistoryEntity log) {
		// log.getExecutionMachine();
		// log.getId();
		log.setContent(log.getContent() + "\n任务超时,Killed!!");
		log.setEndTime(new Timestamp(new Date().getTime()));
		log.setResult(JobRunResult.FAILED);
		jobService.saveLog(log);
	}

	public void refreshLog(Long logId, String log) {
		this.logHolder.put(logId, log);
	}

}
