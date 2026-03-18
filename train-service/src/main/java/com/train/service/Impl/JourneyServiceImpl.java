package com.train.service.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.train.dto.CoachConfigRequest;
import com.train.dto.JourneyRequest;
import com.train.dto.JourneyResponse;
import com.train.entity.Coach;
import com.train.entity.Journey;
import com.train.entity.Seat;
import com.train.entity.SeatStatus;
import com.train.entity.Train;
import com.train.exception.ResourceNotFoundException;
import com.train.repository.CoachRepository;
import com.train.repository.JourneyRepository;
import com.train.repository.SeatRepository;
import com.train.repository.TrainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JourneyServiceImpl implements com.train.service.JourneyService {

    private static final Logger log = LoggerFactory.getLogger(JourneyServiceImpl.class);
    private final JourneyRepository journeyRepository;
    private final TrainRepository trainRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public JourneyResponse createJourney(JourneyRequest request) {
        log.debug("Creating journey for train ID: {} on date: {}", request.getTrainId(), request.getJourneyDate());

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + request.getTrainId()));

        // Check for time conflicts on the same date
        List<Journey> existingJourneys = journeyRepository.findByTrainIdAndJourneyDate(
                request.getTrainId(), request.getJourneyDate());
        for (Journey existing : existingJourneys) {
            boolean overlaps = !(request.getArrivalTime().isBefore(existing.getDepartureTime()) ||
                                 request.getDepartureTime().isAfter(existing.getArrivalTime()));
            if (overlaps) {
                throw new IllegalArgumentException("Journey time conflict: Train is occupied from "
                        + existing.getDepartureTime() + " to " + existing.getArrivalTime());
            }
        }

        Journey journey = new Journey();
        journey.setTrain(train);
        journey.setJourneyDate(request.getJourneyDate());
        journey.setDepartureTime(request.getDepartureTime());
        journey.setArrivalTime(request.getArrivalTime());
        Journey saved = journeyRepository.save(journey);

        // FIXED: coaches belong to train; seats are generated per journey
        // Use existing train coaches or create from config if provided
        List<Coach> coaches;
        if (request.getCoachConfigs() != null && !request.getCoachConfigs().isEmpty()) {
            coaches = createCoachesForTrain(train, request.getCoachConfigs());
        } else {
            coaches = coachRepository.findByTrain_Id(train.getId());
        }

        // Generate seats for each coach scoped to this journey
        int totalSeats = generateSeatsForJourney(coaches, saved.getId());
        log.info("Journey created with ID: {}, Total seats generated: {}", saved.getId(), totalSeats);

        return mapToResponse(saved, totalSeats, totalSeats);
    }

    // Creates and persists coaches linked to the train (not journey)
    private List<Coach> createCoachesForTrain(Train train, List<CoachConfigRequest> configs) {
        List<Coach> coaches = new ArrayList<>();
        Map<String, Integer> coachCounters = new HashMap<>();

        for (CoachConfigRequest config : configs) {
            String coachType = config.getCoachType();
            for (int i = 1; i <= config.getNumberOfCoaches(); i++) {
                int num = coachCounters.getOrDefault(coachType, 0) + 1;
                coachCounters.put(coachType, num);

                Coach coach = new Coach();
                coach.setCoachNumber(coachType + num);
                coach.setCoachType(coachType);
                coach.setTotalSeats(config.getSeatsPerCoach());
                coach.setTrain(train);  // FIXED: linked to train
                coaches.add(coachRepository.save(coach));
            }
        }
        return coaches;
    }

    // Generates AVAILABLE seats for each coach scoped to a specific journey
    private int generateSeatsForJourney(List<Coach> coaches, Long journeyId) {
        int total = 0;
        for (Coach coach : coaches) {
            for (int i = 1; i <= coach.getTotalSeats(); i++) {
                Seat seat = new Seat();
                seat.setSeatNumber(String.format("%s-%02d", coach.getCoachNumber(), i));
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setCoach(coach);
                seat.setJourneyId(journeyId);  // FIXED: seat availability per journey
                seatRepository.save(seat);
                total++;
            }
        }
        return total;
    }

    @Override
    public List<JourneyResponse> getAllJourneys() {
        log.debug("Fetching all journeys");
        return journeyRepository.findAll().stream()
                .map(j -> {
                    int[] counts = getSeatCounts(j.getId());
                    return mapToResponse(j, counts[0], counts[1]);
                })
                .toList();
    }

    @Override
    public JourneyResponse getJourneyById(Long id) {
        log.debug("Fetching journey with ID: {}", id);
        Journey journey = journeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + id));
        int[] counts = getSeatCounts(id);
        return mapToResponse(journey, counts[0], counts[1]);
    }

    @Override
    public List<JourneyResponse> getJourneysByDate(LocalDate date) {
        log.debug("Fetching journeys for date: {}", date);
        return journeyRepository.findByJourneyDate(date).stream()
                .map(j -> {
                    int[] counts = getSeatCounts(j.getId());
                    return mapToResponse(j, counts[0], counts[1]);
                })
                .toList();
    }

    @Override
    @Transactional
    public void cancelJourney(Long journeyId) {
        log.debug("Cancelling journey with ID: {}", journeyId);
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + journeyId));

        // FIXED: count booked seats via seatRepository scoped to journeyId
        long bookedSeats = seatRepository.countByJourneyIdAndStatus(journeyId, SeatStatus.BOOKED);
        if (bookedSeats > 0) {
            throw new IllegalStateException("Cannot cancel journey. " + bookedSeats + " seat(s) already booked.");
        }

        // Delete all seats for this journey before deleting the journey
        seatRepository.deleteByJourneyId(journeyId);
        journeyRepository.delete(journey);
        log.info("Journey cancelled successfully with ID: {}", journeyId);
    }

    // Returns [totalSeats, availableSeats] for a journey
    private int[] getSeatCounts(Long journeyId) {
        int total = seatRepository.countByJourneyId(journeyId);
        int available = seatRepository.countByJourneyIdAndStatus(journeyId, SeatStatus.AVAILABLE);
        return new int[]{total, available};
    }

    private JourneyResponse mapToResponse(Journey journey, int totalSeats, int availableSeats) {
        Train train = journey.getTrain();
        return new JourneyResponse(
                journey.getId(),
                train.getId(),
                train.getTrainNumber(),
                train.getName(),
                train.getSource(),
                train.getDestination(),
                journey.getJourneyDate(),
                journey.getDepartureTime(),
                journey.getArrivalTime(),
                totalSeats,
                availableSeats
        );
    }
}