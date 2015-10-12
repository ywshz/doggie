package org.yws.doggieweb.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.yws.doggieweb.models.WorkerEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface WorkerRepository extends CrudRepository<WorkerEntity,Long> {
	public List<WorkerEntity> findByNameAndUrl(String name, String url);
}
