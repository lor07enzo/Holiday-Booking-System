package com.lorenzo.pelone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackModel {
    int id;
    UserModel user;
    ReservationModel reservation;
    double score;
    String title;
    String description;
}
