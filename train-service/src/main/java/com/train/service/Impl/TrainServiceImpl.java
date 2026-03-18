package com.train.service.Impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.common.enums.CoachType;
import com.train.client.FareClient;
import com.train.dto.FareResponse;
import com.train.dto.TrainFareResponse;
import com.train.dto.TrainRequest;
import com.train.dto.TrainResponse;
import com.train.entity.Journey;
import com.train.entity.SeatStatus;
import com.train.entity.Train;
import com.train.exception.DuplicateTrainException;
import com.train.exception.ResourceNotFoundException;
import com.train.repository.CoachRepository;
import com.train.repository.JourneyRepository;
import com.train.repository.SeatRepository;
import com.train.repository.StationRepository;
import com.train.repository.TrainRepository;
import com.train.service.TrainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private static final Logger log = LoggerFactory.getLogger(TrainServiceImpl.class);
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final CoachRepository coachRepository;
    private final JourneyRepository journeyRepository;
    private final SeatRepository seatRepository;
    private final FareClient fareClient;

    @Override
    public TrainResponse addTrain(TrainRequest request) {
        log.debug("Adding new train: {}", request.getName());
        if (trainRepository.findByTrainNumber(request.getTrainNumber()).isPresent()) {
            log.error("Train with number {} already exists", request.getTrainNumber());
            throw new DuplicateTrainException("Train with this number already exists");
        }
        Train train = new Train();
        train.setTrainNumber(request.getTrainNumber());
        train.setName(request.getName());
        train.setSource(request.getSource());
        train.setDestination(request.getDestination());
        Train saved = trainRepository.save(train);
        log.info("Train added successfully with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<TrainResponse> getAllTrains() {
        log.debug("Fetching all trains");
        List<TrainResponse> trains = trainRepository.findAll().stream().map(this::toResponse).toList();
        log.info("Retrieved {} trains", trains.size());
        return trains;
    }

    @Override
    public TrainResponse getTrainById(Long trainId) {
        log.debug("Fetching train by id: {}", trainId);
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> {
                    log.error("Train not found with id: {}", trainId);
                    return new ResourceNotFoundException("Train not found with ID: " + trainId);
                });
        return toResponse(train);
    }

    @Override
    public TrainResponse updateTrain(Long id, TrainRequest request) {
        log.debug("Updating train with id: {}", id);
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Train not found with id: {}", id);
                    return new ResourceNotFoundException("Train not found with ID: " + id);
                });
        train.setTrainNumber(request.getTrainNumber());
        train.setName(request.getName());
        train.setSource(request.getSource());
        train.setDestination(request.getDestination());
        Train updated = trainRepository.save(train);
        log.info("Train updated successfully with id: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    public void deleteTrain(Long id) {
        log.debug("Deleting train with id: {}", id);
        if (!trainRepository.existsById(id)) {
            log.error("Train not found with id: {}", id);
            throw new ResourceNotFoundException("Train not found with ID: " + id);
        }
        trainRepository.deleteById(id);
        log.info("Train deleted successfully with id: {}", id);
    }

    @Override
    public List<String> getAllStations() {
        log.debug("Fetching all stations");
        List<String> stations = stationRepository.findAll().stream().map(s -> s.getName()).sorted().toList();
        log.info("Retrieved {} stations", stations.size());
        return stations;
    }

    @Override
    public List<String> searchStations(String query) {
        log.debug("Searching stations with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return getAllStations();
        }
        List<String> stations = stationRepository.searchByNameOrCityOrCode(query.trim())
                .stream().map(s -> s.getName()).toList();
        log.info("Found {} stations for query: {}", stations.size(), query);
        return stations;
    }

    @Override
    public List<TrainResponse> searchTrains(String source, String destination) {
        log.debug("Searching trains from {} to {}", source, destination);
        return trainRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public TrainFareResponse getTrainWithFare(Long trainId, CoachType coachType) {
        log.debug("Fetching train with fare for trainId: {}, coachType: {}", trainId, coachType);
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> {
                    log.error("Train not found with id: {}", trainId);
                    return new ResourceNotFoundException("Train not found");
                });
        try {
            FareResponse fare = fareClient.getFare(trainId, coachType.name());
            log.info("Fare retrieved successfully: {}", fare.getAmount());
            return new TrainFareResponse(
                    train.getId(), train.getName(), train.getSource(),
                    train.getDestination(), coachType.name(), fare.getAmount());
        } catch (Exception e) {
            log.error("Failed to fetch fare from Fare Service: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Integer getTotalSeatsByCoachType(Long trainId, CoachType coachType) {
        log.debug("Fetching total seats for trainId: {}, coachType: {}", trainId, coachType);
        Integer total = coachRepository.sumTotalSeatsByTrainIdAndCoachType(trainId, coachType);
        if (total == null) {
            log.warn("No coaches found for trainId: {}, coachType: {}, returning default", trainId, coachType);
            return 72;
        }
        log.info("Total seats for trainId: {}, coachType: {} = {}", trainId, coachType, total);
        return total;
    }

    @Override
    public Integer getAvailableSeatsByCoachType(Long trainId, CoachType coachType, String date) {
        log.debug("Fetching available seats for trainId: {}, coachType: {}, date: {}", trainId, coachType, date);
        LocalDate journeyDate = LocalDate.parse(date);

        List<Journey> journeys = journeyRepository.findByTrainIdAndJourneyDate(trainId, journeyDate);
        if (journeys.isEmpty()) {
            log.warn("No journey found for trainId: {}, date: {}, returning default", trainId, date);
            return 72;
        }

        Long journeyId = journeys.get(0).getId();
        Integer available = seatRepository.countByTrainCoachJourneyAndStatus(
                trainId, coachType.name(), journeyId, SeatStatus.AVAILABLE);

        if (available == null) available = 0;
        log.info("Available seats for trainId: {}, coachType: {}, date: {} = {}", trainId, coachType, date, available);
        return available;
    }

    // Private helper to avoid repeating mapping logic
    private TrainResponse toResponse(Train train) {
        return new TrainResponse(
                train.getId(),
                train.getTrainNumber(),
                train.getName(),
                train.getSource(),
                train.getDestination()
        );
    }
}