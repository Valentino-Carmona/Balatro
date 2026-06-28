package com.balatro.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class JokerStrategiesTest {

    @Test
    void testIncreasePointsStrategy() {
        Score score = new Score(10, 1, 0);
        ScoringStrategy strategy = new IncreasePointsStrategy(50);
        
        strategy.apply(score);
        
        assertEquals(60, score.getPoints());
        assertEquals(1, score.getMult());
    }

    @Test
    void testIncreaseMultStrategy() {
        Score score = new Score(10, 2, 0);
        ScoringStrategy strategy = new IncreaseMultStrategy(3);
        
        strategy.apply(score);
        
        assertEquals(10, score.getPoints());
        assertEquals(5, score.getMult());
    }

    @Test
    void testMultiplyMultStrategy() {
        Score score = new Score(10, 2, 0);
        ScoringStrategy strategy = new MultiplyMultStrategy(3);
        
        strategy.apply(score);
        
        assertEquals(10, score.getPoints());
        assertEquals(6, score.getMult()); // 2 * 3 = 6
    }

    @Test
    void testDiscardJokerStrategyCumbreMistica() {
        // "Cumbre Mistica" multiplies mult by 15 per discard used in current round.
        ScoringStrategy scoringStrategy = new MultiplyMultStrategy(15);
        
        // Mock Rounds to hold the current round with discards used
        Round round = new Round(4, 3, 1000);
        Rounds rounds = new Rounds(new ArrayList<>());
        rounds.setCurrentRound(round);
        
        DiscardJokerStrategy discardStrategy = new DiscardJokerStrategy(scoringStrategy, rounds);
        
        // Score before applying Joker
        Score score = new Score(10, 2, 0);
        
        // No discards used yet
        discardStrategy.apply(score);
        assertEquals(2, score.getMult(), "Should not multiply if no discards used");
        
        // Use 1 discard
        Player dummyPlayer = new Player(new Deck(new ArrayList<>()), "P1");
        round.playerDiscard(dummyPlayer);
        
        discardStrategy.apply(score);
        assertEquals(30, score.getMult(), "Should multiply by 15 once (2 * 15 = 30)");
        
        // Use 1 more discard
        round.playerDiscard(dummyPlayer);
        
        discardStrategy.apply(score);
        assertEquals(6750, score.getMult(), "Should multiply by 15 twice (30 * 15 * 15)"); // Wait, apply re-applies on current score. Actually score.getMult() was 30, so 30 * 15 * 15 = 6750.
    }
}
