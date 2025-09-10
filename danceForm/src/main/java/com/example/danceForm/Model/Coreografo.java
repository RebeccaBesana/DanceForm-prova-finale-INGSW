package com.example.danceForm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coreografo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    // Necessario per serializzazione/deserializzazione JSON e Vaadin
    public Coreografo() {}

    // Costruttore
    public Coreografo(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Costruttore senza id (lo genera il repository)
    public Coreografo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
}
