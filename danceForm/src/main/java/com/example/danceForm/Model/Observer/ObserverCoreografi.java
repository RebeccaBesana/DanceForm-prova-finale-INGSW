package com.example.danceForm.Model.Observer;

import com.example.danceForm.Model.Coreografo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConcreteObserverCoreografi.class, name = "observer")
})

public interface ObserverCoreografi {
    void aggiorna(Coreografia coreografia, String messaggio, Long autoreId);
    Coreografo getCoreografo();
}
