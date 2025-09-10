package com.example.danceForm.View;

import com.example.danceForm.Controller.CoreografiaController;
import com.example.danceForm.Dto.BallerinoPoint;
import com.example.danceForm.Dto.FormazioneViewData;
import com.example.danceForm.Model.Ballerino;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Model.Decorator.Formazione;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisualizzaCoreografiaViewTest {

    private CoreografiaController mockController;
    private VisualizzaCoreografiaView view;
    private UI mockUI;
    private VaadinSession mockSession;

    @BeforeEach
    void setup() {
        mockController = mock(CoreografiaController.class);
        view = new VisualizzaCoreografiaView(mockController);

        mockSession = mock(VaadinSession.class);
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
        when(e.getLocation()).thenReturn(new Location("coreografia/" + (id == null ? "" : id), QueryParameters.empty()));
        return e;
    }

    @Test
    void beforeEnter_idMancante() {
        view.beforeEnter(eventWithId(null));
        assertEquals("⚠️ ID non fornito", view.getTitoloCoreografiaSpan().getText());
    }

    @Test
    void beforeEnter_idNonNumerico() {
        view.beforeEnter(eventWithId("abc")); // Long.parseLong("abc") -> NumberFormatException
        assertEquals("❌ Errore nel caricamento", view.getTitoloCoreografiaSpan().getText());
    }

    @Test
    void beforeEnter_coreografiaNonTrovata() throws IOException {
        when(mockController.getCoreografiaById(99L)).thenReturn(null);
        view.beforeEnter(eventWithId("99"));
        assertEquals("⚠️ Coreografia non trovata", view.getTitoloCoreografiaSpan().getText());
    }

    @Test
    void beforeEnter_ioException() throws IOException {
        when(mockController.getCoreografiaById(1L)).thenThrow(new IOException("boom"));
        view.beforeEnter(eventWithId("1"));
        assertEquals("❌ Errore nel caricamento", view.getTitoloCoreografiaSpan().getText());
    }

    @Test
    void beforeEnter_coreografiaSenzaFormazioni() throws IOException {
        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(1L);
        when(coreo.getNome()).thenReturn("Coreo A");

        Ballerino b1 = mock(Ballerino.class); when(b1.getNome()).thenReturn("Anna");
        Ballerino b2 = mock(Ballerino.class); when(b2.getNome()).thenReturn("Luca");

        when(coreo.getBallerini()).thenReturn(List.of(b1, b2));
        when(coreo.getFormazioni()).thenReturn(Collections.emptyList());

        when(mockController.getCoreografiaById(1L)).thenReturn(coreo);
        when(mockController.isBeingEdited(1L)).thenReturn(false);

        view.beforeEnter(eventWithId("1"));

        assertEquals("Coreo A", view.getTitoloCoreografiaSpan().getText());
        assertEquals(2, view.getLayoutBallerini().getComponentCount()); // nomi renderizzati
        // Nessuna formazione -> nomeFormazione vuoto
        assertEquals("", view.getNomeFormazioneSpan().getText());
    }

    @Test
    void beforeEnter_coreografiaConFormazione() throws IOException {
        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(7L);
        when(coreo.getNome()).thenReturn("Coreo X");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());

        Formazione f0 = mock(Formazione.class);
        when(coreo.getFormazioni()).thenReturn(List.of(f0));

        when(mockController.getCoreografiaById(7L)).thenReturn(coreo);
        when(mockController.isBeingEdited(7L)).thenReturn(false);

        // DTO della formazione
        BallerinoPoint p1 = mock(BallerinoPoint.class);
        when(p1.getX()).thenReturn(100);
        when(p1.getY()).thenReturn(200);
        when(p1.getNome()).thenReturn("Anna");

        BallerinoPoint p2 = mock(BallerinoPoint.class);
        when(p2.getX()).thenReturn(300);
        when(p2.getY()).thenReturn(400);
        when(p2.getNome()).thenReturn("Luca");

        FormazioneViewData data = mock(FormazioneViewData.class);
        when(data.getNomeFormazione()).thenReturn("Intro");
        when(data.getPunti()).thenReturn(List.of(p1, p2));
        when(data.getNota()).thenReturn("Entrata laterale");
        when(data.getMinutaggio()).thenReturn("1.5"); // <- è String nella tua classe

        when(mockController.getFormazioneViewData(7L, 0)).thenReturn(data);

        view.beforeEnter(eventWithId("7"));

        assertEquals("Coreo X", view.getTitoloCoreografiaSpan().getText());
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());
        assertEquals("📝 Note: Entrata laterale", view.getNoteSpan().getText());
        assertEquals("⏱ Minutaggio: 1.5 min", view.getMinutiSpan().getText());

        // per ogni punto: un pallino + un nome -> 2 componenti * 2 = 4
        assertEquals(4, view.getPalcoDiv().getComponentCount());
    }

    @Test
    void mostraFormazioneCorrente_ioException() throws IOException {
        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(5L);
        when(coreo.getNome()).thenReturn("Coreo IOEx");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());
        when(coreo.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));

        when(mockController.getCoreografiaById(5L)).thenReturn(coreo);
        when(mockController.isBeingEdited(5L)).thenReturn(false);
        when(mockController.getFormazioneViewData(5L, 0)).thenThrow(new IOException("boom"));

        assertDoesNotThrow(() -> view.beforeEnter(eventWithId("5")));
    }

    @Test
    void mostraFormazioneCorrente_IllegalArgument() throws IOException {
        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(6L);
        when(coreo.getNome()).thenReturn("Coreo IllegalArg");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());
        when(coreo.getFormazioni()).thenReturn(List.of(mock(Formazione.class)));

        when(mockController.getCoreografiaById(6L)).thenReturn(coreo);
        when(mockController.isBeingEdited(6L)).thenReturn(false);
        when(mockController.getFormazioneViewData(6L, 0))
                .thenThrow(new IllegalArgumentException("indice non valido"));

        assertDoesNotThrow(() -> view.beforeEnter(eventWithId("6")));
    }

    @Test
    void cambiaFormazione() throws IOException {
        Coreografia coreo = mock(Coreografia.class);
        when(coreo.getId()).thenReturn(10L);
        when(coreo.getNome()).thenReturn("Coreo Step");
        when(coreo.getBallerini()).thenReturn(Collections.emptyList());

        Formazione f0 = mock(Formazione.class);
        Formazione f1 = mock(Formazione.class);
        when(coreo.getFormazioni()).thenReturn(List.of(f0, f1));

        when(mockController.getCoreografiaById(10L)).thenReturn(coreo);
        when(mockController.isBeingEdited(10L)).thenReturn(false);

        FormazioneViewData data0 = mock(FormazioneViewData.class);
        when(data0.getNomeFormazione()).thenReturn("Intro");
        when(data0.getPunti()).thenReturn(Collections.emptyList());
        when(data0.getNota()).thenReturn("");
        when(data0.getMinutaggio()).thenReturn(null);

        FormazioneViewData data1 = mock(FormazioneViewData.class);
        when(data1.getNomeFormazione()).thenReturn("Finale");
        when(data1.getPunti()).thenReturn(Collections.emptyList());
        when(data1.getNota()).thenReturn("");
        when(data1.getMinutaggio()).thenReturn(null);

        when(mockController.getFormazioneViewData(10L, 0)).thenReturn(data0);
        when(mockController.getFormazioneViewData(10L, 1)).thenReturn(data1);

        // entra -> mostra 0
        view.beforeEnter(eventWithId("10"));
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());

        // avanti
        view.nextFormation();
        assertEquals("Formazione: Finale", view.getNomeFormazioneSpan().getText());

        // indietro
        view.prevFormation();
        assertEquals("Formazione: Intro", view.getNomeFormazioneSpan().getText());
    }
}
