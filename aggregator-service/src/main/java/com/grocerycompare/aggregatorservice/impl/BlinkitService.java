package com.grocerycompare.aggregatorservice.impl;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.service.ProviderService;
import org.springframework.stereotype.Service;

@Service
public class BlinkitService implements ProviderService {

    @Override
    public ProviderResponse getPrice(String city, String item) {
        return ProviderResponse.builder()
                .provider(getProviderName())
                .item(item)
                .city(city)
                .price(68.0)
                .available(true)
                .deliveryMinutes(10)
                .build();
    }

    @Override
    public String getProviderName() {
        return "Blinkit";
    }
}