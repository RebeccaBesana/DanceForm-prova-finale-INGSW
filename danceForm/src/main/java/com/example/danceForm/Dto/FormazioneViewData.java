package com.example.danceForm.Dto;

import java.util.List;

/**
 * DTO che incapsula tutte le informazioni necessarie per visualizzare una formazione nella View:
 * - Nome della formazione
 * - Elenco dei ballerini con le loro coordinate (BallerinoPoint)
 * - Eventuali note
 * - Eventuale minutaggio
 * Viene generato dal controller e passato alla View già pronto per il rendering.
 */

public class FormazioneViewData {
    private final String nomeFormazione;
    private final List<BallerinoPoint> punti;
    private final String nota;       // può essere null
    private final String minutaggio; // può essere null

    public FormazioneViewData(String nomeFormazione, List<BallerinoPoint> punti, String nota, String minutaggio) {
        this.nomeFormazione = nomeFormazione;
        this.punti = punti;
        this.nota = nota;
        this.minutaggio = minutaggio;
    }
    public String getNomeFormazione() { return nomeFormazione; }
    public List<BallerinoPoint> getPunti() { return punti; }
    public String getNota() { return nota; }
    public String getMinutaggio() { return minutaggio; }
}
