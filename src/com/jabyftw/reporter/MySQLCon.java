package com.jabyftw.reporter;

import java.sql.*;
import java.util.logging.Level;

public class MySQLCon {

    private Reporter reporter;
    String user;
    String pass;
    String url;
    public Connection conn = null;

    public MySQLCon(Reporter plugin, String username, String password, String url) {
        this.reporter = plugin;
        this.user = username;
        this.pass = password;
        this.url = url;
        reporter.log(1, "URL: " + url);
    }

    public Connection getConn() {
        if (conn != null) {
            reporter.log(1, "Returned conn");
            return conn;
        }
        try {
            conn = DriverManager.getConnection(url, user, pass);
            reporter.log(1, "Reconnected");
            return conn;
        } catch (SQLException e) {
            reporter.getLogger().log(Level.WARNING, "Couldn't connect to MySQL: " + e.getMessage());
        }
        reporter.log(1, "Returned null");
        return null;
    }

    public void closeConn() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                reporter.getLogger().log(Level.WARNING, "Couldn't connect to MySQL: " + ex.getMessage());
            }
        }
    }
}
