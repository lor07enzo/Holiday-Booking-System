package com.lorenzo.pelone.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lorenzo.pelone.model.FeedbackModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.repository.FeedbackDAO;
import com.lorenzo.pelone.repository.ReservationDAO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {
    private final FeedbackDAO feedbackDAO;
    private final ReservationDAO reservationDAO;

    
    public List<FeedbackModel> getAllFeedback() {
        return feedbackDAO.findAll();
    }

    @Transactional
    public String createFeedback(ReservationModel reservationId, UserModel user, String title, String text, int score) {
        ReservationModel res = reservationDAO.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + reservationId));

        if (feedbackDAO.existsByReservationId(reservationId.getId())) {
            throw new IllegalArgumentException("A feedback for this reservation has already been submitted.");
        }

        if (!"Completed".equalsIgnoreCase(res.getStatus())) {
            throw new IllegalArgumentException("You can only leave feedback for completed bookings.");
        }

        FeedbackModel feedback = new FeedbackModel();
        feedback.setReservation(res); 
        feedback.setUser(user);
        feedback.setTitle(title);
        feedback.setText(text);
        feedback.setScore(score);

        FeedbackModel saved = feedbackDAO.save(feedback);
        
        log.info("Feedback created successfully for reservation {}", reservationId);
        return String.valueOf(saved.getId());
    }
}
