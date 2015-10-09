package org.yws.doggieweb.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.yws.doggieweb.models.JobEntity;
import org.yws.doggieweb.models.JobHistoryEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface JobHistoryRepository extends CrudRepository<JobHistoryEntity,Long> {
	
//	@Query("from JobHistoryEntity where job = ?1 order by startTime desc") 
    List<JobHistoryEntity> findTop10ByJobOrderByStartTimeDesc(JobEntity job);
	
}
