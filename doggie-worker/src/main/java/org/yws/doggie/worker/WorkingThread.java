package org.yws.doggie.worker;

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
    private RestTemplate restTemplate = new RestTemplate();

    private final int SUCCESS = 1;
    private final int FAILED = 0;
    private final int EXCEPTION = 2;
    private BlockingQueue<JobInfoRequest> jobQueue;

    public WorkingThread(String workingFolder, String responseUrl, BlockingQueue<JobInfoRequest> jobQueue) {
        this.workingFolder = workingFolder;
        this.responseUrl = responseUrl;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobInfoRequest job = jobQueue.take();
                doTheJob(job);
            } catch (InterruptedException e) {
                continue;
            }
        }
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
            File file = writeScriptToFile(absPath, job.getScript());
            bw.write("生成脚本文件，然后开始执行：");
            int exitCode = executeScript(absPath, job.getType(), file, bw);
            if (exitCode == 0) {
                //TODO SUCCESS
                bw.write("任务完成，运行结果是：成功");
                bw.close();
                responseJob(SUCCESS, logFile, null);
            } else {
                bw.write("任务完成，运行结果是：失败");
                bw.close();
                responseJob(FAILED, logFile, null);
            }
        } catch (Exception e) {
            //任务异常中断，原因是。。。
            responseJob(FAILED, logFile, "任务异常中断，原因是" + e.getMessage());
        }

    }

    private void responseJob(int status, File logFile, String appendLog) {
        try {
            InputStream in = new FileInputStream(logFile);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();

            String log = new String(data);
            JobInfoResponse res = new JobInfoResponse();

            if (SUCCESS == status) {
                res.setSucceed(true);
            } else {
                res.setSucceed(false);
            }
            res.setMessage(log);
            if (EXCEPTION == status) {
                res.setMessage(res.getMessage() + "\n" + appendLog);
            }

            restTemplate.postForObject(responseUrl, res, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int executeScript(String absPath, String type, File file, final BufferedWriter bw) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(type,
                file.getAbsolutePath());
        builder.directory(new File(absPath));
        Process process = builder.start();

        final InputStream inputStream = process.getInputStream();
        final InputStream errorStream = process.getErrorStream();

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
        }

        int exitCode = -999;
        exitCode = process.waitFor();
        process = null;
        return exitCode;
    }

    private File writeScriptToFile(String absPath, String script) throws IOException {
        File file = null;
        script = DateRender.render(script);
        file = new File(absPath + File.separator + UUID.randomUUID().toString() + ".hive");
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
        File folder = new File(workingFolder + File.separator + historyId.toString());
        folder.mkdirs();
        return folder.getAbsolutePath();
    }

}
