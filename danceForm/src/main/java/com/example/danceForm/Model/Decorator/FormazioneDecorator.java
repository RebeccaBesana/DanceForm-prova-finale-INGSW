package com.example.danceForm.Model.Decorator;


import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FormazioneDecorator implements Formazione {

    @JsonProperty("component")
    protected Formazione componente;

    public FormazioneDecorator() {}
    public FormazioneDecorator(Formazione componente) {
        this.componente = componente;
    }

    public Formazione getComponente() {
        return componente;
    }

    @Override
    public Long getId() {
        return componente.getId();
    }
    @Override
    public void setId(long id) {
        this.componente.setId(id);
    }
    @Override
    public String getNome() {
        return componente.getNome();
    }
    @Override
    public void setNome(String nome) {
        componente.setNome(nome);
    }
    @Override
    public void visualizza() {
        componente.visualizza();
    }
}
