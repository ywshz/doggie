package org.yws.doggie.worker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yws.doggie.worker.JobInfoRequest;
import org.yws.doggie.worker.JobInfoResponse;
import org.yws.doggie.worker.WorkerManager;

@RestController
@RequestMapping("/")
public class WorkerController {
    @Autowired
    private WorkerManager workerManager;
	@RequestMapping("/job_receiver")
	public JobInfoResponse job_receiver(@RequestBody JobInfoRequest jobInfo) {
        try {
            workerManager.addJob(jobInfo);
        } catch (InterruptedException e) {
            return new JobInfoResponse(false,e.getMessage());
        }
        return new JobInfoResponse(true,"SUCCESS");
	}

    @RequestMapping("/get_log")
    public String get_log(@RequestBody Long logId) {
        return workerManager.getRunningJobLog(logId);
    }
}
