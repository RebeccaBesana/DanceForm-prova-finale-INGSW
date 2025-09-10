package com.example.danceForm.Repository;

import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Model.Coreografo;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoreografiaRepositoryTest {

    private CoreografiaRepository repository;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        //genera un nuovo file temporaneo
        testFile = File.createTempFile("coreografie", ".json");
        //cancellare automaticamente il file temporaneo
        testFile.deleteOnExit();
        //nuova versione CoreografiaRepository che sovrascrive metodo getFilePath utilizzando file temporaneo
        repository = new CoreografiaRepository() {
            protected String getFilePath() {
                return testFile.getAbsolutePath();
            }
        };
    }

    @Test
    void testFindByCoreografoId() throws IOException {
        Coreografo coreografo = new Coreografo(5L, "test", "123");
        ConcreteObserverCoreografi observer = new ConcreteObserverCoreografi(coreografo);

        Coreografia coreo = new Coreografia();
        coreo.setId(10L);
        coreo.setNome("Test");
        coreo.aggiungiOsservatore(observer);

        repository.save(coreo);

        List<Coreografia> result = repository.findByCoreografoId(5L);
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getNome());
    }

    @Test
    void testFindById() throws IOException {
        Coreografia c = new Coreografia();
        c.setId(1L);
        repository.save(c);

        Coreografia result = repository.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateCoreografiaDoesNotDuplicate() throws IOException {
            Coreografia coreo = new Coreografia();
        coreo.setId(1L);
        coreo.setNome("Versione1");
        repository.save(coreo);

        coreo.setNome("Versione2");
        Coreografia updated = repository.save(coreo);

        List<Coreografia> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Versione2", all.get(0).getNome());
    }


    @Test
    void testSaveNewCoreografia() throws IOException {
        Coreografia c1 = new Coreografia();
        c1.setId(10L);
        repository.save(c1);

        Coreografia c2 = new Coreografia(); // senza ID
        repository.save(c2);

        assertNotNull(c2.getId());
        assertTrue(c2.getId() > 10L);
    }

    @Test
    void testDeleteById() throws IOException {
        Coreografia c = new Coreografia();
        c.setId(1L);
        repository.save(c);

        repository.deleteById(1L);
        assertNull(repository.findById(1L));
    }

}