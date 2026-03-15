package com.lorenzo.pelone.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lorenzo.pelone.model.HabitationModel;

@Repository
public interface HabitationDAO extends JpaRepository<HabitationModel, Integer> {

    // Fà ritornare tutte le abitazioni di un host
    List<HabitationModel> findAllByHostHostCode(int hostCode);
    
    // Controlla se esiste un host con quel codice
    @Query("SELECT COUNT(h) > 0 FROM HostModel h WHERE h.hostCode = :hostCode")
    boolean existsByHostCode(@Param("hostCode") int hostCode);

    // Metodo per ottenere il codice host di un'abitazione
    @Query("SELECT h.host.hostCode FROM HabitationModel h WHERE h.id = :id")
    Optional<Integer> findHostCodeByHabitationId(@Param("id") int habitationId);

}
