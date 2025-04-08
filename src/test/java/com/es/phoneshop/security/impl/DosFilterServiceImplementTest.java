package com.es.phoneshop.security.impl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

public class DosFilterServiceImplementTest {

    private DosFilterServiceImplement dosFilterService;

    @Before
    public void setUp() {
        dosFilterService = DosFilterServiceImplement.getInstance();
    }

    @Test
    public void testIsAllowedUnderMaxRequest() {
        String ip = "192.168.0.1";

        for (int i = 0; i < 10; i++) {
            assertTrue(dosFilterService.isAllowed(ip));
        }
    }

    @Test
    public void testIsNotAllowedAboveMaxRequest() {
        String ip = "192.167.0.1";

        for (int i = 0; i < 20; i++) {
            assertTrue(dosFilterService.isAllowed(ip));
        }

        assertFalse(dosFilterService.isAllowed(ip));
    }

    @Test
    public void testIsAllowedAfterClearing() throws InterruptedException {
        String ip = "192.166.0.1";

        for (int i = 0; i < 20; i++) {
            assertTrue(dosFilterService.isAllowed(ip));
        }

        TimeUnit.MINUTES.sleep(1);

        assertTrue(dosFilterService.isAllowed(ip));
    }
}
