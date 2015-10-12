package org.yws.doggieweb.models;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "workers")
public class WorkerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	@Basic
	@Column(name = "worker_base_url")
	private String url;
	@Basic
	@Column(name = "name")
	private String name;
	@Basic
	@Column(name = "create_date")
	private Date createDate;

	public WorkerEntity() {
		super();
	}

	public WorkerEntity(Long id, String url, String name) {
		super();
		this.id = id;
		this.url = url;
		this.name = name;
	}

	public WorkerEntity(Long id, String url, String name, Date createDate) {
		super();
		this.id = id;
		this.url = url;
		this.name = name;
		this.createDate = createDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
