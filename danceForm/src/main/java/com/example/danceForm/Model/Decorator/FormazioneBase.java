package com.example.danceForm.Model.Decorator;

import java.util.List;

import com.example.danceForm.Model.Ballerino;
import com.example.danceForm.Model.Posizione;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class FormazioneBase implements Formazione {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    // Mappa che associa l'id del ballerino alla sua posizione specifica per questa formazione
    @JsonProperty("posizioniBallerini")
    private Map<Long, Posizione> posizioniBallerini = new HashMap<>();

    // Costruttori, getter, setter
    public FormazioneBase() {}
    public FormazioneBase(String nome, List<Ballerino> ballerini) {
        this.id = com.example.danceForm.Util.IdManager.nextFormazioneId(); //Assegna ID progressivo
        this.nome = nome;
        this.posizioniBallerini = new HashMap<>();
    }

    @Override
    public Long getId() {
        return id;
    }
    @Override
    public void setId (long id) {
        this.id = id;
    }
    @Override
    public String getNome() {
        return nome;
    }
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<Long, Posizione> getPosizioniBallerini() {
        return posizioniBallerini;
    }
    public void assegnaPosizione(Long ballerinoId, Posizione posizione) {
           posizioniBallerini.put(ballerinoId, posizione);
    }

    @Override
    public void visualizza() {
        System.out.println("Visualizzazione della formazione: " + nome);
    }
}
