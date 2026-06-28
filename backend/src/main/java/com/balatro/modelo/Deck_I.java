package com.balatro.modelo;

import java.util.List;

public interface Deck_I {
    List<Card> dealCards(int numberCards);
    void resetDeck();
    int remainingCards();
    void addCard(Card card);
    void removeCard(Card card);
}
