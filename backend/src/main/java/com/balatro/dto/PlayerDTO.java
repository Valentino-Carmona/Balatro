package com.balatro.dto;

import java.util.List;

public class PlayerDTO {
    private String name;
    private int money;
    private List<CardDTO> handCards;
    private List<JokerDTO> jokers;
    private List<TarotDTO> tarots;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
    public List<CardDTO> getHandCards() { return handCards; }
    public void setHandCards(List<CardDTO> handCards) { this.handCards = handCards; }
    public List<JokerDTO> getJokers() { return jokers; }
    public void setJokers(List<JokerDTO> jokers) { this.jokers = jokers; }
    public List<TarotDTO> getTarots() { return tarots; }
    public void setTarots(List<TarotDTO> tarots) { this.tarots = tarots; }
}
