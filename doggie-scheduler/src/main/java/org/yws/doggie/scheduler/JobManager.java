package org.yws.doggie.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yws.doggie.scheduler.models.*;
import org.yws.doggie.scheduler.service.JobService;
import org.yws.doggie.scheduler.service.MailService;

import javax.annotation.PostConstruct;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

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

	@Autowired
	private JobService jobService;
	private final Trigger RUN_NOW_TRIGGER = newTrigger()
			.withIdentity("manual_trigger_run_now").startNow()
			.withSchedule(simpleSchedule()).build();

	@PostConstruct
	void init() throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory();
		scheduler = sf.getScheduler();
		scheduler.start();

		initJobs();
		initMonitorOfJobTimeout();
	}

	private void initMonitorOfJobTimeout() {
		List<JobHistoryEntity> cpy = Collections.EMPTY_LIST;
		runningJobsLock.lock();
		try {
			cpy = new ArrayList<JobHistoryEntity>(runningJobs.size());
			Collections.copy(cpy, runningJobs);
		} finally {
			runningJobsLock.unlock();
		}

		// 遍历CPY寻找过期的，然后去数据库比对
		for (JobHistoryEntity log : cpy) {
			long start = log.getStartTime().getTime();
			long cost = System.currentTimeMillis() - start;
			if (cost - start > 7200000) {
				// 超过2小时
				JobHistoryEntity dbLog = jobService.getJobHistory(log.getId());
				if (dbLog.getEndTime() == null) {
					killJob(dbLog);
					notifyFailedJob(dbLog);
				}
			}
		}

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
		JobDetail jobDetail = newJob(DistributedJob.class).withIdentity(
				String.valueOf(jobEntity.getId())).build();
		jobDetail.getJobDataMap().put("JOB_ID", jobEntity.getId());
		jobDetail.getJobDataMap().put("SCRIPT", jobEntity.getScript());
		jobDetail.getJobDataMap().put("JOB_TYPE", jobEntity.getJobType());
		jobDetail.getJobDataMap().put("TRIGGER_TYPE", TriggerType.AUTO);

		if (ScheduleType.CRON == jobEntity.getScheduleType()) {
			CronTrigger trigger = newTrigger()
					.withIdentity(TRIGGER_OF_JOB + jobEntity.getId())
					.withSchedule(cronSchedule(jobEntity.getCron())).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			scheduler.addJob(jobDetail, true);
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

	public void removeScheduledJob(Long jobId) throws SchedulerException {
		scheduler.deleteJob(new JobKey(jobId.toString()));

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
	public boolean resumeJob(Long jobId) throws SchedulerException {
		JobKey key = new JobKey(String.valueOf(jobId));
		if (scheduler.checkExists(key)) {
			scheduler.triggerJob(key);
			return true;
		} else {
			return false;
		}
	}

	public void manualJob(Long jobId) throws SchedulerException {
		JobEntity jobEntity = jobService.getByJobId(jobId);

		JobDetail jobDetail = newJob(DistributedJob.class).withIdentity(
				"manual_job_" + jobId).build();
		jobDetail.getJobDataMap().put("JOB_ID", jobEntity.getId());
		jobDetail.getJobDataMap().put("SCRIPT", jobEntity.getScript());
		jobDetail.getJobDataMap().put("JOB_TYPE", jobEntity.getJobType());
		jobDetail.getJobDataMap().put("TRIGGER_TYPE", TriggerType.MANUAL);

		scheduler.scheduleJob(jobDetail, RUN_NOW_TRIGGER);
	}

	public void setJobFailed(JobHistoryEntity historyEntity) {
		historyEntity.setEndTime(new Timestamp(new Date().getTime()));
		historyEntity.setResult(JobRunResult.FAILED);
		jobService.saveLog(historyEntity);

		notifyFailedJob(historyEntity);
	}

	public void setJobSucceed(JobHistoryEntity historyEntity) {
		historyEntity.setEndTime(new Timestamp(new Date().getTime()));
		historyEntity.setResult(JobRunResult.SUCCESS);
		jobService.saveLog(historyEntity);
	}

	public String getRunningJobLog(Long id) {
		return null;
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

}
