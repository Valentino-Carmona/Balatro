package com.balatro.modelo;

import java.util.List;

public class GameSession {
    private final String id;
    private final Player player;
    private final Deck_I deck;
    private Store store;
    private Round round;
    private int currentAnte;
    private int currentBlind; // 1 = Small, 2 = Big, 3 = Boss
    private boolean isGameOver;
    private Rounds allRounds;
    private List<Store> allStores;
    private int roundIndex;

    public GameSession(String id, Player player, Deck_I deck) {
        this.id = id;
        this.player = player;
        this.deck = deck;
        this.currentAnte = 1;
        this.currentBlind = 1;
        this.isGameOver = false;
    }

    public String getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Deck_I getDeck() {
        return deck;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public int getCurrentAnte() {
        return currentAnte;
    }

    public void setCurrentAnte(int currentAnte) {
        this.currentAnte = currentAnte;
    }

    public int getCurrentBlind() {
        return currentBlind;
    }

    public void setCurrentBlind(int currentBlind) {
        this.currentBlind = currentBlind;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public Rounds getAllRounds() {
        return allRounds;
    }

    public void setAllRounds(Rounds allRounds) {
        this.allRounds = allRounds;
    }

    public List<Store> getAllStores() {
        return allStores;
    }

    public void setAllStores(List<Store> allStores) {
        this.allStores = allStores;
    }

    public int getRoundIndex() {
        return roundIndex;
    }

    public void setRoundIndex(int roundIndex) {
        this.roundIndex = roundIndex;
    }
}
