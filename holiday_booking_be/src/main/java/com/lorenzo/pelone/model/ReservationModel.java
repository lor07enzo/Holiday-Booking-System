package com.lorenzo.pelone.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ReservationModel {
    private int id;
    private HabitationModel habitation;
    private UserModel user;
    private String status; // Confirmed | Annulled | Completed
    private LocalDate startDate;
    private LocalDate endDate;
}
