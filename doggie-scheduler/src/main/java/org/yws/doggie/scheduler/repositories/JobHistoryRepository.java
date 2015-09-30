package org.yws.doggie.scheduler.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.yws.doggie.scheduler.models.JobEntity;
import org.yws.doggie.scheduler.models.JobHistoryEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface JobHistoryRepository extends CrudRepository<JobHistoryEntity,Long> {
    List<JobHistoryEntity> findByJob(JobEntity file);
}
