package com.grocerycompare.aggregatorservice.service;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregatorService {

    private final List<ProviderService> providers;

    public List<ProviderResponse> compare(String city, String item) {
        return providers.stream()
                .map(provider -> provider.getPrice(city, item))
                .sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
                .toList();
    }
}