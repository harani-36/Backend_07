package com.search.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.search.client.TrainClient;
import com.search.client.JourneyClient;
import com.search.dto.TrainResponse;
import com.search.dto.JourneyResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final TrainClient trainClient;
    private final JourneyClient journeyClient;

    @GetMapping("/stations")
    public List<String> getAllStations() {
        log.info("Fetching all stations");
        return trainClient.getAllStations();
    }

    @GetMapping
    public List<TrainResponse> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Searching trains from {} to {} on {}", source, destination, date);

        if (date != null) {
            List<JourneyResponse> journeys = journeyClient.getJourneysByDate(date);
            List<TrainResponse> results = journeys.stream()
                    .filter(j -> j.getSource().equalsIgnoreCase(source)
                               && j.getDestination().equalsIgnoreCase(destination))
                    .map(j -> new TrainResponse(
                            j.getTrainId(),
                            j.getTrainNumber(),
                            j.getTrainName(),
                            j.getSource(),
                            j.getDestination(),
                            j.getDepartureTime(),
                            j.getArrivalTime()))
                    .toList();
            log.debug("Found {} journey-based trains", results.size());
            return results;
        }

        List<TrainResponse> results = trainClient.searchTrains(source, destination);
        log.debug("Found {} trains", results.size());
        return results;
    }
}
