package com.fare.dto;

import com.common.enums.CoachType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FareResponse {
    private Long trainId;
    private CoachType coachType;
    private Double baseFare;
    private Double finalFare;

    public FareResponse(Long trainId, CoachType coachType, Double baseFare, Double finalFare) {
        this.trainId = trainId;
        this.coachType = coachType;
        this.baseFare = baseFare;
        this.finalFare = finalFare;
    }
}
