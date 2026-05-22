package com.grocerycompare.alertservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grocerycompare.alertservice.model.SearchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchEventConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @KafkaListener(topics = "grocery-search-events", groupId = "alert-service-group")
    public void consume(String message) {
        try {
            SearchEvent event = objectMapper.readValue(message, SearchEvent.class);
            log.info("Received search event — item: {} city: {} searchedAt: {}",
                    event.getItem(),
                    event.getCity(),
                    event.getSearchedAt());
        } catch (Exception e) {
            log.error("Failed to process search event: {}", e.getMessage());
        }
    }
}