package com.lorenzo.pelone.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.repository.HabitationRepository;
import com.lorenzo.pelone.repository.ReservationRepository;
import com.lorenzo.pelone.repository.UserRepository;

public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final HabitationRepository habitationRepository;
    private final UserRepository userRepository;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.habitationRepository = new HabitationRepository();
        this.userRepository = new UserRepository();
    }


    public List<ReservationModel> getAllReservations() {
        try {
            reservationRepository.updateExpiredReservations();
            return reservationRepository.allReservations();
        } catch (SQLException e) {
            logger.error("Error fetching reservations", e);
            throw new RuntimeException("Error fetching reservations: " + e.getMessage(), e);
        }
    }

    public List<ReservationModel> getReservationsLastMonth() {
        try {
            return reservationRepository.reservationsLastMonth();
        } catch (SQLException e) {
            logger.error("Errore nel filtraggio delle prenotazioni", e);
            throw new RuntimeException("Error fetching reservations for last month: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getDashboardStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("topUsers", reservationRepository.getTop5UsersByDays());
        stats.put("topHosts", reservationRepository.getTopHosts());
        stats.put("mostPopularHabitation", reservationRepository.getMostPopularHabitation());
        
        return stats;
    }

    public ReservationModel createReservation(int habitationId, int userId, LocalDate startDate, LocalDate endDate) {
        // Validazione date
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book dates in the past");
        }
        
        Connection conn = null;
        try {
            reservationRepository.checkAvailability(habitationId, userId, startDate, endDate);
            
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            int reservationId = reservationRepository.insertReservation(conn, habitationId, userId, startDate, endDate);
            
            conn.commit();
            
            ReservationModel complete = reservationRepository.getReservationById(reservationId);

            int hostCode = habitationRepository.getHostCodeByHabitationId(habitationId);
            userRepository.updateSuperHostStatus(hostCode);
            
            logger.info("Reservation created successfully! ID: {}", reservationId);
            return complete;
            
        } catch (IllegalArgumentException e) {
            throw e;
            
        } catch (SQLException e) {
            logger.error("Error creating reservation or updating host: ", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back: ", ex);
                }
            }
            throw new RuntimeException("Error Server: " + e.getMessage(), e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }

    
}