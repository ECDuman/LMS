package com.demo.lms.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class AuditLogEvent extends ApplicationEvent {

    private final String action;
    private final String entityType;
    private final UUID entityId;
    private final UUID userId;
    private final String actorEmail;

    public AuditLogEvent(Object source, String action, String entityType, UUID entityId, UUID userId, String actorEmail) {
        super(source);
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.actorEmail = actorEmail;
    }
}