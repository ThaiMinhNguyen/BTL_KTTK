package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.ShiftSlot;
import com.nemo.btl_kttk.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeShiftDAO extends DAO {
    
    private UserDAO userDAO;
    private ShiftSlotDAO shiftSlotDAO;
    
    public EmployeeShiftDAO() {
        super();
        userDAO = new UserDAO();
        shiftSlotDAO = new ShiftSlotDAO();
    }
    
    public List<EmployeeShift> getEmployeeShiftsByUserId(int userId) {
        List<EmployeeShift> employeeShifts = new ArrayList<>();
        String sql = "SELECT * FROM EmployeeShift WHERE tblUserId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeShift employeeShift = new EmployeeShift();
                employeeShift.setId(rs.getInt("id"));
                employeeShift.setRegistrationDate(rs.getTimestamp("registrationDate").toLocalDateTime());
                
                User employee = userDAO.getUserById(rs.getInt("tblUserId"));
                employeeShift.setEmployee(employee);
                
                ShiftSlot shiftSlot = shiftSlotDAO.getShiftSlotById(rs.getInt("tblShiftSlotId"));
                employeeShift.setShiftSlot(shiftSlot);
                
                employeeShifts.add(employeeShift);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeShifts;
    }
    
    public EmployeeShift getEmployeeShiftById(int id) {
        String sql = "SELECT * FROM EmployeeShift WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EmployeeShift employeeShift = new EmployeeShift();
                employeeShift.setId(rs.getInt("id"));
                employeeShift.setRegistrationDate(rs.getTimestamp("registrationDate").toLocalDateTime());
                
                User employee = userDAO.getUserById(rs.getInt("tblUserId"));
                employeeShift.setEmployee(employee);
                
                ShiftSlot shiftSlot = shiftSlotDAO.getShiftSlotById(rs.getInt("tblShiftSlotId"));
                employeeShift.setShiftSlot(shiftSlot);
                
                return employeeShift;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registerShift(int userId, int shiftId) {
        // kiểm tra xem shift có available khôgn
        ShiftSlotDAO ssDAO = new ShiftSlotDAO();
        if (!ssDAO.isShiftAvailable(shiftId)) {
            return false;
        }
        
        // kiểm tra xem user đã đăng ký chưa
        String checkSql = "SELECT COUNT(*) FROM EmployeeShift WHERE tblUserId = ? AND tblShiftSlotId = ?";
        try {
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setInt(1, userId);
            checkPs.setInt(2, shiftId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // đăng ký shift
        String sql = "INSERT INTO EmployeeShift (registrationDate, tblShiftSlotId, tblUserId) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, shiftId);
            ps.setInt(3, userId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelRegistration(int employeeShiftId) {
        String sql = "DELETE FROM EmployeeShift WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeShiftId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean managerRegisterShiftForEmployee(int employeeId, int shiftId) {
        // kiểm tra xem shift có available không
        ShiftSlotDAO ssDAO = new ShiftSlotDAO();
        if (!ssDAO.isShiftAvailable(shiftId)) {
            return false;
        }
        
        // kiểm tra xem employee đã đăng ký shift này chưa
        String checkSql = "SELECT COUNT(*) FROM EmployeeShift WHERE tblUserId = ? AND tblShiftSlotId = ?";
        try {
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setInt(1, employeeId);
            checkPs.setInt(2, shiftId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Employee đã đăng ký shift này rồi
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // đăng ký shift cho employee
        String sql = "INSERT INTO EmployeeShift (registrationDate, tblShiftSlotId, tblUserId) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, shiftId);
            ps.setInt(3, employeeId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<User> getEmployeesRegisteredForShift(int shiftId) {
        List<User> registeredEmployees = new ArrayList<>();
        String sql = "SELECT u.* FROM User u " +
                     "INNER JOIN EmployeeShift es ON u.id = es.tblUserId " +
                     "WHERE es.tblShiftSlotId = ? AND u.active = 1";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shiftId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setHourlyRate(rs.getDouble("hourlyRate"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("active"));
                registeredEmployees.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registeredEmployees;
    }
    
    public int getRegisteredCountForShift(int shiftId) {
        String sql = "SELECT COUNT(*) FROM EmployeeShift WHERE tblShiftSlotId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shiftId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
} 