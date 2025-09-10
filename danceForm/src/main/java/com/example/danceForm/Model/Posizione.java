package com.example.danceForm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Posizione {

    @JsonProperty("x")
    private int x;

    @JsonProperty("y")
    private int y;

    // Costruttore base
    public Posizione() {
        this.x =0;
        this.y =0;
    }

    // Costruttore
    public Posizione(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getter e Setter
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
