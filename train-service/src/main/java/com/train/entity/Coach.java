package com.train.entity;

import java.util.List;

import com.common.enums.CoachType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "coach")
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coachNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoachType coachType;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 72;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    private List<Seat> seats;

    public Coach() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoachNumber() { return coachNumber; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }

    public CoachType getCoachType() { return coachType; }
    public void setCoachType(CoachType coachType) { this.coachType = coachType; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }

    public Long getTrainId() { return train != null ? train.getId() : null; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}
