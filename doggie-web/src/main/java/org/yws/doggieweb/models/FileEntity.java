package org.yws.doggieweb.models;// default package

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * File entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "file")
//@JsonIgnoreProperties(value={"files"})
//@JsonFilter()
public class FileEntity implements java.io.Serializable {

	// Fields

	private Long id;
	@JsonIgnore
	private FileEntity parent;
	private String name;
	private Timestamp createTime;
	private Timestamp modifyTime;
	private FileType fileType;
	private JobEntity job;
	private Set<FileEntity> files = new HashSet<FileEntity>(0);

	// Constructors

	/** default constructor */
	public FileEntity() {
	}

	public FileEntity(Long id) {
		this.id=id;
	}

	/** minimal constructor */
	public FileEntity(Long id, String name, FileType fileType) {
		this.id = id;
		this.name = name;
		this.fileType = fileType;
	}

	/** full constructor */
	public FileEntity(Long id, FileEntity parent, String name, Timestamp createTime,
					  Timestamp modifyTime, FileType fileType, JobEntity job, Set<FileEntity> files) {
		this.id = id;
		this.parent = parent;
		this.name = name;
		this.createTime = createTime;
		this.modifyTime = modifyTime;
		this.fileType = fileType;
		this.job = job;
		this.files = files;
	}

	// Property accessors
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent")
	public FileEntity getParent() {
		return this.parent;
	}

	public void setParent(FileEntity parent) {
		this.parent = parent;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "create_time", length = 19)
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modify_time", length = 19)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Basic
	@Column(name = "file_type",nullable = false)
	@Enumerated(EnumType.ORDINAL)
	public FileType getFileType() {
		return this.fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id")
	public JobEntity getJob() {
		return this.job;
	}

	public void setJob(JobEntity job) {
		this.job = job;
	}

    @JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parent")
	public Set<FileEntity> getFiles() {
		return this.files;
	}

	public void setFiles(Set<FileEntity> files) {
		this.files = files;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FileEntity fileEntity = (FileEntity) o;

		if (fileType != fileEntity.fileType) return false;
		if (id != fileEntity.id) return false;
		if (createTime != null ? !createTime.equals(fileEntity.createTime) : fileEntity.createTime != null) return false;
		if (modifyTime != null ? !modifyTime.equals(fileEntity.modifyTime) : fileEntity.modifyTime != null) return false;
		if (name != null ? !name.equals(fileEntity.name) : fileEntity.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
		result = 31 * result + (modifyTime != null ? modifyTime.hashCode() : 0);
		result = 31 * result + fileType.ordinal();
		return result;
	}
}