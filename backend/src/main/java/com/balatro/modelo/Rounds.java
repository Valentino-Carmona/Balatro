package com.balatro.modelo;

import java.util.List;

public class Rounds {
    private final List<Round> rounds;
    private int index;
    private Round currentRound; // Ronda activa sincronizada por GameManager

    public Rounds(List<Round> rounds) {
        this.rounds = rounds;
        this.index = 0;
    }

    public Round getRound(int indexRound) {
        return this.rounds.get(indexRound);
    }

    /**
     * Sincroniza la ronda activa de la sesión. Debe llamarse desde GameManager
     * cada vez que se configura una nueva ronda, para que los comodines de tipo
     * "Descarte" accedan a los descartes reales del jugador.
     */
    public void setCurrentRound(Round round) {
        this.currentRound = round;
    }

    public void useJoker(DiscardJokerStrategy discardJokerStrategy) {
        // Usamos currentRound (ronda activa con discardsUsed real) en lugar
        // del Round original del JSON que siempre tiene discardsUsed = 0.
        Round target = (this.currentRound != null) ? this.currentRound : this.getRound(this.index);
        target.useJoker(discardJokerStrategy);
    }

    public void nextRound() {
        if (this.index < this.rounds.size() - 1) {
            this.index++;
        }
    }

    public int getSize() {
        return this.rounds.size();
    }
}
