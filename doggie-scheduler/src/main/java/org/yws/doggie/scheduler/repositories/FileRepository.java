package org.yws.doggie.scheduler.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.yws.doggie.scheduler.models.FileEntity;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface FileRepository extends CrudRepository<FileEntity,Long> {
    List<FileEntity> findByParent(FileEntity parent);
    List<FileEntity> findByParent(FileEntity parent, Sort sort);
}
