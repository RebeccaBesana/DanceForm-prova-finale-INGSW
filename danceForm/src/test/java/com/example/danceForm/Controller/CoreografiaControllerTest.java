package com.example.danceForm.Controller;

import com.example.danceForm.Model.Decorator.Formazione;
import com.example.danceForm.Model.Decorator.FormazioneBase;
import com.example.danceForm.Model.Decorator.MinutaggioDecorator;
import com.example.danceForm.Model.Decorator.NotaDecorator;
import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Service.CoreografiaService;
import com.example.danceForm.Service.CoreografoService;
import com.example.danceForm.Model.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreografiaControllerTest {

    private CoreografiaService service;
    private CoreografiaController controller;
    private CoreografoService coreografoService;

    @BeforeEach
    void setUp() {
        service = mock(CoreografiaService.class);
        coreografoService = mock (CoreografoService.class);// Simula comportamento service
        controller = new CoreografiaController(service, coreografoService);
    }

    @Test
    void testSalvaCoreografia() throws IOException {
        Coreografia coreografia = new Coreografia();
        when(service.saveCoreografia(coreografia)).thenReturn(coreografia);

        Coreografia result = controller.salvaCoreografia(coreografia);
        assertEquals(coreografia, result);
        verify(service).saveCoreografia(coreografia);
    }

    @Test
    void testCreaCoreografia_Valido() throws IOException {
        // Arrange
        String nome = "Coreografia Test";
        Integer numeroBallerini = 2;
        List<String> nomi = List.of("Alice", "Bob");
        Long coreografoId = 1L;
        Coreografo coreografo = new Coreografo(coreografoId, "utente", "pw");

        when(coreografoService.getCoreografoById(coreografoId)).thenReturn(coreografo);

        // Act
        Coreografia coreografia = controller.creaCoreografia(nome, numeroBallerini, nomi, coreografoId);

        // Assert
        assertNotNull(coreografia);
        assertEquals(nome, coreografia.getNome());
        assertEquals(2, coreografia.getBallerini().size());
        assertEquals(coreografoId, coreografia.getProprietarioId());

        verify(coreografoService).getCoreografoById(coreografoId);
    }

    @Test
    void testCreaCoreografia_NomeNonValido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.creaCoreografia("", 2, List.of("A", "B"), 1L));
        assertEquals("Inserisci un nome per la coreografia.", ex.getMessage());
    }

    @Test
    void testCreaCoreografia_NumeroBalleriniNonValido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.creaCoreografia("Test", 0, List.of("A", "B"), 1L));
        assertEquals("Numero ballerini non valido.", ex.getMessage());
    }

    @Test
    void testCreaCoreografia_NomiNonValido() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.creaCoreografia("Test", 2, List.of("Alice", ""), 1L));
        assertEquals("Inserisci tutti i nomi dei ballerini.", ex.getMessage());
    }

    @Test
    void testEliminaCoreografia_UnicoOsservatore() throws IOException {
        Coreografo c = new Coreografo(1L, "utente", "pw");
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi(c);

        Coreografia coreografia = new Coreografia();
        coreografia.setId(10L);
        coreografia.aggiungiOsservatore(observer);

        String result = controller.eliminaCoreografia(coreografia, 1L);

        verify(service).deleteCoreografia(10L);
        assertEquals("Coreografia eliminata completamente", result);
    }

    @Test
    void testEliminaCoreografia_ConAltriOsservatori() throws IOException {
        Coreografo c1 = new Coreografo(1L, "utente", "pw");
        Coreografo c2 = new Coreografo(2L, "altro", "pw");

        Coreografia coreografia = new Coreografia();
        coreografia.setId(20L);
        coreografia.aggiungiOsservatore(new ConcreteObserverCoreografi(c1));
        coreografia.aggiungiOsservatore(new ConcreteObserverCoreografi(c2));

        String result = controller.eliminaCoreografia(coreografia, 1L);

        verify(service, never()).deleteCoreografia(any());
        verify(service).rimuoviOsservatore(20L, 1L);
        assertEquals("Rimosso dalla coreografia condivisa", result);
    }


    @Test
    void testGetCoreografiaById() throws IOException {
        Coreografia coreografia = new Coreografia();
        when(service.getCoreografiaById(5L)).thenReturn(coreografia);

        Coreografia result = controller.getCoreografiaById(5L);
        assertEquals(coreografia, result);
    }

    @Test
    void testGetCoreografiePerUtente() throws IOException {
        List<Coreografia> lista = List.of(new Coreografia(), new Coreografia());
        when(service.getCoreografieVisibili(7L)).thenReturn(lista);

        List<Coreografia> result = controller.getCoreografiePerUtente(7L);
        assertEquals(lista, result);
    }

    @Test
    void testRinominaCoreografia() throws IOException {
        try (MockedStatic<UI> mockedUI = mockStatic(UI.class)) {
            UI mockUI = mock(UI.class);
            VaadinSession session = mock(VaadinSession.class);

            mockedUI.when(UI::getCurrent).thenReturn(mockUI);
            when(mockUI.getSession()).thenReturn(session);
            when(session.getAttribute("coreografoId")).thenReturn(42L);

            controller.rinominaCoreografia(1L, "NuovoNome");
            verify(service).rinominaCoreografia(1L, "NuovoNome", 42L);
        }
    }

    @Test
    void testAggiungiOsservatore() throws IOException {
        controller.aggiungiOsservatore(1L, "luca");
        verify(service).aggiungiOsservatore(1L, "luca");
    }

    @Test
    void testRimuoviOsservatore() throws IOException {
        controller.rimuoviOsservatore(3L, 2L);
        verify(service).rimuoviOsservatore(3L, 2L);
    }

    @Test
    void testRecuperaNotificheRitornaNonLetteEdAggiorna() throws IOException {
        // Setup coreografo e osservatore
        Long coreografoId = 1L;
        Coreografo coreografo = new Coreografo(coreografoId, "utente", "pw");

        Notifica n1 = new Notifica("Messaggio 1", false);
        Notifica n2 = new Notifica("Messaggio 2", false);

        ConcreteObserverCoreografi observer = spy(new ConcreteObserverCoreografi(coreografo));
        observer.aggiungiNotifica(n1.getMessaggio());
        observer.aggiungiNotifica(n2.getMessaggio());

        Coreografia coreografia = new Coreografia();
        coreografia.setId(10L);
        coreografia.aggiungiOsservatore(observer);

        // Mock del service
        when(service.getCoreografieVisibili(coreografoId)).thenReturn(List.of(coreografia));
        when(service.saveCoreografia(coreografia)).thenReturn(coreografia);

        // Esecuzione
        List<Notifica> result = controller.recuperaNotifiche(coreografoId);

        // Verifiche
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(n -> n.getMessaggio().equals("Messaggio 1")));
        verify(service).saveCoreografia(coreografia);
        verify(observer).segnaTutteComeLette();
    }

    @Test
    void testIsBeingEdited() {
        try (MockedStatic<com.example.danceForm.Util.CoreografiaAccessManager> mockManager = mockStatic(com.example.danceForm.Util.CoreografiaAccessManager.class)) {
            var instance = mock(com.example.danceForm.Util.CoreografiaAccessManager.class);
            mockManager.when(com.example.danceForm.Util.CoreografiaAccessManager::getInstance).thenReturn(instance);
            when(instance.isBeingEdited(1L)).thenReturn(true);

            assertTrue(controller.isBeingEdited(1L));
            verify(instance).isBeingEdited(1L);
        }
    }

    @Test
    void testTryAcquireEditLock() {
        try (MockedStatic<com.example.danceForm.Util.CoreografiaAccessManager> mockManager = mockStatic(com.example.danceForm.Util.CoreografiaAccessManager.class)) {
            var instance = mock(com.example.danceForm.Util.CoreografiaAccessManager.class);
            mockManager.when(com.example.danceForm.Util.CoreografiaAccessManager::getInstance).thenReturn(instance);
            when(instance.tryAcquire(1L, "sess")).thenReturn(true);

            assertTrue(controller.tryAcquireEditLock(1L, "sess"));
            verify(instance).tryAcquire(1L, "sess");
        }
    }

    @Test
    void testReleaseEditLock() {
        try (MockedStatic<com.example.danceForm.Util.CoreografiaAccessManager> mockManager = mockStatic(com.example.danceForm.Util.CoreografiaAccessManager.class)) {
            var instance = mock(com.example.danceForm.Util.CoreografiaAccessManager.class);
            mockManager.when(com.example.danceForm.Util.CoreografiaAccessManager::getInstance).thenReturn(instance);

            controller.releaseEditLock(2L, "sess");
            verify(instance).release(2L, "sess");
        }
    }

    @Test
    void testGetFormazioneViewData_Valid() throws IOException {
        Ballerino b = new Ballerino("Alice", new Posizione(0, 0));
        b.setId(1L);

        Posizione pos = new Posizione(10, 20);
        FormazioneBase base = new FormazioneBase();
        base.assegnaPosizione(1L, pos);

        Formazione formazione = new NotaDecorator(new MinutaggioDecorator(base, "2min"), "nota");
        formazione.setNome("FormazioneTest");

        Coreografia c = new Coreografia();
        c.setBallerini(List.of(b));
        c.setFormazioni(List.of(formazione));

        when(service.getCoreografiaById(1L)).thenReturn(c);

        var result = controller.getFormazioneViewData(1L, 0);

        assertEquals("FormazioneTest", result.getNomeFormazione());
        assertEquals(1, result.getPunti().size());
        assertEquals("nota", result.getNota());
        assertEquals("2min", result.getMinutaggio());
    }

    @Test
    void testGetFormazioneViewData_CoreografiaNonTrovata() throws IOException {
        when(service.getCoreografiaById(1L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> controller.getFormazioneViewData(1L, 0));
    }

    @Test
    void testGetFormazioneViewData_NessunaFormazione() throws IOException {
        Coreografia c = new Coreografia();
        c.setFormazioni(List.of());
        when(service.getCoreografiaById(1L)).thenReturn(c);
        assertThrows(IllegalArgumentException.class, () -> controller.getFormazioneViewData(1L, 0));
    }

    @Test
    void testGetFormazioneViewData_IndiceNonValido() throws IOException {
        Coreografia c = new Coreografia();
        c.setFormazioni(List.of(new FormazioneBase()));
        when(service.getCoreografiaById(1L)).thenReturn(c);
        assertThrows(IllegalArgumentException.class, () -> controller.getFormazioneViewData(1L, 5));
    }

    @Test
    void testAssignPosizione() throws IOException {
        Coreografia c = new Coreografia();
        FormazioneBase base = new FormazioneBase();
        c.setFormazioni(List.of(base));

        when(service.getCoreografiaById(1L)).thenReturn(c);

        controller.assignPosizione(1L, 0, 2L, 50, 60);
        assertEquals(50, base.getPosizioniBallerini().get(2L).getX());
        assertEquals(60, base.getPosizioniBallerini().get(2L).getY());
        verify(service).saveCoreografia(c);
    }

    @Test
    void testRemoveFormazione() throws IOException {
        Coreografia c = new Coreografia();
        c.setFormazioni(new java.util.ArrayList<>(List.of(new FormazioneBase())));
        when(service.getCoreografiaById(1L)).thenReturn(c);

        controller.removeFormazione(1L, 0);
        assertTrue(c.getFormazioni().isEmpty());
        verify(service).saveCoreografia(c);
    }

    @Test
    void testCreaFormazioneVuota() throws IOException {
        controller.creaFormazioneVuota(9L, "Formazione X");
        verify(service).creaFormazioneVuota(9L, "Formazione X");
    }

    @Test
    void testUpsertNota_Nuova() throws IOException {
        Coreografia c = new Coreografia();

        // Creo base e avvolgo con un MinutaggioDecorator
        FormazioneBase base = new FormazioneBase();
        MinutaggioDecorator minDec = new MinutaggioDecorator(base, "3min");
        c.setFormazioni(new java.util.ArrayList<>(List.of(minDec)));
        when(service.getCoreografiaById(1L)).thenReturn(c);

        controller.upsertNota(1L, 0, "nuova nota");
        // MinutaggioDecorator deve essere avvolto in un NotaDecorator
        assertTrue(c.getFormazioni().get(0) instanceof NotaDecorator
                || (c.getFormazioni().get(0) instanceof MinutaggioDecorator
                && ((MinutaggioDecorator) c.getFormazioni().get(0)).getComponente() instanceof NotaDecorator));

        verify(service).saveCoreografia(c);
    }


    @Test
    void testUpsertNota_Esistente() throws IOException {
        NotaDecorator nota = new NotaDecorator(new FormazioneBase(), "vecchia");
        Coreografia c = new Coreografia();
        c.setFormazioni(new java.util.ArrayList<>(List.of(nota)));
        when(service.getCoreografiaById(1L)).thenReturn(c);

        controller.upsertNota(1L, 0, "nuova");
        assertEquals("nuova", nota.getNota());
        verify(service).saveCoreografia(c);
    }

    @Test
    void testUpsertMinutaggio_Nuovo() throws IOException {
        Coreografia c = new Coreografia();

        // Creo base e avvolgo con un NotaDecorator
        FormazioneBase base = new FormazioneBase();
        NotaDecorator notaDec = new NotaDecorator(base, "nota iniziale");
        c.setFormazioni(new java.util.ArrayList<>(List.of(notaDec)));
        when(service.getCoreografiaById(1L)).thenReturn(c);

        controller.upsertMinutaggio(1L, 0, "5min");
        //NotaDecorator deve avvolgere un MinutaggioDecorator
        assertTrue(c.getFormazioni().get(0) instanceof MinutaggioDecorator
                || (c.getFormazioni().get(0) instanceof NotaDecorator
                && ((NotaDecorator) c.getFormazioni().get(0)).getComponente() instanceof MinutaggioDecorator));

        verify(service).saveCoreografia(c);
    }


    @Test
    void testUpsertMinutaggio_Esistente() throws IOException {
        MinutaggioDecorator md = new MinutaggioDecorator(new FormazioneBase(), "3min");
        Coreografia c = new Coreografia();
        c.setFormazioni(new java.util.ArrayList<>(List.of(md)));
        when(service.getCoreografiaById(1L)).thenReturn(c);

        controller.upsertMinutaggio(1L, 0, "5min");
        assertEquals("5min", md.getMinutaggio());
        verify(service).saveCoreografia(c);
    }

    @Test
    void testSaveAndNotifyModified() throws IOException {
        Coreografia c = spy(new Coreografia());
        c.setNome("Prova");
        controller.saveAndNotifyModified(c, 42L);
        verify(c).notificaOsservatori(anyString(), eq(42L));
        verify(service).saveCoreografia(c);
    }

}
