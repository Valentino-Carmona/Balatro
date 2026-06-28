package com.balatro.modelo;

public class Joker implements JokerApply {
    private String nombre;
    private String descripcion;
    private JokerStrategy jokerStrategy;

    public Joker(String nombre, String descripcion , JokerStrategy jokerStrategy) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.jokerStrategy = jokerStrategy;
    }

    public void apply (Score score) {
        this.jokerStrategy.apply(score);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
