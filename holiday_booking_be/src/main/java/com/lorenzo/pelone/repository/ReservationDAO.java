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

public class ReservationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDAO.class);

    String sqlReservation = "SELECT r.*, " +
                            "h.id as hab_id, h.name as hab_name, h.description, h.address as hab_address, " +
                            "h.floor, h.rooms, h.price, h.start_available, h.end_available, h.created_at as hab_created_at, " +
                            "u.id as user_id, u.name as user_name, u.last_name, u.email, u.address as user_address, u.created_at as user_created_at, " +
                            "host_u.id as host_user_id, host_u.name as host_name, host_u.last_name as host_last_name, " +
                            "host_u.email as host_email, host_u.address as host_address, host_u.created_at as host_created_at, " +
                            "ho.host_code, ho.super_host " +
                            "FROM reservations r " +
                            "JOIN habitations h ON r.habitation_id = h.id " +
                            "JOIN users u ON r.user_id = u.id " +
                            "JOIN hosts ho ON h.host_code = ho.host_code " +
                            "JOIN users host_u ON ho.user_id = host_u.id ";

    public List<ReservationModel> allReservations() throws SQLException {
        List<ReservationModel> reservations = new ArrayList<>();
        
        String sql = sqlReservation;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                reservations.add(buildReservationFromResultSet(rs));
            }
        }
        
        return reservations;
    }

    public ReservationModel getReservationById(int reservationId) throws SQLException {
        String sql = "SELECT r.*, " +
                     "h.id as hab_id, h.name as hab_name, h.description, h.address as hab_address, " +
                     "h.floor, h.rooms, h.price, h.start_available, h.end_available, h.created_at as hab_created_at, " +
                     "u.id as user_id, u.name as user_name, u.last_name, u.email, u.address as user_address, u.created_at as user_created_at, " +
                     "host_u.id as host_user_id, host_u.name as host_name, host_u.last_name as host_last_name, " +
                     "host_u.email as host_email, host_u.address as host_address, host_u.created_at as host_created_at, " +
                     "ho.host_code, ho.super_host " +
                     "FROM reservations r " +
                     "JOIN habitations h ON r.habitation_id = h.id " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN hosts ho ON h.host_code = ho.host_code " +
                     "JOIN users host_u ON ho.user_id = host_u.id " +
                     "WHERE r.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return buildReservationFromResultSet(rs);
            }
            
            throw new SQLException("Reservation not found with ID: " + reservationId);
        }
    }

    public List<ReservationModel> reservationsLastMonth() throws SQLException {
        List<ReservationModel> list = new ArrayList<>();
        String sql = sqlReservation + "WHERE r.created_at >= NOW() - INTERVAL '1 month'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(buildReservationFromResultSet(rs));
            }
        }
        return list;
    }

    // Metodo per avere la top 5 di utenti
    public List<Map<String, Object>> getTop5UsersByDays() throws SQLException {
        List<Map<String, Object>> users = new ArrayList<>();
        String sql = """
            SELECT u.id, u.name, u.last_name, u.email, SUM(r.end_date - r.start_date) as total_days
            FROM reservations r
            JOIN users u ON r.user_id = u.id
            WHERE r.created_at >= NOW() - INTERVAL '1 month'
            GROUP BY u.id, u.name, u.last_name, u.email
            ORDER BY total_days DESC
            LIMIT 5
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(Map.of(
                    "id", rs.getInt("id"),
                    "fullName", rs.getString("name") + " " + rs.getString("last_name"),
                    "days", rs.getInt("total_days"),
                    "email", rs.getString("email")
                ));
            }
        }
        return users;
    }
    
    // Metodo per avere il migliore host
    public List<Map<String, Object>> getTopHosts() throws SQLException {
        List<Map<String, Object>> hosts = new ArrayList<>();
        String sql = """
            SELECT h.host_code, h.super_host, u.name, u.last_name, u.email, COUNT(r.id) as res_count
            FROM reservations r
            JOIN habitations hab ON r.habitation_id = hab.id
            JOIN hosts h ON hab.host_code = h.host_code
            JOIN users u ON h.user_id = u.id
            WHERE r.created_at >= NOW() - INTERVAL '1 month'
            GROUP BY h.host_code, h.super_host, u.name, u.last_name, u.email
            ORDER BY res_count DESC
            LIMIT 1
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hosts.add(Map.of(
                    "hostCode", rs.getInt("host_code"), 
                    "superHost", rs.getBoolean("super_host"),
                    "name", rs.getString("name") + " " + rs.getString("last_name"), 
                    "email", rs.getString("email"),
                    "count", rs.getInt("res_count")
                ));
            }
        }
        return hosts;
    }

    public Map<String, Object> getMostPopularHabitation() throws SQLException {
        String sql = """
            SELECT h.id, h.name, h.address, h.price, COUNT(r.id) as total_reservations
            FROM habitations h
            JOIN reservations r ON h.id = r.habitation_id
            WHERE r.created_at >= NOW() - INTERVAL '1 month'
              AND r.status != 'Annulled'
            GROUP BY h.id, h.name, h.address, h.price
            ORDER BY total_reservations DESC
            LIMIT 1
            """;
    
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("name", rs.getString("name"));
                map.put("address", rs.getString("address"));
                map.put("price", rs.getDouble("price"));
                map.put("reservationCount", rs.getInt("total_reservations"));
                return map;
            }
        }
        return null;
    }

    // Metodo per avere l'ultima prenotazione di un'utente
    public ReservationModel getLastReservationByUser(int userId) throws SQLException {
        String sql = "SELECT r.*, " +
                 "u.name as user_name, u.last_name as user_lastname, u.email as user_email, " +
                 "h.name as hab_name, h.address as hab_address " +
                 "FROM reservations r " +
                 "JOIN users u ON r.user_id = u.id " +
                 "JOIN habitations h ON r.habitation_id = h.id " +
                 "WHERE r.user_id = ? " +
                 "ORDER BY r.start_date DESC LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReservationModel res = new ReservationModel();
                    res.setId(rs.getInt("id"));
                    res.setStatus(rs.getString("status"));
                    res.setStartDate(rs.getDate("start_date").toLocalDate());
                    res.setEndDate(rs.getDate("end_date").toLocalDate());
    
                    UserModel user = new UserModel();
                    user.setName(rs.getString("user_name"));
                    user.setLastName(rs.getString("user_lastname"));
                    user.setEmail(rs.getString("user_email"));
                    res.setUser(user);
    
                    HabitationModel hab = new HabitationModel();
                    hab.setName(rs.getString("hab_name"));
                    hab.setAddress(rs.getString("hab_address"));
                    res.setHabitation(hab); 
    
                    return res;
                }
            }
        }
        return null;
    }

    public int insertReservation(Connection conn, int habitationId, int userId, 
                                 LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "INSERT INTO reservations (habitation_id, user_id, status, start_date, end_date) " +
                     "VALUES (?, ?, 'Confirmed', ?, ?) RETURNING id";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, habitationId);
            ps.setInt(2, userId);
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                logger.debug("Reservation inserted with ID: {}", id);
                return id;
            }
            
            throw new SQLException("Failed to insert reservation");
        }
    }

    public void checkAvailability(int habitationId, int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT " +
                     "EXISTS(SELECT 1 FROM habitations WHERE id = ?) as hab_exists, " +
                     "EXISTS(SELECT 1 FROM users WHERE id = ?) as user_exists, " +
                     "EXISTS(SELECT 1 FROM habitations WHERE id = ? AND ? >= start_available AND ? <= end_available) as in_period, " +
                     "NOT EXISTS(SELECT 1 FROM reservations WHERE habitation_id = ? AND status != 'Annulled' " +
                     "  AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?) " +
                     "  OR (start_date >= ? AND end_date <= ?))) as not_booked";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, habitationId);
            ps.setInt(2, userId);
            ps.setInt(3, habitationId);
            ps.setDate(4, Date.valueOf(startDate));
            ps.setDate(5, Date.valueOf(endDate));
            ps.setInt(6, habitationId);
            ps.setDate(7, Date.valueOf(endDate));
            ps.setDate(8, Date.valueOf(startDate));
            ps.setDate(9, Date.valueOf(endDate));
            ps.setDate(10, Date.valueOf(endDate));
            ps.setDate(11, Date.valueOf(startDate));
            ps.setDate(12, Date.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                if (!rs.getBoolean("hab_exists")) {
                    throw new IllegalArgumentException("Habitation does not exist");
                }
                if (!rs.getBoolean("user_exists")) {
                    throw new IllegalArgumentException("User does not exist");
                }
                if (!rs.getBoolean("in_period")) {
                    throw new IllegalArgumentException("Dates outside availability period");
                }
                if (!rs.getBoolean("not_booked")) {
                    throw new IllegalArgumentException("Habitation not available for selected dates");
                }
            }
        }
    }

    private ReservationModel buildReservationFromResultSet(ResultSet rs) throws SQLException {
        UserModel user = new UserModel();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("user_address"));
        user.setCreatedAt(rs.getTimestamp("user_created_at").toLocalDateTime());
        
        UserModel hostUser = new UserModel();
        hostUser.setId(rs.getInt("host_user_id"));
        hostUser.setName(rs.getString("host_name"));
        hostUser.setLastName(rs.getString("host_last_name"));
        hostUser.setEmail(rs.getString("host_email"));
        hostUser.setAddress(rs.getString("host_address"));
        hostUser.setCreatedAt(rs.getTimestamp("host_created_at").toLocalDateTime());
        
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
        habitation.setStartAvailable(rs.getDate("start_available").toLocalDate());
        habitation.setEndAvailable(rs.getDate("end_available").toLocalDate());
        habitation.setCreatedAt(rs.getTimestamp("hab_created_at").toLocalDateTime());
        
        ReservationModel reservation = new ReservationModel();
        reservation.setId(rs.getInt("id"));
        reservation.setHabitation(habitation);
        reservation.setUser(user);
        reservation.setStatus(rs.getString("status"));
        reservation.setStartDate(rs.getDate("start_date").toLocalDate());
        reservation.setEndDate(rs.getDate("end_date").toLocalDate());
        reservation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return reservation;
    }

    // Update sullo stato della prenotazione
    public void updateExpiredReservations() throws SQLException {
        
        String sql = "UPDATE reservations " +
                     "SET status = 'Completed' " +
                     "WHERE end_date < CURRENT_DATE " +
                     "AND status = 'Confirmed'";
    
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Syncronization database: " + rowsUpdated + " reservations changed to 'Completed'.");
            }
        }
    }
}