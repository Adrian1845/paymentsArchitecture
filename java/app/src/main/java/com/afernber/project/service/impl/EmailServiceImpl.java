package com.afernber.project.service.impl;

import com.afernber.project.constant.EmailConstants;
import com.afernber.project.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    /**
     * Sends an email.
     * @param to , who will receive the email
     * @param subject , what the subject is going to be
     * @param templateName , the name of the template that will be the body of the email
     * @param variables, the variables for the template
     */
    @Async
    public void sendHtmlEmail(String to,
                              String subject,
                              String templateName,
                              Map<String, Object> variables,
                              String attachmentName,
                              InputStreamSource attachmentSource) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(EmailConstants.FROM);

            if (attachmentSource != null && attachmentName != null) {
                helper.addAttachment(attachmentName, attachmentSource);
            }

            mailSender.send(mimeMessage);
            log.info("📧 Email with attachment sent to {}", to);
        } catch (MessagingException e) {
            log.error("❌ Failed to send email with attachment", e);
        }
    }
}
