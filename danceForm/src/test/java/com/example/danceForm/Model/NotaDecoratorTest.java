package com.example.danceForm.Model;

import com.example.danceForm.Model.Decorator.FormazioneBase;
import com.example.danceForm.Model.Decorator.NotaDecorator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotaDecoratorTest {

    @Test
    void testGetAndSetNota() {
        FormazioneBase base = new FormazioneBase("Intro danza", List.of());
        NotaDecorator decorato = new NotaDecorator(base, "Luce blu");

        assertEquals("Luce blu", decorato.getNota());

        decorato.setNota("Luce strobo");
        assertEquals("Luce strobo", decorato.getNota());
    }

    // Serve per coprire il ramo di visualizzazione
    @Test
    void testVisualizza() {
        FormazioneBase base = new FormazioneBase("Esibizione", List.of());
        NotaDecorator decorato = new NotaDecorator(base, "Pausa di effetto");
        decorato.visualizza();
    }

    @Test
    void testIdAndNomeDelegation() {
        FormazioneBase base = new FormazioneBase("Formazione Originale", List.of());
        base.setId(88L);
        NotaDecorator decorato = new NotaDecorator(base, "Nota iniziale");

        assertEquals(base, decorato.getComponente());
        assertEquals(88L, decorato.getId());
        assertEquals("Formazione Originale", decorato.getNome());

        decorato.setId(99L);
        decorato.setNome("Modificata con nota");

        assertEquals(99L, base.getId());
        assertEquals("Modificata con nota", base.getNome());
    }

    @Test
    void testCostruttoreDefaultNotaDecorator() {
        NotaDecorator n = new NotaDecorator();
        assertNotNull(n);
    }
}
