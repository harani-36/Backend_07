package com.booking.entity;

import java.time.LocalDateTime;

import com.common.enums.CoachType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String userEmail;

    @Column(nullable = false)
    private Long journeyId;

    @Column(nullable = false)
    private Long trainId;

    @Column(nullable = false)
    private Long coachId;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false, length = 100)
    private String passengerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachType coachType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus bookingStatus;

    @Column
    private String paymentId;

    @Column(nullable = false)
    private Double fare;

    @Column(nullable = false)
    private LocalDateTime bookingTime;
}
