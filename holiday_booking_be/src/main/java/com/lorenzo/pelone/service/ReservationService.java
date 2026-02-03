package com.lorenzo.pelone.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.repository.ReservationRepository;

public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    
    public List<ReservationModel> getAllReservations() {
        try {
            return reservationRepository.allReservations();
        } catch (SQLException e) {
            logger.error("Error fetching reservations ", e);
            throw new RuntimeException("Error fetching reservations ", e);
        }
    }

    public ReservationService () {
        this.reservationRepository = new ReservationRepository();
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
            reservationRepository.isAvailable(habitationId, userId, startDate, endDate);
            
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            ReservationModel created = reservationRepository.insertReservation(conn, habitationId, userId, startDate, endDate);
            ReservationModel complete = reservationRepository.getCompleteReservation(created.getId(), habitationId, userId);
            
            conn.commit();
            logger.info("Reservation created successfully! ID: ", complete.getId());
            return complete;
            
        } catch (IllegalArgumentException e) {
            throw e;
            
        } catch (SQLException e) {
            logger.error("Error creating reservation ", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back ", ex);
                }
            }
            throw new RuntimeException("Error creating reservation ", e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection ", e);
                }
            }
        }
    }

}
