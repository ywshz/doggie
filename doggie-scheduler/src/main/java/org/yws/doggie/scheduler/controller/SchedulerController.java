package org.yws.doggie.scheduler.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yws.doggie.scheduler.JobInfoResponse;
import org.yws.doggie.scheduler.JobManager;
import org.yws.doggie.scheduler.models.JobHistoryEntity;
import org.yws.doggie.scheduler.models.JobRunResult;
import org.yws.doggie.scheduler.service.JobService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class SchedulerController {
    @Autowired
    private JobService jobService;
    @Autowired
    private JobManager jobManager;

    @RequestMapping("/job_response")
    public boolean job_response(@RequestBody JobInfoResponse jobResponse) {
        JobHistoryEntity his = jobService.getJobHistory(jobResponse.getHistoryId());
        if (his == null) {
            return false;
        }
        String end = jobResponse.isSucceed() ? "\nJob Run Success." : "\nJob Run Failed.";
        his.setResult(jobResponse.isSucceed() ? JobRunResult.SUCCESS : JobRunResult.FAILED);
        his.setContent(his.getContent() + "\n" + jobResponse.getMessage() + end);
        jobManager.removeFromRunningList(his);
        return true;
    }

    @RequestMapping("/trigger_job")
    public Map<String,Object> trigger_job(@RequestParam(required=true) Long jobId) {
        Map<String,Object> rs = new HashMap<String, Object>();
        rs.put("job_status","OFF");
        rs.put("op_result",Boolean.FALSE);
        if(jobManager.isScheduled(jobId)){
            try {
                jobManager.removeScheduledJob(jobId);
                rs.put("job_status","OFF");
            } catch (SchedulerException e) {
                return rs;
            }
        }else{
            try {
                jobManager.scheduleJob(jobId);
                rs.put("job_status","ON");
            } catch (SchedulerException e) {
                return rs;
            }
        }
        rs.put("op_result",Boolean.TRUE);
        return rs;
    }
}
