package org.yws.doggie.worker.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yws.doggie.worker.JobInfoRequest;
import org.yws.doggie.worker.JobInfoResponse;

@RestController
@RequestMapping("/")
public class WorkerController {

	@RequestMapping("/job_receiver")
	public JobInfoResponse home(@RequestBody JobInfoRequest jobInfo) {
		System.out.println(jobInfo.getScript());
		return new JobInfoResponse(true,"123");
	}
}
