package org.yws.doggie.scheduler.repositories;

import org.springframework.data.repository.CrudRepository;
import org.yws.doggie.scheduler.models.FileEntity;
import org.yws.doggie.scheduler.models.JobEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface JobRepository extends CrudRepository<JobEntity,Long> {
    JobEntity findOneByFile(FileEntity file);
}
