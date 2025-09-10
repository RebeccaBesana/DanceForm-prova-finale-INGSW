package com.example.danceForm.Model;

import com.example.danceForm.Model.Decorator.FormazioneBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FormazioneBaseTest {

    @Test
    void testCostruttoreEGetters() {
        FormazioneBase formazione = new FormazioneBase("Formazione A", List.of());
        assertNotNull(formazione.getId());
        assertEquals("Formazione A", formazione.getNome());
        assertNotNull(formazione.getPosizioniBallerini());
        assertTrue(formazione.getPosizioniBallerini().isEmpty());
    }

    @Test
    void testSetIdAndSetNome() {
        FormazioneBase formazione = new FormazioneBase();
        formazione.setId(100L);
        formazione.setNome("Nuova Formazione");

        assertEquals(100L, formazione.getId());
        assertEquals("Nuova Formazione", formazione.getNome());
    }

    @Test
    void testAssegnaPosizione() {
        FormazioneBase formazione = new FormazioneBase();
        Posizione posizione = new Posizione(1, 2);
        formazione.assegnaPosizione(42L, posizione);

        Map<Long, Posizione> mappa = formazione.getPosizioniBallerini();
        assertTrue(mappa.containsKey(42L));
        assertEquals(posizione, mappa.get(42L));
        formazione.visualizza();// Serve per coprire il ramo di visualizzazione
    }

}
