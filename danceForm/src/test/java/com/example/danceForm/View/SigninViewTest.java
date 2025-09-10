package com.example.danceForm.View;

import com.example.danceForm.Controller.AuthController;
import com.example.danceForm.Model.Coreografo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SigninViewTest {

    private AuthController mockController;
    private SigninView signinView;
    private UI mockUI;
    private VaadinSession mockSession;

    @BeforeEach
    void setup() {
        // Mock del controller di autenticazione
        mockController = mock(AuthController.class);

        // Mock della sessione Vaadin
        mockSession = mock(VaadinSession.class);

        // Mock della UI corrente con la sessione associata
        mockUI = mock(UI.class);
        when(mockUI.getSession()).thenReturn(mockSession);
        UI.setCurrent(mockUI);
        signinView = new SigninView(mockController);
    }

    @Test
    void registrazioneConCampiValidi() {
        // Inserisce dati validi
        signinView.getRegisterUsername().setValue("nuovoUtente");
        signinView.getRegisterPassword().setValue("password123");

        // Click sul bottone di registrazione
        signinView.getSigninButton().click();

        // Verifica che venga impostato l'username nella sessione
        verify(mockUI.getSession()).setAttribute("username", "nuovoUtente");

        // Verifica che avvenga la navigazione a "home"
        verify(mockUI).navigate("home");
    }

    @Test
    void registrazioneConCampiVuoti() {
        // Lascia username e password vuoti
        signinView.getRegisterUsername().setValue("");
        signinView.getRegisterPassword().setValue("");

        signinView.getSigninButton().click();

        Span label = signinView.getMessageLabel();
        assertEquals("⚠️Completa tutti i campi", label.getText());
    }

    @Test
    void registrazioneUsernameGiaEsistente() {
        // Inserisce dati validi
        signinView.getRegisterUsername().setValue("utenteEsistente");
        signinView.getRegisterPassword().setValue("password");

        // Simula comportamento del controller che lancia IllegalArgumentException
        try {
            doThrow(new IllegalArgumentException("Username già esistente."))
                    .when(mockController).registrazione(any(Coreografo.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       signinView.getSigninButton().click();

        assertEquals("⚠️ Username già esistente.", signinView.getMessageLabel().getText());
    }

    @Test
    void registrazioneGeneraEccezione() {
        signinView.getRegisterUsername().setValue("utenteErrore");
        signinView.getRegisterPassword().setValue("password");

        // Simula crash del sistema
        try {
            doThrow(new RuntimeException("Crash"))
                    .when(mockController).registrazione(any(Coreografo.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        signinView.getSigninButton().click();

        // Nessuna eccezione deve essere lanciata, la notifica è visiva
        assertTrue(true); // passaggio implicito, la logica mostra solo Notification
    }

    @Test
    void clickSuTornaAlLogin() {
        signinView.getLoginButton().click();
        verify(mockUI).navigate("");
    }
}
