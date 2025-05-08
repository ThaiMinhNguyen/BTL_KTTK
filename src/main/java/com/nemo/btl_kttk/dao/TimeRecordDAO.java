package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.Payment;
import com.nemo.btl_kttk.models.TimeRecord;
import com.nemo.btl_kttk.models.User;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeRecordDAO extends DAO {
    
    private EmployeeShiftDAO employeeShiftDAO;
    private PaymentDAO paymentDAO;
    private UserDAO userDAO;
    
    public TimeRecordDAO() {
        super();
        employeeShiftDAO = new EmployeeShiftDAO();
        paymentDAO = new PaymentDAO();
        userDAO = new UserDAO();
    }
    
    public TimeRecord getTimeRecordById(int id) {
        String sql = "SELECT * FROM TimeRecord WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractTimeRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public List<TimeRecord> getTimeRecordsByUser(int userId, java.util.Date weekStartDate) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        
        // Tính ngày kết thúc tuần (weekStartDate + 6 ngày)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekStartDate);
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        java.util.Date weekEndDate = calendar.getTime();
        
        String sql = "SELECT tr.* FROM TimeRecord tr " +
                     "JOIN EmployeeShift es ON tr.tblEmployeeShiftId = es.id " +
                     "WHERE es.tblEmployeeId = ? " +
                     "AND tr.actualStartTime >= ? " +
                     "AND tr.actualStartTime <= ? " +
                     "ORDER BY tr.actualStartTime ASC";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(weekStartDate.getTime()));
            ps.setTimestamp(3, new Timestamp(weekEndDate.getTime()));
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TimeRecord timeRecord = extractTimeRecordFromResultSet(rs);
                timeRecords.add(timeRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return timeRecords;
    }
    
    
    public List<TimeRecord> getTimeRecordsByEmployeeShift(int employeeShiftId) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM TimeRecord WHERE tblEmployeeShiftId = ? ORDER BY actualStartTime ASC";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeShiftId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TimeRecord timeRecord = extractTimeRecordFromResultSet(rs);
                timeRecords.add(timeRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return timeRecords;
    }
    
    
    public boolean createTimeRecord(TimeRecord timeRecord) {
        String sql = "INSERT INTO TimeRecord (actualStartTime, actualEndTime, tblEmployeeShiftId) " +
                     "VALUES (?, ?, ?)";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(timeRecord.getActualStartTime()));
            
            if (timeRecord.getActualEndTime() != null) {
                ps.setTimestamp(2, Timestamp.valueOf(timeRecord.getActualEndTime()));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }
            
            ps.setInt(3, timeRecord.getEmployeeShift().getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                timeRecord.setId(generatedKeys.getInt(1));
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateTimeRecord(TimeRecord timeRecord) {
        String sql = "UPDATE TimeRecord SET actualStartTime = ?, actualEndTime = ?, tblEmployeeShiftId = ? " +
                     "WHERE id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(timeRecord.getActualStartTime()));
            
            if (timeRecord.getActualEndTime() != null) {
                ps.setTimestamp(2, Timestamp.valueOf(timeRecord.getActualEndTime()));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }
            
            ps.setInt(3, timeRecord.getEmployeeShift().getId());
            ps.setInt(4, timeRecord.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
   
    public boolean updateEndTime(int timeRecordId, LocalDateTime endTime) {
        String sql = "UPDATE TimeRecord SET actualEndTime = ? WHERE id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(endTime));
            ps.setInt(2, timeRecordId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
   
    private TimeRecord extractTimeRecordFromResultSet(ResultSet rs) throws SQLException {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(rs.getInt("id"));
        
        Timestamp startTimestamp = rs.getTimestamp("actualStartTime");
        if (startTimestamp != null) {
            timeRecord.setActualStartTime(startTimestamp.toLocalDateTime());
        }
        
        Timestamp endTimestamp = rs.getTimestamp("actualEndTime");
        if (endTimestamp != null) {
            timeRecord.setActualEndTime(endTimestamp.toLocalDateTime());
        }
        
        // Lấy thông tin EmployeeShift
        int employeeShiftId = rs.getInt("tblEmployeeShiftId");
        EmployeeShift employeeShift = employeeShiftDAO.getEmployeeShiftById(employeeShiftId);
        timeRecord.setEmployeeShift(employeeShift);
        
        // Load payment if exists
        int paymentId = rs.getInt("tblPaymentId");
        if (!rs.wasNull()) {
            Payment payment = paymentDAO.getPaymentById(paymentId);
            timeRecord.setPayment(payment);
        }
        
        return timeRecord;
    }
} 