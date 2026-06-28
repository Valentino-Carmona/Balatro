package com.balatro.modelo;

import java.util.List;
import java.util.ArrayList;
import com.balatro.dto.ScoreEventDTO;

public class Score {
    private int points;
    private float mult;
    private int addMult;
    private List<ScoreEventDTO> events;

    public Score(int value, float mult, int addMult) {
        this.points = value;
        this.mult = mult;
        this.addMult = addMult;
        this.events = new ArrayList<>();
    }

    public void logEvent(String type, String name, int addedPoints, float addedMult) {
        this.events.add(new ScoreEventDTO(type, name, addedPoints, addedMult, this.points, this.mult));
    }

    public List<ScoreEventDTO> getEvents() {
        return this.events;
    }

    public int getTotalPoints() {
        return (int) (points * mult);
    }

    public void applyScore(Score incomingScore) {
        incomingScore.points += this.points;
        incomingScore.mult += this.addMult;
        incomingScore.mult *= this.mult;
    }

    public void replacePoints(int points) {
        this.points = points;
    }

    public void replaceMult(float mult) {this.mult = mult;}

    public void replaceAddMult(int addMult) {
        this.addMult = addMult;
    }

    public void increasePoints(int points) {this.points += points;}

    public void increaseMult(float mult) {this.mult += mult;}

    public void multiplyMult(float mult) {this.mult *= mult;}

    public int getPoints() { return this.points; }
    public float getMult() { return this.mult; }
    public int getAddMult() { return this.addMult; }
}
