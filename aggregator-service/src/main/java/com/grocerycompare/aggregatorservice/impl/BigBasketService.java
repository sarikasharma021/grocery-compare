package com.grocerycompare.aggregatorservice.service.impl;

import com.grocerycompare.aggregatorservice.model.ProviderResponse;
import com.grocerycompare.aggregatorservice.service.ProviderService;
import org.springframework.stereotype.Service;

@Service
public class BigBasketService implements ProviderService {

    @Override
    public ProviderResponse getPrice(String city, String item) {
        return ProviderResponse.builder()
                .provider(getProviderName())
                .item(item)
                .city(city)
                .price(60.0)
                .available(true)
                .deliveryMinutes(30)
                .build();
    }

    @Override
    public String getProviderName() {
        return "BigBasket";
    }
}