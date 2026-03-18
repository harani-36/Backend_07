package com.train.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FareResponse {
    private Long trainId;
    private String coachType;
    private Double amount;
}