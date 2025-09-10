package com.example.danceForm.View;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class LogoutViewTest {

    private UI mockUI;
    private VaadinSession mockSession;
    private LogoutView logoutView;

    @BeforeEach
    void setup() {
        // Crea e imposta UI mock
        mockUI = mock(UI.class);
        mockSession = mock(VaadinSession.class);

        when(mockUI.getSession()).thenReturn(mockSession);
        UI.setCurrent(mockUI);

        logoutView = new LogoutView();
    }

    @Test
    void clickSuTornaAlLogin_chiudeSessioneENavigaLogin() {
        // clicca il bottone "Torna al login"
        logoutView.getTornaAlLogin().click();

        // Verifica che venga chiusa la sessione
        verify(mockSession).close();

        // Verifica che avvenga la navigazione alla root
        verify(mockUI).navigate("");
    }
}
