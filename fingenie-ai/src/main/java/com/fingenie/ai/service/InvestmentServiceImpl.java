package com.fingenie.ai.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fingenie.ai.dto.InvestmentRequest;
import com.fingenie.ai.dto.InvestmentResponse;
import com.fingenie.ai.exception.BusinessException;
import com.fingenie.ai.strategy.InvestmentStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentServiceImpl implements InvestmentService {

    private final Map<String, InvestmentStrategy> strategyMap;

    @Override
    public InvestmentResponse getRecommendation(InvestmentRequest request) {

        InvestmentStrategy strategy =
                strategyMap.get(request.getRiskLevel().toUpperCase());

        if (strategy == null) {
            throw new BusinessException("Invalid risk level");
        }

        return strategy.suggest(request);
    }
}
