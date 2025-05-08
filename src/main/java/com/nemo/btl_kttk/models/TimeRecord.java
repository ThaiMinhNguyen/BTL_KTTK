package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeRecord implements Serializable {
    private int id;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private EmployeeShift employeeShift;
    private Payment payment;
    
    public TimeRecord() {
    }
    
    public TimeRecord(int id, LocalDateTime actualStartTime, LocalDateTime actualEndTime, EmployeeShift employeeShift) {
        this.id = id;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.employeeShift = employeeShift;
    }
    
    public TimeRecord(LocalDateTime actualStartTime, LocalDateTime actualEndTime, EmployeeShift employeeShift) {
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.employeeShift = employeeShift;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public EmployeeShift getEmployeeShift() {
        return employeeShift;
    }

    public void setEmployeeShift(EmployeeShift employeeShift) {
        this.employeeShift = employeeShift;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    @Override
    public String toString() {
        return "TimeRecord{" + "id=" + id + ", actualStartTime=" + actualStartTime + 
                ", actualEndTime=" + actualEndTime + ", employeeShift=" + employeeShift.getId() + '}';
    }
} 