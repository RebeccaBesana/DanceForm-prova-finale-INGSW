package com.example.danceForm.Model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.danceForm.Model.Ballerino;
import com.example.danceForm.Model.Posizione;
import org.junit.jupiter.api.*;

class BallerinoTest {
    @Test
    void testConstructorAssignsFieldsCorrectly() {
        Posizione posizione = new Posizione(5, 3);
        Ballerino ballerino = new Ballerino("Giulia", posizione);

        assertNotNull(ballerino.getId(), "L'id dovrebbe essere assegnato automaticamente");
        assertEquals("Giulia", ballerino.getNome(), "Il nome non corrisponde");
        assertEquals(posizione, ballerino.getPosizione(), "La posizione non corrisponde");
    }

    @Test
    void testSetId() {
        Ballerino ballerino = new Ballerino();
        ballerino.setId(42L);

        assertEquals(42L, ballerino.getId());
    }
}
