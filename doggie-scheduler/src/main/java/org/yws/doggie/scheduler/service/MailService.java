package org.yws.doggie.scheduler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by wangshu.yang on 2015/10/9.
 */
@Service
public class MailService {
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.account}")
    private String account;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.default.subject.prefix}")
    private String subjectPrefix;

    private Session session;
    Executor exec = Executors.newFixedThreadPool(5);

    @PostConstruct
    private void init() {
        Properties properties = new Properties();
        properties.setProperty("mail.host", host);
        properties.put("username", account);
        properties.put("password", password);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");//设置debug模式 后台输出邮件发送的过程

        this.session = Session.getDefaultInstance(properties);
    }

    public void sendMail(final String[] to, final String title, final String content) {
        exec.execute(new Runnable(){

            @Override
            public void run() {
                try {
                    Address[] addresses = new Address[to.length];
                    for (int i = 0, len = to.length; i < len; i++) {
                        addresses[i] = new InternetAddress(to[i]);
                    }
                    //邮件信息
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(from));//设置发送人
                    message.setRecipients(Message.RecipientType.TO, addresses);
                    message.setContent(content, "text/html;charset=UTF-8");//设置邮件内容
                    message.setSubject(subjectPrefix + title);//设置邮件主题
                    message.saveChanges();

                    Transport tran = session.getTransport();
                    tran.connect(host, account, password);
                    tran.sendMessage(message, message.getAllRecipients());
                    tran.close();
                }catch(MessagingException e){
                    e.printStackTrace();
                }
            }
        });
    }

}
