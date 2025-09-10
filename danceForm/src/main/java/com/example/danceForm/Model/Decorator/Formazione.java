package com.example.danceForm.Model.Decorator;

import com.example.danceForm.Model.Posizione;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

/**
 * Interfaccia componente per pattern Decorator
 * Abilita serializzazione polimorfica JSON con Jackson
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FormazioneBase.class, name = "base"),
        @JsonSubTypes.Type(value = NotaDecorator.class, name = "nota"),
        @JsonSubTypes.Type(value = MinutaggioDecorator.class, name = "minutaggio")
})

public interface Formazione {
    Long getId();
    void setId(long id);
    String getNome();
    void setNome(String nome);
    void visualizza();  // Metodo concettualmente utile per esempio applicazione pattern
}

