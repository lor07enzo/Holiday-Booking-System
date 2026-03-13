package com.lorenzo.pelone.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "Habitation ID is required")
    @Min(value = 1, message = "Habitation ID must be a valid ID")
    private Integer habitationId; 

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be a valid ID")
    private Integer userId; 

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
