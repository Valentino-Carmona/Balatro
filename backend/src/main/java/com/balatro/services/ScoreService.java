package com.balatro.services;

import com.balatro.dto.CardDTO;
import com.balatro.dto.PlayHandRequestDTO;
import com.balatro.dto.ScoreResponseDTO;
import com.balatro.modelo.Card;
import com.balatro.modelo.PlayingHand;
import com.balatro.modelo.Rank;
import com.balatro.modelo.Score;
import com.balatro.modelo.Suit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService {

    public ScoreResponseDTO calculateHandScore(PlayHandRequestDTO request) {
        PlayingHand playingHand = new PlayingHand();
        
        // Mapear DTO a Modelo
        List<CardDTO> cardDTOs = request.getCards();
        if (cardDTOs != null) {
            for (CardDTO dto : cardDTOs) {
                Score baseScore = new Score(dto.getPoints(), dto.getMult(), dto.getAddMult());
                Suit suit = Suit.fromString(dto.getSuit());
                Rank rank = Rank.fromString(dto.getRank());
                
                Card card = new Card(baseScore, suit, rank);
                playingHand.addCard(card);
            }
        }

        // Evaluar la mano de poker
        Score finalScore = playingHand.play();
        
        // Formatear respuesta
        String handName = playingHand.getHandString();
        return new ScoreResponseDTO(
            finalScore.getPoints(), 
            finalScore.getMult(), 
            finalScore.getTotalPoints(), 
            handName
        );
    }
}
