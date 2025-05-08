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

        String sql = "SELECT tr.* FROM TimeRecord tr "
                + "JOIN EmployeeShift es ON tr.tblEmployeeShiftId = es.id "
                + "WHERE es.tblEmployeeId = ? "
                + "AND tr.actualStartTime >= ? "
                + "AND tr.actualStartTime <= ? "
                + "ORDER BY tr.actualStartTime ASC";

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


    public List<TimeRecord> getTimeRecordsByPaymentId(int paymentId) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM TimeRecord WHERE tblPaymentId = ? ORDER BY actualStartTime ASC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, paymentId);

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

        int paymentId = rs.getInt("tblPaymentId");
        if (!rs.wasNull()) {
            Payment payment = paymentDAO.getPaymentById(paymentId);
            timeRecord.setPayment(payment);
        }

        return timeRecord;
    }
}
