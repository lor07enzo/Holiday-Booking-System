package com.lorenzo.pelone.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.FeedbackModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.model.UserModel;

public class FeedbackRepository {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackRepository.class);


    public List<FeedbackModel> allFeedback() throws SQLException {
        List<FeedbackModel> feedbacks = new ArrayList<>();
        String sql = "SELECT f.*, " +
                     "u.id as u_id, u.name as u_name, u.last_name as u_last, " +
                     "r.id as r_id, r.start_date, r.end_date, r.status " +
                     "FROM feedback f " +
                     "JOIN users u ON f.user_id = u.id " +
                     "JOIN reservations r ON f.reservation_id = r.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                feedbacks.add(mapResultSetToModel(rs));
            }
        }
        return feedbacks;
    }

    public String insertFeedback(int reservationId, int userId, String title, String text, int score) throws SQLException {
        String sql = "INSERT INTO feedback (id, reservation_id, user_id, title, text_description, score) VALUES (?, ?, ?, ?, ?, ?) RETURNING created_at";
        String newUuid = UUID.randomUUID().toString();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newUuid);
            ps.setInt(2, reservationId);
            ps.setInt(3, userId);
            ps.setString(4, title);
            ps.setString(5, text);
            ps.setInt(6, score);

            ps.executeUpdate();
            logger.info("Feedback inserito con successo. UUID: {}", newUuid);
            return newUuid;
        }
    }

    private FeedbackModel mapResultSetToModel(ResultSet rs) throws SQLException {
        // Popolamento User
        UserModel user = new UserModel();
        user.setId(rs.getInt("u_id"));
        user.setName(rs.getString("u_name"));
        user.setLastName(rs.getString("u_last"));

        // Popolamento Reservation (essenziale)
        ReservationModel res = new ReservationModel();
        res.setId(rs.getInt("r_id"));
        res.setStartDate(rs.getDate("start_date").toLocalDate());
        res.setEndDate(rs.getDate("end_date").toLocalDate());
        res.setStatus(rs.getString("status"));

        // Popolamento Feedback
        FeedbackModel feedback = new FeedbackModel();
        feedback.setId(rs.getString("id"));
        feedback.setUser(user);
        feedback.setReservation(res);
        feedback.setTitle(rs.getString("title"));
        feedback.setText(rs.getString("text_description"));
        feedback.setScore(rs.getInt("score"));

        return feedback;
    }

    
}
