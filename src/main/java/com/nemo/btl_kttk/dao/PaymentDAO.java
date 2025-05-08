package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.Payment;
import com.nemo.btl_kttk.models.TimeRecord;
import com.nemo.btl_kttk.models.User;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO extends DAO {
    
    private UserDAO userDAO;
    
    public PaymentDAO() {
        super();
        userDAO = new UserDAO();
    }
    
    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM Payment WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setWeekStartDate(rs.getDate("weekStartDate"));
                payment.setPaymentDate(rs.getDate("paymentDate"));
                payment.setTotalHour(rs.getDouble("totalHour"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setStatus(rs.getString("status"));
                
                // Load employee
                User employee = userDAO.getUserById(rs.getInt("tblEmployeeId"));
                payment.setEmployee(employee);
                
                // Load processed by
                User processedBy = userDAO.getUserById(rs.getInt("tblProcessedById"));
                payment.setProcessedBy(processedBy);
                
                
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private List<TimeRecord> getTimeRecordsForPayment(int paymentId) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id FROM TimeRecord WHERE tblPaymentId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, paymentId);
            ResultSet rs = ps.executeQuery();
            TimeRecordDAO timeRecordDAO = new TimeRecordDAO();
            while (rs.next()) {
                TimeRecord timeRecord = timeRecordDAO.getTimeRecordById(rs.getInt("id"));
                if (timeRecord != null) {
                    timeRecords.add(timeRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeRecords;
    }
   
    public boolean processPayment(int paymentId, int processedById, Date paymentDate) {
        String sql = "UPDATE Payment SET paymentDate = ?, status = 'PAID', tblProcessedById = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(paymentDate.getTime()));
            ps.setInt(2, processedById);
            ps.setInt(3, paymentId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public List<Payment> getPaymentsByWeek(Date weekStartDate) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE weekStartDate = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(weekStartDate.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setWeekStartDate(rs.getDate("weekStartDate"));
                payment.setPaymentDate(rs.getDate("paymentDate"));
                payment.setTotalHour(rs.getDouble("totalHour"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setStatus(rs.getString("status"));
                
                // Load employee
                User employee = userDAO.getUserById(rs.getInt("tblEmployeeId"));
                payment.setEmployee(employee);
                
                // Load processed by
                int processedById = rs.getInt("tblProcessedById");
                if (!rs.wasNull()) {
                    User processedBy = userDAO.getUserById(processedById);
                    payment.setProcessedBy(processedBy);
                }
                
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
    
    
    public Payment getUserPaymentByWeek(int employeeId, java.util.Date weekStartDate) {
        String sql = "SELECT * FROM Payment WHERE tblEmployeeId = ? AND weekStartDate = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, employeeId);
            ps.setDate(2, new java.sql.Date(weekStartDate.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setWeekStartDate(rs.getDate("weekStartDate"));
                payment.setPaymentDate(rs.getDate("paymentDate"));
                payment.setTotalHour(rs.getDouble("totalHour"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setStatus(rs.getString("status"));
                
                // Load employee
                User employee = userDAO.getUserById(employeeId);
                payment.setEmployee(employee);
                
                // Load processed by
                int processedById = rs.getInt("tblProcessedById");
                if (!rs.wasNull()) {
                    User processedBy = userDAO.getUserById(processedById);
                    payment.setProcessedBy(processedBy);
                }
                
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO Payment (weekStartDate, paymentDate, totalHour, amount, status, tblEmployeeId, tblProcessedById) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, new java.sql.Date(payment.getWeekStartDate().getTime()));
            
            if (payment.getPaymentDate() != null) {
                ps.setDate(2, new java.sql.Date(payment.getPaymentDate().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            
            ps.setDouble(3, payment.getTotalHour());
            ps.setDouble(4, payment.getAmount());
            ps.setString(5, payment.getStatus());
            ps.setInt(6, payment.getEmployee().getId());
            
            if (payment.getProcessedBy() != null) {
                ps.setInt(7, payment.getProcessedBy().getId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                payment.setId(generatedKeys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment ORDER BY paymentDate DESC";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setWeekStartDate(rs.getDate("weekStartDate"));
                payment.setPaymentDate(rs.getDate("paymentDate"));
                payment.setTotalHour(rs.getDouble("totalHour"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setStatus(rs.getString("status"));
                
                // Load employee
                User employee = userDAO.getUserById(rs.getInt("tblEmployeeId"));
                payment.setEmployee(employee);
                
                // Load processed by
                int processedById = rs.getInt("tblProcessedById");
                if (!rs.wasNull()) {
                    User processedBy = userDAO.getUserById(processedById);
                    payment.setProcessedBy(processedBy);
                }
                
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
} 