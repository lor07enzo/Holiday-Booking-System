package com.lorenzo.pelone.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lorenzo.pelone.model.ReservationModel;

import jakarta.transaction.Transactional;

@Repository
public interface ReservationDAO extends JpaRepository<ReservationModel, Integer>{

    // Metodo per avere tutte le prenotazioni nell'ultimo mese
    List<ReservationModel> findAllByCreatedAtAfter(LocalDateTime date);

    // Metodo che permette di ottenere l'ultima prenotazione di un'utente
    Optional<ReservationModel> findFirstByUserIdOrderByStartDateDesc(Integer userId);

    // Metodo per aggiornare lo stato delle prenotazioni scadute (end_date < oggi) a "Completed"
    @Modifying
    @Transactional
    @Query(value = "UPDATE reservations r SET status = 'Completed' " +
           "WHERE r.end_date < CURRENT_DATE AND r.status = 'Confirmed'", nativeQuery = true)
    int updateExpiredReservations();

    // Controllo di disponibilità di un'abitazione
    @Query(value = "SELECT NOT EXISTS(SELECT 1 FROM reservations WHERE habitation_id = :habId " +
           "AND status != 'Annulled' " +
           "AND ((start_date <= :end AND end_date >= :start)))", nativeQuery = true)
    boolean isAvailable(@Param("habId") int habId, @Param("start") LocalDate start, @Param("end") LocalDate end);


    // --- STATISTICHE (Native Queries) ---
    @Query(value = """
        SELECT u.id AS "id", 
               u.name AS "name", 
               u.last_name AS "lastName", 
               u.email AS "email", 
               SUM(r.end_date - r.start_date) AS "days"
        FROM reservations r JOIN users u ON r.user_id = u.id
        WHERE r.created_at >= NOW() - INTERVAL '1 month'
        GROUP BY u.id, u.name, u.last_name, u.email
        ORDER BY "days" DESC LIMIT 5
        """, nativeQuery = true)
    List<Map<String, Object>> getTop5UsersByDays();

    @Query(value = """
        SELECT h.host_code AS "hostCode", 
               h.super_host AS "superHost", 
               u.name AS "name", 
               u.last_name AS "lastName", 
               u.email AS "email", 
               COUNT(r.id) AS "count"
        FROM reservations r
        JOIN habitations hab ON r.habitation_id = hab.id
        JOIN hosts h ON hab.host_code = h.host_code
        JOIN users u ON h.user_id = u.id
        WHERE r.created_at >= NOW() - INTERVAL '1 month'
        GROUP BY h.host_code, h.super_host, u.name, u.last_name, u.email
        ORDER BY "count" DESC LIMIT 1
        """, nativeQuery = true)
    List<Map<String, Object>> getTopHosts();

    @Query(value = """
        SELECT h.id AS "id", 
               h.name AS "name", 
               h.address AS "address", 
               h.price AS "price", 
               COUNT(r.id) AS "reservationCount"
        FROM habitations h JOIN reservations r ON h.id = r.habitation_id
        WHERE r.created_at >= NOW() - INTERVAL '1 month' AND r.status != 'Annulled'
        GROUP BY h.id, h.name, h.address, h.price
        ORDER BY "reservationCount" DESC LIMIT 1
        """, nativeQuery = true)
    Map<String, Object> getMostPopularHabitation();
}