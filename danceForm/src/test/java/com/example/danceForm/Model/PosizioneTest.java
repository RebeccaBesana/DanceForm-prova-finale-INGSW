package com.example.danceForm.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PosizioneTest {

    @Test
    void testDefaultConstructor() {
        Posizione posizione = new Posizione();
        assertEquals(0, posizione.getX(), "x dovrebbe essere 0 nel costruttore default");
        assertEquals(0, posizione.getY(), "y dovrebbe essere 0 nel costruttore default");
    }

    @Test
    void testParametrizedConstructor() {
        Posizione posizione = new Posizione(10, 20);
        assertEquals(10, posizione.getX());
        assertEquals(20, posizione.getY());
    }

    @Test
    void testSetAndGetX() {
        Posizione posizione = new Posizione();
        posizione.setX(7);
        assertEquals(7, posizione.getX());
    }

    @Test
    void testSetAndGetY() {
        Posizione posizione = new Posizione();
        posizione.setY(14);
        assertEquals(14, posizione.getY());
    }
}
