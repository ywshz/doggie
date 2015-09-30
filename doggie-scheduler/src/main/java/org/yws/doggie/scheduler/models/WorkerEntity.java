package org.yws.doggie.scheduler.models;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by ywszjut on 15/7/25.
 */
@Entity
@Table(name = "workers", schema = "", catalog = "cattle")
public class WorkerEntity {
	private long id;
	private String workerUrl;
	private String name;
	private Date createDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    @Basic
    @Column(name = "worker_base_url")
	public String getWorkerUrl() {
		return workerUrl;
	}

	public void setWorkerUrl(String workerUrl) {
		this.workerUrl = workerUrl;
	}

	@Basic
    @Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
    @Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Override
	public String toString(){
		return "workerUrl:" + workerUrl + ", name:" + name;
	}
}
