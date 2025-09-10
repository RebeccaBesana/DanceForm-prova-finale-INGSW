package com.example.danceForm.Model.Observer;

import com.example.danceForm.Model.Coreografo;
import com.example.danceForm.Model.Notifica;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("observer")
public class ConcreteObserverCoreografi implements ObserverCoreografi {

    private Coreografo coreografo;
    private List<Notifica> notifiche = new ArrayList<>();

    public ConcreteObserverCoreografi() {}  // Necessario per Jackson

    public ConcreteObserverCoreografi(Coreografo coreografo) {
        this.coreografo = coreografo;
    }

    @Override
    public void aggiorna(Coreografia coreografia, String messaggio, Long autoreId) {
        if (!coreografo.getId().equals(autoreId)) {
            notifiche.add(new Notifica(messaggio, false));
        }
    }

    public List<Notifica> getNotifiche() {
        return notifiche;
    }

    public List<Notifica> getNotificheNonLette() {
        return new ArrayList<>(notifiche.stream()
                .filter(n -> !n.isLetta())
                .toList());
    }

    public void aggiungiNotifica(String messaggio) {
        notifiche.add(new Notifica(messaggio, false));
    }

    public void segnaTutteComeLette() {
        notifiche.forEach(n -> n.setLetta(true));
        this.notifiche.clear();
    }

    public Coreografo getCoreografo() {
        return coreografo;
    }

    public void setCoreografo(Coreografo coreografo) {
        this.coreografo = coreografo;
    }

}
