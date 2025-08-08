package com.demo.lms.listener;

import com.demo.lms.event.AuditLogEvent;
import com.demo.lms.model.AuditLog;
import com.demo.lms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final AuditLogRepository auditLogRepository;

    @Async
    @EventListener
    public void handleAuditLogEvent(AuditLogEvent event) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(event.getAction());
        auditLog.setEntityType(event.getEntityType());
        auditLog.setEntityId(event.getEntityId());
        auditLog.setUserId(event.getUserId());
        auditLog.setActorEmail(event.getActorEmail());
        auditLogRepository.save(auditLog);
    }
}