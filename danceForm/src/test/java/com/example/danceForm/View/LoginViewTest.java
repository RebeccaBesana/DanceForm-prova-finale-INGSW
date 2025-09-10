package com.example.danceForm.View;

import com.example.danceForm.Controller.AuthController;
import com.example.danceForm.Model.Coreografo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginViewTest {

    private AuthController mockController;
    private LoginView loginView;
    private UI mockUI;

    @BeforeEach
    void setup() {
        // Mock del controller di autenticazione
        mockController = mock(AuthController.class);

        // Mock della sessione Vaadin
        VaadinSession mockSession = mock(VaadinSession.class);

        // Mock della UI corrente con la sessione associata
        mockUI = mock(UI.class);
        when(mockUI.getSession()).thenReturn(mockSession);
        UI.setCurrent(mockUI); // mockUi come istanza corrente
        loginView = new LoginView(mockController);
    }

    @Test
    void loginConCredenzialiCorrette() {
        // Simula login con credenziali corrette
        Coreografo finto = new Coreografo(42L, "utente", "pw");
        try {
            when(mockController.login("utente", "pw")).thenReturn(finto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Inserisce credenziali e clicca login
        loginView.getLoginUsername().setValue("utente");
        loginView.getLoginPassword().setValue("pw");
        loginView.getLoginButton().click();

        // Verifica che siano state salvate le informazioni in sessione
        verify(UI.getCurrent().getSession()).setAttribute("username", "utente");
        verify(UI.getCurrent().getSession()).setAttribute("coreografoId", 42L);
    }

    @Test
    void loginConCredenzialiErrate() {
        // Simula login fallito (utente non trovato)
        try {
            when(mockController.login("utente", "sbagliata")).thenReturn(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Inserisce credenziali errate
        loginView.getLoginUsername().setValue("utente");
        loginView.getLoginPassword().setValue("sbagliata");
        loginView.getLoginButton().click();

        // Verifica che venga mostrato il messaggio di errore
        Span label = loginView.getMessageLabel();
        assertEquals("⚠️ Username o password errati.", label.getText());
    }

    @Test
    void loginLanciaEccezione() {
        // Simula crash del sistema durante il login
        try {
            when(mockController.login("crash", "boom")).thenThrow(new RuntimeException("Errore tecnico"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Inserisce dati che causano eccezione
        loginView.getLoginUsername().setValue("crash");
        loginView.getLoginPassword().setValue("boom");
        loginView.getLoginButton().click();

        // Verifica che venga mostrato il messaggio di errore tecnico
        Span label = loginView.getMessageLabel();
        assertTrue(label.getText().contains("❌ Errore durante il login"));
    }

    @Test
    void clickSuCreaAccount() {
        loginView.getRegisterButton().click();
        verify(mockUI).navigate("signin");
    }
}
