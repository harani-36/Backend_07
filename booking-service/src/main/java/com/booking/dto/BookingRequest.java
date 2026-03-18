package com.booking.dto;

import com.common.enums.CoachType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Journey ID is required")
    @Positive(message = "Journey ID must be positive")
    private Long journeyId;

    @NotNull(message = "Train ID is required")
    @Positive(message = "Train ID must be positive")
    private Long trainId;

    private Long seatId;

    @NotBlank(message = "Passenger name is required")
    @Size(min = 2, max = 100, message = "Passenger name must be between 2 and 100 characters")
    private String passengerName;

    @NotNull(message = "Coach type is required")
    private CoachType coachType;

    @NotNull(message = "Fare is required")
    @Positive(message = "Fare must be positive")
    private Double fare;
}
