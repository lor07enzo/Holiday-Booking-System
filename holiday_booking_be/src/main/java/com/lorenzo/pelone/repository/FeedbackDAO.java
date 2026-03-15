package com.lorenzo.pelone.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lorenzo.pelone.model.FeedbackModel;

@Repository
public interface FeedbackDAO extends JpaRepository<FeedbackModel, UUID> {

    // Controllo se feedback già esiste su una prenotazione
    boolean existsByReservationId(int reservationId);
}
