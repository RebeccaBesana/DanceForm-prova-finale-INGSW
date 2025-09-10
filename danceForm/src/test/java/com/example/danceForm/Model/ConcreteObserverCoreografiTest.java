package com.example.danceForm.Model;

import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteObserverCoreografiTest {

    @Test
    void testAggiornaNonAutoreRiceveNotifica() {
        Coreografo marco = new Coreografo(1L, "Marco", "pw");
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi(marco);
        Coreografia coreografia = new Coreografia();

        observer.aggiorna(coreografia, "Nuovo aggiornamento", 2L);

        List<Notifica> notifiche = observer.getNotifiche();
        assertEquals(1, notifiche.size());
        assertEquals("Nuovo aggiornamento", notifiche.get(0).getMessaggio());
        assertFalse(notifiche.get(0).isLetta());

        Coreografo luca = new Coreografo(2L, "Luca", "pwL");
        observer.setCoreografo(luca);
        assertEquals(luca, observer.getCoreografo());
    }



    @Test
    void testAggiungiNotifica() {
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi();
        observer.aggiungiNotifica("Test");

        assertEquals(1, observer.getNotifiche().size());
        assertEquals("Test", observer.getNotifiche().get(0).getMessaggio());
    }

    @Test
    void testSegnaTutteComeLette() {
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi();
        observer.aggiungiNotifica("Messaggio 1");
        observer.aggiungiNotifica("Messaggio 2");

        observer.segnaTutteComeLette();
        assertTrue(observer.getNotifiche().isEmpty());
    }

    @Test
    void testGetNotificheNonLette() {
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi();
        observer.aggiungiNotifica("Letta");
        observer.getNotifiche().get(0).setLetta(true);
        observer.aggiungiNotifica("Non letta");

        List<Notifica> nonLette = observer.getNotificheNonLette();
        assertEquals(1, nonLette.size());
        assertEquals("Non letta", nonLette.get(0).getMessaggio());
    }
}
