package com.example.danceForm.View;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

// Classe di test per ProtectedView usando una sottoclasse concreta di test
class ProtectedViewTest {

    private UI mockUI;
    private VaadinSession mockSession;
    private BeforeEnterEvent mockEvent;

    // Sottoclasse concreta per testare la classe astratta
    static class TestProtectedView extends ProtectedView {}

    private TestProtectedView testView;

    @BeforeEach
    void setup() {
        // Mock di UI e sessione
        mockUI = mock(UI.class);
        mockSession = mock(VaadinSession.class);
        when(mockUI.getSession()).thenReturn(mockSession);
        UI.setCurrent(mockUI);

        // Mock evento di routing
        mockEvent = mock(BeforeEnterEvent.class);

        // Inizializza la view
        testView = new TestProtectedView();
    }

    @Test
    void utenteNonAutenticato() {
        // Caso: username non presente
        when(mockSession.getAttribute("username")).thenReturn(null);

        testView.beforeEnter(mockEvent);

        // Verifica che avvenga il forward alla LoginView
        verify(mockEvent).forwardTo(LoginView.class);
    }

    @Test
    void utenteConUsernameVuoto() {
        // Caso: username presente ma vuoto
        when(mockSession.getAttribute("username")).thenReturn("");

        testView.beforeEnter(mockEvent);

        // Verifica che avvenga il forward alla LoginView
        verify(mockEvent).forwardTo(LoginView.class);
    }

    @Test
    void utenteAutenticato() {
        // Caso: username valido
        when(mockSession.getAttribute("username")).thenReturn("utenteValido");

        testView.beforeEnter(mockEvent);

        // Verifica che NON avvenga il forward
        verify(mockEvent, never()).forwardTo(LoginView.class);
    }
}
