package com.fare.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.common.enums.CoachType;
import com.fare.dto.FareRequest;
import com.fare.dto.FareResponse;
import com.fare.entity.Fare;
import com.fare.exception.ResourceNotFoundException;
import com.fare.repository.FareRepository;
import com.fare.service.DynamicPricingService;
import com.fare.service.FareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FareServiceImpl implements FareService {

    private final FareRepository fareRepository;
    private final DynamicPricingService dynamicPricingService;
    private final RestTemplate restTemplate;

    @Override
    public FareResponse addFare(FareRequest request) {
        Fare fare = new Fare();
        fare.setTrainId(request.getTrainId());
        fare.setCoachType(request.getCoachType());
        fare.setBaseFare(request.getBaseFare());
        Fare saved = fareRepository.save(fare);
        log.info("Fare saved for train {}, coach {}, baseFare {}", saved.getTrainId(), saved.getCoachType(), saved.getBaseFare());
        return new FareResponse(saved.getTrainId(), saved.getCoachType(), saved.getBaseFare(), saved.getBaseFare());
    }

    @Override
    public FareResponse getFare(Long trainId, CoachType coachType) {
        return getFare(trainId, coachType, LocalDate.now());
    }

    @Override
    public FareResponse getFare(Long trainId, CoachType coachType, LocalDate journeyDate) {
        Fare fare = fareRepository.findByTrainIdAndCoachType(trainId, coachType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fare not found for train " + trainId + " and coach type " + coachType));

        int availableSeats = fetchAvailableSeats(trainId, coachType, journeyDate);
        int totalSeats = fetchTotalSeats(trainId, coachType);
        int daysUntilJourney = (int) ChronoUnit.DAYS.between(LocalDate.now(), journeyDate);

        double multiplier = dynamicPricingService.calculateMultiplier(availableSeats, totalSeats, daysUntilJourney);
        double finalFare = Math.round(fare.getBaseFare() * multiplier * 100.0) / 100.0;

        log.info("Fare for train {}, coach {}: base={}, available={}/{}, days={}, multiplier={}, final={}",
                trainId, coachType, fare.getBaseFare(), availableSeats, totalSeats, daysUntilJourney, multiplier, finalFare);

        return new FareResponse(fare.getTrainId(), fare.getCoachType(), fare.getBaseFare(), finalFare);
    }

    @Override
    public List<CoachType> getAvailableCoachTypes(Long trainId) {
        return fareRepository.findCoachTypesByTrainId(trainId);
    }

    private int fetchAvailableSeats(Long trainId, CoachType coachType, LocalDate journeyDate) {
        try {
            String url = "http://TRAIN-SERVICE/trains/seats/available?trainId=" + trainId
                    + "&coachType=" + coachType.name() + "&date=" + journeyDate;
            Integer seats = restTemplate.getForObject(url, Integer.class);
            return seats != null ? seats : 50;
        } catch (Exception e) {
            log.warn("Could not fetch available seats for train {}, coach {}: {}", trainId, coachType, e.getMessage());
            return 50;
        }
    }

    private int fetchTotalSeats(Long trainId, CoachType coachType) {
        try {
            String url = "http://TRAIN-SERVICE/trains/coaches/total-seats?trainId=" + trainId
                    + "&coachType=" + coachType.name();
            Integer seats = restTemplate.getForObject(url, Integer.class);
            return seats != null ? seats : 72;
        } catch (Exception e) {
            log.warn("Could not fetch total seats for train {}, coach {}: {}", trainId, coachType, e.getMessage());
            return 72;
        }
    }
}
