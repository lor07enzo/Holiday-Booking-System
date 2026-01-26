package com.lorenzo.pelone.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ReservationModel {
    int id;
    HabitationModel habitation;
    UserModel user;
    LocalDate startDate;
    LocalDate endDate;
}
