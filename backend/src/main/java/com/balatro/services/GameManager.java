package com.balatro.services;

import com.balatro.modelo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameManager {
    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();

    public GameSession createGame(String playerName) {
        String sessionId = UUID.randomUUID().toString(); 
        return createGameWithId(sessionId, playerName);
    }

    public GameSession createGameWithId(String sessionId, String playerName) {
        try {
            Parser parser = new Parser("balatro.json");
            Deck_I deck = parser.parseDeck();
            Player player = new Player(deck, playerName);
            player.addMoney(4); // initial money

            Rounds allRounds = parser.parseRounds();
            List<Store> allStores = parser.parseStores(player, allRounds);

            GameSession session = new GameSession(sessionId, player, deck);
            session.setAllRounds(allRounds);
            session.setAllStores(allStores);
            session.setRoundIndex(0);
            
            // Setup initial round (Ante 1, Blind 1)
            setupNextRound(session);
            
            sessions.put(sessionId, session);
            return session;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando balatro.json: " + e.getMessage(), e);
        }
    }

    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void setupNextRound(GameSession session) {
        Rounds allRounds = session.getAllRounds();
        int baseRoundIndex = session.getCurrentAnte() - 1;
        if (baseRoundIndex >= allRounds.getSize()) {
            baseRoundIndex = allRounds.getSize() - 1; // Cap at max ante if we exceed JSON
        }
        
        Round baseRound = allRounds.getRound(baseRoundIndex);
        
        int targetScore = baseRound.getTargetScore();
        if (session.getCurrentBlind() == 2) {
            targetScore = (int) (targetScore * 1.5);
        } else if (session.getCurrentBlind() == 3) {
            targetScore = targetScore * 2;
        }

        Round currentRound = new Round(baseRound.getHandsLeft(), baseRound.getDiscardsLeft(), targetScore);
        
        session.setRound(currentRound);
        // Sincronizamos la ronda activa en el objeto Rounds para que los comodines
        // de tipo "Descarte" lean los descartes reales del jugador (discardsUsed).
        session.getAllRounds().setCurrentRound(currentRound);
        
        // Give player initial 8 cards if empty or refill
        refillPlayerHand(session);
        
        // Generate new store for this round
        generateStore(session);
    }

    public void refillPlayerHand(GameSession session) {
        Player player = session.getPlayer();
        int neededCards = 8 - player.numberOfCards();
        if (neededCards > 0) {
            List<Card> newCards = session.getDeck().dealCards(neededCards);
            if (!newCards.isEmpty()) {
                player.reciveCards(newCards);
            }
        }
    }

    private void generateStore(GameSession session) {
        int baseRoundIndex = session.getCurrentAnte() - 1;
        if (baseRoundIndex >= session.getAllStores().size()) {
            baseRoundIndex = session.getAllStores().size() - 1;
        }
        Store store = session.getAllStores().get(baseRoundIndex);
        session.setStore(store);
    }
}
