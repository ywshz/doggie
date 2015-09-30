package org.yws.doggie.scheduler.repositories;

import org.springframework.data.repository.CrudRepository;
import org.yws.doggie.scheduler.models.WorkerEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface WorkersRepository extends CrudRepository<WorkerEntity, Long> {
}
