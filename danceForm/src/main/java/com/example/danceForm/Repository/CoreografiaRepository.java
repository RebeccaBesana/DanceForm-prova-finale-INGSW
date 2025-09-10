package com.example.danceForm.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.danceForm.Model.Observer.Coreografia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;


/**
 * Repository per la gestione della persistenza delle Coreografie tramite file JSON.
 */

@Repository
public class CoreografiaRepository {

    // Mapper di Jackson per serializzare/deserializzare JSON
    private final ObjectMapper objectMapper;

    // Percorso del file JSON usato per la persistenza
    private final String filePath;

    // Costruttore principale
    public CoreografiaRepository() {
        this("src/main/resources/coreografie.json");
    }

    // Costruttore personalizzabile (per test)
    public CoreografiaRepository(String filePath) {
        this.objectMapper = new ObjectMapper();
        this.filePath = filePath;
    }

    // Getter path usato nei metodi
    protected String getFilePath() {
        return filePath;
    }


    /**
     * Recupera tutte le coreografie salvate nel file JSON.
     * Se il file non esiste o è vuoto, ritorna una lista vuota.
     */
    public List<Coreografia> findAll() throws IOException {
        File file = new File(getFilePath());

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();  // File vuoto o inesistente => lista vuota
        }
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Coreografia.class));
    }

    /**
     * Cerca tutte le coreografie in cui coreografoID appartiene alla lista osservatori.
     */
    public List<Coreografia> findByCoreografoId(Long coreografoId) throws IOException {
        return findAll().stream()
                .filter(c -> c.getOsservatori().stream().anyMatch(o -> o.getCoreografo().getId().equals(coreografoId))
                ).toList();
    }

    /**
     * Cerca una coreografia nel file JSON in base all'ID.
     */
    public Coreografia findById(Long id) throws IOException {
        return findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Salva una coreografia nel file JSON.
     * Se la coreografia con lo stesso ID esiste già, viene aggiornata.
     */
    public Coreografia save(Coreografia coreografia) throws IOException {
        List<Coreografia> coreografie = findAll();

        // Cerca se la coreografia esiste già (per update)
        boolean updated = false;
        for (int i = 0; i < coreografie.size(); i++) {
            if (coreografie.get(i).getId().equals(coreografia.getId())) {
                coreografie.set(i, coreografia);  // Aggiorna
                updated = true;
                break;
            }
        }

        if (coreografia.getId() == null) {
            // Nuova coreografia: assegna ID progressivo
            long maxId = coreografie.stream()
                    .mapToLong(c -> c.getId() != null ? c.getId() : 0L)
                    .max()
                    .orElse(0L);
            coreografia.setId(maxId + 1);
        }

        if (!updated) {
            coreografie.add(coreografia);  // Nuova coreografia
        }

        // Scrive la lista aggiornata nel file JSON
        objectMapper.writeValue(new File(getFilePath()), coreografie);
        return coreografia;
    }

    /**
     * Elimina una coreografia dal file JSON tramite ID.
     */
    public void deleteById(Long id) throws IOException {
        List<Coreografia> coreografie = findAll();
        coreografie.removeIf(c -> c.getId().equals(id));

        // Riscrive il file JSON senza la coreografia eliminata
        objectMapper.writeValue(new File(getFilePath()), coreografie);
    }
}
