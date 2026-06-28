package com.balatro.dto;

import java.util.List;

public class StoreDTO {
    private List<JokerDTO> jokers;
    private List<TarotDTO> tarots;
    private List<CardDTO> cards;

    public List<JokerDTO> getJokers() { return jokers; }
    public void setJokers(List<JokerDTO> jokers) { this.jokers = jokers; }
    public List<TarotDTO> getTarots() { return tarots; }
    public void setTarots(List<TarotDTO> tarots) { this.tarots = tarots; }
    public List<CardDTO> getCards() { return cards; }
    public void setCards(List<CardDTO> cards) { this.cards = cards; }
}
