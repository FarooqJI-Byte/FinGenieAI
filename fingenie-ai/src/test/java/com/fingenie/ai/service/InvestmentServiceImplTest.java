package com.fingenie.ai.service;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.strategy.InvestmentStrategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceImplTest {

    @Mock
    private Map<String, InvestmentStrategy> strategyMap;

    @Mock
    private InvestmentStrategy lowRiskStrategy;

    @InjectMocks
    private InvestmentServiceImpl investmentService;

    // ✅ SUCCESS CASE
    @Test
    void getRecommendation_shouldReturnStrategyResult() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("low");

        InvestmentResponse mockResponse = new InvestmentResponse(
                "Low Risk Plan",
                "Invest in FD and Bonds",
                5.0
        );

        when(strategyMap.get("LOW")).thenReturn(lowRiskStrategy);
        when(lowRiskStrategy.suggest(request)).thenReturn(mockResponse);

        InvestmentResponse response = investmentService.getRecommendation(request);

        assertNotNull(response);
        assertEquals("Low Risk Plan", response.getStrategyName());
        assertEquals("Invest in FD and Bonds", response.getRecommendation());
    }

    // ✅ INVALID RISK LEVEL
    @Test
    void getRecommendation_shouldThrowException_whenInvalidRisk() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("unknown");

        when(strategyMap.get("UNKNOWN")).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> investmentService.getRecommendation(request));
    }

    // ✅ CASE INSENSITIVITY CHECK
    @Test
    void getRecommendation_shouldHandleLowerCaseRisk() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("low");

        when(strategyMap.get("LOW")).thenReturn(lowRiskStrategy);
        when(lowRiskStrategy.suggest(request))
                .thenReturn(new InvestmentResponse("Plan", "Desc", 4.5));

        InvestmentResponse response = investmentService.getRecommendation(request);

        assertNotNull(response);
        verify(strategyMap).get("LOW");
    }

    // ✅ ENSURE STRATEGY CALLED
    @Test
    void getRecommendation_shouldCallStrategySuggest() {

        InvestmentRequest request = new InvestmentRequest();
        request.setRiskLevel("low");

        when(strategyMap.get("LOW")).thenReturn(lowRiskStrategy);

        when(lowRiskStrategy.suggest(request))
                .thenReturn(new InvestmentResponse("Plan", "Desc", 4.5));

        investmentService.getRecommendation(request);

        verify(lowRiskStrategy, times(1)).suggest(request);
    }
}