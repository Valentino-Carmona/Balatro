package com.balatro.services;

import com.balatro.dto.CardDTO;
import com.balatro.dto.PlayHandRequestDTO;
import com.balatro.dto.ScoreResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreServiceTest {

    private ScoreService scoreService;

    @BeforeEach
    void setUp() {
        scoreService = new ScoreService();
    }

    @Test
    void testCalculateHandScoreWithCards() {
        PlayHandRequestDTO request = new PlayHandRequestDTO();
        CardDTO card1 = new CardDTO();
        card1.setSuit("Corazones");
        card1.setRank("As");
        card1.setPoints(11);
        card1.setMult(1);
        card1.setAddMult(0);

        request.setCards(java.util.List.of(card1));

        ScoreResponseDTO response = scoreService.calculateHandScore(request);

        assertNotNull(response);
        assertEquals("HighCard", response.getHandName());
        assertTrue(response.getTotalScore() > 0);
    }

    @Test
    void testCalculateHandScoreNullCards() {
        PlayHandRequestDTO request = new PlayHandRequestDTO();
        request.setCards(null);

        assertThrows(NullPointerException.class, () -> {
            scoreService.calculateHandScore(request);
        });
    }
}
