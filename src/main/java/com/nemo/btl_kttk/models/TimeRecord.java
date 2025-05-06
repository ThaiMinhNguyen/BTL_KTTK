package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeRecord implements Serializable {
    private int id;
    private EmployeeShift employeeShift;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Payment payment;
    
    public TimeRecord() {
    }
    
    public TimeRecord(int id, EmployeeShift employeeShift, LocalDateTime actualStartTime, 
            LocalDateTime actualEndTime, Payment payment) {
        this.id = id;
        this.employeeShift = employeeShift;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.payment = payment;
    }
    
    public TimeRecord(EmployeeShift employeeShift, LocalDateTime actualStartTime, 
            LocalDateTime actualEndTime, Payment payment) {
        this.employeeShift = employeeShift;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.payment = payment;
    }
    
    // Constructor không có id và payment (khi tạo mới, payment sẽ được gán tự động bởi trigger)
    public TimeRecord(EmployeeShift employeeShift, LocalDateTime actualStartTime, 
            LocalDateTime actualEndTime) {
        this.employeeShift = employeeShift;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmployeeShift getEmployeeShift() {
        return employeeShift;
    }

    public void setEmployeeShift(EmployeeShift employeeShift) {
        this.employeeShift = employeeShift;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    @Override
    public String toString() {
        return "TimeRecord{" + "id=" + id + ", employeeShift=" + employeeShift.getId() + 
                ", actualStartTime=" + actualStartTime + ", actualEndTime=" + actualEndTime + '}';
    }
} 