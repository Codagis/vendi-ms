package com.vendi.vendi_ms.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Serviço de rate limiting para proteção contra ataques de força bruta.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@Slf4j
public class RateLimitingService {

    private static final int MAX_ATTEMPTS_PER_MINUTE = 10;
    private static final int MAX_ATTEMPTS_PER_HOUR = 100;
    private static final int MAX_ATTEMPTS_PER_DAY = 1000;
    
    private static final int BLOCK_DURATION_MINUTES = 15;
    private static final int BLOCK_DURATION_HOURS = 2;
    private static final int BLOCK_DURATION_DAYS = 24;

    private final ConcurrentMap<String, AttemptTracker> ipAttempts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AttemptTracker> userAttempts = new ConcurrentHashMap<>();

    public boolean isAllowed(String identifier, String type) {
        ConcurrentMap<String, AttemptTracker> attemptsMap = getAttemptsMap(type);
        AttemptTracker tracker = attemptsMap.computeIfAbsent(identifier, k -> new AttemptTracker());
        
        LocalDateTime now = LocalDateTime.now();
        
        tracker.cleanupOldAttempts(now);
        if (tracker.isBlocked(now)) {
            log.warn("RATE LIMIT EXCEEDED - {}: {} está bloqueado até {}", 
                    type, identifier, tracker.getBlockedUntil());
            return false;
        }
        
        
        if (tracker.getAttemptsLastMinute() >= MAX_ATTEMPTS_PER_MINUTE) {
            tracker.blockUntil(now.plusMinutes(BLOCK_DURATION_MINUTES));
            log.warn("RATE LIMIT EXCEEDED - {}: {} excedeu limite por minuto", type, identifier);
            return false;
        }
        
        if (tracker.getAttemptsLastHour() >= MAX_ATTEMPTS_PER_HOUR) {
            tracker.blockUntil(now.plusHours(BLOCK_DURATION_HOURS));
            log.warn("RATE LIMIT EXCEEDED - {}: {} excedeu limite por hora", type, identifier);
            return false;
        }
        
        if (tracker.getAttemptsLastDay() >= MAX_ATTEMPTS_PER_DAY) {
            tracker.blockUntil(now.plusDays(BLOCK_DURATION_DAYS));
            log.warn("RATE LIMIT EXCEEDED - {}: {} excedeu limite por dia", type, identifier);
            return false;
        }
        
        
        tracker.recordAttempt(now);
        
        return true;
    }

    public void recordFailedLogin(String username, String ip) {
        recordFailedAttempt(username, "USER");
        recordFailedAttempt(ip, "IP");
        
        log.warn("LOGIN FALHADO - Username: {} - IP: {}", username, ip);
    }

    public void recordAccessDenied(String username, String ip, String resource) {
        recordFailedAttempt(username, "USER");
        recordFailedAttempt(ip, "IP");
        
        log.warn("ACESSO NEGADO - Username: {} - IP: {} - Recurso: {}", username, ip, resource);
    }

    private void recordFailedAttempt(String identifier, String type) {
        ConcurrentMap<String, AttemptTracker> attemptsMap = getAttemptsMap(type);
        AttemptTracker tracker = attemptsMap.computeIfAbsent(identifier, k -> new AttemptTracker());
        
        LocalDateTime now = LocalDateTime.now();
        tracker.recordFailedAttempt(now);
        
        
        int failedAttempts = tracker.getFailedAttemptsLastHour();
        if (failedAttempts >= 5) {
            tracker.blockUntil(now.plusMinutes(15));
        } else if (failedAttempts >= 10) {
            tracker.blockUntil(now.plusHours(1));
        } else if (failedAttempts >= 20) {
            tracker.blockUntil(now.plusDays(1));
        }
    }

    private ConcurrentMap<String, AttemptTracker> getAttemptsMap(String type) {
        return "USER".equals(type) ? userAttempts : ipAttempts;
    }

