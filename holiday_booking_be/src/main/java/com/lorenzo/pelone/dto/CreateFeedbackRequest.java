package com.lorenzo.pelone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeedbackRequest {
    private int reservationId;
    private int userId;
    private String title;
    private String text;
    private int score;
}
