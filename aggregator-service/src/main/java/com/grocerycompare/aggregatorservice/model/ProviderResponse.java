package com.grocerycompare.aggregatorservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponse implements Serializable {
    private String provider;
    private String item;
    private String city;
    private double price;
    private boolean available;
    private int deliveryMinutes;
}