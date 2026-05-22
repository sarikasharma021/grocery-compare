package com.grocerycompare.aggregatorservice.service;

import com.grocerycompare.aggregatorservice.exception.InvalidRequestException;
import com.grocerycompare.aggregatorservice.kafka.SearchEventProducer;
import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.model.SearchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregatorService {

    private final List<ProviderService> providers;
    private final SearchEventProducer searchEventProducer;

    @Cacheable(value = "compareCache", key = "#city + '-' + #item + '-' + #sortBy + '-' + #onlyAvailable")
    public List<ProviderResponse> compare(String city, String item, String sortBy, boolean onlyAvailable) {

        if (city == null || city.isBlank()) {
            throw new InvalidRequestException("City cannot be empty");
        }

        if (item == null || item.isBlank()) {
            throw new InvalidRequestException("Item cannot be empty");
        }

        System.out.println("Cache MISS — fetching from providers for: " + city + " - " + item);

        SearchEvent event = SearchEvent.builder()
                .city(city)
                .item(item)
                .sortBy(sortBy)
                .onlyAvailable(onlyAvailable)
                .searchedAt(LocalDateTime.now())
                .build();

        searchEventProducer.publishSearchEvent(event);

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