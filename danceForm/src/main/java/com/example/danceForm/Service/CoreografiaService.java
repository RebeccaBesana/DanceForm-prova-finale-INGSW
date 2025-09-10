package com.example.danceForm.Service;

import com.example.danceForm.Model.*;
import com.example.danceForm.Model.Decorator.FormazioneBase;
import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Model.Observer.ObserverCoreografi;
import com.example.danceForm.Repository.CoreografiaRepository;
import com.example.danceForm.Repository.CoreografoRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CoreografiaService {

    private final CoreografiaRepository coreografiaRepository;
    private final CoreografoRepository coreografoRepository;

    public CoreografiaService(CoreografiaRepository coreografiaRepository,CoreografoRepository coreografoRepository) {
        this.coreografiaRepository = coreografiaRepository;
        this.coreografoRepository = coreografoRepository;
    }

    //coreografia in base ID coreografia
    public Coreografia getCoreografiaById(Long id) throws IOException {
        return coreografiaRepository.findById(id);
    }

    //salva
    public Coreografia saveCoreografia(Coreografia coreografia) throws IOException {
        return coreografiaRepository.save(coreografia);
    }

    //rinomina
    public void rinominaCoreografia(Long id, String nuovoNome, Long autoreId) throws IOException {
        Coreografia coreografia = coreografiaRepository.findById(id);
        coreografia.aggiornaNomeCoreografia(nuovoNome,autoreId);
        coreografiaRepository.save(coreografia);
    }

    // coreografie di cui sono osservatore
    public List<Coreografia> getCoreografieVisibili(Long coreografoId) throws IOException {
        return coreografiaRepository.findByCoreografoId(coreografoId);
    }

    //elimina
    public void deleteCoreografia(Long id) throws IOException {
        coreografiaRepository.deleteById(id);
    }

    // Metodi per gestire osservatori
    public void aggiungiOsservatore(Long coreografiaId, String username) throws IOException {
        Coreografia c = coreografiaRepository.findById(coreografiaId);
        Coreografo nuovoOsservatore = coreografoRepository.findByUsername(username);
        Coreografo mittente = coreografoRepository.findById(c.getProprietarioId()); // chi condivide

        if (nuovoOsservatore == null) {
            throw new IllegalArgumentException("Coreografo non trovato.");
        }
        // Evita doppia aggiunta
        for (ObserverCoreografi o : c.getOsservatori()) {
            if (o instanceof ConcreteObserverCoreografi concrete &&
                    concrete.getCoreografo().getId().equals(nuovoOsservatore.getId())) {
                return; // già osservatore
            }
        }

        // Aggiungi come osservatore
        ConcreteObserverCoreografi osservatore = new ConcreteObserverCoreografi(nuovoOsservatore);
        osservatore.aggiungiNotifica("La coreografia: " + c.getNome() +" di " + mittente.getUsername() + " è stata condivisa con te");
        c.aggiungiOsservatore(osservatore);

        // Aggiungi proprietario come osservatore se non già presente
        boolean giaOsservatore = c.getOsservatori().stream()
                .anyMatch(obs -> (obs).getCoreografo().getId().equals(mittente.getId()));
        if (!giaOsservatore) {
            c.aggiungiOsservatore(new ConcreteObserverCoreografi(mittente));
        }

        coreografiaRepository.save(c);
    }

    public void rimuoviOsservatore(Long coreografiaId, Long coreografoId) throws IOException {
        Coreografia c = coreografiaRepository.findById(coreografiaId);
        List<ObserverCoreografi> obs=c.getOsservatori();
        ObserverCoreografi daRimuovere = null;
        for (ObserverCoreografi o : obs){
            if(o.getCoreografo().getId().equals(coreografoId))
            {
                daRimuovere = o;
            }
        }
        if (daRimuovere != null) {
            c.rimuoviOsservatore(daRimuovere);
        }
        coreografiaRepository.save(c);
    }

    public void creaFormazioneVuota(Long coreografiaId, String nomeFormazione) throws IOException {
        Coreografia coreografia = coreografiaRepository.findById(coreografiaId);
        FormazioneBase nuova = new FormazioneBase(nomeFormazione, coreografia.getBallerini());
        coreografia.getFormazioni().add(nuova);
        coreografiaRepository.save(coreografia);
    }
}
