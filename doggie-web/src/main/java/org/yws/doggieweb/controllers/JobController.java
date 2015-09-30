package org.yws.doggieweb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yws.doggieweb.convert.CaseInsensitiveConverter;
import org.yws.doggieweb.models.*;
import org.yws.doggieweb.service.JobService;

import java.util.List;

/**
 * Created by ywszjut on 15/7/25.
 */
@Controller
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AllocationType.class, new CaseInsensitiveConverter<AllocationType>(AllocationType.class));
        binder.registerCustomEditor(JobType.class, new CaseInsensitiveConverter<JobType>(JobType.class));
        binder.registerCustomEditor(ScheduleStatus.class, new CaseInsensitiveConverter<ScheduleStatus>(ScheduleStatus.class));
        binder.registerCustomEditor(ScheduleType.class, new CaseInsensitiveConverter<ScheduleType>(ScheduleType.class));
        binder.registerCustomEditor(FileType.class, new CaseInsensitiveConverter<FileType>(FileType.class));
    }

    @RequestMapping(value = "get_by_file_id")
    @ResponseBody
    public JobEntity get_by_file_id(Long fileId) {
        return jobService.getByFileId(fileId);
    }

    @RequestMapping(value = "get_history_list")
    @ResponseBody
    public List<JobHistoryEntity> get_history_list(Long jobId) {
        return jobService.getJobHistoryList(jobId);
    }

    @RequestMapping(value = "update_job")
    @ResponseBody
    public CommonResponse update_job(Long id, String name, JobType jobType, ScheduleType scheduleType, String cron, String dependencies, String script, AllocationType allocationType, String executionMachine) {
        try {
            JobEntity job = jobService.getByJobId(id);
            job.setName(name);
            job.setJobType(jobType);
            job.setScheduleType(scheduleType);
            job.setCron(cron);
            job.setDependencies(dependencies);
            job.setScript(script);
            job.setAllocationType(allocationType);
            job.setExecutionMachine(executionMachine);
            job.getFile().setName(job.getName());
            jobService.updateJob(job);
            return CommonResponse.SUCCESS();
        } catch (Exception e) {
            return  CommonResponse.FAILED(e.getMessage());
        }
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public CommonResponse delete(Long jobId) {
        try{
            jobService.delete(jobId);
            return CommonResponse.SUCCESS();
        }catch(Exception e){
            return CommonResponse.FAILED(e.getMessage());
        }
    }

}
