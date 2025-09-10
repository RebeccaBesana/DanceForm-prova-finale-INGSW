package com.example.danceForm.Dto;

import com.example.danceForm.Dto.BallerinoPoint;
import com.example.danceForm.Dto.FormazioneViewData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testBallerinoPointGetters() {
        BallerinoPoint bp = new BallerinoPoint("Luca", 100, 200);

        assertEquals("Luca", bp.getNome(), "Il nome del ballerino non è corretto");
        assertEquals(100, bp.getX(), "La coordinata X non è corretta");
        assertEquals(200, bp.getY(), "La coordinata Y non è corretta");
    }

    @Test
    void testFormazioneViewDataGetters() {
        BallerinoPoint p1 = new BallerinoPoint("Luca", 10, 20);
        BallerinoPoint p2 = new BallerinoPoint("Marco", 30, 40);
        List<BallerinoPoint> punti = List.of(p1, p2);

        FormazioneViewData fvd = new FormazioneViewData("Formazione A", punti, "Nota esempio", "2 min");

        assertEquals("Formazione A", fvd.getNomeFormazione(), "Il nome formazione non è corretto");
        assertEquals(punti, fvd.getPunti(), "La lista di punti non è corretta");
        assertEquals("Nota esempio", fvd.getNota(), "La nota non è corretta");
        assertEquals("2 min", fvd.getMinutaggio(), "Il minutaggio non è corretto");
    }

}
