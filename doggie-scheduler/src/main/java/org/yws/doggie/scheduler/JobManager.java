package org.yws.doggie.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yws.doggie.scheduler.models.*;
import org.yws.doggie.scheduler.service.JobService;

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
		runningJobsLock.lock();
		try{
			List<JobHistoryEntity> cpy = new ArrayList<JobHistoryEntity>(runningJobs.size());
			Collections.copy(cpy,runningJobs);
		}finally {
			runningJobsLock.unlock();
		}

		// TODO 遍历CPY寻找过期的，然后去数据库比对

		
	}

	private void initJobs() throws SchedulerException {
		for (JobEntity job : jobService.getAllJobs()) {
			scheduleJob(job);
		}
	}

	public void scheduleJob(Long jobId) throws SchedulerException {
		scheduleJob(jobService.getByJobId(jobId));
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
	}

	public void setJobSucceed(JobHistoryEntity historyEntity) {
		historyEntity.setEndTime(new Timestamp(new Date().getTime()));
		historyEntity.setResult(JobRunResult.SUCCESS);
		jobService.saveLog(historyEntity);
	}

	public String getRunningJobLog(Long id) {
		return null;
	}

	public void addToRunningList(JobHistoryEntity log){
		runningJobsLock.lock();
		try{
			runningJobs.add(log);
		}finally {
			runningJobsLock.unlock();
		}
	}

	public void removeFromRunningList(JobHistoryEntity log){
		runningJobsLock.lock();
		try{
			runningJobs.remove(log);
		}finally {
			runningJobsLock.unlock();
		}
        log.setEndTime(new Timestamp(new Date().getTime()));
        jobService.saveLog(log);
	}

	public void killJob(JobHistoryEntity log) {
		log.getExecutionMachine();
		log.getId();
		//doKill
	}

}
