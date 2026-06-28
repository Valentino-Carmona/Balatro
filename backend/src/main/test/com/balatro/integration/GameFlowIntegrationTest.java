package com.balatro.integration;

import com.balatro.controllers.BuyRequestDTO;
import com.balatro.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de Integracion del Backend - Balatro Engine
 *
 * A diferencia de los unit tests con @WebMvcTest (que mockean GameManager),
 * estas pruebas levantan el contexto completo de Spring Boot con un servidor
 * HTTP real embebido y prueban flujos end-to-end reales sin ningun mock.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static String sessionId;
    private static List<CardDTO> handCards;

    private String base() { return "http://localhost:" + port + "/api/v1/game"; }
    private String scoreBase() { return "http://localhost:" + port + "/api/v1/score"; }

    // IT-01
    @Test @Order(1)
    @DisplayName("IT-01: start crea sesion valida con estado inicial correcto")
    void testStartGame_CreatesValidSession() {
        ResponseEntity<GameSessionDTO> response = restTemplate.postForEntity(
            base() + "/start?name=Jugador1", null, GameSessionDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameSessionDTO dto = response.getBody();
        assertNotNull(dto);
        assertNotNull(dto.getSessionId());
        assertFalse(dto.getSessionId().isEmpty());
        assertEquals(1, dto.getCurrentAnte());
        assertEquals(1, dto.getCurrentBlind());
        assertFalse(dto.isGameOver());
        assertNotNull(dto.getPlayer());
        assertEquals("Jugador1", dto.getPlayer().getName());
        assertFalse(dto.getPlayer().getHandCards().isEmpty());
        assertTrue(dto.getRound().getHandsLeft() > 0);

        sessionId = dto.getSessionId();
        handCards = dto.getPlayer().getHandCards();
    }

    // IT-02
    @Test @Order(2)
    @DisplayName("IT-02: GET /state con sesion valida devuelve el estado")
    void testGetState_ReturnsCurrentState() {
        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", sessionId);
        ResponseEntity<GameSessionDTO> r = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(h), GameSessionDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(sessionId, r.getBody().getSessionId());
    }

    // IT-03
    @Test @Order(3)
    @DisplayName("IT-03: GET /state con sesion invalida devuelve 404")
    void testGetState_UnknownSession_Returns404() {
        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", "sesion-no-existe-abc123");
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(h), String.class);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-04
    @Test @Order(4)
    @DisplayName("IT-04: POST /discard descarta cartas y reduce descartes disponibles")
    void testDiscardCards_ReducesDiscardsLeft() {
        assertNotNull(sessionId);
        assertNotNull(handCards);

        HttpHeaders getH = new HttpHeaders(); getH.set("X-Session-ID", sessionId);
        GameSessionDTO before = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(getH), GameSessionDTO.class).getBody();
        int discardsBefore = before.getRound().getDiscardsLeft();

        PlayHandRequestDTO req = new PlayHandRequestDTO();
        req.setCards(List.of(handCards.get(0)));

        HttpHeaders postH = new HttpHeaders();
        postH.set("X-Session-ID", sessionId);
        postH.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<GameSessionDTO> r = restTemplate.exchange(
            base() + "/discard", HttpMethod.POST, new HttpEntity<>(req, postH), GameSessionDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(discardsBefore - 1, r.getBody().getRound().getDiscardsLeft());
    }

    // IT-05
    @Test @Order(5)
    @DisplayName("IT-05: POST /play juega una mano y reduce handsLeft")
    void testPlayHand_ReturnsScoreAndUpdatedState() {
        HttpHeaders getH = new HttpHeaders(); getH.set("X-Session-ID", sessionId);
        GameSessionDTO state = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(getH), GameSessionDTO.class).getBody();
        assertNotNull(state);
        int handsLeftBefore = state.getRound().getHandsLeft();
        List<CardDTO> currentHand = state.getPlayer().getHandCards();
        assertFalse(currentHand.isEmpty());

        PlayHandRequestDTO req = new PlayHandRequestDTO();
        req.setCards(List.of(currentHand.get(0)));

        HttpHeaders postH = new HttpHeaders();
        postH.set("X-Session-ID", sessionId);
        postH.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<PlayResponseDTO> r = restTemplate.exchange(
            base() + "/play", HttpMethod.POST, new HttpEntity<>(req, postH), PlayResponseDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody().getHandScore());
        assertNotNull(r.getBody().getHandScore().getHandName());
        assertTrue(r.getBody().getHandScore().getTotalScore() >= 0);
        assertEquals(handsLeftBefore - 1, r.getBody().getGameState().getRound().getHandsLeft());
    }

    // IT-06
    @Test @Order(6)
    @DisplayName("IT-06: POST /evaluate-hand devuelve preview sin alterar handsLeft")
    void testEvaluateHand_DoesNotConsumeHand() {
        HttpHeaders getH = new HttpHeaders(); getH.set("X-Session-ID", sessionId);
        GameSessionDTO before = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(getH), GameSessionDTO.class).getBody();
        assertNotNull(before);
        int handsLeftBefore = before.getRound().getHandsLeft();
        List<CardDTO> hand = before.getPlayer().getHandCards();
        assertFalse(hand.isEmpty());

        PlayHandRequestDTO req = new PlayHandRequestDTO();
        req.setCards(List.of(hand.get(0)));

        HttpHeaders postH = new HttpHeaders();
        postH.set("X-Session-ID", sessionId);
        postH.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ScoreResponseDTO> r = restTemplate.exchange(
            base() + "/evaluate-hand", HttpMethod.POST, new HttpEntity<>(req, postH), ScoreResponseDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody().getHandName());

        GameSessionDTO after = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(getH), GameSessionDTO.class).getBody();
        assertEquals(handsLeftBefore, after.getRound().getHandsLeft(),
            "evaluate-hand NO debe reducir handsLeft");
    }

    // IT-07
    @Test @Order(7)
    @DisplayName("IT-07: POST /buy con sesion invalida devuelve 404")
    void testBuyItem_InvalidSession_Returns404() {
        BuyRequestDTO req = new BuyRequestDTO(); req.setType("joker"); req.setName("Cualquier Joker");
        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", "sesion-invalida-xyz"); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/buy", HttpMethod.POST, new HttpEntity<>(req, h), String.class);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-08
    @Test @Order(8)
    @DisplayName("IT-08: POST /buy compra joker de tienda si hay fondos")
    void testBuyJoker_WhenAffordable_AddsToPlayer() {
        HttpHeaders getH = new HttpHeaders(); getH.set("X-Session-ID", sessionId);
        GameSessionDTO state = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(getH), GameSessionDTO.class).getBody();
        assertNotNull(state);

        if (state.getStore() == null || state.getStore().getJokers() == null
                || state.getStore().getJokers().isEmpty()) {
            System.out.println("IT-08: Sin jokers en tienda, test omitido.");
            return;
        }

        String jokerName = state.getStore().getJokers().get(0).getName();
        int jokersBefore = state.getPlayer().getJokers() != null
            ? state.getPlayer().getJokers().size() : 0;

        BuyRequestDTO req = new BuyRequestDTO(); req.setType("joker"); req.setName(jokerName);
        HttpHeaders postH = new HttpHeaders();
        postH.set("X-Session-ID", sessionId); postH.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<GameSessionDTO> r = restTemplate.exchange(
            base() + "/buy", HttpMethod.POST, new HttpEntity<>(req, postH), GameSessionDTO.class);

        if (r.getStatusCode() == HttpStatus.OK) {
            int jokersAfter = r.getBody().getPlayer().getJokers() != null
                ? r.getBody().getPlayer().getJokers().size() : 0;
            assertEquals(jokersBefore + 1, jokersAfter);
        }
    }

    // IT-09
    @Test @Order(9)
    @DisplayName("IT-09: POST /next-round avanza el blind con sesion nueva")
    void testNextRound_AdvancesBlind() {
        ResponseEntity<GameSessionDTO> startResp = restTemplate.postForEntity(
            base() + "/start?name=Tester2", null, GameSessionDTO.class);
        String freshSid = startResp.getBody().getSessionId();
        int blindBefore = startResp.getBody().getCurrentBlind();

        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", freshSid); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<GameSessionDTO> r = restTemplate.exchange(
            base() + "/next-round", HttpMethod.POST, new HttpEntity<>(h), GameSessionDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(blindBefore + 1, r.getBody().getCurrentBlind());
        assertFalse(r.getBody().getPlayer().getHandCards().isEmpty());
    }

    // IT-10
    @Test @Order(10)
    @DisplayName("IT-10: POST /next-round con sesion invalida devuelve 404")
    void testNextRound_InvalidSession_Returns404() {
        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", "no-existe");
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/next-round", HttpMethod.POST, new HttpEntity<>(h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-11
    @Test @Order(11)
    @DisplayName("IT-11: POST /reorder-jokers con sesion invalida devuelve 404")
    void testReorderJokers_InvalidSession_Returns404() {
        ReorderJokerRequestDTO req = new ReorderJokerRequestDTO();
        req.setJokerName("Algun Joker"); req.setDirection(-1);
        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", "no-existe"); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/reorder-jokers", HttpMethod.POST, new HttpEntity<>(req, h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-12
    @Test @Order(12)
    @DisplayName("IT-12: POST /remove-joker con sesion invalida devuelve 404")
    void testRemoveJoker_InvalidSession_Returns404() {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", "no-existe"); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/remove-joker", HttpMethod.POST, new HttpEntity<>(Map.of("name", "X"), h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-13
    @Test @Order(13)
    @DisplayName("IT-13: POST /remove-tarot con sesion invalida devuelve 404")
    void testRemoveTarot_InvalidSession_Returns404() {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", "no-existe"); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/remove-tarot", HttpMethod.POST, new HttpEntity<>(Map.of("name", "X"), h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-14
    @Test @Order(14)
    @DisplayName("IT-14: POST /use-tarot con sesion invalida devuelve 404")
    void testUseTarot_InvalidSession_Returns404() {
        UseTarotRequestDTO req = new UseTarotRequestDTO(); req.setTarotName("El Loco");
        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", "no-existe"); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.exchange(
            base() + "/use-tarot", HttpMethod.POST, new HttpEntity<>(req, h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
    }

    // IT-15
    @Test @Order(15)
    @DisplayName("IT-15: POST /score/calculate calcula puntaje de una carta valida")
    void testScoreCalculate_SingleCard_ReturnsScore() {
        CardDTO card = new CardDTO();
        card.setSuit("Corazones"); card.setRank("As"); card.setPoints(11); card.setMult(1); card.setAddMult(0);

        PlayHandRequestDTO req = new PlayHandRequestDTO(); req.setCards(List.of(card));
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ScoreResponseDTO> r = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(req, h), ScoreResponseDTO.class);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody().getHandName());
        assertTrue(r.getBody().getTotalScore() >= 0);
    }

    // IT-16
    @Test @Order(16)
    @DisplayName("IT-16: POST /score/calculate con rank invalido devuelve 400")
    void testScoreCalculate_InvalidRank_Returns400() {
        CardDTO card = new CardDTO();
        card.setSuit("Corazones"); card.setRank("INVALIDO"); card.setPoints(0); card.setMult(1); card.setAddMult(0);

        PlayHandRequestDTO req = new PlayHandRequestDTO(); req.setCards(List.of(card));
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> r = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(req, h), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    // IT-17
    @Test @Order(17)
    @DisplayName("IT-17: Flujo completo start -> play -> next-round -> play consistente")
    void testFullGameFlow_StartPlayNextRoundPlay() {
        ResponseEntity<GameSessionDTO> startResp = restTemplate.postForEntity(
            base() + "/start?name=FlowTester", null, GameSessionDTO.class);
        assertEquals(HttpStatus.OK, startResp.getStatusCode());
        GameSessionDTO state = startResp.getBody();
        String sid = state.getSessionId();
        assertEquals(1, state.getCurrentAnte());
        assertEquals(1, state.getCurrentBlind());

        PlayHandRequestDTO playReq = new PlayHandRequestDTO();
        playReq.setCards(List.of(state.getPlayer().getHandCards().get(0)));
        HttpHeaders postH = new HttpHeaders();
        postH.set("X-Session-ID", sid); postH.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<PlayResponseDTO> playResp = restTemplate.exchange(
            base() + "/play", HttpMethod.POST, new HttpEntity<>(playReq, postH), PlayResponseDTO.class);
        assertEquals(HttpStatus.OK, playResp.getStatusCode());
        assertNotNull(playResp.getBody().getHandScore());

        HttpHeaders nextH = new HttpHeaders(); nextH.set("X-Session-ID", sid);
        ResponseEntity<GameSessionDTO> nextResp = restTemplate.exchange(
            base() + "/next-round", HttpMethod.POST, new HttpEntity<>(nextH), GameSessionDTO.class);
        assertEquals(HttpStatus.OK, nextResp.getStatusCode());
        assertEquals(2, nextResp.getBody().getCurrentBlind());

        List<CardDTO> newHand = nextResp.getBody().getPlayer().getHandCards();
        assertFalse(newHand.isEmpty());
        PlayHandRequestDTO playReq2 = new PlayHandRequestDTO();
        playReq2.setCards(List.of(newHand.get(0)));
        HttpHeaders postH2 = new HttpHeaders();
        postH2.set("X-Session-ID", sid); postH2.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<PlayResponseDTO> playResp2 = restTemplate.exchange(
            base() + "/play", HttpMethod.POST, new HttpEntity<>(playReq2, postH2), PlayResponseDTO.class);
        assertEquals(HttpStatus.OK, playResp2.getStatusCode());
        assertFalse(playResp2.getBody().getGameState().isGameOver());
    }

    // IT-18
    @Test @Order(18)
    @DisplayName("IT-18: Dos sesiones simultaneas son completamente independientes")
    void testMultipleSessions_AreIndependent() {
        ResponseEntity<GameSessionDTO> resp1 = restTemplate.postForEntity(
            base() + "/start?name=Jugador_A", null, GameSessionDTO.class);
        ResponseEntity<GameSessionDTO> resp2 = restTemplate.postForEntity(
            base() + "/start?name=Jugador_B", null, GameSessionDTO.class);

        String sid1 = resp1.getBody().getSessionId();
        String sid2 = resp2.getBody().getSessionId();
        assertNotEquals(sid1, sid2);
        int initialHandsSid2 = resp2.getBody().getRound().getHandsLeft();

        PlayHandRequestDTO playReq = new PlayHandRequestDTO();
        playReq.setCards(List.of(resp1.getBody().getPlayer().getHandCards().get(0)));
        HttpHeaders h1 = new HttpHeaders(); h1.set("X-Session-ID", sid1); h1.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.exchange(base() + "/play", HttpMethod.POST, new HttpEntity<>(playReq, h1), PlayResponseDTO.class);

        HttpHeaders h2 = new HttpHeaders(); h2.set("X-Session-ID", sid2);
        GameSessionDTO state2 = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(h2), GameSessionDTO.class).getBody();
        assertEquals(initialHandsSid2, state2.getRound().getHandsLeft(),
            "La sesion 2 no debe verse afectada por acciones de la sesion 1");
    }
}