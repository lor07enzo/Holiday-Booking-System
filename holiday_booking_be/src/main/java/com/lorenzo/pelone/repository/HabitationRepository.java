package com.lorenzo.pelone.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;

public class HabitationRepository {
    private static final Logger logger = LoggerFactory.getLogger(HabitationRepository.class);


    public List<HabitationModel> allHabitations() throws SQLException {
        List<HabitationModel> habitations = new ArrayList<>();
        String sql = "SELECT hab.*, " +
                    "u.id as user_id, u.name as user_name, u.last_name, u.email, u.address as user_address, u.created_at as user_created_at, " +
                    "h.host_code, h.super_host " +
                    "FROM habitations hab " +
                    "JOIN hosts h ON hab.host_code = h.host_code " +
                    "JOIN users u ON h.user_id = u.id";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                
                UserModel user = new UserModel();
                user.setId(rs.getInt("user_id"));
                user.setName(rs.getString("user_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("user_address"));
                user.setCreatedAt(rs.getTimestamp("user_created_at").toLocalDateTime());
                
                HostModel host = new HostModel();
                host.setUser(user);
                host.setHostCode(rs.getInt("host_code"));
                host.setSuperHost(rs.getBoolean("super_host"));

                HabitationModel habitation = new HabitationModel();
                habitation.setId(rs.getInt("id"));
                habitation.setHost(host);
                habitation.setName(rs.getString("name"));
                habitation.setDescription(rs.getString("description"));
                habitation.setAddress(rs.getString("address"));
                habitation.setFloor(rs.getInt("floor"));
                habitation.setRooms(rs.getInt("rooms"));
                habitation.setPrice(rs.getDouble("price"));
                habitation.setStartAvailable(rs.getDate("start_available").toLocalDate());
                habitation.setEndAvailable(rs.getDate("end_available").toLocalDate());
                habitation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                habitations.add(habitation);
            }
        }
        return habitations;
    }

    public HostModel getHostByCode(int hostCode) throws SQLException {
        String sql = "SELECT h.*, u.name, u.last_name, u.email, u.address, u.created_at as user_created_at " +
                     "FROM hosts h JOIN users u ON h.user_id = u.id WHERE h.host_code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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
            return null;
        }
    }


    public HabitationModel insertHabitation (Connection conn, HabitationModel habitation, int hostCode) throws SQLException {
        String sql = "INSERT INTO habitations (host_code, name, description, address, floor, rooms, price, start_available, end_available) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hostCode);
            ps.setString(2, habitation.getName());
            ps.setString(3, habitation.getDescription());
            ps.setString(4, habitation.getAddress());
            ps.setInt(5, habitation.getFloor());
            ps.setInt(6, habitation.getRooms());
            ps.setDouble(7, habitation.getPrice());
            ps.setDate(8, Date.valueOf(habitation.getStartAvailable()));
            ps.setDate(9, Date.valueOf(habitation.getEndAvailable()));
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                habitation.setId(rs.getInt("id"));
                habitation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            
            logger.debug("Habitation inserted with ID: ", habitation.getId());
            return habitation;
        }
    }


    public boolean hostCodeExists(int hostCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM hosts WHERE host_code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, hostCode);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
