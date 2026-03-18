package com.train.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JourneyResponse {
    private Long id;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private LocalDate journeyDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private int totalSeats;
    private int availableSeats;
}
