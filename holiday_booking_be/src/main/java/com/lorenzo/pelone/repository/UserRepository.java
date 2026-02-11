package com.lorenzo.pelone.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);


    public List<UserModel> allUsers() {
        List<UserModel> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserModel user = new UserModel();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    user.setCreatedAt(ts.toLocalDateTime());
                }
                users.add(user);
            }
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw new RuntimeException("Error fetching users", e);
        }
        return users;
    }

    public UserModel userById(int id) {
        String sql = "SELECT id, name, last_name, email, address FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                UserModel user = new UserModel();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                
                return user;
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching user by ID: " + id, e);
            throw new RuntimeException("Error fetching user by ID: " + id, e);
        }
    }

    public List<HostModel> allHosts() {
        List<HostModel> hosts = new ArrayList<>();
        String sql = "SELECT h.*, u.name, u.last_name, u.email, u.address, u.created_at as user_created_at " +
                    "FROM hosts h JOIN users u ON h.user_id = u.id";

        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserModel user = new UserModel();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setCreatedAt(rs.getTimestamp("user_created_at").toLocalDateTime());
    
                HostModel host = new HostModel();
                host.setUser(user);
                host.setHostCode(rs.getInt("host_code"));
                host.setSuperHost(rs.getBoolean("super_host"));
    
                hosts.add(host);
            }

        } catch (Exception e) {
            logger.error("Error fetching all hosts", e);
            throw new RuntimeException("Error fetching hosts", e);
        }
        return hosts;
    }

    public HostModel hostbyCode(int hostCode) {
        String sql = "SELECT h.*, u.name, u.last_name, u.email, u.address, u.created_at as user_created_at " +
                    "FROM hosts h JOIN users u ON h.user_id = u.id WHERE h.host_code = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, hostCode);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                UserModel user = new UserModel();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setCreatedAt(rs.getTimestamp("user_created_at").toLocalDateTime());
    
                HostModel host = new HostModel();
                host.setUser(user);
                host.setHostCode(rs.getInt("host_code"));
                host.setSuperHost(rs.getBoolean("super_host"));
    
                return host;
            }
        } catch (Exception e) {
            logger.error("Error fetching host by code: " + hostCode, e);
            throw new RuntimeException("Error fetching host by code: " + hostCode, e);
        }
        return null;
    }

    // Metodo per avere il numero di prenotazioni di un host
    public List<Map<String, Object>> getHostsWithMonthlyReservations() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = """
            SELECT h.host_code, h.super_host, u.name, u.last_name, u.email,
                   (SELECT COUNT(r.id) 
                    FROM reservations r 
                    JOIN habitations hab ON r.habitation_id = hab.id 
                    WHERE hab.host_code = h.host_code 
                    AND r.created_at >= NOW() - INTERVAL '1 month') as monthly_res
            FROM hosts h 
            JOIN users u ON h.user_id = u.id
            """;
    
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
    
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("hostCode", rs.getInt("host_code"));
                row.put("superHost", rs.getBoolean("super_host"));
                row.put("resHostLastMonth", rs.getInt("monthly_res")); 

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("name", rs.getString("name"));
                userMap.put("lastName", rs.getString("last_name"));
                userMap.put("email", rs.getString("email"));

                if (rs.getTimestamp("created_at") != null) {
                    userMap.put("createdAt", rs.getTimestamp("created_at").toInstant().toString());
                }
                row.put("user", userMap);
                result.add(row);
            }
        } catch (SQLException e) {
            logger.error("Error fetching host stats", e);
        }
        return result;
    }

    public UserModel insertUser(Connection conn, UserModel user) throws SQLException {
        String sql = "INSERT INTO users (name, last_name, email, address) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            
            logger.debug("User inserted with ID: {}", user.getId());
            return user;
        }
    }

     public HostModel insertHost(Connection conn, UserModel user, int hostCode) throws SQLException {
        String sql = "INSERT INTO hosts (user_id, host_code, super_host) VALUES (?, ?, ?) RETURNING host_code, created_at";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.setInt(2, hostCode);
            ps.setBoolean(3, false);
            
            ResultSet rs = ps.executeQuery();
            
            HostModel host = new HostModel();
            host.setUser(user);
            host.setSuperHost(false);
            
            if (rs.next()) {
                host.setHostCode(rs.getInt("host_code"));
            }
            
            logger.debug("Host inserted with code: {}", host.getHostCode());
            return host;
        }
    }

    // metodo per swichare da host a superHost
    public void updateSuperHostStatus(int hostCode) {
        String sql = "UPDATE hosts SET super_host = (" +
                     "  SELECT COUNT(r.id) >= 100 " +
                     "  FROM reservations r " +
                     "  JOIN habitations h ON r.habitation_id = h.id " +
                     "  WHERE h.host_code = ? AND r.status != 'Annulled'" +
                     ") WHERE host_code = ?";
    
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, hostCode);
            ps.setInt(2, hostCode);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                logger.debug("Status SuperHost aggiornato per il codice: {}", hostCode);
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'aggiornamento dello status SuperHost per: " + hostCode, e);
            throw new RuntimeException("Errore database status SuperHost", e);
        }
    }

    // Controllo se l'utente già esiste
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
