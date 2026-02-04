package com.lorenzo.pelone.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.repository.ReservationRepository;

public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    public ReservationService(){
        this.reservationRepository = new ReservationRepository();
    }

    
    public List<ReservationModel> getAllReservations() {
        try {
            return reservationRepository.allReservations();
        } catch (SQLException e) {
            logger.error("Error fetching reservations", e);
            throw new RuntimeException("Error fetching reservations", e);
        }
    }


    public ReservationModel createReservation(
            int habitationId,
            int userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDates(startDate, endDate);

        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            ensureAvailability(habitationId, startDate, endDate);

            ReservationModel created =
                    reservationRepository.insertReservation(
                            conn, habitationId, userId, startDate, endDate
                    );

            ReservationModel complete =
                    reservationRepository.getCompleteReservation(
                            created.getId()
                    );

            conn.commit();
            logger.info("Reservation created with ID {}", complete.getId());
            return complete;

        } catch (IllegalArgumentException e) {
            rollback(conn);
            throw e;

        } catch (SQLException e) {
            rollback(conn);
            logger.error("Error creating reservation", e);
            throw new RuntimeException("Error creating reservation", e);

        } finally {
            close(conn);
        }
    }


    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Dates are required");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                "Start date must be before end date"
            );
        }
        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Cannot book dates in the past"
            );
        }
    }

    private void ensureAvailability(
            int habitationId,
            LocalDate start,
            LocalDate end
    ) throws SQLException {

        Map<Integer, List<ReservationModel>> reservationsByHab =
                reservationRepository.getReservationsByHabitation();

        List<ReservationModel> existing =
                reservationsByHab.get(habitationId);

        if (existing == null) {
            return;
        }

        boolean overlaps = existing.stream().anyMatch(r ->
            !(end.isBefore(r.getStartDate()) ||
              start.isAfter(r.getEndDate()))
        );

        if (overlaps) {
            throw new IllegalArgumentException(
                "Habitation not available for selected dates"
            );
        }
    }


    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("Rollback failed", e);
            }
        }
    }

    private void close(Connection conn) {
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
