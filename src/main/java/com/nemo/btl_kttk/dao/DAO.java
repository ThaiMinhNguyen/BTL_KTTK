package com.nemo.btl_kttk.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shift_management";
    private static final String USER = "root";
    private static final String PASS = "22012003";
    
    protected Connection connection;
    
    public DAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected Connection getConnection() {
        return connection;
    }
    
    protected void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 