package com.example.danceForm.Model.Observer;

public interface Subject {
    void aggiungiOsservatore(ObserverCoreografi osservatore);
    void rimuoviOsservatore(ObserverCoreografi osservatore);
    void notificaOsservatori(String messaggio, Long autoreId);
}


