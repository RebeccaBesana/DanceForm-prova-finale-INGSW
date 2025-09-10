package com.example.danceForm.Model.Observer;

import com.example.danceForm.Model.Ballerino;
import com.example.danceForm.Model.Decorator.Formazione;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

public class Coreografia implements Subject {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("numeroBallerini")
    private final int numeroBallerini;  // Numero ballerini immutable

    @JsonProperty("ballerini")
    private List<Ballerino> ballerini;

    @JsonProperty("formazioni")
    private List<Formazione> formazioni;

    @JsonProperty("proprietarioId")
    private Long proprietarioId;  // id del coreografo creatore

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ConcreteObserverCoreografi.class, name = "observer")
    })
    // Gli osservatori (i coreografi)
    private List<ObserverCoreografi> osservatori;

    public Coreografia() {
        this.numeroBallerini = 0;
        this.ballerini = new ArrayList<>();
        this.formazioni = new ArrayList<>();
        this.osservatori = new ArrayList<>();
    }

    // Costruttore completo
    public Coreografia(String nome, int numeroBallerini, List<Ballerino> ballerini, List<Formazione> formazioni, Long proprietarioId, List<ObserverCoreografi> osservatori) {
        this.nome = nome;
        this.numeroBallerini = numeroBallerini;
        this.ballerini = ballerini != null ? ballerini : new ArrayList<>();
        this.formazioni = formazioni != null ? formazioni : new ArrayList<>();
        this.proprietarioId = proprietarioId;
        this.osservatori = new ArrayList<>();
    }

    public Coreografia(long l, String testCoreo1) {
        this.id = l;
        this.nome = testCoreo1;
        this.numeroBallerini = 0;
    }

    // Metodi Observer pattern
    @Override
    public void aggiungiOsservatore(ObserverCoreografi osservatore) {
        osservatori.add(osservatore);
    }
    @Override
    public void rimuoviOsservatore(ObserverCoreografi osservatore) {
        osservatori.remove(osservatore);
    }
    @Override
    public void notificaOsservatori(String messaggio, Long autoreId) {
        for (ObserverCoreografi osservatore : osservatori) {
            osservatore.aggiorna(this, messaggio, autoreId);
        }
    }

    // Metodo per aggiornare il nome della coreografia e notificare
    public void aggiornaNomeCoreografia(String nome, Long autoreId) {
      String vecchioNome=this.nome;
      this.nome = nome;
      notificaOsservatori("Il nome della coreografia "+vecchioNome+" è stato cambiato in: " + nome, autoreId);
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Ballerino> getBallerini() {
        return ballerini;
    }

    public void setBallerini(List<Ballerino> ballerini) { this.ballerini = ballerini; }

    public List<Formazione> getFormazioni() {
        return formazioni;
    }

    public void setFormazioni(List<Formazione> formazioni) {this.formazioni = formazioni;}

    public List<ObserverCoreografi> getOsservatori() {
        return osservatori;
    }

    public Long getProprietarioId() {
        return proprietarioId;
    }

}
