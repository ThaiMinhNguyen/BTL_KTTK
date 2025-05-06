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
                
                // load employee
                User employee = userDAO.getUserById(rs.getInt("tblUserId"));
                employeeShift.setEmployee(employee);
                
                // load shift slot
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
                
                // load employee
                User employee = userDAO.getUserById(rs.getInt("tblUserId"));
                employeeShift.setEmployee(employee);
                
                // load shift slot
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
        // Check if shift is available
        ShiftSlotDAO ssDAO = new ShiftSlotDAO();
        if (!ssDAO.isShiftAvailable(shiftId)) {
            return false;
        }
        
        // Check if user has already registered for this shift
        String checkSql = "SELECT COUNT(*) FROM EmployeeShift WHERE tblUserId = ? AND tblShiftSlotId = ?";
        try {
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setInt(1, userId);
            checkPs.setInt(2, shiftId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;  // Already registered
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // Register for the shift
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
} 