package com.balatro.dto;

public class PlayResponseDTO {
    private GameSessionDTO gameState;
    private ScoreResponseDTO handScore;
    private boolean roundWon;

    public GameSessionDTO getGameState() { return gameState; }
    public void setGameState(GameSessionDTO gameState) { this.gameState = gameState; }
    public ScoreResponseDTO getHandScore() { return handScore; }
    public void setHandScore(ScoreResponseDTO handScore) { this.handScore = handScore; }
    public boolean isRoundWon() { return roundWon; }
    public void setRoundWon(boolean roundWon) { this.roundWon = roundWon; }
}
