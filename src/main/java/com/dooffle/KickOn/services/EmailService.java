package com.dooffle.KickOn.services;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    Configuration configuration;

    /**
     * This method will send compose and send the message
     * */
    public void sendMail(String to, String subject, String body)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setFrom(from);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }

    public void sendTemplateMail(String to, String subject, String templateName, Map map) throws MessagingException {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(subject);
            String emailContent = getMailBody(map, templateName);
            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

    }

    public String getMailBody(Map<String, Object> variables, String templateName) throws  IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        configuration.getTemplate(templateName).process(variables, stringWriter);
        return stringWriter.getBuffer().toString();
    }



    public void sendRegisterMail(String email, String name) {

        Map map = new HashMap();
        map.put("name",name);
        try {
            sendTemplateMail(email, "Welcome to Dooffle", "email_welcome.ftlh", map);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendResetMail(String email, String otp, String firstName) {

        Map map = new HashMap();
        map.put("name",firstName);
        map.put("otp",otp);

        sendMail(email, "OTP", "Use OTP:"+otp);

        // sendTemplateMail(email, "OTP to reset password", "reset_password.ftlh", map);
    }
}
