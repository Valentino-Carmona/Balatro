package com.balatro.services;

import com.balatro.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
    }

    @Test
    void testCreateGame() {
        GameSession session = gameManager.createGame("TestPlayer");
        
        assertNotNull(session);
        assertNotNull(session.getId());
        assertEquals("TestPlayer", session.getPlayer().getName());
        assertEquals(4, session.getPlayer().getMoney());
        assertEquals(8, session.getPlayer().numberOfCards());
        assertEquals(1, session.getCurrentAnte());
        assertEquals(1, session.getCurrentBlind());
        assertNotNull(session.getRound());
        assertNotNull(session.getStore());
        assertNotNull(gameManager.getSession(session.getId()));
    }

    @Test
    void testSetupNextRoundBlindProgression() {
        GameSession session = gameManager.createGame("P1");
        
        // Initial state: Ante 1, Blind 1
        assertEquals(1, session.getCurrentAnte());
        assertEquals(1, session.getCurrentBlind());
        int targetBlind1 = session.getRound().getTargetScore();

        // Advance to Blind 2
        session.setCurrentBlind(2);
        gameManager.setupNextRound(session);
        assertEquals(2, session.getCurrentBlind());
        assertEquals(1, session.getCurrentAnte());
        int targetBlind2 = session.getRound().getTargetScore();
        assertEquals((int)(targetBlind1 * 1.5), targetBlind2);

        // Advance to Blind 3 (Boss)
        session.setCurrentBlind(3);
        gameManager.setupNextRound(session);
        assertEquals(3, session.getCurrentBlind());
        int targetBlind3 = session.getRound().getTargetScore();
        assertEquals(targetBlind1 * 2, targetBlind3);
    }

    @Test
    void testRefillPlayerHand() {
        GameSession session = gameManager.createGame("P1");
        Player player = session.getPlayer();
        
        // Initially 8 cards
        assertEquals(8, player.numberOfCards());
        
        // Clear hand
        player.removeCards();
        
        assertEquals(0, player.numberOfCards());
        
        // Refill hand
        gameManager.refillPlayerHand(session);
        
        assertEquals(8, player.numberOfCards());
    }

    @Test
    void testRemoveSession() {
        GameSession session = gameManager.createGame("P1");
        String id = session.getId();
        
        assertNotNull(gameManager.getSession(id));
        
        gameManager.removeSession(id);
        
        assertNull(gameManager.getSession(id));
    }
}
