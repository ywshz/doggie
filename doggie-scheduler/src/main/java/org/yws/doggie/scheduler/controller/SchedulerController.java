package org.yws.doggie.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yws.doggie.scheduler.JobInfoResponse;
import org.yws.doggie.scheduler.JobManager;
import org.yws.doggie.scheduler.models.JobHistoryEntity;
import org.yws.doggie.scheduler.models.JobRunResult;
import org.yws.doggie.scheduler.service.JobService;

@RestController
@RequestMapping("/")
public class SchedulerController {
    @Autowired
    private JobManager jobManager;
    @Autowired
    private JobService jobService;

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
}
