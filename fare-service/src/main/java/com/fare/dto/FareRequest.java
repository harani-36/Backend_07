package com.fare.dto;

import com.common.enums.CoachType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FareRequest {
    private Long trainId;
    private CoachType coachType;
    private Double baseFare;
}
