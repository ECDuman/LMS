package com.demo.lms.service;

import com.demo.lms.event.AuditLogEvent;
import com.demo.lms.model.User;
import com.demo.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AuditLoggerService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    public void log(String action, String entityType, UUID entityId) {
        // We use AtomicReference to make actorEmail and userId final or effectively final.
        final AtomicReference<String> actorEmailRef = new AtomicReference<>("UNKNOWN");
        final AtomicReference<UUID> userIdRef = new AtomicReference<>(null);

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            actorEmailRef.set(userDetails.getUsername());

            User currentUser = userRepository.findByEmailIgnoreCase(actorEmailRef.get())
                    .orElseThrow(() -> new RuntimeException("No user found for audit log: " + actorEmailRef.get()));
            userIdRef.set(currentUser.getId());
        } else {
            throw new RuntimeException("Authenticated user information was not found for the audit log.");
        }

        eventPublisher.publishEvent(new AuditLogEvent(this, action, entityType, entityId, userIdRef.get(), actorEmailRef.get()));
    }

    public void log(String action, String entityType, UUID entityId, String actorEmail) {
        final AtomicReference<UUID> userIdRef = new AtomicReference<>(null);

        if (actorEmail != null && !actorEmail.isEmpty()) {
            userRepository.findByEmailIgnoreCase(actorEmail).ifPresent(user -> userIdRef.set(user.getId()));
        }
        UUID userId = userIdRef.get();

        eventPublisher.publishEvent(new AuditLogEvent(this, action, entityType, entityId, userId, actorEmail));
    }
}
