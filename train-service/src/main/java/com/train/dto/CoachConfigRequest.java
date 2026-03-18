package com.train.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoachConfigRequest {
    @NotBlank(message = "Coach type is required")
    private String coachType;
    
    @Min(value = 1, message = "Number of coaches must be at least 1")
    private int numberOfCoaches;
    
    @Min(value = 1, message = "Seats per coach must be at least 1")
    private int seatsPerCoach;
}
