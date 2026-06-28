package com.balatro.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoundTest {

    private Round round;

    @BeforeEach
    void setUp() {
        // hands = 4, discards = 3, targetScore = 1000
        round = new Round(4, 3, 1000);
    }

    @Test
    void testInitialState() {
        assertEquals(4, round.getHandsLeft());
        assertEquals(3, round.getDiscardsLeft());
        assertEquals(0, round.getPlayerScore());
        assertEquals(1000, round.getTargetScore());
        assertFalse(round.verifyStatePlayer());
        assertFalse(round.verifyEndGame());
    }

    @Test
    void testReduceHands() {
        round.reduceHands();
        assertEquals(3, round.getHandsLeft());
    }

    @Test
    void testPlayerDiscard() {
        Player player = new Player(new Deck(new FactoryCard().createBaseDeck()), "P1");
        
        round.playerDiscard(player);
        assertEquals(2, round.getDiscardsLeft());
        
        // Cannot discard below 0
        round.playerDiscard(player);
        round.playerDiscard(player);
        assertEquals(0, round.getDiscardsLeft());
        
        round.playerDiscard(player); // Should not decrease further
        assertEquals(0, round.getDiscardsLeft());
    }

    @Test
    void testAddPlayerScoreAndWin() {
        Score score = new Score(100, 10, 0); // Total points = 1000
        round.addPlayerScore(score);

        assertEquals(1000, round.getPlayerScore());
        assertTrue(round.verifyStatePlayer());
        assertFalse(round.verifyEndGame(), "End game means losing when out of hands, if won it shouldn't trigger endgame loss flag");
    }

    @Test
    void testEndGameByNoHands() {
        round.reduceHands();
        round.reduceHands();
        round.reduceHands();
        round.reduceHands();

        assertEquals(0, round.getHandsLeft());
        assertFalse(round.verifyStatePlayer());
        assertTrue(round.verifyEndGame(), "Should end game if out of hands and score not reached");
    }

    @Test
    void testUseJokerCallsDiscardCorrectAmount() {
        // Mocking DiscardJokerStrategy to count uses
        class MockDiscardJoker extends DiscardJokerStrategy {
            int uses = 0;
            public MockDiscardJoker() { super(new IncreasePointsStrategy(1), null); }
            @Override public void use() { uses++; }
        }

        MockDiscardJoker mockJoker = new MockDiscardJoker();
        Player dummyPlayer = new Player(new Deck(new FactoryCard().createBaseDeck()), "p");
        
        // Discard twice
        round.playerDiscard(dummyPlayer);
        round.playerDiscard(dummyPlayer);
        
        round.useJoker(mockJoker);
        
        assertEquals(2, mockJoker.uses, "Joker should be used once per discard");
    }
}
