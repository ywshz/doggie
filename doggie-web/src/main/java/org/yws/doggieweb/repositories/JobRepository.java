package org.yws.doggieweb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.yws.doggieweb.models.FileEntity;
import org.yws.doggieweb.models.JobEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface JobRepository extends CrudRepository<JobEntity,Long> {
    JobEntity findOneByFile(FileEntity file);
}
