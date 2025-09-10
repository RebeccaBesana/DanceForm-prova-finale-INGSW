package com.example.danceForm.Controller;

import com.example.danceForm.Service.CoreografoService;
import com.example.danceForm.Model.Coreografo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private CoreografoService service;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        service = mock(CoreografoService.class); // Simula comportamento service
        controller = new AuthController(service);
    }

    @Test
    void testRegistrazione() throws IOException {
        Coreografo c = new Coreografo("alice", "pw");
        controller.registrazione(c);
        verify(service).saveCoreografo(c);
    }

    @Test
    void testLoginSuccess() throws IOException {
        Coreografo c1 = new Coreografo(1L, "luca", "1234");
        Coreografo c2 = new Coreografo(2L, "maria", "abcd");

        when(service.getAllCoreografi()).thenReturn(List.of(c1, c2));

        Coreografo result = controller.login("maria", "abcd");
        assertNotNull(result);
        assertEquals("maria", result.getUsername());
    }

    @Test
    void testLoginFailure() throws IOException {
        Coreografo c1 = new Coreografo(1L, "luca", "1234");

        when(service.getAllCoreografi()).thenReturn(List.of(c1));

        Coreografo result = controller.login("maria", "abcd");
        assertNull(result);
    }
}
