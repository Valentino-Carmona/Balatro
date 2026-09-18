package com.balatro.controllers;

import com.balatro.dto.CardDTO;
import com.balatro.dto.PlayHandRequestDTO;
import com.balatro.services.ScoreService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ScoreController.class, properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration"})
public class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScoreService scoreService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCalculateScoreSuccess() throws Exception {
        PlayHandRequestDTO req = new PlayHandRequestDTO();
        CardDTO card = new CardDTO();
        card.setSuit("Corazones");
        card.setRank("As");
        req.setCards(java.util.List.of(card));

        Mockito.when(scoreService.calculateHandScore(any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/score/calculate")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testCalculateScoreException() throws Exception {
        Mockito.when(scoreService.calculateHandScore(any())).thenThrow(new IllegalArgumentException("invalid"));

        mockMvc.perform(post("/api/v1/score/calculate")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PlayHandRequestDTO())))
                .andExpect(status().isBadRequest());
    }
}
