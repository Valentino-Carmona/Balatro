package com.balatro.dto;

public class CardDTO {
    private String suit;
    private String rank;
    private int points;
    private float mult;
    private int addMult;

    public CardDTO() {}

    public CardDTO(String suit, String rank, int points, float mult, int addMult) {
        this.suit = suit;
        this.rank = rank;
        this.points = points;
        this.mult = mult;
        this.addMult = addMult;
    }

    public String getSuit() { return suit; }
    public void setSuit(String suit) { this.suit = suit; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public float getMult() { return mult; }
    public void setMult(float mult) { this.mult = mult; }

    public int getAddMult() { return addMult; }
    public void setAddMult(int addMult) { this.addMult = addMult; }
}
