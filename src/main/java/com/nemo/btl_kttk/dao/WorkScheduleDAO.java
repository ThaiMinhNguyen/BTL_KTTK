package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.User;
import com.nemo.btl_kttk.models.WorkSchedule;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WorkScheduleDAO extends DAO {
    
    private UserDAO userDAO;
    
    public WorkScheduleDAO() {
        super();
        userDAO = new UserDAO();
    }
    
    public WorkSchedule getWorkScheduleById(int id) {
        String sql = "SELECT * FROM WorkSchedule WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WorkSchedule workSchedule = new WorkSchedule();
                workSchedule.setId(rs.getInt("id"));
                workSchedule.setName(rs.getString("name"));
                workSchedule.setCreateDate(rs.getDate("createDate"));
                
                int createdById = rs.getInt("tblUserId");
                User createdBy = userDAO.getUserById(createdById);
                workSchedule.setCreatedBy(createdBy);
                
                return workSchedule;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public List<WorkSchedule> getAllWorkSchedules() {
        List<WorkSchedule> workSchedules = new ArrayList<>();
        String sql = "SELECT * FROM WorkSchedule ORDER BY createDate DESC";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                WorkSchedule workSchedule = new WorkSchedule();
                workSchedule.setId(rs.getInt("id"));
                workSchedule.setName(rs.getString("name"));
                workSchedule.setCreateDate(rs.getDate("createDate"));
                
                int createdById = rs.getInt("tblUserId");
                User createdBy = userDAO.getUserById(createdById);
                workSchedule.setCreatedBy(createdBy);
                
                workSchedules.add(workSchedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workSchedules;
    }
    
    
    
    public int createWorkSchedule(String name, int createdBy) {
        String sql = "INSERT INTO WorkSchedule (name, createDate, tblUserID) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, createdBy);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean deleteWorkSchedule(int scheduleId) {
        String sql = "DELETE FROM WorkSchedule WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, scheduleId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 