package com.example.danceForm.Service;

import com.example.danceForm.Model.*;
import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Repository.CoreografiaRepository;
import com.example.danceForm.Repository.CoreografoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreografiaServiceTest {

    private CoreografiaRepository coreografiaRepo;
    private CoreografoRepository coreografoRepo;
    private CoreografiaService service;

    @BeforeEach
    void setUp() {
        coreografiaRepo = mock(CoreografiaRepository.class); // Simula comportamento repository
        coreografoRepo = mock(CoreografoRepository.class); // Simula comportamento repository
        service = new CoreografiaService(coreografiaRepo, coreografoRepo);
    }

    @Test
    void testGetCoreografiaById() throws IOException {
        Coreografia coreografia = new Coreografia();
        when(coreografiaRepo.findById(1L)).thenReturn(coreografia);

        Coreografia result = service.getCoreografiaById(1L);
        assertEquals(coreografia, result);
    }

    @Test
    void testSaveCoreografia() throws IOException {
        Coreografia c = new Coreografia();
        when(coreografiaRepo.save(c)).thenReturn(c);

        Coreografia result = service.saveCoreografia(c);
        assertEquals(c, result);
    }

    @Test
    void testRinominaCoreografia() throws IOException {
        Coreografia c = mock(Coreografia.class);
        when(coreografiaRepo.findById(1L)).thenReturn(c);

        service.rinominaCoreografia(1L, "NuovoNome", 5L);
        verify(c).aggiornaNomeCoreografia("NuovoNome", 5L);
        verify(coreografiaRepo).save(c);
    }

    @Test
    void testGetCoreografieVisibili() throws IOException {
        List<Coreografia> lista = List.of(new Coreografia(), new Coreografia());
        when(coreografiaRepo.findByCoreografoId(3L)).thenReturn(lista);

        List<Coreografia> result = service.getCoreografieVisibili(3L);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteCoreografia() throws IOException {
        service.deleteCoreografia(7L);
        verify(coreografiaRepo).deleteById(7L);
    }

    @Test
    void testAggiungiOsservatoreNuovo() throws IOException {
        Coreografo nuovo = new Coreografo(2L, "luca", "pw");
        Coreografo mittente = new Coreografo(1L, "mario", "pw");

        Coreografia coreografia = new Coreografia();
        coreografia.setId(10L);
        coreografia.setId(1L);
        coreografia.getOsservatori().clear(); // inizialmente vuoto
        coreografia.setId(10L);

        when(coreografiaRepo.findById(10L)).thenReturn(coreografia);
        when(coreografoRepo.findByUsername("luca")).thenReturn(nuovo);
        when(coreografoRepo.findById(null)).thenReturn(mittente); // proprietarioId = null finora

        // Simula proprietario
        coreografia.setId(10L);
        when(coreografoRepo.findById(any())).thenReturn(mittente);

        coreografia.setId(10L); // forza ID

        service.aggiungiOsservatore(10L, "luca");

        verify(coreografiaRepo).save(coreografia);
        assertEquals(2, coreografia.getOsservatori().size());
    }

    @Test
    void testAggiungiOsservatoreGiaPresente() throws IOException {
        Coreografo osservatore = new Coreografo(3L, "giulia", "pw");
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi(osservatore);

        Coreografia coreografia = new Coreografia();
        coreografia.aggiungiOsservatore(observer);
        coreografia.setId(8L);

        when(coreografiaRepo.findById(8L)).thenReturn(coreografia);
        when(coreografoRepo.findByUsername("giulia")).thenReturn(osservatore);

        service.aggiungiOsservatore(8L, "giulia");

        verify(coreografiaRepo, never()).save(any()); // già presente → non salva
    }

    @Test
    void testAggiungiOsservatoreCoreografoNonTrovato() throws IOException {
        when(coreografiaRepo.findById(12L)).thenReturn(new Coreografia());
        when(coreografoRepo.findByUsername("xxx")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                service.aggiungiOsservatore(12L, "xxx"));
    }

    @Test
    void testRimuoviOsservatore() throws IOException {
        Coreografo c1 = new Coreografo(99L, "utente", "pw");
        ConcreteObserverCoreografi o = new ConcreteObserverCoreografi(c1);

        Coreografia coreografia = new Coreografia();
        coreografia.aggiungiOsservatore(o);

        when(coreografiaRepo.findById(5L)).thenReturn(coreografia);

        service.rimuoviOsservatore(5L, 99L);

        assertTrue(coreografia.getOsservatori().isEmpty());
        verify(coreografiaRepo).save(coreografia);
    }

    @Test
    void testCreaFormazioneVuota() throws IOException {
        Coreografia c = new Coreografia();
        c.getBallerini().add(new Ballerino("Mario", new Posizione(1, 2)));

        when(coreografiaRepo.findById(15L)).thenReturn(c);

        service.creaFormazioneVuota(15L, "Intro");

        assertEquals(1, c.getFormazioni().size());
        assertEquals("Intro", c.getFormazioni().get(0).getNome());
        verify(coreografiaRepo).save(c);
    }
}
