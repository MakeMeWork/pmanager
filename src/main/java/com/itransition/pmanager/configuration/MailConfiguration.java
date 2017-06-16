package com.itransition.pmanager.configuration;

import com.itransition.pmanager.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Created by Lenovo on 14.06.2017.
 */
@Configuration
@PropertySource("classpath:gmail.properties")
public class MailConfiguration {
    @Autowired
    private SettingsService settingsService;

    @Value("${mail.protocol}")
    private String protocol;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.smtp.auth}")
    private boolean auth;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;

    @Bean(name = "mailSender")
    JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        if(settingsService.getProperty("username")!= ""){
            mailSender.setUsername(settingsService.getProperty("username")+"@gmail.com");
        }else{
            mailSender.setUsername(username+"@gmail.com");
        }
        if(settingsService.getProperty("username")!= ""){
            mailSender.setPassword(settingsService.getProperty("password"));
        }else{
            mailSender.setPassword(password);
        }
        return mailSender;
    }
}
