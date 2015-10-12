package org.yws.doggie.scheduler;

import org.springframework.stereotype.Component;
import org.yws.doggie.scheduler.models.WorkerEntity;

import java.util.List;
import java.util.Random;

@Component("randomWorkerSelectStrategy")
public class RandomWorkerSelectStrategy implements WorkerSelectStrategy {
	Random random = new Random();

	@Override
	public WorkerEntity select(List<WorkerEntity> workers) {
		if (workers == null || workers.isEmpty()) {
			return null;
		}

		int index = random.nextInt(workers.size());
		return workers.get(index);
	}

}