package org.yws.doggieweb.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yws.doggieweb.models.FileEntity;
import org.yws.doggieweb.models.JobEntity;
import org.yws.doggieweb.models.JobHistoryEntity;
import org.yws.doggieweb.models.WorkerEntity;
import org.yws.doggieweb.repositories.FileRepository;
import org.yws.doggieweb.repositories.JobHistoryRepository;
import org.yws.doggieweb.repositories.JobRepository;
import org.yws.doggieweb.repositories.WorkerRepository;

/**
 * Created by ywszjut on 15/7/27.
 */
@Component("jobService")
@Transactional
public class JobService {
	private static final Logger log = LoggerFactory.getLogger(JobService.class);

	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private WorkerRepository workerRepository;
	@Autowired
	private JobHistoryRepository jobHistoryRepository;

	public JobEntity getByJobId(Long jobId) {
		return jobRepository.findOne(jobId);
	}

	public JobEntity getByFileId(Long fileId) {
		return jobRepository.findOneByFile(new FileEntity(fileId));
	}

	public List<JobHistoryEntity> getJobHistoryList(Long jobId) {
		return jobHistoryRepository
				.findTop10ByJobOrderByStartTimeDesc(new JobEntity(jobId));
	}

	public void updateJob(JobEntity modifiedJob) {
		jobRepository.save(modifiedJob);
	}

	public void delete(Long jobId) {
		JobEntity job = jobRepository.findOne(jobId);
		jobRepository.delete(job);
	}

	public List<WorkerEntity> listWorkers() {
		Iterable<WorkerEntity> ite = workerRepository.findAll();
		Iterator<WorkerEntity> it = ite.iterator();
		List<WorkerEntity> rs = new ArrayList<WorkerEntity>();
		while (it.hasNext()) {
			rs.add(it.next());
		}
		return rs;
	}

	public boolean saveWorker(WorkerEntity entity) {
		if (workerRepository.findByNameAndUrl(entity.getName(), entity.getUrl())
				.isEmpty()) {
			workerRepository.save(entity);
			return true;
		} else {
			return false;
		}
	}

	public boolean deleteWorker(Long id) {
		if(workerRepository.exists(id)){
			workerRepository.delete(id);
			return true;
		}else{
			return false;
		}
	}
}
