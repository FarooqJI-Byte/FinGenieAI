package com.fingenie.ai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;
import com.fingenie.ai.strategy.InvestmentStrategy;

/**
 * ✅ Unit Test for InvestmentServiceImpl
 */
class InvestmentServiceImplTest {

    private InvestmentServiceImpl investmentService;

    private Map<String, InvestmentStrategy> strategyMap;

    private InvestmentStrategy lowRiskStrategy;
    private InvestmentStrategy highRiskStrategy;

    @BeforeEach
    void setup() {

        // ✅ Mock strategies
        lowRiskStrategy = mock(InvestmentStrategy.class);
        highRiskStrategy = mock(InvestmentStrategy.class);

        // ✅ Strategy Map
        strategyMap = new HashMap<>();
        strategyMap.put("LOW", lowRiskStrategy);
        strategyMap.put("HIGH", highRiskStrategy);

        investmentService = new InvestmentServiceImpl(strategyMap);
    }

    /**
     * ✅ TEST: LOW RISK SUCCESS
     */
    @Test
    void getRecommendation_lowRisk_success() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("low");

        // ✅ Use BUILDER (IMPORTANT FIX)
        InvestmentResponse mockResponse = InvestmentResponse.builder()
                .strategyName("FD Strategy")
                .recommendation("Invest in Fixed Deposits")
                .expectedReturn(6.5)
                .build();

        when(lowRiskStrategy.suggest(request))
                .thenReturn(mockResponse);

        InvestmentResponse response = investmentService.getRecommendation(request);

        assertNotNull(response);
        assertEquals("FD Strategy", response.getStrategyName());
        assertEquals("Invest in Fixed Deposits", response.getRecommendation());
        assertEquals(6.5, response.getExpectedReturn());

        // ✅ Verify correct strategy used
        verify(lowRiskStrategy).suggest(request);
        verify(highRiskStrategy, never()).suggest(any());
    }

    /**
     * ✅ TEST: HIGH RISK SUCCESS
     */
    @Test
    void getRecommendation_highRisk_success() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("HIGH");

        InvestmentResponse mockResponse = InvestmentResponse.builder()
                .strategyName("Stock Strategy")
                .recommendation("Invest in equities")
                .expectedReturn(15.0)
                .build();

        when(highRiskStrategy.suggest(request))
                .thenReturn(mockResponse);

        InvestmentResponse response = investmentService.getRecommendation(request);

        assertEquals("Stock Strategy", response.getStrategyName());

        verify(highRiskStrategy).suggest(request);
        verify(lowRiskStrategy, never()).suggest(any());
    }

    /**
     * ❌ TEST: INVALID RISK LEVEL
     */
    @Test
    void getRecommendation_shouldThrow_whenInvalidRisk() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("MEDIUM"); // not in map

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> investmentService.getRecommendation(request));

        assertEquals("Invalid risk level", ex.getMessage());
    }

    /**
     * ❌ TEST: NULL RISK LEVEL
     */
    @Test
    void getRecommendation_shouldThrow_whenRiskNull() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel(null); // causes toUpperCase crash

        assertThrows(NullPointerException.class,
                () -> investmentService.getRecommendation(request));
    }
}
