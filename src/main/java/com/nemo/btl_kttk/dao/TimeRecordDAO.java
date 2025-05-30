package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.TimeRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public List<TimeRecord> getTimeRecordsByPaymentId(int paymentId) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM TimeRecord WHERE tblPaymentId = ? ORDER BY actualStartTime ASC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, paymentId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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

                timeRecord.setLateFee(rs.getDouble("lateFee"));
                timeRecord.setEarlyFee(rs.getDouble("earlyFee"));
                
                // Lấy thông tin EmployeeShift
                int employeeShiftId = rs.getInt("tblEmployeeShiftId");
                EmployeeShift employeeShift = employeeShiftDAO.getEmployeeShiftById(employeeShiftId);
                timeRecord.setEmployeeShift(employeeShift);

                timeRecords.add(timeRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeRecords;
    }

    public double calculateLateFee(TimeRecord timeRecord, double lateMinutesFeeRate) {
        if (timeRecord.getActualStartTime() == null || timeRecord.getEmployeeShift() == null) {
            return 0.0;
        }

        LocalDateTime scheduledStartTime = timeRecord.getEmployeeShift().getShiftSlot().getStartTime()
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime actualStartTime = timeRecord.getActualStartTime();

        if (actualStartTime.isAfter(scheduledStartTime)) {
            long lateMinutes = ChronoUnit.MINUTES.between(scheduledStartTime, actualStartTime);
            return lateMinutes * lateMinutesFeeRate;
        }

        return 0.0;
    }

    public double calculateEarlyFee(TimeRecord timeRecord, double earlyMinutesFeeRate) {
        if (timeRecord.getActualEndTime() == null || timeRecord.getEmployeeShift() == null) {
            return 0.0;
        }

        LocalDateTime scheduledEndTime = timeRecord.getEmployeeShift().getShiftSlot().getEndTime()
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime actualEndTime = timeRecord.getActualEndTime();

        if (actualEndTime.isBefore(scheduledEndTime)) {
            long earlyMinutes = ChronoUnit.MINUTES.between(actualEndTime, scheduledEndTime);
            return earlyMinutes * earlyMinutesFeeRate;
        }

        return 0.0;
    }

    public double calculateOvertimeBonus(TimeRecord timeRecord, double overtimeRate, double hourlyRate) {
        if (timeRecord.getActualStartTime() == null || timeRecord.getActualEndTime() == null || timeRecord.getEmployeeShift() == null) {
            return 0.0;
        }

        double actualHours = java.time.Duration.between(
            timeRecord.getActualStartTime(),
            timeRecord.getActualEndTime()
        ).toMillis() / (1000.0 * 60 * 60);

        Date scheduledStart = timeRecord.getEmployeeShift().getShiftSlot().getStartTime();
        Date scheduledEnd = timeRecord.getEmployeeShift().getShiftSlot().getEndTime();
        double scheduledHours = (scheduledEnd.getTime() - scheduledStart.getTime()) / (1000.0 * 60 * 60);

        if (actualHours > scheduledHours) {
            double overtimeHours = actualHours - scheduledHours;
            return overtimeHours * hourlyRate * (overtimeRate - 1.0);
        }

        return 0.0;
    }

    public boolean updateTimeRecordFees(int timeRecordId, double lateFee, double earlyFee) {
        String sql = "UPDATE TimeRecord SET lateFee = ?, earlyFee = ? WHERE id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, lateFee);
            ps.setDouble(2, earlyFee);
            ps.setInt(3, timeRecordId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean calculateAndUpdateAllFees(TimeRecord timeRecord, double lateMinutesFeeRate, 
            double earlyMinutesFeeRate, double overtimeRate, double hourlyRate) {
        
        double lateFee = calculateLateFee(timeRecord, lateMinutesFeeRate);
        double earlyFee = calculateEarlyFee(timeRecord, earlyMinutesFeeRate);
        double overtimeBonus = calculateOvertimeBonus(timeRecord, overtimeRate, hourlyRate);
        
        // Cập nhật TimeRecord object
        timeRecord.setLateFee(lateFee);
        timeRecord.setEarlyFee(earlyFee);
        
        // Cập nhật database
        return updateTimeRecordFees(timeRecord.getId(), lateFee, earlyFee);
    }

    public TimeRecord createTimeRecord(LocalDateTime actualStartTime, LocalDateTime actualEndTime, int employeeShiftId) {
        try {
            // 1. Lấy thông tin EmployeeShift
            EmployeeShift employeeShift = employeeShiftDAO.getEmployeeShiftById(employeeShiftId);
            if (employeeShift == null) {
                System.out.println("Không tìm thấy EmployeeShift với ID: " + employeeShiftId);
                return null;
            }

            // 2. Lấy hourlyRate của nhân viên
            double hourlyRate = employeeShift.getEmployee().getHourlyRate();

            // 3. Tạo TimeRecord mới
            TimeRecord timeRecord = new TimeRecord();
            timeRecord.setActualStartTime(actualStartTime);
            timeRecord.setActualEndTime(actualEndTime);
            timeRecord.setEmployeeShift(employeeShift);

            // 4. Tính toán các khoản phí với giá trị mặc định
            double lateFee = calculateLateFee(timeRecord, 0.5);  // 0.5 đồng/phút đi muộn
            double earlyFee = calculateEarlyFee(timeRecord, 0.5); // 0.5 đồng/phút về sớm

            // 5. Set các giá trị đã tính
            timeRecord.setLateFee(lateFee);
            timeRecord.setEarlyFee(earlyFee);

            // 6. Lưu vào database
            String sql = "INSERT INTO TimeRecord (actualStartTime, actualEndTime, lateFee, earlyFee, tblEmployeeShiftId) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(actualStartTime));
            ps.setTimestamp(2, Timestamp.valueOf(actualEndTime));
            ps.setDouble(3, lateFee);
            ps.setDouble(4, earlyFee);
            ps.setInt(5, employeeShiftId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Không thể tạo TimeRecord, không có dòng nào bị ảnh hưởng.");
            }

            // 7. Lấy ID được tạo tự động
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    timeRecord.setId(generatedKeys.getInt(1));
                    return timeRecord;
                } else {
                    throw new SQLException("Không thể tạo TimeRecord, không có ID được tạo.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
