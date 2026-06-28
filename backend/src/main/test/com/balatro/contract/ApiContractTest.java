package com.balatro.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ============================================================
 *  PRUEBAS DE CONTRATO DE API  -  Balatro Engine
 * ============================================================
 *
 * PROPOSITO:
 *   Garantizar que el CONTRATO entre el Backend y el Frontend
 *   nunca se rompa silenciosamente. Si un campo es renombrado,
 *   eliminado o cambia de tipo, estos tests fallan ANTES de
 *   llegar a produccion.
 *
 * DIFERENCIA con los otros tipos de tests:
 *   - Unit Tests        -> Prueban logica de negocio (con mocks)
 *   - Integration Tests -> Prueban flujos HTTP end-to-end
 *   - Contract Tests    -> Prueban la ESTRUCTURA y TIPOS del JSON
 *
 * QUE SE VERIFICA:
 *   - Cada campo obligatorio existe en la respuesta
 *   - Cada campo tiene el tipo correcto (String/Integer/Boolean/Array)
 *   - Los valores de dominio son validos (suit in {"Corazones",...})
 *   - El Content-Type siempre es application/json
 *   - Los codigos HTTP de error son exactos (404, 400 - nunca 500)
 *   - Ningun campo obligatorio es null
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiContractTest {

    @LocalServerPort
    private int port;

    @org.springframework.beans.factory.annotation.Value("${cors.allowed.origins:http://localhost:5173}")
    private String allowedOrigins;

    @BeforeAll
    static void setUpAll() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Set<String> VALID_SUITS =
        Set.of("Corazones", "Picas", "Diamantes", "Trebol");
    private static final Set<String> VALID_RANKS =
        Set.of("2","3","4","5","6","7","8","9","10","J","Q","K","A");

    private String base()      { return "http://localhost:" + port + "/api/v1/game"; }
    private String scoreBase() { return "http://localhost:" + port + "/api/v1/score"; }

    private JsonNode startSession(String name) throws Exception {
        ResponseEntity<String> r = restTemplate.postForEntity(
            base() + "/start?name=" + name, null, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        return objectMapper.readTree(r.getBody());
    }

    private String playCard(String sid, JsonNode card) {
        return String.format(
            "{\"cards\":[{\"suit\":\"%s\",\"rank\":\"%s\",\"points\":0,\"mult\":1,\"addMult\":0}]}",
            card.get("suit").asText(), card.get("rank").asText());
    }

    // =========================================================
    // CT-01: Contrato raiz de GameSessionDTO
    // =========================================================
    @Test @Order(1)
    @DisplayName("CT-01: POST /start -> GameSessionDTO contiene todos los campos requeridos")
    void contract_StartGame_GameSessionDTO() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
            base() + "/start?name=ContractTester", null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/json"),
            "Content-Type debe ser application/json");

        JsonNode body = objectMapper.readTree(response.getBody());

        assertTrue(body.has("sessionId") && body.get("sessionId").isTextual(), "sessionId debe ser String");
        assertFalse(body.get("sessionId").asText().isEmpty(), "sessionId no puede estar vacio");

        assertTrue(body.has("currentAnte") && body.get("currentAnte").isInt(), "currentAnte debe ser Integer");
        assertTrue(body.get("currentAnte").asInt() >= 1, "currentAnte >= 1");

        assertTrue(body.has("currentBlind") && body.get("currentBlind").isInt(), "currentBlind debe ser Integer");
        int blind = body.get("currentBlind").asInt();
        assertTrue(blind >= 1 && blind <= 3, "currentBlind debe ser 1-3, fue: " + blind);

        assertTrue(body.has("gameOver") && body.get("gameOver").isBoolean(), "gameOver debe ser Boolean");
        assertTrue(body.has("player") && body.get("player").isObject(), "player debe ser Object");
        assertTrue(body.has("round")  && body.get("round").isObject(),  "round debe ser Object");
        assertTrue(body.has("store"),                                    "store debe estar presente");
    }

    // =========================================================
    // CT-02: Contrato de PlayerDTO
    // =========================================================
    @Test @Order(2)
    @DisplayName("CT-02: PlayerDTO tiene name, money, handCards[], jokers[], tarots[]")
    void contract_PlayerDTO_RequiredFields() throws Exception {
        JsonNode player = startSession("PlayerContract").get("player");

        assertTrue(player.has("name") && player.get("name").isTextual(), "player.name debe ser String");
        assertFalse(player.get("name").asText().isEmpty(), "player.name no puede estar vacio");

        assertTrue(player.has("money") && player.get("money").isInt(), "player.money debe ser Integer");
        assertTrue(player.get("money").asInt() >= 0, "player.money debe ser >= 0");

        assertTrue(player.has("handCards") && player.get("handCards").isArray(), "handCards debe ser Array");
        assertTrue(player.get("handCards").size() > 0, "handCards no puede estar vacia al inicio");

        assertTrue(player.has("jokers") && player.get("jokers").isArray(), "jokers debe ser Array");
        assertTrue(player.has("tarots") && player.get("tarots").isArray(), "tarots debe ser Array");
    }

    // =========================================================
    // CT-03: Contrato de CardDTO (cada carta de la mano)
    // =========================================================
    @Test @Order(3)
    @DisplayName("CT-03: CardDTO tiene suit/rank validos y campos numericos correctos")
    void contract_CardDTO_ValidSuitRankAndNumerics() throws Exception {
        JsonNode handCards = startSession("CardContract").get("player").get("handCards");
        assertTrue(handCards.size() > 0, "Debe haber cartas para validar el contrato");

        for (int i = 0; i < handCards.size(); i++) {
            JsonNode card = handCards.get(i);
            String ctx = "handCards[" + i + "]";

            assertTrue(card.has("suit") && card.get("suit").isTextual(), ctx + ".suit debe ser String");
            assertTrue(VALID_SUITS.contains(card.get("suit").asText()),
                ctx + ".suit '" + card.get("suit").asText() + "' no valido. Validos: " + VALID_SUITS);

            assertTrue(card.has("rank") && card.get("rank").isTextual(), ctx + ".rank debe ser String");
            assertTrue(VALID_RANKS.contains(card.get("rank").asText()),
                ctx + ".rank '" + card.get("rank").asText() + "' no valido. Validos: " + VALID_RANKS);

            assertTrue(card.has("points") && card.get("points").isInt(), ctx + ".points debe ser Integer");
            assertTrue(card.get("points").asInt() >= 0, ctx + ".points >= 0");

            // mult es float en el backend -> serializa como numero decimal (ej: 1.0)
            assertTrue(card.has("mult") && card.get("mult").isNumber(), ctx + ".mult debe ser Number (puede ser float)");
            assertTrue(card.has("addMult") && card.get("addMult").isInt(), ctx + ".addMult debe ser Integer");
        }
    }

    // =========================================================
    // CT-04: Contrato de RoundDTO
    // =========================================================
    @Test @Order(4)
    @DisplayName("CT-04: RoundDTO tiene handsLeft, discardsLeft, playerScore, targetScore con rangos validos")
    void contract_RoundDTO_NumericFieldsWithValidRanges() throws Exception {
        JsonNode round = startSession("RoundContract").get("round");

        assertTrue(round.has("handsLeft") && round.get("handsLeft").isInt(), "handsLeft debe ser Integer");
        assertTrue(round.get("handsLeft").asInt() > 0, "handsLeft debe ser > 0");

        assertTrue(round.has("discardsLeft") && round.get("discardsLeft").isInt(), "discardsLeft debe ser Integer");
        assertTrue(round.get("discardsLeft").asInt() >= 0, "discardsLeft >= 0");

        assertTrue(round.has("playerScore") && round.get("playerScore").isInt(), "playerScore debe ser Integer");
        assertTrue(round.get("playerScore").asInt() >= 0, "playerScore >= 0");

        assertTrue(round.has("targetScore") && round.get("targetScore").isInt(), "targetScore debe ser Integer");
        assertTrue(round.get("targetScore").asInt() > 0, "targetScore > 0");
    }

    // =========================================================
    // CT-05: Contrato de StoreDTO
    // =========================================================
    @Test @Order(5)
    @DisplayName("CT-05: StoreDTO tiene jokers[], tarots[], cards[] con shape correcto")
    void contract_StoreDTO_TypedListsWithShape() throws Exception {
        JsonNode store = startSession("StoreContract").get("store");
        assertNotNull(store, "store no debe ser null");
        assertFalse(store.isNull(), "store no debe ser null");

        assertTrue(store.has("jokers") && store.get("jokers").isArray(), "store.jokers debe ser Array");
        assertTrue(store.has("tarots") && store.get("tarots").isArray(), "store.tarots debe ser Array");
        assertTrue(store.has("cards")  && store.get("cards").isArray(),  "store.cards debe ser Array");

        if (store.get("jokers").size() > 0) {
            JsonNode joker = store.get("jokers").get(0);
            assertTrue(joker.has("name") && joker.get("name").isTextual(), "joker.name debe ser String");
            assertTrue(joker.has("description"), "joker.description debe existir");
            assertTrue(joker.has("cost") && joker.get("cost").isInt(), "joker.cost debe ser Integer");
            assertTrue(joker.get("cost").asInt() >= 0, "joker.cost >= 0");
        }

        if (store.get("tarots").size() > 0) {
            JsonNode tarot = store.get("tarots").get(0);
            assertTrue(tarot.has("name") && tarot.get("name").isTextual(), "tarot.name debe ser String");
            assertTrue(tarot.has("description"), "tarot.description debe existir");
            assertTrue(tarot.has("cost") && tarot.get("cost").isInt(), "tarot.cost debe ser Integer");
        }
    }

    // =========================================================
    // CT-06: Contrato de PlayResponseDTO
    // =========================================================
    @Test @Order(6)
    @DisplayName("CT-06: POST /play -> PlayResponseDTO con gameState, handScore y roundWon")
    void contract_PlayHand_PlayResponseDTO() throws Exception {
        JsonNode startBody = startSession("PlayContract");
        String sid    = startBody.get("sessionId").asText();
        JsonNode card = startBody.get("player").get("handCards").get(0);

        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", sid);
        h.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            base() + "/play", HttpMethod.POST, new HttpEntity<>(playCard(sid, card), h), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/json"));

        JsonNode body = objectMapper.readTree(response.getBody());

        // gameState: GameSessionDTO completo
        assertTrue(body.has("gameState") && body.get("gameState").isObject(), "gameState debe ser Object");
        JsonNode gs = body.get("gameState");
        assertTrue(gs.has("sessionId"),    "gameState.sessionId debe existir");
        assertTrue(gs.has("player"),       "gameState.player debe existir");
        assertTrue(gs.has("round"),        "gameState.round debe existir");
        assertTrue(gs.has("gameOver"),     "gameState.gameOver debe existir");
        assertTrue(gs.has("currentAnte"),  "gameState.currentAnte debe existir");
        assertTrue(gs.has("currentBlind"), "gameState.currentBlind debe existir");

        // handScore: con nombre, puntos, mult y total
        assertTrue(body.has("handScore") && body.get("handScore").isObject(), "handScore debe ser Object");
        JsonNode hs = body.get("handScore");
        assertTrue(hs.has("handName") && hs.get("handName").isTextual(), "handScore.handName debe ser String");
        assertFalse(hs.get("handName").asText().isEmpty(), "handScore.handName no puede estar vacio");
        assertTrue(hs.has("totalScore") && hs.get("totalScore").isInt(), "handScore.totalScore debe ser Integer");
        assertTrue(hs.has("points")     && hs.get("points").isInt(),     "handScore.points debe ser Integer");
        // El campo en ScoreResponseDTO se llama 'multiplier' (no 'mult') y es float
        assertTrue(hs.has("multiplier") && hs.get("multiplier").isNumber(), "handScore.multiplier debe ser Number");
        assertTrue(hs.get("multiplier").asDouble() >= 0, "handScore.multiplier debe ser >= 0");
        assertTrue(hs.get("totalScore").asInt() >= 0, "totalScore >= 0");

        // roundWon: Boolean
        assertTrue(body.has("roundWon") && body.get("roundWon").isBoolean(), "roundWon debe ser Boolean");
    }

    // =========================================================
    // CT-07: Contrato de /discard -> GameSessionDTO completo
    // =========================================================
    @Test @Order(7)
    @DisplayName("CT-07: POST /discard -> devuelve GameSessionDTO completo (no subconjunto)")
    void contract_Discard_ReturnsFullGameSessionDTO() throws Exception {
        JsonNode startBody = startSession("DiscardContract");
        String sid    = startBody.get("sessionId").asText();
        JsonNode card = startBody.get("player").get("handCards").get(0);

        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", sid);
        h.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            base() + "/discard", HttpMethod.POST, new HttpEntity<>(playCard(sid, card), h), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode body = objectMapper.readTree(response.getBody());

        assertTrue(body.has("sessionId"),    "/discard debe incluir sessionId");
        assertTrue(body.has("currentAnte"),  "/discard debe incluir currentAnte");
        assertTrue(body.has("currentBlind"), "/discard debe incluir currentBlind");
        assertTrue(body.has("player"),       "/discard debe incluir player");
        assertTrue(body.has("round"),        "/discard debe incluir round");
        assertTrue(body.has("gameOver"),     "/discard debe incluir gameOver");
        assertTrue(body.has("store"),        "/discard debe incluir store");
    }

    // =========================================================
    // CT-08: Contrato de ScoreResponseDTO (/score/calculate)
    // =========================================================
    @Test @Order(8)
    @DisplayName("CT-08: POST /score/calculate -> ScoreResponseDTO con handName, points, mult, totalScore")
    void contract_ScoreCalculate_ScoreResponseDTO() throws Exception {
        String payload = "{\"cards\":[{\"suit\":\"Corazones\",\"rank\":\"As\",\"points\":11,\"mult\":1,\"addMult\":0}]}";
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(payload, h), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/json"));

        JsonNode body = objectMapper.readTree(response.getBody());

        assertTrue(body.has("handName") && body.get("handName").isTextual(), "handName debe ser String");
        assertFalse(body.get("handName").asText().isEmpty(), "handName no puede estar vacio");
        assertTrue(body.has("totalScore") && body.get("totalScore").isInt(), "totalScore debe ser Integer");
        assertTrue(body.get("totalScore").asInt() >= 0, "totalScore >= 0");
        assertTrue(body.has("points") && body.get("points").isInt(), "points debe ser Integer");
        // El multiplicador se serializa como 'multiplier' (float) en ScoreResponseDTO
        assertTrue(body.has("multiplier") && body.get("multiplier").isNumber(), "multiplier debe ser Number");
        assertTrue(body.get("multiplier").asDouble() >= 0, "multiplier debe ser >= 0");
    }

    // =========================================================
    // CT-09: Contrato de /evaluate-hand
    // =========================================================
    @Test @Order(9)
    @DisplayName("CT-09: POST /evaluate-hand -> ScoreResponseDTO con handName y numericos")
    void contract_EvaluateHand_ScoreResponseDTO() throws Exception {
        JsonNode startBody = startSession("EvalContract");
        String sid    = startBody.get("sessionId").asText();
        JsonNode card = startBody.get("player").get("handCards").get(0);

        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", sid); h.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            base() + "/evaluate-hand", HttpMethod.POST, new HttpEntity<>(playCard(sid, card), h), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode body = objectMapper.readTree(response.getBody());

        assertTrue(body.has("handName") && body.get("handName").isTextual(), "handName debe ser String");
        assertTrue(body.has("totalScore") && body.get("totalScore").isInt(), "totalScore debe ser Integer");
        assertTrue(body.has("points")     && body.get("points").isInt(),     "points debe ser Integer");
        assertTrue(body.has("multiplier") && body.get("multiplier").isNumber(), "multiplier debe ser Number");
    }

    // =========================================================
    // CT-10: Contrato de /next-round -> GameSessionDTO con blind avanzado
    // =========================================================
    @Test @Order(10)
    @DisplayName("CT-10: POST /next-round -> GameSessionDTO completo con currentBlind en rango")
    void contract_NextRound_ReturnsFullGameSessionDTO() throws Exception {
        String sid = startSession("NextRoundContract").get("sessionId").asText();
        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", sid);

        ResponseEntity<String> response = restTemplate.exchange(
            base() + "/next-round", HttpMethod.POST, new HttpEntity<>(h), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode body = objectMapper.readTree(response.getBody());

        assertTrue(body.has("sessionId"),    "sessionId debe existir");
        assertTrue(body.has("currentAnte"),  "currentAnte debe existir");
        assertTrue(body.has("currentBlind"), "currentBlind debe existir");
        assertTrue(body.has("player"),       "player debe existir");
        assertTrue(body.has("round"),        "round debe existir");
        assertTrue(body.has("gameOver"),     "gameOver debe existir");
        assertTrue(body.has("store"),        "store debe existir");
        assertTrue(body.get("currentBlind").isInt(), "currentBlind debe ser Integer");
        int newBlind = body.get("currentBlind").asInt();
        assertTrue(newBlind >= 1 && newBlind <= 3, "currentBlind debe ser 1-3, fue: " + newBlind);
        assertTrue(body.get("player").get("handCards").size() > 0, "handCards no debe estar vacia");
    }

    // =========================================================
    // CT-11: Contrato de error HTTP 404 para sesion invalida
    // =========================================================
    @Test @Order(11)
    @DisplayName("CT-11: Sesion invalida -> HTTP 404 en todos los endpoints con sesion")
    void contract_InvalidSession_AlwaysReturns404() {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Session-ID", "sesion-contrato-invalida");
        h.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> r1 = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r1.getStatusCode(), "/state con sesion invalida debe ser 404");

        ResponseEntity<String> r2 = restTemplate.exchange(
            base() + "/play", HttpMethod.POST, new HttpEntity<>("{\"cards\":[]}", h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r2.getStatusCode(), "/play con sesion invalida debe ser 404");

        ResponseEntity<String> r3 = restTemplate.exchange(
            base() + "/discard", HttpMethod.POST, new HttpEntity<>("{\"cards\":[]}", h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r3.getStatusCode(), "/discard con sesion invalida debe ser 404");

        ResponseEntity<String> r4 = restTemplate.exchange(
            base() + "/next-round", HttpMethod.POST, new HttpEntity<>(h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r4.getStatusCode(), "/next-round con sesion invalida debe ser 404");

        ResponseEntity<String> r5 = restTemplate.exchange(
            base() + "/buy", HttpMethod.POST,
            new HttpEntity<>("{\"type\":\"joker\",\"name\":\"X\"}", h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r5.getStatusCode(), "/buy con sesion invalida debe ser 404");

        ResponseEntity<String> r6 = restTemplate.exchange(
            base() + "/use-tarot", HttpMethod.POST,
            new HttpEntity<>("{\"tarotName\":\"El Loco\"}", h), String.class);
        assertEquals(HttpStatus.NOT_FOUND, r6.getStatusCode(), "/use-tarot con sesion invalida debe ser 404");
    }

    // =========================================================
    // CT-12: Contrato de error 400 para datos invalidos
    // =========================================================
    @Test @Order(12)
    @DisplayName("CT-12: /score/calculate con rank invalido -> HTTP 400 exacto (no 500)")
    void contract_InvalidRank_Returns400() {
        String payload = "{\"cards\":[{\"suit\":\"Corazones\",\"rank\":\"INVALIDO\",\"points\":0,\"mult\":1,\"addMult\":0}]}";
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(payload, h), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
            "/score/calculate con rank invalido DEBE ser 400, no " + response.getStatusCode());
    }

    // =========================================================
    // CT-13: Content-Type application/json en todos los endpoints
    // =========================================================
    @Test @Order(13)
    @DisplayName("CT-13: Todos los endpoints exitosos devuelven Content-Type: application/json")
    void contract_AllEndpoints_ReturnJsonContentType() throws Exception {
        ResponseEntity<String> startResp = restTemplate.postForEntity(
            base() + "/start?name=ContentTypeContract", null, String.class);
        assertTrue(startResp.getHeaders().getContentType().toString().contains("application/json"),
            "POST /start debe devolver application/json");

        String sid = objectMapper.readTree(startResp.getBody()).get("sessionId").asText();
        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", sid);

        ResponseEntity<String> stateResp = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(h), String.class);
        assertTrue(stateResp.getHeaders().getContentType().toString().contains("application/json"),
            "GET /state debe devolver application/json");

        HttpHeaders hj = new HttpHeaders(); hj.set("X-Session-ID", sid); hj.setContentType(MediaType.APPLICATION_JSON);
        String payload = "{\"cards\":[{\"suit\":\"Corazones\",\"rank\":\"As\",\"points\":11,\"mult\":1,\"addMult\":0}]}";
        ResponseEntity<String> scoreResp = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(payload, hj), String.class);
        assertTrue(scoreResp.getHeaders().getContentType().toString().contains("application/json"),
            "POST /score/calculate debe devolver application/json");
    }

    // =========================================================
    // CT-14: sessionId es consistente entre /start y /state
    // =========================================================
    @Test @Order(14)
    @DisplayName("CT-14: sessionId es identico en /start y /state para la misma sesion")
    void contract_SessionId_ConsistentAcrossEndpoints() throws Exception {
        JsonNode startBody = startSession("SessionIdContract");
        String sidFromStart = startBody.get("sessionId").asText();

        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", sidFromStart);
        ResponseEntity<String> stateResp = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(h), String.class);

        String sidFromState = objectMapper.readTree(stateResp.getBody()).get("sessionId").asText();
        assertEquals(sidFromStart, sidFromState,
            "sessionId debe ser identico en /start y /state para la misma sesion");
    }

    // =========================================================
    // CT-15: player.name es inmutable entre requests
    // =========================================================
    @Test @Order(15)
    @DisplayName("CT-15: player.name no cambia entre /start y /state (campo inmutable)")
    void contract_PlayerName_IsImmutableAcrossRequests() throws Exception {
        JsonNode startBody = startSession("NombreInmutable");
        String nameFromStart = startBody.get("player").get("name").asText();
        String sid = startBody.get("sessionId").asText();

        HttpHeaders h = new HttpHeaders(); h.set("X-Session-ID", sid);
        JsonNode stateBody = objectMapper.readTree(
            restTemplate.exchange(base() + "/state", HttpMethod.GET,
                new HttpEntity<>(h), String.class).getBody());

        assertEquals(nameFromStart, stateBody.get("player").get("name").asText(),
            "player.name debe ser inmutable entre requests");
        assertEquals("NombreInmutable", nameFromStart,
            "player.name debe ser exactamente el nombre con que se inicio la partida");
    }

    // =========================================================
    // CT-16: Pruebas de Headers de Seguridad (Módulo 2)
    // =========================================================
    @Test @Order(16)
    @DisplayName("CT-16: El servidor incluye headers de seguridad correctos")
    void contract_SecurityHeaders_ArePresent() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            base() + "/start?name=SecHeadersTester", null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        HttpHeaders headers = response.getHeaders();

        // X-Frame-Options
        assertTrue(headers.containsKey("X-Frame-Options"), "Falta header X-Frame-Options");
        assertEquals("DENY", headers.getFirst("X-Frame-Options"), "X-Frame-Options debe ser DENY");

        // X-Content-Type-Options
        assertTrue(headers.containsKey("X-Content-Type-Options"), "Falta header X-Content-Type-Options");
        assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"), "X-Content-Type-Options debe ser nosniff");

        // Cache-Control
        assertTrue(headers.containsKey("Cache-Control"), "Falta header Cache-Control");
        String cacheControl = headers.getFirst("Cache-Control");
        assertTrue(cacheControl.contains("no-cache") || cacheControl.contains("no-store"),
            "Cache-Control debe prohibir almacenamiento en cache, fue: " + cacheControl);

        // Content-Security-Policy
        assertTrue(headers.containsKey("Content-Security-Policy"), "Falta header Content-Security-Policy");
        assertTrue(headers.getFirst("Content-Security-Policy").contains("default-src 'self'"),
            "CSP debe contener default-src 'self'");
    }

    @Test @Order(17)
    @DisplayName("CT-17: CORS headers estan presentes para peticiones GET con Origin")
    void contract_CORSHeaders_AreReturned() {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.set("Origin", allowedOrigins.split(",")[0]);

        // Usamos GET para evitar problemas de HttpURLConnection con metodos OPTIONS
        ResponseEntity<String> response = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(reqHeaders), String.class);

        System.out.println("DEBUG CORS GET - Status: " + response.getStatusCode());
        System.out.println("DEBUG CORS GET - Headers: " + response.getHeaders());

        HttpHeaders respHeaders = response.getHeaders();
        assertTrue(respHeaders.containsKey("Access-Control-Allow-Origin"),
            "Falta el header Access-Control-Allow-Origin en la respuesta GET. Headers recibidos: " + respHeaders);
    }

    // =========================================================
    // CT-18: Robustez - JSON malformado (Módulo 3)
    // =========================================================
    @Test @Order(18)
    @DisplayName("CT-18: POST con JSON malformado devuelve 400 Bad Request y no 500")
    void contract_MalformedJSON_Returns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String malformedJson = "{invalid-json-syntax:[cards:{}";

        ResponseEntity<String> response = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(malformedJson, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
            "JSON malformado debe retornar HTTP 400, no " + response.getStatusCode());
    }

    // =========================================================
    // CT-19: Robustez - Payload vacío (Módulo 3)
    // =========================================================
    @Test @Order(19)
    @DisplayName("CT-19: POST con cuerpo vacio devuelve 400 o error controlado y no 500")
    void contract_EmptyPayload_ReturnsClientError() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String emptyPayload = "";

        ResponseEntity<String> response = restTemplate.exchange(
            scoreBase() + "/calculate", HttpMethod.POST, new HttpEntity<>(emptyPayload, headers), String.class);

        // Puede ser 400 Bad Request o similar, pero NUNCA 500 Internal Server Error
        assertTrue(response.getStatusCode().is4xxClientError(),
            "Payload vacio debe retornar un codigo HTTP 4xx de cliente, fue: " + response.getStatusCode());
    }

    // =========================================================
    // CT-20: Robustez - Intento de Path Traversal en Session ID (Módulo 3)
    // =========================================================
    @Test @Order(20)
    @DisplayName("CT-20: Intento de Path Traversal en Session ID devuelve 404 y no 500")
    void contract_PathTraversalSessionId_Returns404() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Session-ID", "../../../../../etc/passwd");

        ResponseEntity<String> response = restTemplate.exchange(
            base() + "/state", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
            "Session ID con caracteres de path traversal debe retornar 404, no " + response.getStatusCode());
    }
}
