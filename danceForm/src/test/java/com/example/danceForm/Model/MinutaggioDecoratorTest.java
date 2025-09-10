package com.example.danceForm.Model;

import com.example.danceForm.Model.Decorator.FormazioneBase;
import com.example.danceForm.Model.Decorator.MinutaggioDecorator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MinutaggioDecoratorTest {

    @Test
    void testGetAndSetMinutaggio() {
        FormazioneBase base = new FormazioneBase("Coreografia A", List.of());
        MinutaggioDecorator decorato = new MinutaggioDecorator(base, "3:20");

        assertEquals("3:20", decorato.getMinutaggio());

        decorato.setMinutaggio("4:00");
        assertEquals("4:00", decorato.getMinutaggio());
    }

    // serve solo a coprire il ramo della visualizzazione
    @Test
    void testVisualizza() {
        FormazioneBase base = new FormazioneBase("Coreografia B", List.of());
        MinutaggioDecorator decorato = new MinutaggioDecorator(base, "5:00");
        decorato.visualizza();
    }

    @Test
    void testIdAndNomeDelegation() {
        FormazioneBase base = new FormazioneBase("Base", List.of());
        base.setId(123L);
        MinutaggioDecorator decorato = new MinutaggioDecorator(base, "2:00");

        assertEquals(base,decorato.getComponente());
        assertEquals(123L, decorato.getId());
        assertEquals("Base", decorato.getNome());

        decorato.setId(456L);
        decorato.setNome("Modificata");

        assertEquals(456L, base.getId());
        assertEquals("Modificata", base.getNome());

    }

    @Test
    void testCostruttoreDefaultMinutaggioDecorator() {
        MinutaggioDecorator m = new MinutaggioDecorator();
        assertNotNull(m);
    }
}
