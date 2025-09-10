package com.example.danceForm.Model.Decorator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MinutaggioDecorator extends FormazioneDecorator {

    @JsonProperty("minutaggio")
    private String minutaggio;

    public MinutaggioDecorator() {}
    @JsonCreator
    public MinutaggioDecorator(
            @JsonProperty("component") Formazione componente,
            @JsonProperty("minutaggio") String minutaggio
    ){
        super(componente);
        this.minutaggio = minutaggio;
    }

    public String getMinutaggio() {
        return minutaggio;
    }
    public void setMinutaggio(String minutaggio) {
        this.minutaggio = minutaggio;
    }
    @Override
    public void visualizza() {
        componente.visualizza();
        System.out.println("Minutaggio: " + minutaggio);
    }
}
