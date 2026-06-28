package com.balatro.dto;

public class ReorderJokerRequestDTO {
    private String jokerName;
    private int direction; // -1 for left, 1 for right

    public String getJokerName() { return jokerName; }
    public void setJokerName(String jokerName) { this.jokerName = jokerName; }
    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }
}
