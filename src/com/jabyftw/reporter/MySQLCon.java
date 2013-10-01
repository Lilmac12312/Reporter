package com.jabyftw.reporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQLCon{
    private Reporter p;
    String user;
    String pass;
    String host;
    int port;
    String db;
    public Connection conn;
    
    public MySQLCon(Reporter plugin, String username, String password, String host, int port, String database) {
        this.p = plugin; // plugin
        this.user = username; this.pass = password; // User, pass;
        this.host = host; this.port = port; this.db = database; // Host, port, db;
    }
    
    public Connection startConn() {
        if(conn != null) {
            return conn;
        } else {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, pass);
                return conn;
            } catch (SQLException e) {
                p.getLogger().log(Level.WARNING, "Couldn't connect to MySQL: " + e.getMessage());
            }
        }
        return null;
    }
    
    public Connection getConn() {
        return conn;
    }
    
    public void closeConn() {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                p.getLogger().log(Level.WARNING, "Couldn't close MySQL Connection: " + e.getMessage());
            }
        }
    }
}
