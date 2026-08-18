package com.kanban.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PasswordResetRateLimiter {

    private static final int MAX_PER_EMAIL = 3;
    private static final int MAX_PER_IP = 10;
    private static final Duration WINDOW = Duration.ofHours(1);

    private final Map<String, Deque<Long>> emailHits = new ConcurrentHashMap<>();
    private final Map<String, Deque<Long>> ipHits = new ConcurrentHashMap<>();

    public void check(String email, String ip) {
        long now = System.currentTimeMillis();
        if (!allow(emailHits, "email:" + email.toLowerCase(), now, MAX_PER_EMAIL)
                || !allow(ipHits, "ip:" + ip, now, MAX_PER_IP)) {
            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many reset requests. Try again later."
            );
        }
    }

    private boolean allow(Map<String, Deque<Long>> buckets, String key, long now, int max) {
        long cutoff = now - WINDOW.toMillis();
        Deque<Long> times = buckets.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (times) {
            while (!times.isEmpty() && times.peekFirst() < cutoff) {
                times.pollFirst();
            }
            if (times.size() >= max) {
                return false;
            }
            times.addLast(now);
            return true;
        }
    }
}
