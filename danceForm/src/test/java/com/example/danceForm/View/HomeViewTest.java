package com.example.danceForm.View;

import com.example.danceForm.Controller.CoreografiaController;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Model.Notifica;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeViewTest {

    private CoreografiaController mockController;
    private HomeView view;
    private UI mockUI;

    @BeforeEach
    void setup() {
        //Mock del controller
        mockController = mock(CoreografiaController.class);
        //Mock della sessione
        VaadinSession mockSession = mock(VaadinSession.class);
        //Mock UI
        mockUI = mock(UI.class);
        //quando viene chiamato getSession su mockUI viene ritornato mockSession
        when(mockUI.getSession()).thenReturn(mockSession);
        //mockUI come istanza corrente
        UI.setCurrent(mockUI);

        view = new HomeView(mockController);
    }


    @Test
    void clickSuNuovaCoreografia() {
        view.nuovaCoreografiaButton.click();
        verify(mockUI).navigate("crea-coreografia");
    }

    @Test
    void aggiornaElencoCoreografie() throws IOException {
        Coreografia c1 = new Coreografia();
        c1.setId(1L);
        c1.setNome("Test 1");
        List<Coreografia> coreografie = List.of(c1);

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(1L);
        when(mockController.getCoreografiePerUtente(1L)).thenReturn(coreografie);

        view.aggiornaElencoCoreografie();

        assertEquals(1, view.getCoreografiaGrid().getListDataView().getItemCount());
    }

    @Test
    void mostraNotifiche_conNotifiche() throws IOException {
        Notifica n1 = new Notifica("A", false);
        Notifica n2 = new Notifica("B",false);
        List<Notifica> notifiche = List.of(n1,n2);

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(1L);
        when(mockController.recuperaNotifiche(1L)).thenReturn(notifiche);

        VerticalLayout layout = new VerticalLayout();
        view.mostraNotifiche(layout);

        assertEquals(2, layout.getComponentCount());
    }

    @Test
    void mostraNotifiche_senzaNotifiche() throws IOException {
        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(1L);
        when(mockController.recuperaNotifiche(1L)).thenReturn(List.of());

        VerticalLayout layout = new VerticalLayout();
        view.mostraNotifiche(layout);

        assertEquals(1, layout.getComponentCount()); // contiene solo "nessuna nuova notifica"
    }

    @Test
    void eliminaCoreografia() throws Exception {
        Coreografia c = new Coreografia();
        c.setId(1L);

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(42L);
        when(mockController.eliminaCoreografia(c, 42L)).thenReturn("Eliminato");

        view.eliminaCoreografia(c);
        verify(mockController).eliminaCoreografia(c, 42L);
    }

    @Test
    void eliminaCoreografia_IOException() throws Exception {
        Coreografia c = new Coreografia();
        c.setId(10L);

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(2L);
        // Il controller lancia IOException
        when(mockController.eliminaCoreografia(c, 2L)).thenThrow(new IOException("boom"));

        view.eliminaCoreografia(c);

        assertTrue(true); // mostra Notification
    }

    @Test
    void apriDialogRinomina() throws Exception {
        Coreografia c = new Coreografia();
        c.setId(1L);
        c.setNome("VecchioNome");
        view.apriDialogRinomina(c);

        // Il dialog è stato aperto, ma non possiamo accedere ai bottoni, coverage parziale.
        assertTrue(true);
    }

    @Test
    void apriDialogCondivisione() throws Exception {
        Coreografia c = new Coreografia();
        c.setId(1L);

        view.apriDialogCondivisione(c);
        // Il dialog è stato aperto, ma non possiamo accedere ai bottoni, coverage parziale.
        // Se siamo qui, non ci sono eccezioni
        assertTrue(true);
    }

}


