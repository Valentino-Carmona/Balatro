package com.balatro.dto;

import java.util.List;

public class UseTarotRequestDTO {
    private String tarotName;
    private List<String> targetCardNames;

    public String getTarotName() { return tarotName; }
    public void setTarotName(String tarotName) { this.tarotName = tarotName; }
    public List<String> getTargetCardNames() { return targetCardNames; }
    public void setTargetCardNames(List<String> targetCardNames) { this.targetCardNames = targetCardNames; }
}
