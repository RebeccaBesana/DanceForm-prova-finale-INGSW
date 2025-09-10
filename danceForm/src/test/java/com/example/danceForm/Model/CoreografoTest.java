package com.example.danceForm.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CoreografoTest {

    @Test
    void testCostruttoreCompleto() {
        Coreografo coreografo = new Coreografo(1L, "admin", "pass");
        assertEquals(1L, coreografo.getId());
        assertEquals("admin", coreografo.getUsername());
        assertEquals("pass", coreografo.getPassword());
    }

    @Test
    void testCostruttoreParziale() {
        Coreografo coreografo = new Coreografo("utente", "1234");
        assertNull(coreografo.getId());
        assertEquals("utente", coreografo.getUsername());
        assertEquals("1234", coreografo.getPassword());
    }

    @Test
    void testSetIdAndUsername() {
        Coreografo coreografo = new Coreografo();
        coreografo.setId(9L);
        coreografo.setUsername("mario");

        assertEquals(9L, coreografo.getId());
        assertEquals("mario", coreografo.getUsername());
    }
}
