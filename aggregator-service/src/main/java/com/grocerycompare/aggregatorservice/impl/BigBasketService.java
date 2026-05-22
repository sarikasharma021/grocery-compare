package com.grocerycompare.aggregatorservice.impl;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.service.ProviderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BigBasketService implements ProviderService {

    @Override
    @CircuitBreaker(name = "providerService", fallbackMethod = "fallback")
    public ProviderResponse getPrice(String city, String item) {
        return ProviderResponse.builder()
                .provider(getProviderName())
                .item(item)
                .city(city)
                .price(68.0)
                .available(true)
                .deliveryMinutes(10)
                .build();
    }

    public ProviderResponse fallback(String city, String item, Throwable t) {
        log.warn("Bigbasketservice circuit breaker triggered: {}", t.getMessage());
        return null;
    }

    @Override
    public String getProviderName() {
        return "BigBasketService";
    }
}