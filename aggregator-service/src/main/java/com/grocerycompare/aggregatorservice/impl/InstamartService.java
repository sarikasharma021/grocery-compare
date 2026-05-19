package com.grocerycompare.aggregatorservice.service.impl;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.service.ProviderService;
import org.springframework.stereotype.Service;

@Service
public class InstamartService implements ProviderService {

    @Override
    public ProviderResponse getPrice(String city, String item) {
        return ProviderResponse.builder()
                .provider(getProviderName())
                .item(item)
                .city(city)
                .price(65.0)
                .available(false)
                .deliveryMinutes(15)
                .build();
    }

    @Override
    public String getProviderName() {
        return "Instamart";
    }
}