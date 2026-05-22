package com.grocerycompare.alertservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEvent {
    private String city;
    private String item;
    private String sortBy;
    private boolean onlyAvailable;
    private LocalDateTime searchedAt;
}