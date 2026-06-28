package com.balatro.modelo;

public interface TarotApply {
    void apply(Score score);
    void use();
    String getName();
    String getDescription();
    Tarotable getTargetType();
}
