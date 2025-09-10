package com.example.danceForm.Util;

import java.util.HashMap;
import java.util.Map;

public class CoreografiaAccessManager {

    // Unica istanza
    private static final CoreografiaAccessManager instance = new CoreografiaAccessManager();
    // Mappa che associa ID della coreografia all’ID dell’utente che la sta modificando
    private final Map<Long, String> editingUsers = new HashMap<>();

    // Costruttore privato, impedisce new CoreografiaAccessManager()
    private CoreografiaAccessManager() {}

    // Metodo pubblico per accedere all'istanza condivisa
    public static CoreografiaAccessManager getInstance() {
        return instance;
    }

    // Tentativo di acquisire il lock, true se riuscito, false se già occupato da un altro utente
    public synchronized boolean tryAcquire(Long coreografiaId, String sessionId) {
        if (!editingUsers.containsKey(coreografiaId)) {
            editingUsers.put(coreografiaId, sessionId);
            return true;
        }
        return editingUsers.get(coreografiaId).equals(sessionId); // Già acquisito dalla stessa sessione
    }

    // Rilascia il lock solo se detenuto dall’utente specificato
    public synchronized void release(Long coreografiaId, String sessionId) {
        if (editingUsers.containsKey(coreografiaId) && editingUsers.get(coreografiaId).equals(sessionId)) {
            editingUsers.remove(coreografiaId);
        }
    }

    // Controllo
    public synchronized boolean isBeingEdited(Long coreografiaId) {
        return editingUsers.containsKey(coreografiaId);
    }
}
