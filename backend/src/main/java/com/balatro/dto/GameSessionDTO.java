package com.balatro.dto;

public class GameSessionDTO {
    private String sessionId;
    private PlayerDTO player;
    private RoundDTO round;
    private int currentAnte;
    private int currentBlind;
    private boolean isGameOver;
    private StoreDTO store;

    // Getters and setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public PlayerDTO getPlayer() { return player; }
    public void setPlayer(PlayerDTO player) { this.player = player; }
    public RoundDTO getRound() { return round; }
    public void setRound(RoundDTO round) { this.round = round; }
    public int getCurrentAnte() { return currentAnte; }
    public void setCurrentAnte(int currentAnte) { this.currentAnte = currentAnte; }
    public int getCurrentBlind() { return currentBlind; }
    public void setCurrentBlind(int currentBlind) { this.currentBlind = currentBlind; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { this.isGameOver = gameOver; }
    public StoreDTO getStore() { return store; }
    public void setStore(StoreDTO store) { this.store = store; }
}
