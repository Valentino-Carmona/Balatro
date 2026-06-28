package com.balatro.dto;

import java.util.List;
import java.util.ArrayList;

public class ScoreResponseDTO {
    private int points;
    private float multiplier;
    private int totalScore;
    private String handName;
    private List<ScoreEventDTO> events;

    public ScoreResponseDTO() {}

    public ScoreResponseDTO(int points, float multiplier, int totalScore, String handName) {
        this.points = points;
        this.multiplier = multiplier;
        this.totalScore = totalScore;
        this.handName = handName;
        this.events = new ArrayList<>();
    }

    public ScoreResponseDTO(int points, float multiplier, int totalScore, String handName, List<ScoreEventDTO> events) {
        this.points = points;
        this.multiplier = multiplier;
        this.totalScore = totalScore;
        this.handName = handName;
        this.events = events;
    }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public float getMultiplier() { return multiplier; }
    public void setMultiplier(float multiplier) { this.multiplier = multiplier; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public String getHandName() { return handName; }
    public void setHandName(String handName) { this.handName = handName; }

    public List<ScoreEventDTO> getEvents() { return events; }
    public void setEvents(List<ScoreEventDTO> events) { this.events = events; }
}
