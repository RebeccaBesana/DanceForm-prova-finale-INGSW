package com.example.danceForm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Ballerino {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;


    @JsonProperty("posizione")
    private Posizione posizione;


    // Costruttore
    public Ballerino(String nome, Posizione posizione) {
        this.id = com.example.danceForm.Util.IdManager.nextBallerinoId(); // Assegna ID progressivo
        this.nome = nome;
        this.posizione = posizione;
    }

    // Costruttore default
    public Ballerino() {}

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Posizione getPosizione() {
        return posizione;
    }
}
