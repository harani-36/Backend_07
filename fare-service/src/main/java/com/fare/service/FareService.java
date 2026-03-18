package com.fare.service;

import java.time.LocalDate;
import java.util.List;

import com.common.enums.CoachType;
import com.fare.dto.FareRequest;
import com.fare.dto.FareResponse;

public interface FareService {
    FareResponse addFare(FareRequest request);
    FareResponse getFare(Long trainId, CoachType coachType);
    FareResponse getFare(Long trainId, CoachType coachType, LocalDate journeyDate);
    List<CoachType> getAvailableCoachTypes(Long trainId);
}
