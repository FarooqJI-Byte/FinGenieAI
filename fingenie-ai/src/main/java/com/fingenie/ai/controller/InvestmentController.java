package com.fingenie.ai.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fingenie.ai.dto.ApiResponse;
import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;
import com.fingenie.ai.service.InvestmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origin}")
@RequestMapping("/ai/invest")
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvestmentResponse>> getRecommendation(
            @Valid @RequestBody InvestmentRequest request) {

        InvestmentResponse response =
                investmentService.getRecommendation(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Investment recommendation generated",
                        response,
                        LocalDateTime.now()
                )
        );
    }
}
