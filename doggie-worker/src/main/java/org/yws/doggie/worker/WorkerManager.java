package org.yws.doggie.worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {
	private final int THREAD_COUNT = 10;
	private final Executor exec = Executors.newFixedThreadPool(THREAD_COUNT);

	@Value("${worker.working_folder}")
	private String workingFolder;
	@Value("${scheduler.response.url}")
	private String responseUrl;
	@Value("${scheduler.sendlog.url}")
	private String sendlogUrl;

	public void addJob(JobInfoRequest job) throws InterruptedException {
		exec.execute(new WorkingThread(workingFolder, responseUrl, sendlogUrl,
				job));
	}

	public String getRunningJobLog(Long historyId) {
		File logFile = new File(workingFolder + File.separator
				+ historyId.toString() + File.separator + "job.log");
		if (!logFile.exists()) {
			return "Log not found!!";
		}
		InputStream in = null;
		try {
			in = new FileInputStream(logFile);
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();
			return new String(data);
		} catch (Exception e) {
			return "Reading log error!!";
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				return "Reading log error!!";
			}
		}
	}

}
