package com.afernber.project.service;

import org.springframework.core.io.InputStreamSource;

import java.util.Map;

public interface EmailService {
    void sendHtmlEmail(String to,
                                     String subject,
                                     String templateName,
                                     Map<String, Object> variables,
                                     String attachmentName,
                                     InputStreamSource attachmentSource);
}
