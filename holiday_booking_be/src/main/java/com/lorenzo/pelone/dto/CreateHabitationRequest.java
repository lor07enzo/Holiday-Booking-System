package com.lorenzo.pelone.dto;

import com.lorenzo.pelone.model.HabitationModel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHabitationRequest {

    @NotNull(message = "Host code is required")
    @Min(value = 1, message = "Invalid host code")
    private Integer hostCode; 

    @NotNull(message = "Habitation data is required")
    @Valid 
    private HabitationModel habitation;
}
