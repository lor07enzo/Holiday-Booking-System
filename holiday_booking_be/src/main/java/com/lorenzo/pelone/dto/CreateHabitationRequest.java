package com.lorenzo.pelone.dto;

import com.lorenzo.pelone.model.HabitationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateHabitationRequest {
    private int hostCode; 
    private HabitationModel habitation;
}
