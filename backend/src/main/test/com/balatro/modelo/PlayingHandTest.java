package com.balatro.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayingHandTest {

    private PlayingHand playingHand;

    @BeforeEach
    void setUp() {
        playingHand = new PlayingHand();
    }

    private Card createCard(String rank, String suit) {
        // Base points: 2-10 face value, J/Q/K 10, A 11. Mult is 1.
        int pts = 0;
        try { pts = Integer.parseInt(rank); } catch (Exception e) { pts = rank.equals("As") ? 11 : 10; }
        return new Card(new Score(pts, 1, 0), Suit.fromString(suit), Rank.fromString(rank));
    }

    @Test
    void testEmptyHand() {
        assertTrue(playingHand.isHandNull());
    }

    @Test
    void testHighCard() {
        playingHand.addCard(createCard("2", "Corazones"));
        playingHand.addCard(createCard("7", "Picas"));
        playingHand.addCard(createCard("4", "Trebol"));

        String handStr = playingHand.getHandString();
        assertEquals("HighCard", handStr);
    }

    @Test
    void testPair() {
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("7", "Picas"));
        playingHand.addCard(createCard("4", "Trebol"));

        String handStr = playingHand.getHandString();
        assertEquals("OnePair", handStr);
    }

    @Test
    void testTwoPair() {
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("7", "Picas"));
        playingHand.addCard(createCard("4", "Trebol"));
        playingHand.addCard(createCard("4", "Diamantes"));

        String handStr = playingHand.getHandString();
        assertEquals("DoublePair", handStr);
    }

    @Test
    void testThreeOfAKind() {
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("7", "Picas"));
        playingHand.addCard(createCard("7", "Trebol"));
        playingHand.addCard(createCard("4", "Diamantes"));

        String handStr = playingHand.getHandString();
        assertEquals("Trio", handStr);
    }

    @Test
    void testStraight() {
        playingHand.addCard(createCard("5", "Corazones"));
        playingHand.addCard(createCard("6", "Picas"));
        playingHand.addCard(createCard("7", "Trebol"));
        playingHand.addCard(createCard("8", "Diamantes"));
        playingHand.addCard(createCard("9", "Corazones"));

        String handStr = playingHand.getHandString();
        assertEquals("Straight", handStr);
    }

    @Test
    void testFlush() {
        playingHand.addCard(createCard("2", "Corazones"));
        playingHand.addCard(createCard("6", "Corazones"));
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("Jota", "Corazones"));
        playingHand.addCard(createCard("Rey", "Corazones"));

        String handStr = playingHand.getHandString();
        assertEquals("Flush", handStr);
    }

    @Test
    void testFullHouse() {
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("7", "Picas"));
        playingHand.addCard(createCard("7", "Trebol"));
        playingHand.addCard(createCard("4", "Diamantes"));
        playingHand.addCard(createCard("4", "Corazones"));

        String handStr = playingHand.getHandString();
        assertEquals("FullHouse", handStr);
    }

    @Test
    void testFourOfAKind() {
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("7", "Picas"));
        playingHand.addCard(createCard("7", "Trebol"));
        playingHand.addCard(createCard("7", "Diamantes"));

        String handStr = playingHand.getHandString();
        assertEquals("Poker", handStr);
    }

    @Test
    void testStraightFlush() {
        playingHand.addCard(createCard("5", "Corazones"));
        playingHand.addCard(createCard("6", "Corazones"));
        playingHand.addCard(createCard("7", "Corazones"));
        playingHand.addCard(createCard("8", "Corazones"));
        playingHand.addCard(createCard("9", "Corazones"));

        String handStr = playingHand.getHandString();
        assertEquals("ColorStraight", handStr);
    }
}
