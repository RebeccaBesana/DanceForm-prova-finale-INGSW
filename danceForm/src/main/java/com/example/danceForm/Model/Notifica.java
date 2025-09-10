package com.example.danceForm.Model;

public class Notifica {

    private String messaggio;
    private boolean letta;

    public Notifica() {}  // Necessario per Jackson

    public Notifica(String messaggio, boolean letta) {
        this.messaggio = messaggio;
        this.letta = letta;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public boolean isLetta() {
        return letta;
    }

    public void setLetta(boolean letta) {
        this.letta = letta;
    }
}

