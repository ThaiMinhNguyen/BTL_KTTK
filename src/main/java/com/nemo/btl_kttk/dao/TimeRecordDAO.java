package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.Payment;
import com.nemo.btl_kttk.models.TimeRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TimeRecordDAO extends DAO {
    
    private EmployeeShiftDAO employeeShiftDAO;
    private PaymentDAO paymentDAO;
    
    public TimeRecordDAO() {
        super();
        employeeShiftDAO = new EmployeeShiftDAO();
        paymentDAO = new PaymentDAO();
    }
    
    public TimeRecord getTimeRecordById(int id) {
        String sql = "SELECT * FROM TimeRecord WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TimeRecord timeRecord = new TimeRecord();
                timeRecord.setId(rs.getInt("id"));
                timeRecord.setActualStartTime(rs.getTimestamp("actualStartTime").toLocalDateTime());
                timeRecord.setActualEndTime(rs.getTimestamp("actualEndTime").toLocalDateTime());
                
                // Load employee shift
                EmployeeShift employeeShift = employeeShiftDAO.getEmployeeShiftById(rs.getInt("tblEmployeeShiftId"));
                timeRecord.setEmployeeShift(employeeShift);
                
                // Load payment if exists
                int paymentId = rs.getInt("tblPaymentId");
                if (!rs.wasNull()) {
                    Payment payment = paymentDAO.getPaymentById(paymentId);
                    timeRecord.setPayment(payment);
                }
                
                return timeRecord;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<TimeRecord> getTimeRecordsByEmployeeShiftId(int employeeShiftId) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM TimeRecord WHERE tblEmployeeShiftId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeShiftId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TimeRecord timeRecord = new TimeRecord();
                timeRecord.setId(rs.getInt("id"));
                timeRecord.setActualStartTime(rs.getTimestamp("actualStartTime").toLocalDateTime());
                timeRecord.setActualEndTime(rs.getTimestamp("actualEndTime").toLocalDateTime());
                
                // Load employee shift
                EmployeeShift employeeShift = employeeShiftDAO.getEmployeeShiftById(employeeShiftId);
                timeRecord.setEmployeeShift(employeeShift);
                
                // Load payment if exists
                int paymentId = rs.getInt("tblPaymentId");
                if (!rs.wasNull()) {
                    Payment payment = paymentDAO.getPaymentById(paymentId);
                    timeRecord.setPayment(payment);
                }
                
                timeRecords.add(timeRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeRecords;
    }
    
    public boolean addTimeRecord(TimeRecord timeRecord) {
        String sql = "INSERT INTO TimeRecord (actualStartTime, actualEndTime, tblEmployeeShiftId) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(timeRecord.getActualStartTime()));
            ps.setTimestamp(2, Timestamp.valueOf(timeRecord.getActualEndTime()));
            ps.setInt(3, timeRecord.getEmployeeShift().getId());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                timeRecord.setId(generatedKeys.getInt(1));
            }
            
            // Let MySQL trigger handle payment association
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateTimeRecord(TimeRecord timeRecord) {
        String sql = "UPDATE TimeRecord SET actualStartTime = ?, actualEndTime = ?, tblEmployeeShiftId = ?, tblPaymentId = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(timeRecord.getActualStartTime()));
            ps.setTimestamp(2, Timestamp.valueOf(timeRecord.getActualEndTime()));
            ps.setInt(3, timeRecord.getEmployeeShift().getId());
            
            if (timeRecord.getPayment() != null) {
                ps.setInt(4, timeRecord.getPayment().getId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            ps.setInt(5, timeRecord.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteTimeRecord(int id) {
        String sql = "DELETE FROM TimeRecord WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 