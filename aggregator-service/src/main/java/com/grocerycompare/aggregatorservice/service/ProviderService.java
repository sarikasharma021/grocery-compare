package com.grocerycompare.aggregatorservice.service;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;

public interface ProviderService {
    ProviderResponse getPrice(String city, String item);
    String getProviderName();
}