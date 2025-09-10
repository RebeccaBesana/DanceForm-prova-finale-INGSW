package com.example.danceForm.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdManagerTest {

    @Test
    void testNextBallerinoIdIncrementsCorrectly() {
        long id1 = IdManager.nextBallerinoId();
        long id2 = IdManager.nextBallerinoId();
        long id3 = IdManager.nextBallerinoId();

        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }

    @Test
    void testNextFormazioneIdIncrementsCorrectly() {
        long id1 = IdManager.nextFormazioneId();
        long id2 = IdManager.nextFormazioneId();

        assertTrue(id2 > id1);
    }

}
