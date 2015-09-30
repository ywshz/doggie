package org.yws.doggieweb.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.yws.doggieweb.models.FileEntity;

import java.util.List;

/**
 * Created by ywszjut on 15/7/27.
 */
public interface FileRepository extends CrudRepository<FileEntity,Long> {
    List<FileEntity> findByParent(FileEntity parent);
    List<FileEntity> findByParent(FileEntity parent, Sort sort);
}
