package com.example.danceForm.Model.Decorator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NotaDecorator extends FormazioneDecorator {

    @JsonProperty("nota")
    private String nota;

    public NotaDecorator() {}

    @JsonCreator
    public NotaDecorator(
            @JsonProperty("component") Formazione componente,
            @JsonProperty("nota") String nota
    ) {
        super(componente);
        this.nota = nota;
    }

    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }
    @Override
    public void visualizza() {
        componente.visualizza();
        System.out.println("Nota: " + nota);
    }
}
