package com.balatro.controllers;

import com.balatro.dto.PlayHandRequestDTO;
import com.balatro.dto.ScoreResponseDTO;
import com.balatro.services.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/score")
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ScoreResponseDTO> calculateScore(@RequestBody PlayHandRequestDTO request) {
        try {
            ScoreResponseDTO response = scoreService.calculateHandScore(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
