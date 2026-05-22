package com.grocerycompare.aggregatorservice.service;

import com.grocerycompare.aggregatorservice.exception.InvalidRequestException;
import com.grocerycompare.aggregatorservice.kafka.SearchEventProducer;
import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.model.SearchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
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

        log.info("Cache MISS — fetching concurrently from {} providers for: {} in {}",
                providers.size(), item, city);

        publishSearchEvent(city, item, sortBy, onlyAvailable);

        List<CompletableFuture<ProviderResponse>> futures = providers.stream()
                .map(provider -> CompletableFuture
                        .supplyAsync(() -> provider.getPrice(city, item))
                        .orTimeout(3, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.warn("Provider {} timed out or failed: {}",
                                    provider.getProviderName(), ex.getMessage());
                            return null;
                        }))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(response -> response != null)
                .filter(response -> !onlyAvailable || response.isAvailable())
                .sorted(getComparator(sortBy))
                .toList();
    }

    private void publishSearchEvent(String city, String item, String sortBy, boolean onlyAvailable) {
        SearchEvent event = SearchEvent.builder()
                .city(city)
                .item(item)
                .sortBy(sortBy)
                .onlyAvailable(onlyAvailable)
                .searchedAt(LocalDateTime.now())
                .build();
        searchEventProducer.publishSearchEvent(event);
    }

    private Comparator<ProviderResponse> getComparator(String sortBy) {
        return switch (sortBy) {
            case "delivery" -> Comparator.comparingInt(ProviderResponse::getDeliveryMinutes);
            case "availability" -> Comparator.comparing(ProviderResponse::isAvailable).reversed();
            default -> Comparator.comparingDouble(ProviderResponse::getPrice);
        };
    }
}