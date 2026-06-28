package com.balatro.dto;

public class RoundDTO {
    private int handsLeft;
    private int discardsLeft;
    private int playerScore;
    private int targetScore;

    public int getHandsLeft() { return handsLeft; }
    public void setHandsLeft(int handsLeft) { this.handsLeft = handsLeft; }
    public int getDiscardsLeft() { return discardsLeft; }
    public void setDiscardsLeft(int discardsLeft) { this.discardsLeft = discardsLeft; }
    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int playerScore) { this.playerScore = playerScore; }
    public int getTargetScore() { return targetScore; }
    public void setTargetScore(int targetScore) { this.targetScore = targetScore; }
}
