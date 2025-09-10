package com.example.danceForm.Dto;

/**
 * DTO semplice che rappresenta un ballerino
 * Serve per disaccoppiare la logica di posizionamento dal modello di dominio,
 * evitando che la View acceda direttamente a Posizione.
 */

public class BallerinoPoint {
    private final String nome;
    private final int x;
    private final int y;

    public BallerinoPoint(String nome, int x, int y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }
    public String getNome() { return nome; }
    public int getX() { return x; }
    public int getY() { return y; }
}

