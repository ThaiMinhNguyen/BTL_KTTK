package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.Payment;
import com.nemo.btl_kttk.models.User;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                
                User employee = userDAO.getUserById(rs.getInt("tblEmployeeId"));
                payment.setEmployee(employee);
                
                User processedBy = userDAO.getUserById(rs.getInt("tblProcessedById"));
                payment.setProcessedBy(processedBy);
                
                
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                
                User employee = userDAO.getUserById(rs.getInt("tblEmployeeId"));
                payment.setEmployee(employee);
                
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