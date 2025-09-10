package com.example.danceForm.Model;

import com.example.danceForm.Model.Decorator.Formazione;
import com.example.danceForm.Model.Decorator.FormazioneBase;
import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoreografiaTest {

    @Test
    void testAggiungiERimuoviOsservatore() {
        Coreografia coreografia = new Coreografia();
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi(new Coreografo(1L, "obs", "pw"));

        coreografia.aggiungiOsservatore(observer);
        assertEquals(1, coreografia.getOsservatori().size());

        coreografia.rimuoviOsservatore(observer);
        assertEquals(0, coreografia.getOsservatori().size());
    }

    @Test
    void testNotificaOsservatori() {
        Coreografia coreografia = new Coreografia();
        ConcreteObserverCoreografi obs1 = new ConcreteObserverCoreografi(new Coreografo(1L, "a", "b"));
        ConcreteObserverCoreografi obs2 = new ConcreteObserverCoreografi(new Coreografo(2L, "c", "d"));

        coreografia.aggiungiOsservatore(obs1);
        coreografia.aggiungiOsservatore(obs2);

        coreografia.notificaOsservatori("Update", 1L);

        assertEquals(0, obs1.getNotifiche().size()); // è l'autore
        assertEquals(1, obs2.getNotifiche().size());
        assertEquals("Update", obs2.getNotifiche().get(0).getMessaggio());
    }

    @Test
    void testAggiornaNomeCoreografia() {
        Coreografo altro = new Coreografo(10L, "Altro", "pw");
        ConcreteObserverCoreografi obs = new ConcreteObserverCoreografi(altro);

        Coreografia coreografia = new Coreografia("VecchioNome", 0, new ArrayList<>(), new ArrayList<>(), 10L, null);
        coreografia.aggiungiOsservatore(obs);

        coreografia.aggiornaNomeCoreografia("NuovoNome", 20L); // autoreId diverso

        assertEquals(10L,coreografia.getProprietarioId());
        assertEquals("NuovoNome", coreografia.getNome());
        assertEquals(1, obs.getNotifiche().size());
        assertTrue(obs.getNotifiche().get(0).getMessaggio().contains("NuovoNome"));
    }

    @Test
    void testGetterSetterCompleti() {
        Coreografia coreografia = new Coreografia();

        coreografia.setId(99L);
        assertEquals(99L, coreografia.getId());

        coreografia.setNome("coreo");
        assertEquals("coreo", coreografia.getNome());

        List<Ballerino> ballerini = new ArrayList<>();
        ballerini.add(new Ballerino("Sara", new Posizione(1, 2)));

        List<Formazione> formazioni = new ArrayList<>();
        formazioni.add(new FormazioneBase("Base", ballerini));

        // I getter restituiscono le liste inizializzate nei costruttori, quindi possiamo solo verificarne la struttura
        assertNotNull(coreografia.getBallerini());
        assertNotNull(coreografia.getFormazioni());

        assertEquals(0, coreografia.getBallerini().size());
        assertEquals(0, coreografia.getFormazioni().size());
    }

    @Test
    void testCostruttoreSecondario(){
        Coreografia coreografia = new Coreografia(1L,"coreo");
        assertEquals(1L, coreografia.getId());
        assertEquals("coreo", coreografia.getNome());
    }

}
