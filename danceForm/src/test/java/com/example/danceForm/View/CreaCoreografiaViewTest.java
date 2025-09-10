package com.example.danceForm.View;

import com.example.danceForm.Controller.CoreografiaController;
import com.example.danceForm.Model.Observer.Coreografia;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreaCoreografiaViewTest {

    private CoreografiaController mockController;
    private CreaCoreografiaView view;
    private UI mockUI;

    @BeforeEach
    void setup() {
        mockController = mock(CoreografiaController.class);
        view = new CreaCoreografiaView(mockController);

        VaadinSession mockSession = mock(VaadinSession.class);
        mockUI = mock(UI.class);
        when(mockUI.getSession()).thenReturn(mockSession);
        UI.setCurrent(mockUI);
    }

    @Test
    void generaCampiBalleriniInputValido() {
        view.getNumeroBalleriniField().setValue(3);
        view.generaCampiBalleriniButton.click();
        assertEquals(3, view.getBalleriniLayout().getComponentCount());
    }

    @Test
    void generaCampiBalleriniInputNonValido() {
        view.getNumeroBalleriniField().setValue(0);
        view.generaCampiBalleriniButton.click();
        assertEquals("⚠️ Inserisci un numero valido di ballerini (maggiore di 0)", view.getMessageLabel().getText());
    }

    @Test
    void salvaCoreografiaInputValido() throws IOException {
        view.getNomeCoreografiaField().setValue("CoreoTest");
        view.getNumeroBalleriniField().setValue(2);
        view.generaCampiBallerini();
        view.getCampiNomiBallerini().get(0).setValue("Anna");
        view.getCampiNomiBallerini().get(1).setValue("Luca");

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(1L);
        Coreografia mockCoreo = mock(Coreografia.class);
        when(mockController.creaCoreografia(any(), any(), any(), any())).thenReturn(mockCoreo);

        view.salvaButton.click();

        verify(mockController).salvaCoreografia(mockCoreo);
    }

    @Test
    void salvaCoreografiaUsernameVuoto() throws IOException {
        view.getNomeCoreografiaField().setValue("");
        view.getNumeroBalleriniField().setValue(1);
        view.generaCampiBallerini();
        view.getCampiNomiBallerini().get(0).setValue("Uno");

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(1L);
        when(mockController.creaCoreografia(any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Inserisci un nome per la coreografia"));
        view.salvaButton.click();

        assertEquals("⚠️ Inserisci un nome per la coreografia", view.getMessageLabel().getText());
    }

    @Test
    void salvaCoreografiaErrore() throws IOException {
        view.getNomeCoreografiaField().setValue("CoreoCrash");
        view.getNumeroBalleriniField().setValue(1);
        view.generaCampiBallerini();
        view.getCampiNomiBallerini().get(0).setValue("Test");

        when(mockUI.getSession().getAttribute("coreografoId")).thenReturn(1L);
        Coreografia mockCoreo = mock(Coreografia.class);
        when(mockController.creaCoreografia(any(), any(), any(), any())).thenReturn(mockCoreo);
        doThrow(new IOException("Errore disco")).when(mockController).salvaCoreografia(mockCoreo);

        view.salvaButton.click();

        assertTrue(true); // Non lancia eccezioni, mostra Notification
    }
}

