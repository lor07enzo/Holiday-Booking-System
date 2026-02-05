package com.lorenzo.pelone.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackModel {
    private String id;
    private UserModel user;
    private ReservationModel reservation;
    private int score;
    private String title;
    private String text;
    private LocalDateTime createdAt;
}
