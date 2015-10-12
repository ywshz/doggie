package org.yws.doggie.scheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.sql.Timestamp;

/**
 * Created by ywszjut on 15/7/25.
 */
@Entity
@Table(name = "job_history")
public class JobHistoryEntity {
    private long id;
    @JsonIgnore
    private JobEntity job;
    private Timestamp startTime;
    private Timestamp endTime;
    private JobRunResult result;
    private String content;
    private TriggerType triggerType;
    private String executionMachine;

    public JobHistoryEntity(){
    	
    }
    
    public JobHistoryEntity(Long id){
    	this.id=id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    public JobEntity getJob() {
        return job;
    }

    public void setJob(JobEntity job) {
        this.job = job;
    }

    @Basic
    @Column(name = "start_time")
    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "end_time")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Basic
    @Column(name = "result")
    @Enumerated(EnumType.ORDINAL)
    public JobRunResult getResult() {
        return result;
    }

    public void setResult(JobRunResult result) {
        this.result = result;
    }

    @Basic
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "trigger_type")
    @Enumerated(EnumType.ORDINAL)
    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    @Basic
    @Column(name = "execution_machine")
    public String getExecutionMachine() {
        return executionMachine;
    }

    public void setExecutionMachine(String executionMachine) {
        this.executionMachine = executionMachine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobHistoryEntity that = (JobHistoryEntity) o;

        if (id != that.id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result1 = (int) (id ^ (id >>> 32));
        result1 = 31 * result1 + (startTime != null ? startTime.hashCode() : 0);
        result1 = 31 * result1 + (endTime != null ? endTime.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (content != null ? content.hashCode() : 0);
        result1 = 31 * result1 + triggerType.ordinal();
        return result1;
    }
}
