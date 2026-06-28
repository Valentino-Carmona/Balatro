package com.balatro.dto;

public class ScoreEventDTO {
    private String type; // "base", "card", "joker"
    private String name; // name of hand, card, or joker
    private int points; // points added
    private float multiplier; // mult added or applied
    private int currentTotalPoints; // running total points
    private float currentTotalMult; // running total multiplier

    public ScoreEventDTO() {}

    public ScoreEventDTO(String type, String name, int points, float multiplier, int currentTotalPoints, float currentTotalMult) {
        this.type = type;
        this.name = name;
        this.points = points;
        this.multiplier = multiplier;
        this.currentTotalPoints = currentTotalPoints;
        this.currentTotalMult = currentTotalMult;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public float getMultiplier() { return multiplier; }
    public void setMultiplier(float multiplier) { this.multiplier = multiplier; }
    public int getCurrentTotalPoints() { return currentTotalPoints; }
    public void setCurrentTotalPoints(int currentTotalPoints) { this.currentTotalPoints = currentTotalPoints; }
    public float getCurrentTotalMult() { return currentTotalMult; }
    public void setCurrentTotalMult(float currentTotalMult) { this.currentTotalMult = currentTotalMult; }
}
