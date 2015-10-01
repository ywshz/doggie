package org.yws.doggie.worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {
    private final int THREAD_COUNT = 10;
    private BlockingQueue<JobInfoRequest> jobQueue = new ArrayBlockingQueue<JobInfoRequest>(20);
    private final Executor exec = Executors.newFixedThreadPool(THREAD_COUNT);

    @Value("${worker.working_folder}")
    private String workingFolder;
    @Value("${scheduler.response.url}")
    private String responseUrl;

    @PostConstruct
    private void init() {
        initWorkingThread();
    }

    private void initWorkingThread() {
        for (int i = 0; i < THREAD_COUNT; i++)
            exec.execute(new WorkingThread(workingFolder,responseUrl,jobQueue));
    }

    public void addJob(JobInfoRequest job) throws InterruptedException {
        jobQueue.put(job);
    }

    public String getRunningJobLog() {
        return "";
    }

}
