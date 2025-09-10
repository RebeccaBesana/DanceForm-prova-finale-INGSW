package com.example.danceForm.Repository;

import com.example.danceForm.Model.Coreografo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoreografoRepositoryTest {

    private CoreografoRepository repository;
    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        //genera un nuovo file temporaneo
        testFile = File.createTempFile("coreografi", ".json");
        //cancellare automaticamente il file temporaneo
        testFile.deleteOnExit();
        //nuova versione CoreografoRepository che sovrascrive metodo getFilePath utilizzando file temporaneo
        repository = new CoreografoRepository() {
            protected String getFilePath() {
                return testFile.getAbsolutePath();
            }
        };

    }

    @Test
    void testSaveAndFindById() throws IOException {
        Coreografo c = new Coreografo("user", "pass");
        c.setId(1L);
        repository.save(c);

        Coreografo result = repository.findById(1L);
        assertNotNull(result);
        assertEquals("user", result.getUsername());
    }

    @Test
    void testFindByUsername() throws IOException {
        Coreografo c = new Coreografo("mario", "pwd");
        c.setId(2L);
        repository.save(c);

        Coreografo result = repository.findByUsername("mario");
        assertNotNull(result);
        assertEquals("pwd", result.getPassword());
    }

    @Test
    void testDuplicateUsernameThrows() throws IOException {
        Coreografo c1 = new Coreografo("dupe", "123");
        c1.setId(1L);
        repository.save(c1);

        Coreografo c2 = new Coreografo("dupe", "456");

        assertThrows(IllegalArgumentException.class, () -> repository.save(c2));
    }
}