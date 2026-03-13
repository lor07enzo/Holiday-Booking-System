package com.lorenzo.pelone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeedbackRequest {

    @NotNull(message = "Reservation ID is required")
    @Min(1)
    private Integer reservationId;

    @NotNull(message = "User ID is required")
    @Min(1)
    private Integer userId;

    @NotBlank(message = "Title is required")
    private String title;

    private String text;

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 5, message = "Score cannot be higher than 5")
    private Integer score;
}
