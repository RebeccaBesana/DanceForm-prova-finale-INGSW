package com.example.danceForm.View;

import com.example.danceForm.Controller.CoreografiaController;
import com.example.danceForm.Dto.BallerinoPoint;
import com.example.danceForm.Dto.FormazioneViewData;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Model.Decorator.Formazione;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ModificaCoreografiaViewTest {

    private CoreografiaController mockController;
    private ModificaCoreografiaView view;

    private UI mockUI;
    private VaadinSession mockSession;
    private WrappedSession mockWrapped;

    @BeforeEach
    void setup() {
        mockController = mock(CoreografiaController.class);
        view = new ModificaCoreografiaView(mockController);

        // Mock UI e sessione Vaadin
        mockSession = mock(VaadinSession.class);
        mockWrapped = mock(WrappedSession.class);
        when(mockSession.getSession()).thenReturn(mockWrapped);
        when(mockWrapped.getId()).thenReturn("sess-default"); // ID di default per i test

        mockUI = mock(UI.class);
        when(mockUI.getSession()).thenReturn(mockSession);
        UI.setCurrent(mockUI);
    }

    // Helper per simulare un BeforeEnterEvent con parametro :id
    private BeforeEnterEvent eventWithId(String id) {
        BeforeEnterEvent e = mock(BeforeEnterEvent.class);
        RouteParameters params = (id == null)
                ? new RouteParameters(Collections.emptyMap())
                : new RouteParameters(Map.of("id", id));
        when(e.getRouteParameters()).thenReturn(params);
        when(e.getLocation()).thenReturn(new Location("modifica-coreografia/" + (id == null ? "" : id), QueryParameters.empty()));
        return e;
    }

    // Helper per cambiare velocemente l'ID di sessione simulato
    private void setSessionId(String id) {
        when(mockWrapped.getId()).thenReturn(id);
    }

    @Test
    void beforeEnter_idMancante() {
        view.beforeEnter(eventWithId(null));
        assertEquals("", view.getTitoloCoreografiaSpan().getText());
    }

    @Test
    void beforeEnter_lockNonDisponibile() throws IOException {
        BeforeEnterEvent ev = eventWithId("10");
        setSessionId("sess-123");
        when(mockController.tryAcquireEditLock(10L, "sess-123")).thenReturn(false);

        view.beforeEnter(ev);

        verify(mockController).tryAcquireEditLock(10L, "sess-123");
        verify(ev).forwardTo("home");
        verify(mockController, never()).getCoreografiaById(anyLong());
    }

    @Test
    void beforeEnter_lockOk() throws IOException {
        BeforeEnterEvent ev = eventWithId("7");
        setSessionId("sess-xyz");
        when(mockController.tryAcquireEditLock(7L, "sess-xyz")).thenReturn(true);

        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(7L);
        when(coreo.getNome()).thenReturn("Coreo X");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());

        Formazione f0 = mock(Formazione.class);
        when(coreo.getFormazioni()).thenReturn(List.of(f0));
        when(mockController.getCoreografiaById(7L)).thenReturn(coreo);

        // DTO formazione 0
        BallerinoPoint p1 = mock(BallerinoPoint.class);
        when(p1.getX()).thenReturn(120); when(p1.getY()).thenReturn(180); when(p1.getNome()).thenReturn("Anna");
        BallerinoPoint p2 = mock(BallerinoPoint.class);
        when(p2.getX()).thenReturn(300); when(p2.getY()).thenReturn(350); when(p2.getNome()).thenReturn("Luca");

        FormazioneViewData data = mock(FormazioneViewData.class);
        when(data.getNomeFormazione()).thenReturn("Intro");
        when(data.getPunti()).thenReturn(List.of(p1, p2));
        when(data.getNota()).thenReturn("Entrata laterale");
        when(data.getMinutaggio()).thenReturn("1.5");

        when(mockController.getFormazioneViewData(7L, 0)).thenReturn(data);

        view.beforeEnter(ev);

        assertEquals("Coreo X", view.getTitoloCoreografiaSpan().getText());
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());
        assertEquals("📝 Nota: Entrata laterale", view.getNoteSpan().getText());
        assertEquals("⏱ Minutaggio: 1.5", view.getMinutiSpan().getText());
        // 2 ballerini -> 4 componenti
        assertEquals(4, view.getPalcoDiv().getComponentCount());
    }

    @Test
    void beforeEnter_ioException() throws IOException {
        BeforeEnterEvent ev = eventWithId("11");
        setSessionId("sess-io");
        when(mockController.tryAcquireEditLock(11L, "sess-io")).thenReturn(true);
        when(mockController.getCoreografiaById(11L)).thenThrow(new IOException("caricamento ko"));

        assertDoesNotThrow(() -> view.beforeEnter(ev));
    }

    @Test
    void mostraFormazioneCorrente_IOException() throws IOException {
        BeforeEnterEvent ev = eventWithId("5");
        setSessionId("sess-5");
        when(mockController.tryAcquireEditLock(5L, "sess-5")).thenReturn(true);

        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(5L);
        when(coreo.getNome()).thenReturn("Coreo IO");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());
        when(coreo.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));
        when(mockController.getCoreografiaById(5L)).thenReturn(coreo);

        when(mockController.getFormazioneViewData(5L, 0)).thenThrow(new IOException("boom"));

        assertDoesNotThrow(() -> view.beforeEnter(ev));
    }

    @Test
    void mostraFormazioneCorrente_IllegalArgument() throws IOException {
        BeforeEnterEvent ev = eventWithId("6");
        setSessionId("sess-6");
        when(mockController.tryAcquireEditLock(6L, "sess-6")).thenReturn(true);

        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(6L);
        when(coreo.getNome()).thenReturn("Coreo IAE");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());
        when(coreo.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));
        when(mockController.getCoreografiaById(6L)).thenReturn(coreo);

        when(mockController.getFormazioneViewData(6L, 0)).thenThrow(new IllegalArgumentException("indice out"));

        assertDoesNotThrow(() -> view.beforeEnter(ev));
    }

    @Test
    void cambiaFormazione() throws IOException {
        BeforeEnterEvent ev = eventWithId("10");
        setSessionId("sess-10");
        when(mockController.tryAcquireEditLock(10L, "sess-10")).thenReturn(true);

        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(10L);
        when(coreo.getNome()).thenReturn("Coreo Step");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());

        Formazione f0 = mock(Formazione.class);
        Formazione f1 = mock(Formazione.class);
        when(coreo.getFormazioni()).thenReturn(List.of(f0, f1));
        when(mockController.getCoreografiaById(10L)).thenReturn(coreo);

        FormazioneViewData data0 = mock(FormazioneViewData.class);
        when(data0.getNomeFormazione()).thenReturn("Intro");
        when(data0.getPunti()).thenReturn(Collections.emptyList());
        when(data0.getNota()).thenReturn("");
        when(data0.getMinutaggio()).thenReturn("");

        FormazioneViewData data1 = mock(FormazioneViewData.class);
        when(data1.getNomeFormazione()).thenReturn("Finale");
        when(data1.getPunti()).thenReturn(Collections.emptyList());
        when(data1.getNota()).thenReturn("");
        when(data1.getMinutaggio()).thenReturn("");

        when(mockController.getFormazioneViewData(10L, 0)).thenReturn(data0);
        when(mockController.getFormazioneViewData(10L, 1)).thenReturn(data1);

        //e ntra
        view.beforeEnter(ev);
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());

        // avanti
        view.nextFormation();
        assertEquals("Formazione: Finale", view.getNomeFormazioneSpan().getText());

        // indietro
        view.prevFormation();
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());

        // ai limiti
        view.prevFormation();
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());
        view.nextFormation();
        view.nextFormation();
        assertEquals("Formazione: Finale", view.getNomeFormazioneSpan().getText());
    }

    @Test
    void apriDialogCreaFormazione() {
        Coreografia c = mock(Coreografia.class);
        when(c.getId()).thenReturn(1L);
        when(c.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));

        view.setCoreografiaForTest(c);

        assertDoesNotThrow(() -> view.apriDialogCreaFormazione());
    }

    @Test
    void apriDialogNota() {
        Coreografia c = mock(Coreografia.class);
        when(c.getId()).thenReturn(2L);
        when(c.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));

        view.setCoreografiaForTest(c);

        assertDoesNotThrow(() -> view.apriDialogNota());
    }

    @Test
    void apriDialogMinutaggio() {
        Coreografia c = mock(Coreografia.class);
        when(c.getId()).thenReturn(3L);
        when(c.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));

        view.setCoreografiaForTest(c);

        assertDoesNotThrow(() -> view.apriDialogMinutaggio());
    }

    @Test
    void salvaModifiche_ControllerEReleaseLock() throws IOException {
        Coreografia c = mock(Coreografia.class);
        when(c.getId()).thenReturn(50L);
        view.setCoreografiaForTest(c);

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(123L);
        when(mockWrapped.getId()).thenReturn("sess-50");

        view.salvaModifiche();

        verify(mockController).saveAndNotifyModified(c, 123L);
        verify(mockController).releaseEditLock(50L, "sess-50");
    }

    @Test
    void setupBallerini_renderizzaNomi() {
        Coreografia c = mock(Coreografia.class);

        // lista di ballerini
        var b1 = mock(com.example.danceForm.Model.Ballerino.class); when(b1.getNome()).thenReturn("Anna");
        var b2 = mock(com.example.danceForm.Model.Ballerino.class); when(b2.getNome()).thenReturn("Luca");
        when(c.getBallerini()).thenReturn(List.of(b1, b2));

        view.setCoreografiaForTest(c);

        assertDoesNotThrow(() -> view.setupBallerini());
        assertEquals(2, view.getLayoutBallerini().getComponentCount());
    }


}
