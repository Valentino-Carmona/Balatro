package com.balatro.modelo;

public class MultiplyMultStrategy implements ScoringStrategy {
    private final float mult;

    public MultiplyMultStrategy(float mult){
        this.mult = mult;
    }

    public void apply(Score score){
        score.multiplyMult(this.mult);
    }
}
