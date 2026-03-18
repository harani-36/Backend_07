package com.train.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JourneyRequest {
    @NotNull(message = "Train ID is required")
    private Long trainId;
    
    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;
    
    @NotNull(message = "Departure time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "21:00:00")
    private LocalTime departureTime;
    
    @NotNull(message = "Arrival time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "05:30:00")
    private LocalTime arrivalTime;
    
    @NotEmpty(message = "At least one coach configuration is required")
    @Valid
    private List<CoachConfigRequest> coachConfigs;
}
