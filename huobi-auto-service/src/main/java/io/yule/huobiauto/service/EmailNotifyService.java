package io.yule.huobiauto.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chensijiang on 2018/4/16 上午11:22.
 */
@Component
public class EmailNotifyService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailNotifyService.class);

    @Value("${email.smtp.host}")
    private String smtp;

    @Value("${email.smtp.port}")
    private Integer port;

    @Value("${email.fromUserEmail}")
    private String from;

    @Value("${email.fromUserPassword}")
    private String pwd;

    @Value("${email.useSSL}")
    private Boolean ssl;

    private ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public void send(final String to, final String title, final String content) {
        this.threadPool.execute(
                () -> {
                    try {
                        LOG.info("准备发送邮件：{} {}", to, title);
                        Email email = new SimpleEmail();
                        email.setHostName(this.smtp);
                        email.setSmtpPort(this.port);
                        email.setAuthenticator(
                                new DefaultAuthenticator(this.from, this.pwd));
                        email.setSSLOnConnect(this.ssl);
                        email.setFrom(this.from);
                        email.setSubject(title);
                        email.setMsg(content);
                        email.addTo(to);
                        email.send();
                        LOG.info("邮件发生成功。");
                    } catch (Exception ex) {
                        LOG.info("邮件发送失败：{}", ex.getMessage());
                    }
                }
        );
    }
}
