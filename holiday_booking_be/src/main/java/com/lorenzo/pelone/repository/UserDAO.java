package com.lorenzo.pelone.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;

import jakarta.transaction.Transactional;

@Repository
public interface UserDAO extends JpaRepository<UserModel, Integer> {

    boolean existsByEmail(String email);

    @Query("SELECT h FROM HostModel h")
    List<HostModel> findAllHosts();

    @Query("SELECT h FROM HostModel h WHERE h.hostCode = :hostCode")
    Optional<HostModel> findHostByCode(@Param("hostCode") int hostCode);


    // Query per gestire la regola "100 prenotazioni = SuperHost"
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE hosts SET super_host = (
          SELECT COUNT(r.id) >= 100 
          FROM reservations r 
          JOIN habitations h ON r.habitation_id = h.id 
          WHERE h.host_code = :hostCode AND r.status != 'Annulled'
        ) WHERE host_code = :hostCode
        """, nativeQuery = true)
    void updateSuperHostStatus(@Param("hostCode") int hostCode);

    // Query per ottenere gli host con l'intervallo temporale '1 month' e il conteggio aggregato
    @Query(value = """
        SELECT h.host_code as hostCode, h.super_host as superHost, 
               u.name as name, u.last_name as lastName, u.email as email,
               (SELECT COUNT(r.id) FROM reservations r 
                JOIN habitations hab ON r.habitation_id = hab.id 
                WHERE hab.host_code = h.host_code 
                AND r.created_at >= NOW() - INTERVAL '1 month') as resHostLastMonth
        FROM hosts h JOIN users u ON h.user_id = u.id
        """, nativeQuery = true)
    List<Map<String, Object>> getHostsWithMonthlyReservations();
    
    @Modifying
    @Transactional // Necessario per le query di update/insert manuali
    @Query(value = "INSERT INTO hosts (user_id, host_code, super_host) VALUES (:userId, :hostCode, :isSuperHost)", nativeQuery = true)
    void insertHostNative(@Param("userId") int userId, 
                          @Param("hostCode") int hostCode, 
                          @Param("isSuperHost") boolean isSuperHost);
}
