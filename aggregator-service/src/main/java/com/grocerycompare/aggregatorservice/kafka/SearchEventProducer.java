package com.grocerycompare.aggregatorservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grocerycompare.aggregatorservice.model.SearchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final String TOPIC = "grocery-search-events";

    public void publishSearchEvent(SearchEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getCity() + "-" + event.getItem(), message);
            log.info("Published search event for: {} in {}", event.getItem(), event.getCity());
        } catch (Exception e) {
            log.error("Failed to publish search event: {}", e.getMessage());
        }
    }
}