package com.balatro.modelo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void testParseDeck() throws Exception {
        Parser parser = new Parser("balatro.json");
        Deck_I deck = parser.parseDeck();
        
        assertNotNull(deck);
        // Base deck has 52 cards
        assertEquals(52, deck.remainingCards());
    }

    @Test
    void testParseRounds() throws Exception {
        Parser parser = new Parser("balatro.json");
        Rounds rounds = parser.parseRounds();
        
        assertNotNull(rounds);
        // We know it parses some rounds, at least 1
        assertNotNull(rounds.getRound(0));
    }

    @Test
    void testParseStores() throws Exception {
        Parser parser = new Parser("balatro.json");
        Player player = new Player(new Deck(new FactoryCard().createBaseDeck()), "P1");
        Rounds rounds = parser.parseRounds();
        
        List<Store> stores = parser.parseStores(player, rounds);
        
        assertNotNull(stores);
        assertFalse(stores.isEmpty());
        
        Store firstStore = stores.get(0);
        assertNotNull(firstStore.getJokerList());
        assertNotNull(firstStore.getTarots());
        assertNotNull(firstStore.getCards());
    }
}
