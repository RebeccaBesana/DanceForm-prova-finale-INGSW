package com.example.danceForm.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificaTest {

    @Test
    void testCostruttoreConParametri() {
        Notifica notifica = new Notifica("Nuova coreografia", false);

        assertEquals("Nuova coreografia", notifica.getMessaggio());
        assertFalse(notifica.isLetta());
    }

    @Test
    void testCostruttoreDefaultESetter() {
        Notifica notifica = new Notifica();
        assertNull(notifica.getMessaggio()); // non viene inizializzato nel costruttore vuoto
        assertFalse(notifica.isLetta()); // valore boolean default = false

        notifica.setLetta(true);
        assertTrue(notifica.isLetta());
    }
}
