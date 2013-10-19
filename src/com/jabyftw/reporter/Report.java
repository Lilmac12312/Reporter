package com.jabyftw.reporter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Rafael
 */
public class Report {

    private Reporter reporter;
    private MySQLCon sql;
    private SQL sqlTable;
    private String sender, reported, reason;
    private int id, x, y, z;

    public Report(Reporter plugin, MySQLCon sql, String sender, String reported, int x, int y, int z, String reason) {
        this.reporter = plugin;
        this.sql = sql;
        this.sender = sender;
        this.reported = reported;
        this.x = x;
        this.y = y;
        this.z = z;
        this.reason = reason;
    }

    public Report(Reporter plugin, MySQLCon sql, int id, String sender, String reported, int x, int y, int z, String reason) {
        this.reporter = plugin;
        this.sql = sql;
        this.id = id;
        this.sender = sender;
        this.reported = reported;
        this.x = x;
        this.y = y;
        this.z = z;
        this.reason = reason;
    }

    public String getSender() {
        return sender;
    }

    public String getReported() {
        return reported;
    }

    public int getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    public void sendReport() {
        try {
            PreparedStatement ps = sql.getConn().prepareStatement(sqlTable.sendReport, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, sender);
            ps.setString(2, reported);
            ps.setInt(3, x);
            ps.setInt(4, y);
            ps.setInt(5, z);
            ps.setString(6, reason);
            ps.execute();
            
            ResultSet rs = ps.getGeneratedKeys();
            while(rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            reporter.log(2, "Failed to send report! " + ex.getMessage());
        }
    }
}
