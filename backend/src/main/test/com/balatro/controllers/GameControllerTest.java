package com.balatro.controllers;

import com.balatro.dto.*;
import com.balatro.modelo.*;
import com.balatro.services.GameManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameManager gameManager;

    @Autowired
    private ObjectMapper objectMapper;

    private GameSession dummySession;

    @BeforeEach
    void setUp() {
        Deck_I dummyDeck = new Deck(new ArrayList<>());
        Player player = new Player(dummyDeck, "TestPlayer");
        player.addMoney(10);
        
        dummySession = new GameSession("session-123", player, dummyDeck);
        dummySession.setCurrentAnte(1);
        dummySession.setCurrentBlind(1);
        
        Card dummyCard = new Card(new Score(10, 1, 0), Suit.fromString("Corazones"), Rank.fromString("As"));
        player.reciveCards(java.util.List.of(dummyCard));
        
        Round round = new Round(4, 3, 1000);
        dummySession.setRound(round);
        
        Store store = new Store(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        dummySession.setStore(store);
    }

    @Test
    void testStartGame() throws Exception {
        Mockito.when(gameManager.createGame(anyString())).thenReturn(dummySession);

        mockMvc.perform(post("/api/v1/game/start")
                .param("name", "TestPlayer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-123"))
                .andExpect(jsonPath("$.player.name").value("TestPlayer"))
                .andExpect(jsonPath("$.currentAnte").value(1))
                .andExpect(jsonPath("$.currentBlind").value(1));
    }

    @Test
    void testGetStateNotFound() throws Exception {
        Mockito.when(gameManager.getSession("invalid")).thenReturn(null);

        mockMvc.perform(get("/api/v1/game/state")
                .header("X-Session-ID", "invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStateSuccess() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);

        mockMvc.perform(get("/api/v1/game/state")
                .header("X-Session-ID", "session-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-123"));
    }

    @Test
    void testNextRound() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        // Mock the void method setupNextRound to do nothing
        Mockito.doNothing().when(gameManager).setupNextRound(dummySession);

        mockMvc.perform(post("/api/v1/game/next-round")
                .header("X-Session-ID", "session-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBlind").value(2));
    }

    @Test
    void testDiscardCards() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        PlayHandRequestDTO req = new PlayHandRequestDTO();
        CardDTO cardDTO = new CardDTO();
        cardDTO.setSuit("Corazones");
        cardDTO.setRank("A");
        req.setCards(java.util.List.of(cardDTO));
        
        mockMvc.perform(post("/api/v1/game/discard")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testBuyItemJoker() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        BuyRequestDTO req = new BuyRequestDTO();
        req.setType("joker");
        req.setName("Astuto");
        
        mockMvc.perform(post("/api/v1/game/buy")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testPlayHand() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        PlayHandRequestDTO req = new PlayHandRequestDTO();
        CardDTO cardDTO = new CardDTO();
        cardDTO.setSuit("Corazones");
        cardDTO.setRank("A");
        req.setCards(java.util.List.of(cardDTO));
        
        mockMvc.perform(post("/api/v1/game/play")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testEvaluateHand() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        PlayHandRequestDTO req = new PlayHandRequestDTO();
        CardDTO cardDTO = new CardDTO();
        cardDTO.setSuit("Corazones");
        cardDTO.setRank("A");
        req.setCards(java.util.List.of(cardDTO));
        
        mockMvc.perform(post("/api/v1/game/evaluate-hand")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testPlayHandGameOver() throws Exception {
        dummySession.getRound().reduceHands();
        dummySession.getRound().reduceHands();
        dummySession.getRound().reduceHands();
        dummySession.getRound().reduceHands();
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        PlayHandRequestDTO req = new PlayHandRequestDTO();
        CardDTO cardDTO = new CardDTO();
        cardDTO.setSuit("Corazones");
        cardDTO.setRank("A");
        req.setCards(java.util.List.of(cardDTO));
        
        mockMvc.perform(post("/api/v1/game/play")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameState.gameOver").value(true));
    }

    @Test
    void testPlayHandNullSession() throws Exception {
        Mockito.when(gameManager.getSession("invalid")).thenReturn(null);
        mockMvc.perform(post("/api/v1/game/play")
                .header("X-Session-ID", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PlayHandRequestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDiscardNullSession() throws Exception {
        Mockito.when(gameManager.getSession("invalid")).thenReturn(null);
        mockMvc.perform(post("/api/v1/game/discard")
                .header("X-Session-ID", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PlayHandRequestDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testNextRoundNullSession() throws Exception {
        Mockito.when(gameManager.getSession("invalid")).thenReturn(null);
        mockMvc.perform(post("/api/v1/game/next-round")
                .header("X-Session-ID", "invalid"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testNextRoundToNextAnte() throws Exception {
        dummySession.setCurrentBlind(3);
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        Mockito.doNothing().when(gameManager).setupNextRound(dummySession);

        mockMvc.perform(post("/api/v1/game/next-round")
                .header("X-Session-ID", "session-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBlind").value(1))
                .andExpect(jsonPath("$.currentAnte").value(2));
    }

    @Test
    void testBuyItemNotEnoughMoney() throws Exception {
        dummySession.getPlayer().spendMoney(10); // money becomes 0
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        BuyRequestDTO req = new BuyRequestDTO();
        req.setType("joker");
        req.setName("Astuto");
        
        mockMvc.perform(post("/api/v1/game/buy")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBuyItemTarot() throws Exception {
        Tarot tarot = new Tarot("El Loco", "test", new java.util.ArrayList<>(), null);
        dummySession.getStore().getTarots().add(tarot);
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        BuyRequestDTO req = new BuyRequestDTO();
        req.setType("tarot");
        req.setName("El Loco");
        
        mockMvc.perform(post("/api/v1/game/buy")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testBuyItemCard() throws Exception {
        Card card = new Card(new Score(10,1,0), Suit.CLUBS, Rank.ACE);
        dummySession.getStore().getCards().add(card);
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        BuyRequestDTO req = new BuyRequestDTO();
        req.setType("card");
        req.setName("As");
        
        mockMvc.perform(post("/api/v1/game/buy")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testReorderJokers() throws Exception {
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        ReorderJokerRequestDTO req = new ReorderJokerRequestDTO();
        req.setJokerName("Astuto");
        req.setDirection(1);
        
        mockMvc.perform(post("/api/v1/game/reorder-jokers")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveJoker() throws Exception {
        Joker joker = new Joker("Astuto", "test", null);
        dummySession.getPlayer().addJoker(joker);
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        mockMvc.perform(post("/api/v1/game/remove-joker")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Astuto\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveTarot() throws Exception {
        Tarot tarot = new Tarot("El Loco", "test", new java.util.ArrayList<>(), null);
        dummySession.getPlayer().addTarot(tarot);
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        mockMvc.perform(post("/api/v1/game/remove-tarot")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"El Loco\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUseTarot() throws Exception {
        Tarot tarot = new Tarot("El Loco", "test", new java.util.ArrayList<>(), dummySession.getPlayer().getPlayingHand());
        dummySession.getPlayer().addTarot(tarot);
        Mockito.when(gameManager.getSession("session-123")).thenReturn(dummySession);
        
        UseTarotRequestDTO req = new UseTarotRequestDTO();
        req.setTarotName("El Loco");
        req.setTargetCardNames(java.util.List.of("A de Corazones"));
        
        mockMvc.perform(post("/api/v1/game/use-tarot")
                .header("X-Session-ID", "session-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
