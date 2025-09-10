package com.example.danceForm.Util;

public class IdManager {
    private static long ballerinoIdCounter = 1;
    private static long formazioneIdCounter = 1;

    // Chiamato per assegnare nuovo ID a un ballerino
    public static synchronized long nextBallerinoId() {
        return ballerinoIdCounter++;
    }

    // Chiamato per assegnare nuovo ID a una formazione
    public static synchronized long nextFormazioneId() {
        return formazioneIdCounter++;
    }
}
