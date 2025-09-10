package com.example.danceForm.Service;

import com.example.danceForm.Model.Coreografo;
import com.example.danceForm.Repository.CoreografoRepository;
import com.example.danceForm.Service.CoreografoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreografoServiceTest {

    private CoreografoRepository repository;
    private CoreografoService service;

    @BeforeEach
    void setUp() {
        repository = mock(CoreografoRepository.class); // Simula comportamento repository
        service = new CoreografoService(repository);
    }

    @Test
    void testGetAllCoreografi() throws IOException {
        List<Coreografo> lista = List.of(new Coreografo(), new Coreografo());
        when(repository.findAll()).thenReturn(lista);

        List<Coreografo> result = service.getAllCoreografi();
        assertEquals(2, result.size());
    }

    @Test
    void testGetCoreografoById() throws IOException {
        Coreografo c = new Coreografo();
        when(repository.findById(1L)).thenReturn(c);

        Coreografo result = service.getCoreografoById(1L);
        assertEquals(c, result);
    }

    @Test
    void testSaveCoreografo() throws IOException {
        Coreografo c = new Coreografo("user", "pw");
        when(repository.save(c)).thenReturn(c);

        Coreografo result = service.saveCoreografo(c);
        assertEquals(c, result);
    }
}
