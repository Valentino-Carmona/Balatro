package com.balatro.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private Deck_I dummyDeck;

    @BeforeEach
    void setUp() {
        dummyDeck = new Deck_I() {
            @Override
            public List<Card> dealCards(int count) {
                List<Card> cards = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    cards.add(new Card(new Score(2, 1, 0), Suit.fromString("Corazones"), Rank.fromString("2")));
                }
                return cards;
            }
            @Override
            public void resetDeck() { }
            @Override
            public int remainingCards() { return 52; }
            @Override
            public void addCard(Card card) { }
            @Override
            public void removeCard(Card card) { }
        };
        player = new Player(dummyDeck, "TestPlayer");
    }

    @Test
    void testInitialState() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(0, player.getMoney());
        assertEquals(0, player.numberOfCards());
        assertEquals(0, player.getHandSize());
        assertEquals(0, player.getJokers().size());
        assertEquals(0, player.getTarots().size());
    }

    @Test
    void testMoneyManagement() {
        player.addMoney(10);
        assertEquals(10, player.getMoney());

        player.spendMoney(4);
        assertEquals(6, player.getMoney());

        player.spendMoney(10); // Not enough, should do nothing
        assertEquals(6, player.getMoney());
    }

    @Test
    void testCardSelection() {
        Card card = new Card(new Score(11, 1, 0), Suit.fromString("Corazones"), Rank.fromString("As"));
        player.reciveCards(List.of(card));
        
        player.selectCardToHand(card);
        assertEquals(1, player.getHandSize());

        player.unselectCardToHand(card);
        assertEquals(0, player.getHandSize());
    }

    @Test
    void testDiscardHand() {
        Card card = new Card(new Score(10, 1, 0), Suit.fromString("Picas"), Rank.fromString("10"));
        player.reciveCards(List.of(card));
        player.selectCardToHand(card);

        Round round = new Round(4, 3, 300); // 3 discards
        player.discardHand(round);

        assertEquals(2, round.getDiscardsLeft());
        assertEquals(0, player.getHandSize());
        assertEquals(1, player.numberOfCards());
    }

    @Test
    void testPlayHand() {
        Card card1 = new Card(new Score(2, 1, 0), Suit.fromString("Trebol"), Rank.fromString("2"));
        Card card2 = new Card(new Score(3, 1, 0), Suit.fromString("Trebol"), Rank.fromString("3"));
        player.reciveCards(List.of(card1, card2));

        player.selectCardToHand(card1);
        player.selectCardToHand(card2);

        Round round = new Round(4, 3, 300); // 4 hands
        Score score = player.playHand(round);

        assertEquals(3, round.getHandsLeft());
        assertTrue(score.getTotalPoints() > 0);
        assertEquals(2, player.numberOfCards(), "Cards are removed and refilled automatically");
    }

    @Test
    void testPlayHandEmptyHandThrowsException() {
        Round round = new Round(4, 3, 300);
        assertThrows(RuntimeException.class, () -> player.playHand(round));
    }

    @Test
    void testAddAndRemoveJoker() {
        JokerApply dummyJoker = new AlwaysApplyJoker(new Joker("J1", "D1", new ScoreJokerStrategy(new IncreaseMultStrategy(2))));
        player.addJoker(dummyJoker);
        assertEquals(1, player.getJokers().size());

        player.removeJoker(dummyJoker);
        assertEquals(0, player.getJokers().size());
    }

    @Test
    void testReorderJoker() {
        JokerApply j1 = new AlwaysApplyJoker(new Joker("J1", "D1", new ScoreJokerStrategy(new IncreaseMultStrategy(2))));
        JokerApply j2 = new AlwaysApplyJoker(new Joker("J2", "D2", new ScoreJokerStrategy(new IncreaseMultStrategy(3))));
        
        player.addJoker(j1);
        player.addJoker(j2);

        // Current order: J1, J2
        player.reorderJoker("J1", 1); // Move J1 right
        
        // Expected order: J2, J1
        assertEquals("J2", player.getJokers().get(0).getNombre());
        assertEquals("J1", player.getJokers().get(1).getNombre());
    }
}
