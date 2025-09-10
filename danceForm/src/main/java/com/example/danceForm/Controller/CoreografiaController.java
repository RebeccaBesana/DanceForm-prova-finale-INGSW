package com.example.danceForm.Controller;

import com.example.danceForm.Model.Decorator.*;
import com.example.danceForm.Model.Observer.ConcreteObserverCoreografi;
import com.example.danceForm.Model.Observer.Coreografia;
import com.example.danceForm.Model.Observer.ObserverCoreografi;
import com.example.danceForm.Service.CoreografoService;
import com.example.danceForm.Model.*;
import com.example.danceForm.Service.CoreografiaService;
import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.danceForm.Dto.BallerinoPoint;
import com.example.danceForm.Dto.FormazioneViewData;

/**
 * Controller per la gestione delle coreografie.
 * Questo controller verrà usato dalle viste Vaadin per coordinare l'interfaccia utente.
 */

@Component
public class CoreografiaController {

    private final CoreografiaService coreografiaService;
    private final CoreografoService coreografoService;

    public CoreografiaController(CoreografiaService coreografiaService, CoreografoService coreografoService) {
        this.coreografiaService = coreografiaService;
        this.coreografoService= coreografoService;
    }

    public Coreografia salvaCoreografia(Coreografia coreografia) throws IOException {
        return coreografiaService.saveCoreografia(coreografia);
    }

