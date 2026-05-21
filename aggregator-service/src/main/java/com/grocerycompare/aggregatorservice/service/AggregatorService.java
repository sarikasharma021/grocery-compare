package com.grocerycompare.aggregatorservice.service;

import com.grocerycompare.aggregatorservice.exception.InvalidRequestException;
import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregatorService {

    private final List<ProviderService> providers;

    @Cacheable(value = "compareCache", key = "#city + '-' + #item + '-' + #sortBy + '-' + #onlyAvailable")
    public List<ProviderResponse> compare(String city, String item, String sortBy, boolean onlyAvailable) {

        if (city == null || city.isBlank()) {
            throw new InvalidRequestException("City cannot be empty");
        }

        if (item == null || item.isBlank()) {
            throw new InvalidRequestException("Item cannot be empty");
        }

        System.out.println("Cache MISS — fetching from providers for: " + city + " - " + item);

        return providers.stream()
                .map(provider -> provider.getPrice(city, item))
                .filter(response -> !onlyAvailable || response.isAvailable())
                .sorted(getComparator(sortBy))
                .toList();
    }

    private Comparator<ProviderResponse> getComparator(String sortBy) {
        return switch (sortBy) {
            case "delivery" -> Comparator.comparingInt(ProviderResponse::getDeliveryMinutes);
            case "availability" -> Comparator.comparing(ProviderResponse::isAvailable).reversed();
            default -> Comparator.comparingDouble(ProviderResponse::getPrice);
        };
    }
}