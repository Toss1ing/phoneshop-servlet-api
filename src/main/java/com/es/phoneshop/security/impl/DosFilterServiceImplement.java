package com.es.phoneshop.security.impl;

import com.es.phoneshop.security.DosFilterService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DosFilterServiceImplement implements DosFilterService {

    private static final Long MAX_COUNT_REQUEST = 20L;
    Map<String, Long> countRequestMap = new ConcurrentHashMap<>();
    private static DosFilterServiceImplement INSTANCE;
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private DosFilterServiceImplement() {
        scheduler.scheduleAtFixedRate(() -> countRequestMap.clear(), 1, 1, TimeUnit.MINUTES);
    }

    public static DosFilterServiceImplement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DosFilterServiceImplement();
        }
        return INSTANCE;
    }

    @Override
    public boolean isAllowed(String ipAddress) {
        Long count = countRequestMap.getOrDefault(ipAddress, 0L);
        if (count >= MAX_COUNT_REQUEST) {
            return false;
        }
        countRequestMap.put(ipAddress, count + 1);
        return true;
    }

}
