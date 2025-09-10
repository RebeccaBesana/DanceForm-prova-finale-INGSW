package com.example.danceForm.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.danceForm.Model.Coreografo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione dei Coreografi tramite file JSON.
 */
@Repository
public class CoreografoRepository {

    private final ObjectMapper objectMapper;
    // Percorso del file JSON usato per la persistenza
    private final String filePath;

    // Costruttore principale
    public CoreografoRepository() {
        this("src/main/resources/coreografi.json");
    }

    // Costruttore personalizzabile (per test)
    public CoreografoRepository(String filePath) {
        this.objectMapper = new ObjectMapper();
        this.filePath = filePath;
    }

    // Getter path usato nei metodi
    protected String getFilePath() {
        return filePath;
    }

    /** Restituisce tutti i coreografi */
    public List<Coreografo> findAll() throws IOException {
        File file = new File(getFilePath());
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Coreografo.class));
    }

    /** Cerca un coreografo per ID */
    public Coreografo findById(Long id) throws IOException {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /** Cerca un coreografo per username */
    public Coreografo findByUsername(String username) throws IOException {
        return findAll().stream()
                .filter(c -> c.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /** Salva un coreografo */
    public Coreografo save(Coreografo coreografo) throws IOException {
        List<Coreografo> coreografi = findAll();

        // Verifica duplicato username
        Optional<Coreografo> esistente = coreografi.stream()
                .filter(c -> c.getUsername().equals(coreografo.getUsername()))
                .findFirst();

        if (esistente.isPresent()) {
            throw new IllegalArgumentException("Username già esistente.");
        }

        // Genera ID progressivo
        long maxId = coreografi.stream()
                .mapToLong(Coreografo::getId)
                .max()
                .orElse(0L);
        coreografo.setId(maxId + 1);

        coreografi.add(coreografo);
        objectMapper.writeValue(new File(getFilePath()), coreografi);
        return coreografo;
    }
}
