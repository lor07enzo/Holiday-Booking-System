package com.lorenzo.pelone.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.repository.HabitationDAO;
import com.lorenzo.pelone.repository.ReservationDAO;
import com.lorenzo.pelone.repository.UserDAO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final HabitationDAO habitationDAO;
    private final UserDAO userDAO;

    public List<ReservationModel> getAllReservations() {
        reservationDAO.updateExpiredReservations();
        return reservationDAO.findAll();
    }

    public List<ReservationModel> getReservationsLastMonth() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        return reservationDAO.findAllByCreatedAtAfter(lastMonth);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("topUsers", reservationDAO.getTop5UsersByDays());
        stats.put("allHosts", userDAO.getHostsWithMonthlyReservations());
        stats.put("topHosts", reservationDAO.getTopHosts());
        stats.put("mostPopularHabitation", reservationDAO.getMostPopularHabitation());
        
        return stats;
    }

    public ReservationModel lastReservationByUser(int userId) {
        return reservationDAO.findFirstByUserIdOrderByStartDateDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("No reservations found for user: " + userId));
    }

    @Transactional // Gestisce tutto: se un metodo fallisce, fa rollback di TUTTO
    public ReservationModel createReservation(int habitationId, int userId, LocalDate startDate, LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book dates in the past");
        }

        boolean available = reservationDAO.isAvailable(habitationId, startDate, endDate);
        if (!available) {
            throw new IllegalArgumentException("Habitation is already booked for these dates");
        }

        HabitationModel habitation = habitationDAO.findById(habitationId)
            .orElseThrow(() -> new IllegalArgumentException("Habitation not found"));
        UserModel user = userDAO.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReservationModel reservation = new ReservationModel();
        reservation.setHabitation(habitation);
        reservation.setUser(user);
        reservation.setStatus("Confirmed");
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);

        ReservationModel saved = reservationDAO.save(reservation);

        userDAO.updateSuperHostStatus(habitation.getHost().getHostCode());

        log.info("Reservation created successfully for user {} in habitation {}", userId, habitationId);
        return saved;
    }
    
}