    //salva coreografia per Crea nuova coreografia
    public Coreografia creaCoreografia(String nome, Integer numeroBallerini, List<String> nomi, Long coreografoId) throws IOException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Inserisci un nome per la coreografia.");
        }

        if (numeroBallerini == null || numeroBallerini <= 0) {
            throw new IllegalArgumentException("Numero ballerini non valido.");
        }

        if (nomi == null || nomi.size() != numeroBallerini || nomi.stream().anyMatch(n -> n == null || n.isBlank())) {
            throw new IllegalArgumentException("Inserisci tutti i nomi dei ballerini.");
        }

        List<Ballerino> ballerini = nomi.stream()
                .map(nomeBallerino -> new Ballerino(nomeBallerino, new Posizione(0, 0)))
                .toList();

        // Rendi il proprietario un osservatore di default
        Coreografo proprietario = coreografoService.getCoreografoById(coreografoId);
        ConcreteObserverCoreografi osservatoreProprietario = new ConcreteObserverCoreografi(proprietario);
        Coreografia coreografia = new Coreografia(nome, numeroBallerini, ballerini, new ArrayList<>(), coreografoId, new ArrayList<>());
        coreografia.aggiungiOsservatore(osservatoreProprietario);

        return coreografia;
    }

    public String eliminaCoreografia(Coreografia coreografia, Long coreografoId) throws IOException {
        // Controllo se l'utente è l'unico osservatore
        if (coreografia.getOsservatori().size() == 1) {
            // Sono l'unico osservatore → Elimino la coreografia
            coreografiaService.deleteCoreografia(coreografia.getId());
            return "Coreografia eliminata completamente";
        } else {
            // Ci sono altri osservatori → Rimuovo solo me stesso
            rimuoviOsservatore(coreografia.getId(), coreografoId);
            return "Rimosso dalla coreografia condivisa";
        }
    }

    public Coreografia getCoreografiaById(Long id) throws IOException {
        return coreografiaService.getCoreografiaById(id);
    }


    public List<Coreografia> getCoreografiePerUtente(Long coreografoId) throws IOException {
        return coreografiaService.getCoreografieVisibili(coreografoId);
    }

    //Rinomina una coreografia e notifica gli osservatori.
    public void rinominaCoreografia(Long idCoreografia, String nuovoNome) throws IOException {
        Long autoreId = (Long) UI.getCurrent().getSession().getAttribute("coreografoId");
        coreografiaService.rinominaCoreografia(idCoreografia, nuovoNome, autoreId);
    }

    public void aggiungiOsservatore(Long idCoreografia, String username) throws IOException {
        coreografiaService.aggiungiOsservatore(idCoreografia, username);
    }

    public void rimuoviOsservatore(Long coreografiaId, Long coreografoId) throws IOException {
        coreografiaService.rimuoviOsservatore(coreografiaId, coreografoId);
    }

    //metodo che passa le notifiche da mostrare ad homeView
    public List<Notifica> recuperaNotifiche(Long coreografoId) throws IOException {
        List<Coreografia> coreografie = getCoreografiePerUtente(coreografoId);
        List<Notifica> notificheDaMostrare = new ArrayList<>();

        for (Coreografia c : coreografie) {
            for (ObserverCoreografi obs : c.getOsservatori()) {
                if (obs instanceof ConcreteObserverCoreografi observer &&
                        observer.getCoreografo().getId().equals(coreografoId)) {

                    List<Notifica> nonLette = observer.getNotificheNonLette();
                    notificheDaMostrare.addAll(nonLette);

                    observer.segnaTutteComeLette();
                    salvaCoreografia(c);
                }
            }
        }

        return notificheDaMostrare;
    }

    // passaggio tra view e accessManager
    public boolean isBeingEdited(Long coreografiaId) {
        return com.example.danceForm.Util.CoreografiaAccessManager.getInstance().isBeingEdited(coreografiaId);
    }
    public boolean tryAcquireEditLock(Long coreografiaId, String sessionId) {
        return com.example.danceForm.Util.CoreografiaAccessManager.getInstance().tryAcquire(coreografiaId, sessionId);
    }
    public void releaseEditLock(Long coreografiaId, String sessionId) {
        com.example.danceForm.Util.CoreografiaAccessManager.getInstance().release(coreografiaId, sessionId);
    }

    // recupera una formazione di una coreografia e la trasforma in un DTO
    // contenente i dati pronti per la visualizzazione nella View.

    public FormazioneViewData getFormazioneViewData(Long coreografiaId, int indice) throws IOException {
        Coreografia c = getCoreografiaById(coreografiaId);
        validaIndiceFormazione(c, indice);

        Formazione formazioneDecorata = c.getFormazioni().get(indice);

        // Estrai base + raccogli nota/minutaggio dai decorator
        String nota = null;
        String minutaggio = null;
        Formazione f = formazioneDecorata;
        while (f instanceof FormazioneDecorator decorator) {
            if (decorator instanceof NotaDecorator decorNote) {
                nota = decorNote.getNota();
            }
            if (decorator instanceof MinutaggioDecorator decorMin) {
                minutaggio = decorMin.getMinutaggio();
            }
            f = decorator.getComponente();
        }
        FormazioneBase base = (FormazioneBase) f;

        // Prepara i punti (nome + x,y) dei ballerini della coreografia
        Map<Long, Posizione> posizioni = base.getPosizioniBallerini();
        List<BallerinoPoint> punti = new java.util.ArrayList<>();
        for (Ballerino b : c.getBallerini()) {
            Posizione p = posizioni.get(b.getId());
            if (p != null) {
                punti.add(new BallerinoPoint(b.getNome(), p.getX(), p.getY()));
            }
        }

        return new FormazioneViewData(formazioneDecorata.getNome(), punti, nota, minutaggio);
    }

    // Manipolazioni della formazione
    public void assignPosizione(Long coreografiaId, int indiceFormazione, Long ballerinoId, int x, int y) throws IOException {
        Coreografia c = getCoreografiaById(coreografiaId);
        validaIndiceFormazione(c, indiceFormazione);
        Formazione f = c.getFormazioni().get(indiceFormazione);
        while (f instanceof FormazioneDecorator d) { f = d.getComponente(); }
        FormazioneBase base = (FormazioneBase) f;
        base.assegnaPosizione(ballerinoId, new Posizione(x, y));
        salvaCoreografia(c);
    }

    public void removeFormazione(Long coreografiaId, int indiceFormazione) throws IOException {
        Coreografia c = getCoreografiaById(coreografiaId);
        validaIndiceFormazione(c, indiceFormazione);
        c.getFormazioni().remove(indiceFormazione);
        salvaCoreografia(c);
    }

    public void creaFormazioneVuota(Long coreografiaId, String nomeFormazione) throws IOException {
        coreografiaService.creaFormazioneVuota(coreografiaId, nomeFormazione);
    }

    //salva nota, se già presente la modifica, altrimenti decora formazione con nota
    public void upsertNota(Long coreografiaId, int indiceFormazione, String nota) throws IOException {
        Coreografia c = getCoreografiaById(coreografiaId);
        validaIndiceFormazione(c, indiceFormazione);
        Formazione f = c.getFormazioni().get(indiceFormazione);

        // Cerca NotaDecorator
        Formazione cursor = f;
        while (cursor instanceof FormazioneDecorator d) {
            if (d instanceof NotaDecorator nd) {
                nd.setNota(nota);
                salvaCoreografia(c);
                return;
            }
            cursor = ((FormazioneDecorator) cursor).getComponente();
        }
        // Non esiste → crea wrapper
        c.getFormazioni().set(indiceFormazione, new NotaDecorator(f, nota));
        salvaCoreografia(c);
    }

    //salva minutaggio, se già presente lo modifica, altrimenti decora formazione con minutaggio
    public void upsertMinutaggio(Long coreografiaId, int indiceFormazione, String minutaggio) throws IOException {
        Coreografia c = getCoreografiaById(coreografiaId);
        validaIndiceFormazione(c, indiceFormazione);
        Formazione f = c.getFormazioni().get(indiceFormazione);

        // Cerca MinutaggioDecorator
        Formazione cursor = f;
        while (cursor instanceof FormazioneDecorator d) {
            if (d instanceof MinutaggioDecorator md) {
                md.setMinutaggio(minutaggio);
                salvaCoreografia(c);
                return;
            }
            cursor = ((FormazioneDecorator) cursor).getComponente();
        }
        // Non esiste → crea wrapper
        c.getFormazioni().set(indiceFormazione, new MinutaggioDecorator(f, minutaggio));
        salvaCoreografia(c);
    }

    // Salva + notifica osservatori per ModificaCoreografiaView
    public void saveAndNotifyModified(Coreografia coreografia, Long autoreId) throws IOException {
        coreografia.notificaOsservatori("La coreografia " + coreografia.getNome() + " è stata modificata", autoreId);
        salvaCoreografia(coreografia);
    }

    // Utilità interna
    private void validaIndiceFormazione(Coreografia c, int indice) {
        if (c == null) throw new IllegalArgumentException("Coreografia non trovata");
        if (c.getFormazioni() == null || c.getFormazioni().isEmpty()) throw new IllegalArgumentException("Nessuna formazione presente");
        if (indice < 0 || indice >= c.getFormazioni().size()) throw new IllegalArgumentException("Indice formazione non valido");
    }
}
