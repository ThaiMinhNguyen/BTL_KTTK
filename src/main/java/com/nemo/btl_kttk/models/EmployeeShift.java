package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmployeeShift implements Serializable {
    private int id;
    private User employee;
    private ShiftSlot shiftSlot;
    private LocalDateTime registrationDate;
    
    public EmployeeShift() {
    }
    
    public EmployeeShift(int id, User employee, ShiftSlot shiftSlot, LocalDateTime registrationDate) {
        this.id = id;
        this.employee = employee;
        this.shiftSlot = shiftSlot;
        this.registrationDate = registrationDate;
    }
    
    public EmployeeShift(User employee, ShiftSlot shiftSlot, LocalDateTime registrationDate) {
        this.employee = employee;
        this.shiftSlot = shiftSlot;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public ShiftSlot getShiftSlot() {
        return shiftSlot;
    }

    public void setShiftSlot(ShiftSlot shiftSlot) {
        this.shiftSlot = shiftSlot;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    @Override
    public String toString() {
        return "EmployeeShift{" + "id=" + id + ", employee=" + employee.getId() + 
                ", shiftSlot=" + shiftSlot.getId() + ", registrationDate=" + registrationDate + '}';
    }
} 