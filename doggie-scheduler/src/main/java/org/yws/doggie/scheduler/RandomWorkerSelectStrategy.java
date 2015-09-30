package org.yws.doggie.scheduler;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.yws.doggie.scheduler.models.WorkerEntity;

@Component("randomWorkerSelectStrategy")
public class RandomWorkerSelectStrategy implements WorkerSelectStrategy {
	Random random = new Random();

	@Override
	public WorkerEntity select(List<WorkerEntity> workers) {
		if(workers==null || workers.isEmpty()){
			return null;
		}
		
		int index = random.nextInt(workers.size());
		return workers.get(index);
	}

	public static void main(String[] args) {
		Random random = new Random();
		System.out.println(random.nextInt(0));
	}
}
