package com.bookverse.service.impl;

import com.bookverse.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendWelcomeEmail(String toEmail, String name) {
        log.info("[noop] Sending welcome email to {} ({})", name, toEmail);
    }
}
