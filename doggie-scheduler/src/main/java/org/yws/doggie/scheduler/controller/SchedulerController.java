package org.yws.doggie.scheduler.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yws.doggie.scheduler.JobInfoResponse;

@RestController
@RequestMapping("/")
public class SchedulerController {

    @RequestMapping("/job_response")
    public boolean job_response(@RequestBody JobInfoResponse jobResponse) {
        System.out.println(jobResponse.getMessage());
        return true;
    }
}
