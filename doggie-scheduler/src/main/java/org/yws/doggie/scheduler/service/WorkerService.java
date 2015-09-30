package org.yws.doggie.scheduler.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yws.doggie.scheduler.models.WorkerEntity;
import org.yws.doggie.scheduler.repositories.WorkersRepository;

/**
 * Created by ywszjut on 15/7/27.
 */
@Component("workerService")
@Transactional
public class WorkerService {
	private static final Logger log = LoggerFactory
			.getLogger(WorkerService.class);

	@Autowired
	private WorkersRepository workersRepository;

	public List<WorkerEntity> findAll() {
		Iterator<WorkerEntity> ite = workersRepository.findAll().iterator();
		List<WorkerEntity> workerList = new ArrayList<WorkerEntity>();
		while (ite.hasNext()) {
			workerList.add(ite.next());
		}
		return workerList;
	}

	public WorkerEntity get(Long id) {
		return workersRepository.findOne(id);
	}
}
