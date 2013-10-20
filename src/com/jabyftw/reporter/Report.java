package com.jabyftw.reporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.World;

/**
 * @author Rafael
 */
public final class Report {

    private Reporter reporter;
    private MySQLCon sql;
    private String sender, reported, reason, result;
    private int id, x, y, z;
    private boolean resolved;
    private World w;

    public Report(Reporter plugin, MySQLCon sql, int id) {
        this.id = id;
        this.sql = sql;
        getInfoById();
    }

    public Report(Reporter plugin, MySQLCon sql, String sender, String reported, World w, int x, int y, int z, String reason, boolean status, String result) {
        this.reporter = plugin;
        this.sql = sql;
        this.sender = sender;
        this.reported = reported;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.reason = reason;
        this.resolved = status;
        this.result = result;
    }

    public Report(Reporter plugin, MySQLCon sql, int id, String sender, String reported, World w, int x, int y, int z, String reason, boolean status, String result) {
        this.reporter = plugin;
        this.sql = sql;
        this.id = id;
        this.sender = sender;
        this.reported = reported;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.reason = reason;
        this.resolved = status;
        this.result = result;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public World getW() {
        return w;
    }

    public void getInfoById() {
        try {
            Statement s = sql.getConn().createStatement();
            ResultSet rs = s.executeQuery("SELECT `sender`, `reported`, `worldname`, `x`, `y`, `z`, `reason`, `resolved`, `result` FROM `" + reporter.tableName + "` WHERE `id`=" + id + " LIMIT 2;");
            while (rs.next()) {
                sender = rs.getString("sender");
                reported = rs.getString("reported");
                w = reporter.getServer().getWorld(rs.getString("worldname"));
                x = rs.getInt("x");
                y = rs.getInt("y");
                z = rs.getInt("z");
                reason = rs.getString("reason");
                resolved = rs.getBoolean("resolved");
                result = rs.getString("result");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertReport() {
        try {
            Statement s = sql.getConn().createStatement();
            s.execute("INSERT INTO `reporter`(`sender`, `reported`, `worldname`, `x`, `y`, `z`, `reason`, `resolved`, `result`) VALUES ('" + sender + "','" + reported + "','" + w.getName() + "','" + x + "','" + y + "','" + z + "','" + reason + "'," + resolved + ",'" + result + "')", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = s.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            reporter.log(2, "Failed to send report! " + ex.getMessage());
        }
    }

    public void updateStatus() {
        try {
            Statement s = sql.getConn().createStatement();
            s.executeUpdate("UPDATE `" + reporter.tableName + "` SET `resolved`='" + resolved + "',`result`='" + result + "' WHERE `id`='" + id + "';");
            reporter.log(1, id + ":" + resolved + ":" + result);
        } catch (SQLException ex) {
            reporter.log(2, "Failed to update report! " + ex.getMessage());
        }
    }
}
