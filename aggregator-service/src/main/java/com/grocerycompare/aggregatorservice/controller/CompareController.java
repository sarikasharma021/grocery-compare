package com.grocerycompare.aggregatorservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CompareController {

    @GetMapping("/compare")
    public List<Map<String, Object>> compare(
            @RequestParam String city,
            @RequestParam String item) {

        return List.of(
                Map.of(
                        "provider", "Blinkit",
                        "item", item,
                        "city", city,
                        "price", 68,
                        "available", true,
                        "deliveryMinutes", 10
                ),
                Map.of(
                        "provider", "Zepto",
                        "item", item,
                        "city", city,
                        "price", 72,
                        "available", true,
                        "deliveryMinutes", 8
                ),
                Map.of(
                        "provider", "Instamart",
                        "item", item,
                        "city", city,
                        "price", 65,
                        "available", false,
                        "deliveryMinutes", 15
                ),
                Map.of(
                        "provider", "BigBasket",
                        "item", item,
                        "city", city,
                        "price", 60,
                        "available", true,
                        "deliveryMinutes", 30
                )
        );
    }
}