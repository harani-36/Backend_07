package com.search.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainResponse {
    private Long id;
    private String trainNumber;
    private String name;
    private String source;
    private String destination;
    // optional journey data (only populated when searching by date)
    private java.time.LocalTime departureTime;
    private java.time.LocalTime arrivalTime;

    public TrainResponse() {}

    public TrainResponse(Long id, String trainNumber, String name, String source, String destination) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.name = name;
        this.source = source;
        this.destination = destination;
    }

    public TrainResponse(Long id, String trainNumber, String name, String source, String destination,
                         java.time.LocalTime departureTime, java.time.LocalTime arrivalTime) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    // getters and setters for new fields
    public java.time.LocalTime getDepartureTime() {
        return departureTime;
    }
    public void setDepartureTime(java.time.LocalTime departureTime) {
        this.departureTime = departureTime;
    }
    public java.time.LocalTime getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(java.time.LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}