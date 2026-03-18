package com.train.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainRequest {
    @NotBlank(message = "Train number is required")
    @Size(max = 20, message = "Train number must not exceed 20 characters")
    private String trainNumber;
    
    @NotBlank(message = "Train name is required")
    @Size(min = 3, max = 100, message = "Train name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Source is required")
    @Size(max = 100, message = "Source must not exceed 100 characters")
    private String source;
    
    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination must not exceed 100 characters")
    private String destination;
}
