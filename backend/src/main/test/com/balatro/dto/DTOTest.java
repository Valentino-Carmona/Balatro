package com.balatro.dto;

import org.junit.jupiter.api.Test;
import com.balatro.controllers.BuyRequestDTO;
import static org.junit.jupiter.api.Assertions.*;

public class DTOTest {

    @Test
    void testBuyRequestDTO() {
        BuyRequestDTO dto = new BuyRequestDTO();
        dto.setType("joker");
        dto.setName("Astuto");
        assertEquals("joker", dto.getType());
        assertEquals("Astuto", dto.getName());
    }

    @Test
    void testCardDTO() {
        CardDTO dto = new CardDTO();
        dto.setSuit("Corazones");
        dto.setRank("A");
        dto.setPoints(11);
        dto.setMult(2);
        dto.setAddMult(0);
        
        assertEquals("Corazones", dto.getSuit());
        assertEquals("A", dto.getRank());
        assertEquals(11, dto.getPoints());
        assertEquals(2, dto.getMult());
        assertEquals(0, dto.getAddMult());
    }

    @Test
    void testGameSessionDTO() {
        PlayerDTO player = new PlayerDTO();
        StoreDTO store = new StoreDTO();
        RoundDTO round = new RoundDTO();
        
        GameSessionDTO dto = new GameSessionDTO();
        dto.setSessionId("123");
        dto.setPlayer(player);
        dto.setStore(store);
        dto.setRound(round);
        dto.setCurrentAnte(1);
        dto.setCurrentBlind(2);
        
        assertEquals("123", dto.getSessionId());
        assertEquals(player, dto.getPlayer());
        assertEquals(store, dto.getStore());
        assertEquals(round, dto.getRound());
        assertEquals(1, dto.getCurrentAnte());
        assertEquals(2, dto.getCurrentBlind());
    }

    @Test
    void testPlayResponseDTO() {
        GameSessionDTO session = new GameSessionDTO();
        PlayResponseDTO dto = new PlayResponseDTO();
        dto.setRoundWon(true);
        dto.setGameState(session);
        
        assertTrue(dto.isRoundWon());
        assertEquals(session, dto.getGameState());
    }

    @Test
    void testJokerDTO() {
        JokerDTO dto = new JokerDTO();
        dto.setName("Astuto");
        dto.setDescription("test");
        dto.setCost(4);
        
        assertEquals("Astuto", dto.getName());
        assertEquals("test", dto.getDescription());
        assertEquals(4, dto.getCost());
    }

    @Test
    void testPlayHandRequestDTO() {
        PlayHandRequestDTO dto = new PlayHandRequestDTO();
        dto.setCards(java.util.List.of(new CardDTO()));
        
        assertNotNull(dto.getCards());
        assertEquals(1, dto.getCards().size());
    }

    @Test
    void testPlayerDTO() {
        PlayerDTO dto = new PlayerDTO();
        dto.setName("Test");
        dto.setMoney(10);
        dto.setHandCards(new java.util.ArrayList<>());
        dto.setJokers(new java.util.ArrayList<>());
        dto.setTarots(new java.util.ArrayList<>());
        
        assertEquals("Test", dto.getName());
        assertEquals(10, dto.getMoney());
        assertNotNull(dto.getHandCards());
        assertNotNull(dto.getJokers());
        assertNotNull(dto.getTarots());
    }

    @Test
    void testReorderJokerRequestDTO() {
        ReorderJokerRequestDTO dto = new ReorderJokerRequestDTO();
        dto.setJokerName("Astuto");
        dto.setDirection(1);
        
        assertEquals("Astuto", dto.getJokerName());
        assertEquals(1, dto.getDirection());
    }

    @Test
    void testRoundDTO() {
        RoundDTO dto = new RoundDTO();
        dto.setTargetScore(300);
        dto.setHandsLeft(3);
        dto.setDiscardsLeft(2);
        
        assertEquals(300, dto.getTargetScore());
        assertEquals(3, dto.getHandsLeft());
        assertEquals(2, dto.getDiscardsLeft());
    }

    @Test
    void testScoreResponseDTO() {
        ScoreResponseDTO dto = new ScoreResponseDTO(10, 2, 20, "Par");
        assertEquals(10, dto.getPoints());
        assertEquals(2, dto.getMultiplier());
        assertEquals(20, dto.getTotalScore());
        assertEquals("Par", dto.getHandName());
    }

    @Test
    void testStoreDTO() {
        StoreDTO dto = new StoreDTO();
        dto.setJokers(new java.util.ArrayList<>());
        dto.setTarots(new java.util.ArrayList<>());
        dto.setCards(new java.util.ArrayList<>());
        
        assertNotNull(dto.getJokers());
        assertNotNull(dto.getTarots());
        assertNotNull(dto.getCards());
    }

    @Test
    void testTarotDTO() {
        TarotDTO dto = new TarotDTO();
        dto.setName("El Loco");
        dto.setDescription("test");
        dto.setCost(4);
        
        assertEquals("El Loco", dto.getName());
        assertEquals("test", dto.getDescription());
        assertEquals(4, dto.getCost());
    }

    @Test
    void testUseTarotRequestDTO() {
        UseTarotRequestDTO dto = new UseTarotRequestDTO();
        dto.setTarotName("El Loco");
        dto.setTargetCardNames(new java.util.ArrayList<>());
        
        assertEquals("El Loco", dto.getTarotName());
        assertNotNull(dto.getTargetCardNames());
    }
}
