package com.lorenzo.pelone.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.model.FeedbackModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.repository.FeedbackRepository;
import com.lorenzo.pelone.repository.ReservationRepository;

public class FeedbackService {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    private final FeedbackRepository feedbackRepository;
    private final ReservationRepository reservationRepository;

    public FeedbackService() {
        this.feedbackRepository = new FeedbackRepository();
        this.reservationRepository = new ReservationRepository();
    }


    public List<FeedbackModel> getAllFeedback() {
        try {
            return feedbackRepository.allFeedback();
        } catch (SQLException e) {
            logger.error("Error fetching feedback", e);
            throw new RuntimeException("Error fetching feedback: " + e.getMessage());
        }
    }

    public String createFeedback(int reservationId, int userId, String title, String text, int score) {
        try {
            ReservationModel res = reservationRepository.getReservationById(reservationId);
    
            if (res == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            boolean alreadyExists = feedbackRepository.existsByReservationId(reservationId);
            if (alreadyExists) {
                throw new IllegalArgumentException("A feedback for this reservation has already been submitted.");
            }

            if (!"Completed".equals(res.getStatus())) {
                throw new IllegalArgumentException("You can only leave feedback for completed bookings.");
            }
    
            if (score < 1 || score > 5) {
                throw new IllegalArgumentException("The score must be between 1 and 5.");
            }
    
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title of feedback is required.");
            }
    
            if (title.length() > 50) {
                throw new IllegalArgumentException("The title cannot exceed 50 characters.");
            }
            return feedbackRepository.insertFeedback(reservationId, userId, title, text, score);

        } catch (SQLException e) {
            logger.error("Error SQL during POST feedback", e);
            throw new RuntimeException("Internal error during feedback saving.");
        }
    }
}
