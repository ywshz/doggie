package org.yws.doggie.worker;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ywszjut on 15/10/1.
 */
public class WorkingThread implements Runnable {
	private String workingFolder;
	private String responseUrl;
	private String sendLogUrl;
	private RestTemplate restTemplate = new RestTemplate();

	private final int SUCCESS = 1;
	private final int FAILED = 0;
	private final int EXCEPTION = 2;
	private JobInfoRequest job;

	public WorkingThread(String workingFolder, String responseUrl,
			String sendLogUrl, JobInfoRequest job) {
		this.workingFolder = workingFolder;
		this.responseUrl = responseUrl;
		this.sendLogUrl = sendLogUrl;
		this.job = job;
	}

	@Override
	public void run() {
		doTheJob(job);
	}

	private void doTheJob(JobInfoRequest job) {
		String absPath = null;
		BufferedWriter bw = null;
		File logFile = null;
		try {
			absPath = createJobFolder(job.getHistoryId());
			logFile = new File(absPath + File.separator + "job.log");
			FileWriter fw = new FileWriter(logFile);
			bw = new BufferedWriter(fw);
			bw.write("工作目录与日志文件创建完毕，他们位于" + absPath);
			bw.newLine();
			File file = writeScriptToFile(absPath, job.getScript(),
					job.getFilePostfix());
			bw.write("生成脚本文件，然后开始执行：");
			int exitCode = executeScript(absPath, job.getType(),
					job.getHistoryId(), file, bw);
			if (exitCode == 0) {
				bw.write("任务完成，运行结果是：成功");
				bw.close();
				responseJob(job, SUCCESS, logFile, null);
			} else {
				bw.write("任务完成，运行结果是：失败");
				bw.close();
				responseJob(job, FAILED, logFile, null);
			}
		} catch (Exception e) {
			// 任务异常中断，原因是。。。
			responseJob(job, EXCEPTION, logFile, "任务异常中断，原因是" + e.getMessage());
		}

	}

	private void responseJob(JobInfoRequest job, int status, File logFile,
			String appendLog) {
		try {
			InputStream in = new FileInputStream(logFile);
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();

			String log = new String(data);
			JobInfoResponse res = new JobInfoResponse();
			res.setHistoryId(job.getHistoryId());
			res.setMessage(log);
			if (job.getTriggerType() == TriggerType.AUTO) {
				res.setScheduledJob(true);
			} else {
				res.setScheduledJob(false);
			}
			if (SUCCESS == status) {
				res.setSucceed(true);
			} else if (FAILED == status) {
				res.setSucceed(false);
			} else if (EXCEPTION == status) {
				res.setMessage(res.getMessage() + "\n" + appendLog);
			}

			restTemplate.postForObject(responseUrl, res, Boolean.class);
		} catch (Exception e) {
			// 加入到失败反馈队列,定时重试
			e.printStackTrace();
		}

	}

	private int executeScript(String absPath, String type, Long historyId,
			File script, final BufferedWriter bw) throws IOException,
			InterruptedException {
		ProcessBuilder builder = null;
		if ("".equals(type)) {
			builder = new ProcessBuilder(script.getAbsolutePath());
		} else {
			builder = new ProcessBuilder(type, script.getAbsolutePath());
		}

		builder.directory(new File(absPath));
		Process process = builder.start();

		final InputStream inputStream = process.getInputStream();
		final InputStream errorStream = process.getErrorStream();

		final int MAX_LINES = 5000;

		Thread normal = new Thread() {
			@Override
			public void run() {
				try {
					InputStreamReader isr = new InputStreamReader(inputStream);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					int count = 0;
					while ((line = br.readLine()) != null) {
						bw.write(line);
						bw.newLine();
						if (count++ % 5 == 0) {
							bw.flush();
						}

						if (count >= MAX_LINES) {
							bw.write("任务LOG过长,不再记录.");
							bw.flush();
							break;
						}

					}
				} catch (IOException ioE) {
					ioE.printStackTrace();
				}
			}
		};

		Thread error = new Thread() {
			@Override
			public void run() {
				try {
					InputStreamReader isr = new InputStreamReader(errorStream);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					int count = 0;
					while ((line = br.readLine()) != null) {
						bw.write(line);
						bw.newLine();
						if (count++ % 5 == 0) {
							bw.flush();
						}

						if (count >= MAX_LINES) {
							bw.write("任务LOG过长,不再记录.");
							bw.flush();
							break;
						}
					}
				} catch (IOException ioE) {
					ioE.printStackTrace();
				}
			}
		};

		normal.start();
		error.start();

		while (normal.isAlive() || error.isAlive()) {
			Thread.sleep(1000);

			InputStream in = new FileInputStream(absPath + File.separator
					+ "job.log");
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();

			MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>(
					2);
			param.add("logId", historyId);
			param.add("log", new String(data));
			restTemplate.postForObject(sendLogUrl, param, Boolean.class);
		}

		int exitCode = -999;
		exitCode = process.waitFor();
		process = null;
		return exitCode;
	}

	private File writeScriptToFile(String absPath, String script,
			String filePostfix) throws IOException {
		File file = null;
		script = DateRender.render(script);
		file = new File(absPath + File.separator + UUID.randomUUID().toString()
				+ filePostfix);
		file.createNewFile();
		file.setExecutable(true);
		file.setReadable(true);
		file.setWritable(true);
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(script);
		out.close();
		return file;
	}

	/**
	 * mkdir and return the absolute path
	 *
	 * @param historyId
	 * @return
	 */
	private String createJobFolder(Long historyId) {
		File folder = new File(workingFolder + File.separator
				+ historyId.toString());
		folder.mkdirs();
		return folder.getAbsolutePath();
	}

}
