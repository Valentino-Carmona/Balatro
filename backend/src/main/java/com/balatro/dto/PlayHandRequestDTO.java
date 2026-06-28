package com.balatro.dto;

import java.util.List;

public class PlayHandRequestDTO {
    private List<CardDTO> cards;
    
    // Aquí a futuro se pueden agregar JokersDTO o ModifiersDTO
    // private List<JokerDTO> jokers;

    public PlayHandRequestDTO() {}

    public PlayHandRequestDTO(List<CardDTO> cards) {
        this.cards = cards;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardDTO> cards) {
        this.cards = cards;
    }
}
