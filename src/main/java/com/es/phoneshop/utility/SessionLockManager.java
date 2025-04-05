package com.es.phoneshop.utility;

import jakarta.servlet.http.HttpSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionLockManager {

    private static final ConcurrentHashMap<String, Lock> sessionLocks = new ConcurrentHashMap<>();

    public static Lock getSessionLock(HttpSession session) {
        String sessionId = session.getId();
        sessionLocks.putIfAbsent(sessionId, new ReentrantLock());
        return sessionLocks.get(sessionId);
    }

}
