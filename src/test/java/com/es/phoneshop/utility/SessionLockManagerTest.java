package com.es.phoneshop.utility;

import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.locks.Lock;

import static org.junit.Assert.*;

public class SessionLockManagerTest {

    @Mock
    private HttpSession session1;
    @Mock
    private HttpSession session2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(session1.getId()).thenReturn("session1");
        Mockito.when(session2.getId()).thenReturn("session2");
    }

    @Test
    public void testGetSessionLockReturnsSameLockForSameSession() {
        Lock lock1 = SessionLockManager.getSessionLock(session1);
        Lock lock2 = SessionLockManager.getSessionLock(session1);

        assertNotNull(lock1);
        assertNotNull(lock2);
        assertSame(lock1, lock2);
    }

    @Test
    public void testGetSessionLockReturnsDifferentLocksForDifferentSessions() {
        Lock lock1 = SessionLockManager.getSessionLock(session1);
        Lock lock2 = SessionLockManager.getSessionLock(session2);

        assertNotNull(lock1);
        assertNotNull(lock2);
        assertNotSame(lock1, lock2);
    }

}
