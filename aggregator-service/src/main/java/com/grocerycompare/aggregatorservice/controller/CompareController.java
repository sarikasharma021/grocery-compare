package com.grocerycompare.aggregatorservice.controller;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.service.AggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CompareController {

    private final AggregatorService aggregatorService;

    @GetMapping("/compare")
    public List<ProviderResponse> compare(
            @RequestParam String city,
            @RequestParam String item,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "false") boolean onlyAvailable) {

        return aggregatorService.compare(city, item, sortBy, onlyAvailable);
    }
}