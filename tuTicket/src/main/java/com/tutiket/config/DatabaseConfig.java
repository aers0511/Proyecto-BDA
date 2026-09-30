package com.tutiket.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/ticketfan?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el Driver JDBC de MySQL", e);
        }
    }

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocalConnection.get();
        if (conn != null && !conn.isClosed()) {
            return conn;
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void beginTransaction() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(false);
        threadLocalConnection.set(conn);
    }

    public static void commitTransaction() throws SQLException {
        Connection conn = threadLocalConnection.get();
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                conn.close();
                threadLocalConnection.remove();
            }
        }
    }

    public static void rollbackTransaction() {
        Connection conn = threadLocalConnection.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            } finally {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
                threadLocalConnection.remove();
            }
        }
    }
}