    public void cleanupOldAttempts() {
        LocalDateTime now = LocalDateTime.now();
        
        
        ipAttempts.entrySet().removeIf(entry -> {
            entry.getValue().cleanupOldAttempts(now);
            return entry.getValue().isEmpty();
        });
        
        
        userAttempts.entrySet().removeIf(entry -> {
            entry.getValue().cleanupOldAttempts(now);
            return entry.getValue().isEmpty();
        });
        
        log.debug("Limpeza de tentativas antigas concluída");
    }

    public RateLimitStats getStats(String identifier, String type) {
        ConcurrentMap<String, AttemptTracker> attemptsMap = getAttemptsMap(type);
        AttemptTracker tracker = attemptsMap.get(identifier);
        
        if (tracker == null) {
            return new RateLimitStats(0, 0, 0, false, null);
        }
        
        LocalDateTime now = LocalDateTime.now();
        return new RateLimitStats(
            tracker.getAttemptsLastMinute(),
            tracker.getAttemptsLastHour(),
            tracker.getAttemptsLastDay(),
            tracker.isBlocked(now),
            tracker.getBlockedUntil()
        );
    }

    private static class AttemptTracker {
        private final ConcurrentMap<LocalDateTime, Boolean> attempts = new ConcurrentHashMap<>();
        private final ConcurrentMap<LocalDateTime, Boolean> failedAttempts = new ConcurrentHashMap<>();
        private LocalDateTime blockedUntil;

        public void recordAttempt(LocalDateTime time) {
            attempts.put(time, true);
        }

        public void recordFailedAttempt(LocalDateTime time) {
            failedAttempts.put(time, true);
        }

        public void blockUntil(LocalDateTime time) {
            this.blockedUntil = time;
        }

        public boolean isBlocked(LocalDateTime now) {
            return blockedUntil != null && now.isBefore(blockedUntil);
        }

        public LocalDateTime getBlockedUntil() {
            return blockedUntil;
        }

        public int getAttemptsLastMinute() {
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            return (int) attempts.keySet().stream()
                    .filter(time -> time.isAfter(oneMinuteAgo))
                    .count();
        }

        public int getAttemptsLastHour() {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            return (int) attempts.keySet().stream()
                    .filter(time -> time.isAfter(oneHourAgo))
                    .count();
        }

        public int getAttemptsLastDay() {
            LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
            return (int) attempts.keySet().stream()
                    .filter(time -> time.isAfter(oneDayAgo))
                    .count();
        }

        public int getFailedAttemptsLastHour() {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            return (int) failedAttempts.keySet().stream()
                    .filter(time -> time.isAfter(oneHourAgo))
                    .count();
        }

        public void cleanupOldAttempts(LocalDateTime now) {
            LocalDateTime oneDayAgo = now.minusDays(1);
            attempts.entrySet().removeIf(entry -> entry.getKey().isBefore(oneDayAgo));
            failedAttempts.entrySet().removeIf(entry -> entry.getKey().isBefore(oneDayAgo));
        }

        public boolean isEmpty() {
            return attempts.isEmpty() && failedAttempts.isEmpty();
        }
    }

    public static class RateLimitStats {
        private final int attemptsLastMinute;
        private final int attemptsLastHour;
        private final int attemptsLastDay;
        private final boolean isBlocked;
        private final LocalDateTime blockedUntil;

        public RateLimitStats(int attemptsLastMinute, int attemptsLastHour, int attemptsLastDay, 
                            boolean isBlocked, LocalDateTime blockedUntil) {
            this.attemptsLastMinute = attemptsLastMinute;
            this.attemptsLastHour = attemptsLastHour;
            this.attemptsLastDay = attemptsLastDay;
            this.isBlocked = isBlocked;
            this.blockedUntil = blockedUntil;
        }

        
        public int getAttemptsLastMinute() { return attemptsLastMinute; }
        public int getAttemptsLastHour() { return attemptsLastHour; }
        public int getAttemptsLastDay() { return attemptsLastDay; }
        public boolean isBlocked() { return isBlocked; }
        public LocalDateTime getBlockedUntil() { return blockedUntil; }
    }
}
