package org.yws.doggieweb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywszjut on 15/7/25.
 */
@Entity
@Table(name = "job")
public class JobEntity {
    private long id;
    private String name;
    private JobType jobType;
    private ScheduleType scheduleType;
    private ScheduleStatus scheduleStatus;
    private String cron;
    private String dependencies;
    private String script;
    private Timestamp createTime;
    private AllocationType allocationType;
    private String executionMachine;

    private FileEntity file;

    private List<JobEntity> dependencyList = new ArrayList<JobEntity>();

    private List<JobHistoryEntity> jobHistories;

    public JobEntity() {
    }

    public static JobEntity getDefault(FileEntity file){
        JobEntity jobEntity = new JobEntity();
        if(file.getFileType()== FileType.FILE){
            jobEntity.setName("新Job");
        }else{
            jobEntity.setName("新文件夹");
        }
        jobEntity.setFile(file);
        jobEntity.setJobType(JobType.SHELL);
        jobEntity.setScheduleStatus(ScheduleStatus.OFF);
        jobEntity.setScheduleType(ScheduleType.CRON);
        jobEntity.setCron("0 0 0 * * ?");
        jobEntity.setScript("请编辑修改");
        jobEntity.setAllocationType(AllocationType.AUTO);
        return jobEntity;
    }

    public JobEntity(long id) {
        this.id = id;
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

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "job_type")
    @Enumerated(EnumType.ORDINAL)
    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @Basic
    @Column(name = "schedule_type")
    @Enumerated(EnumType.ORDINAL)
    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    @Basic
    @Column(name = "schedule_status")
    @Enumerated(EnumType.ORDINAL)
    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    @Basic
    @Column(name = "cron")
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Basic
    @Column(name = "dependencies")
    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    @Basic
    @Column(name = "script")
    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @JsonIgnore
    @ManyToOne(targetEntity= FileEntity.class,fetch = FetchType.LAZY,cascade=CascadeType.REMOVE)
    @JoinColumn(name="file_id", referencedColumnName="id",nullable=false)
    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "job")
    public List<JobHistoryEntity> getJobHistories() {
        return jobHistories;
    }

    public void setJobHistories(List<JobHistoryEntity> jobHistories) {
        this.jobHistories = jobHistories;
    }

    @Transient
    public List<JobEntity> getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(List<JobEntity> dependencyList) {
        this.dependencyList = dependencyList;
    }

    @Basic
    @Column(name = "allocation_type")
    @Enumerated(EnumType.ORDINAL)
    public AllocationType getAllocationType() {
        return allocationType;
    }

    public void setAllocationType(AllocationType allocationType) {
        this.allocationType = allocationType;
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

        JobEntity jobEntity = (JobEntity) o;

        if (id != jobEntity.id) return false;
        if (allocationType != jobEntity.allocationType) return false;
        if (createTime != null ? !createTime.equals(jobEntity.createTime) : jobEntity.createTime != null) return false;
        if (cron != null ? !cron.equals(jobEntity.cron) : jobEntity.cron != null) return false;
        if (dependencies != null ? !dependencies.equals(jobEntity.dependencies) : jobEntity.dependencies != null)
            return false;
        if (dependencyList != null ? !dependencyList.equals(jobEntity.dependencyList) : jobEntity.dependencyList != null)
            return false;
        if (executionMachine != null ? !executionMachine.equals(jobEntity.executionMachine) : jobEntity.executionMachine != null)
            return false;
        if (file != null ? !file.equals(jobEntity.file) : jobEntity.file != null) return false;
        if (jobHistories != null ? !jobHistories.equals(jobEntity.jobHistories) : jobEntity.jobHistories != null)
            return false;
        if (jobType != jobEntity.jobType) return false;
        if (name != null ? !name.equals(jobEntity.name) : jobEntity.name != null) return false;
        if (scheduleStatus != jobEntity.scheduleStatus) return false;
        if (scheduleType != jobEntity.scheduleType) return false;
        if (script != null ? !script.equals(jobEntity.script) : jobEntity.script != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (jobType != null ? jobType.hashCode() : 0);
        result = 31 * result + (scheduleType != null ? scheduleType.hashCode() : 0);
        result = 31 * result + (scheduleStatus != null ? scheduleStatus.hashCode() : 0);
        result = 31 * result + (cron != null ? cron.hashCode() : 0);
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (allocationType != null ? allocationType.hashCode() : 0);
        result = 31 * result + (executionMachine != null ? executionMachine.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (dependencyList != null ? dependencyList.hashCode() : 0);
        result = 31 * result + (jobHistories != null ? jobHistories.hashCode() : 0);
        return result;
    }
}
