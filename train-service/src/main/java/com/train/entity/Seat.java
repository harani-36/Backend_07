package com.train.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;
    public Seat() {}

    public Seat(Long id, String seatNumber, SeatStatus status, Coach coach, Long journeyId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.status = status;
        this.coach = coach;
        this.journeyId = journeyId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public Coach getCoach() { return coach; }
    public void setCoach(Coach coach) { this.coach = coach; }

    public Long getJourneyId() { return journeyId; }
    public void setJourneyId(Long journeyId) { this.journeyId = journeyId; }
}
