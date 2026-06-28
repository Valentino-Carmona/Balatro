package com.balatro.controllers;

import com.balatro.dto.*;
import com.balatro.modelo.*;
import com.balatro.services.GameManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    @Autowired
    private GameManager gameManager;

    @PostMapping("/start")
    public ResponseEntity<GameSessionDTO> startGame(@RequestParam(defaultValue = "Player") String name) {
        GameSession session = gameManager.createGame(name);
        return ResponseEntity.ok(mapToDTO(session));
    }

    @GetMapping("/state")
    public ResponseEntity<GameSessionDTO> getState(@RequestHeader("X-Session-ID") String sessionId) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/play")
    public ResponseEntity<PlayResponseDTO> playHand(@RequestHeader("X-Session-ID") String sessionId, @RequestBody PlayHandRequestDTO request) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        Player player = session.getPlayer();
        Round round = session.getRound();

        // 1. Deseleccionar todo en playingHand por las dudas
        player.getPlayingHand().discard(); // Limpia la mano actual sin contar como descarte de juego
        
        // 2. Seleccionar las cartas a jugar
        for (CardDTO cDTO : request.getCards()) {
            // Find card in hand
            for (Card c : player.getMainCards()) {
                if (c.getSuit().equals(cDTO.getSuit()) && c.getRankName().equals(cDTO.getRank())) {
                    player.selectCardToHand(c);
                    break;
                }
            }
        }

        // 3. Jugar mano (aplica cartas, jokers, suma al round, reduce manos, y remueve cartas)
        // Guardamos el nombre de la mano antes de descartarla internamente
        String handName = player.getPlayingHand().getHandString();
        Score scoreObj = player.playHand(round);

        // 4. Armar el ScoreResponseDTO con la traza completa
        ScoreResponseDTO scoreResponse = new ScoreResponseDTO(
            scoreObj.getPoints(), 
            scoreObj.getMult(), 
            scoreObj.getTotalPoints(), 
            handName,
            scoreObj.getEvents()
        );

        // 5. Verificar estado de la ronda
        boolean roundWon = round.verifyStatePlayer();
        if (roundWon) {
            player.addMoney(4); // Recompensa base
            session.setGameOver(false);
        } else if (round.getHandsLeft() <= 0) {
            session.setGameOver(true);
        }

        // 6. Rellenar la mano (si la partida sigue)
        gameManager.refillPlayerHand(session);

        PlayResponseDTO response = new PlayResponseDTO();
        response.setGameState(mapToDTO(session));
        response.setHandScore(scoreResponse);
        response.setRoundWon(roundWon);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/discard")
    public ResponseEntity<GameSessionDTO> discardCards(@RequestHeader("X-Session-ID") String sessionId, @RequestBody PlayHandRequestDTO request) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        Player player = session.getPlayer();
        Round round = session.getRound();

        player.getPlayingHand().discard(); 
        
        for (CardDTO cDTO : request.getCards()) {
            for (Card c : player.getMainCards()) {
                if (c.getSuit().equals(cDTO.getSuit()) && c.getRankName().equals(cDTO.getRank())) {
                    player.selectCardToHand(c);
                    break;
                }
            }
        }

        player.discardHand(round);
        gameManager.refillPlayerHand(session);

        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/next-round")
    public ResponseEntity<GameSessionDTO> nextRound(@RequestHeader("X-Session-ID") String sessionId) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        // Advance blind/ante based on round index mapping
        session.setRoundIndex(session.getRoundIndex() + 1);
        
        if (session.getCurrentBlind() == 3) {
            session.setCurrentBlind(1);
            session.setCurrentAnte(session.getCurrentAnte() + 1);
        } else {
            session.setCurrentBlind(session.getCurrentBlind() + 1);
        }

        gameManager.setupNextRound(session);
        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/buy")
    public ResponseEntity<GameSessionDTO> buyItem(@RequestHeader("X-Session-ID") String sessionId, @RequestBody BuyRequestDTO request) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        Player player = session.getPlayer();
        Store store = session.getStore();
        
        // Buy Logic - MVP assumes cost of 4 for everything since the JSON doesn't define costs directly in some formats
        int cost = 4;
        if (player.getMoney() < cost) {
            return ResponseEntity.badRequest().build(); // Not enough money
        }

        if (request.getType().equals("joker")) {
            JokerApply targetJoker = null;
            for (JokerApply j : store.getJokerList()) {
                if (j.getNombre().equals(request.getName())) {
                    targetJoker = j;
                    break;
                }
            }
            if (targetJoker != null) {
                player.spendMoney(cost);
                player.addJoker(targetJoker);
                store.getJokerList().remove(targetJoker);
            }
        } else if (request.getType().equals("tarot")) {
            TarotApply targetTarot = null;
            for (TarotApply t : store.getTarots()) {
                if (t.getName().equals(request.getName())) {
                    targetTarot = t;
                    break;
                }
            }
            if (targetTarot != null) {
                player.spendMoney(cost);
                player.addTarot(targetTarot);
                store.getTarots().remove(targetTarot);
            }
        } else if (request.getType().equals("card")) {
            Card targetCard = null;
            for (Card c : store.getCards()) {
                if (c.getRankName().equals(request.getName()) || c.getSuit().equals(request.getName())) { // simplistic matching for now
                    targetCard = c;
                    break;
                }
            }
            if (targetCard != null) {
                player.spendMoney(cost);
                player.reciveCards(java.util.List.of(targetCard));
                store.getCards().remove(targetCard);
            }
        }

        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/reorder-jokers")
    public ResponseEntity<GameSessionDTO> reorderJokers(@RequestHeader("X-Session-ID") String sessionId, @RequestBody ReorderJokerRequestDTO request) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();
        
        session.getPlayer().reorderJoker(request.getJokerName(), request.getDirection());
        
        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/remove-joker")
    public ResponseEntity<GameSessionDTO> removeJoker(@RequestHeader("X-Session-ID") String sessionId, @RequestBody java.util.Map<String, String> body) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        String jokerName = body.get("name");
        JokerApply target = null;
        for (JokerApply j : session.getPlayer().getJokers()) {
            if (j.getNombre().equals(jokerName)) {
                target = j;
                break;
            }
        }
        if (target != null) {
            session.getPlayer().removeJoker(target);
        }
        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/remove-tarot")
    public ResponseEntity<GameSessionDTO> removeTarot(@RequestHeader("X-Session-ID") String sessionId, @RequestBody java.util.Map<String, String> body) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        String tarotName = body.get("name");
        TarotApply target = null;
        for (TarotApply t : session.getPlayer().getTarots()) {
            if (t.getName().equals(tarotName)) {
                target = t;
                break;
            }
        }
        if (target != null) {
            session.getPlayer().removeTarot(target);
        }
        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/use-tarot")
    public ResponseEntity<GameSessionDTO> useTarot(@RequestHeader("X-Session-ID") String sessionId, @RequestBody UseTarotRequestDTO request) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        Player player = session.getPlayer();
        TarotApply targetTarot = null;
        for (TarotApply t : player.getTarots()) {
            if (t.getName().equals(request.getTarotName())) {
                targetTarot = t;
                break;
            }
        }
        
        if (targetTarot != null) {
            // First, select the cards if it requires cards
            if (request.getTargetCardNames() != null && !request.getTargetCardNames().isEmpty()) {
                for (String cardName : request.getTargetCardNames()) {
                    for (Card c : player.getMainCards()) {
                        String fullName = c.getRankName() + " de " + c.getSuit();
                        if (fullName.equals(cardName)) {
                            player.selectCardToHand(c);
                            break;
                        }
                    }
                }
            }
            
            try {
                player.useTarot(targetTarot);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().build();
            }
            
            // Unselect cards back to main hand
            if (request.getTargetCardNames() != null && !request.getTargetCardNames().isEmpty()) {
                // To avoid concurrent modification, just clear the playing hand by unselecting all
                List<Card> currentlySelected = new ArrayList<>(player.getPlayingHand().getCards());
                for (Card c : currentlySelected) {
                    player.unselectCardToHand(c);
                }
            }
        }
        
        return ResponseEntity.ok(mapToDTO(session));
    }

    @PostMapping("/evaluate-hand")
    public ResponseEntity<ScoreResponseDTO> evaluateHand(@RequestHeader("X-Session-ID") String sessionId, @RequestBody PlayHandRequestDTO request) {
        GameSession session = gameManager.getSession(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        Player player = session.getPlayer();

        // Creamos una mano temporal para evaluarla
        PlayingHand tempHand = new PlayingHand();
        for (CardDTO cDTO : request.getCards()) {
            for (Card c : player.getMainCards()) {
                if (c.getSuit().equals(cDTO.getSuit()) && c.getRankName().equals(cDTO.getRank())) {
                    tempHand.addCard(c);
                    break;
                }
            }
        }

        // Evaluar la base (sin comodines, porque es el preview)
        Score scoreObj = tempHand.play();
        String handName = tempHand.isHandNull() ? "" : tempHand.getHandString();

        ScoreResponseDTO scoreResponse = new ScoreResponseDTO(
            scoreObj.getPoints(), 
            scoreObj.getMult(), 
            scoreObj.getTotalPoints(), 
            handName
        );

        return ResponseEntity.ok(scoreResponse);
    }

    private GameSessionDTO mapToDTO(GameSession session) {
        GameSessionDTO dto = new GameSessionDTO();
        dto.setSessionId(session.getId());
        dto.setCurrentAnte(session.getCurrentAnte());
        dto.setCurrentBlind(session.getCurrentBlind());
        dto.setGameOver(session.isGameOver());

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName(session.getPlayer().getName());
        playerDTO.setMoney(session.getPlayer().getMoney());
        List<CardDTO> handCards = new ArrayList<>();
        for (Card c : session.getPlayer().getMainCards()) {
            handCards.add(new CardDTO(c.getSuit(), c.getRankName(), c.getPoints().getPoints(), c.getPoints().getMult(), c.getPoints().getAddMult()));
        }
        playerDTO.setHandCards(handCards);
        List<JokerDTO> pJokers = new ArrayList<>();
        for (JokerApply j : session.getPlayer().getJokers()) {
            JokerDTO jDTO = new JokerDTO();
            jDTO.setName(j.getNombre());
            jDTO.setDescription(j.getDescripcion());
            jDTO.setCost(4);
            pJokers.add(jDTO);
        }
        playerDTO.setJokers(pJokers);

        List<TarotDTO> pTarots = new ArrayList<>();
        for (TarotApply t : session.getPlayer().getTarots()) {
            TarotDTO tDTO = new TarotDTO();
            tDTO.setName(t.getName());
            tDTO.setDescription(t.getDescription());
            tDTO.setCost(4);
            pTarots.add(tDTO);
        }
        playerDTO.setTarots(pTarots);

        dto.setPlayer(playerDTO);

        RoundDTO roundDTO = new RoundDTO();
        if (session.getRound() != null) {
            roundDTO.setHandsLeft(session.getRound().getHandsLeft());
            roundDTO.setDiscardsLeft(session.getRound().getDiscardsLeft());
            roundDTO.setPlayerScore(session.getRound().getPlayerScore());
            roundDTO.setTargetScore(session.getRound().getTargetScore());
        }
        dto.setRound(roundDTO);

        StoreDTO storeDTO = new StoreDTO();
        if (session.getStore() != null) {
            List<JokerDTO> jokers = new ArrayList<>();
            for (JokerApply j : session.getStore().getJokerList()) {
                JokerDTO jDTO = new JokerDTO();
                jDTO.setName(j.getNombre());
                jDTO.setDescription(j.getDescripcion());
                jDTO.setCost(4);
                jokers.add(jDTO);
            }
            storeDTO.setJokers(jokers);

            List<TarotDTO> tarots = new ArrayList<>();
            for (TarotApply t : session.getStore().getTarots()) {
                TarotDTO tDTO = new TarotDTO();
                tDTO.setName(t.getName());
                tDTO.setDescription(t.getDescription());
                tDTO.setCost(4);
                tarots.add(tDTO);
            }
            storeDTO.setTarots(tarots);

            List<CardDTO> cards = new ArrayList<>();
            for (Card c : session.getStore().getCards()) {
                CardDTO cDTO = new CardDTO(c.getSuit(), c.getRankName(), c.getPoints().getPoints(), c.getPoints().getMult(), c.getPoints().getAddMult());
                cards.add(cDTO);
            }
            storeDTO.setCards(cards);
        }
        dto.setStore(storeDTO);

        return dto;
    }
}
