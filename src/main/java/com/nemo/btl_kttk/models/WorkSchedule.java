package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.util.Date;

public class WorkSchedule implements Serializable {
    private int id;
    private String name;
    private Date createDate;
    private User createdBy;
    
    public WorkSchedule() {
    }
    
    public WorkSchedule(int id, String name, Date createDate, User createdBy) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
        this.createdBy = createdBy;
    }
    
    public WorkSchedule(String name, Date createDate, User createdBy) {
        this.name = name;
        this.createDate = createDate;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    @Override
    public String toString() {
        return "WorkSchedule{" + "id=" + id + ", name=" + name + ", createDate=" + createDate + '}';
    }
} 