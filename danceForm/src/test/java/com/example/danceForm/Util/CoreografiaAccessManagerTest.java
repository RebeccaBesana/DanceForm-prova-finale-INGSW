package com.example.danceForm.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoreografiaAccessManagerTest {

    private CoreografiaAccessManager accessManager;

    @BeforeEach
    void setUp() {
        accessManager = CoreografiaAccessManager.getInstance();
        // Assicura che la mappa sia pulita all'inizio (usando il release in caso serva)
        accessManager.release(1L, "session1");
        accessManager.release(1L, "session2");
    }

    @Test
    void testSingletonInstance() {
        CoreografiaAccessManager instance1 = CoreografiaAccessManager.getInstance();
        CoreografiaAccessManager instance2 = CoreografiaAccessManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testTryAcquireSuccess() {
        boolean acquired = accessManager.tryAcquire(1L, "session1");
        assertTrue(acquired);
        assertTrue(accessManager.isBeingEdited(1L));
    }

    @Test
    void testTryAcquireSameSession() {
        accessManager.tryAcquire(1L, "session1");
        boolean acquiredAgain = accessManager.tryAcquire(1L, "session1");
        assertTrue(acquiredAgain); // stessa sessione → OK
    }

    @Test
    void testTryAcquireDifferentSessionFails() {
        accessManager.tryAcquire(1L, "session1");
        boolean acquired = accessManager.tryAcquire(1L, "session2");
        assertFalse(acquired); // già bloccata da un'altra sessione
    }

    @Test
    void testReleaseSuccess() {
        accessManager.tryAcquire(1L, "session1");
        accessManager.release(1L, "session1");
        assertFalse(accessManager.isBeingEdited(1L));
    }

    @Test
    void testReleaseFailsWrongSession() {
        accessManager.tryAcquire(1L, "session1");
        accessManager.release(1L, "session2");
        assertTrue(accessManager.isBeingEdited(1L)); // lock ancora attivo
    }
}
