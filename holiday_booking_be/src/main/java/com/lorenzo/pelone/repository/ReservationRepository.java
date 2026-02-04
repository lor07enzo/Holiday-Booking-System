package com.lorenzo.pelone.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.model.UserModel;

public class ReservationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ReservationRepository.class);

    
    public List<ReservationModel> allReservations() throws SQLException {
        List<ReservationModel> reservations = new ArrayList<>();
        
        String sql = "SELECT r.*, " +
                        "h.id as hab_id, h.name as hab_name, h.description, h.address as hab_address, " +
                        "h.floor, h.rooms, h.price, h.start_available, h.end_available, " +
                        "u.id as user_id, u.name as user_name, u.last_name, u.email, u.address as user_address, u.created_at as user_created_at, " +
                        "host_u.id as host_user_id, host_u.name as host_name, host_u.last_name as host_last_name, " +
                        "host_u.email as host_email, host_u.address as host_address, " +
                        "ho.host_code, ho.super_host " +
                    "FROM reservations r " +
                    "JOIN habitations h ON r.habitation_id = h.id " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN hosts ho ON h.host_code = ho.host_code " +
                    "JOIN users host_u ON ho.user_id = host_u.id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                reservations.add(buildReservationFromResultSet(rs));
            }
        }
        
        return reservations;
    }

    public Map<Integer, List<ReservationModel>> getReservationsByHabitation()
            throws SQLException {

        Map<Integer, List<ReservationModel>> map = new HashMap<>();

        String sql = """
            SELECT id, habitation_id, start_date, end_date, status
            FROM reservations
            WHERE status != 'Annulled'
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ReservationModel reservation = new ReservationModel();
                reservation.setId(rs.getInt("id"));
                reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                reservation.setStatus(rs.getString("status"));

                int habitationId = rs.getInt("habitation_id");

                map.computeIfAbsent(habitationId, k -> new ArrayList<>()).add(reservation);
            }
        }
        return map;
    }

    public ReservationModel insertReservation(Connection conn, int habitationId, int userId, 
                                            LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "INSERT INTO reservations (habitation_id, user_id, status, start_date, end_date) " +
                    "VALUES (?, ?, 'Confirmed', ?, ?) RETURNING id";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, habitationId);
            ps.setInt(2, userId);
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            
            ReservationModel reservation = new ReservationModel();
            reservation.setStatus("Confirmed");
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            
            if (rs.next()) {
                reservation.setId(rs.getInt("id"));
            }
            
            logger.debug("Reservation inserted with ID: ", reservation.getId());
            return reservation;
        }
    }

    public ReservationModel getCompleteReservation(int reservationId)
            throws SQLException {

        String sql = """
            SELECT r.*, 
                   h.id AS hab_id, h.name AS hab_name, h.description, h.address AS hab_address,
                   h.floor, h.rooms, h.price, h.start_available, h.end_available,
                   u.id AS user_id, u.name AS user_name, u.last_name, u.email, 
                   u.address AS user_address, u.created_at AS user_created_at,
                   host_u.id AS host_user_id, host_u.name AS host_name, 
                   host_u.last_name AS host_last_name, host_u.email AS host_email,
                   host_u.address AS host_address,
                   ho.host_code, ho.super_host
            FROM reservations r
            JOIN habitations h ON r.habitation_id = h.id
            JOIN users u ON r.user_id = u.id
            JOIN hosts ho ON h.host_code = ho.host_code
            JOIN users host_u ON ho.user_id = host_u.id
            WHERE r.id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return buildReservationFromResultSet(rs);
            }
            return null;
        }
    }


    private ReservationModel buildReservationFromResultSet(ResultSet rs)
            throws SQLException {

        UserModel user = new UserModel();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("user_address"));
        user.setCreatedAt(
            rs.getTimestamp("user_created_at").toLocalDateTime()
        );

        UserModel hostUser = new UserModel();
        hostUser.setId(rs.getInt("host_user_id"));
        hostUser.setName(rs.getString("host_name"));
        hostUser.setLastName(rs.getString("host_last_name"));
        hostUser.setEmail(rs.getString("host_email"));
        hostUser.setAddress(rs.getString("host_address"));

        HostModel host = new HostModel();
        host.setUser(hostUser);
        host.setHostCode(rs.getInt("host_code"));
        host.setSuperHost(rs.getBoolean("super_host"));

        HabitationModel habitation = new HabitationModel();
        habitation.setId(rs.getInt("hab_id"));
        habitation.setHost(host);
        habitation.setName(rs.getString("hab_name"));
        habitation.setDescription(rs.getString("description"));
        habitation.setAddress(rs.getString("hab_address"));
        habitation.setFloor(rs.getInt("floor"));
        habitation.setRooms(rs.getInt("rooms"));
        habitation.setPrice(rs.getDouble("price"));
        habitation.setStartAvailable(
            rs.getDate("start_available").toLocalDate()
        );
        habitation.setEndAvailable(
            rs.getDate("end_available").toLocalDate()
        );

        ReservationModel reservation = new ReservationModel();
        reservation.setId(rs.getInt("id"));
        reservation.setHabitation(habitation);
        reservation.setUser(user);
        reservation.setStatus(rs.getString("status"));
        reservation.setStartDate(
            rs.getDate("start_date").toLocalDate()
        );
        reservation.setEndDate(
            rs.getDate("end_date").toLocalDate()
        );

        return reservation;
    }
}
