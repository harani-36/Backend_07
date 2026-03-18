package com.train.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrainFareResponse {

    private Long trainId;
    private String name;
    private String source;
    private String destination;
    private String coachType;
    private Double fareAmount;
}