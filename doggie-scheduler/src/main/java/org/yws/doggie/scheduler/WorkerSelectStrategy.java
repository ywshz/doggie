package org.yws.doggie.scheduler;

import java.util.List;

import org.yws.doggie.scheduler.models.WorkerEntity;

public interface WorkerSelectStrategy {

	WorkerEntity select(List<WorkerEntity> workers);
}
