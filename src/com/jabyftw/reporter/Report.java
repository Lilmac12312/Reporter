package com.jabyftw.reporter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.World;

/**
 * @author Rafael
 */
public class Report {

    private Reporter reporter;
    private MySQLCon sql;
    private String sender, reported, reason;
    private int id, x, y, z;
    private boolean resolved;
    private World w;

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Report(Reporter plugin, MySQLCon sql, int id) {
        this.id = id;
        getInfoById();
    }

    public Report(Reporter plugin, MySQLCon sql, String sender, String reported, World w, int x, int y, int z, String reason, boolean status) {
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
    }

    public Report(Reporter plugin, MySQLCon sql, int id, String sender, String reported, World w, int x, int y, int z, String reason, boolean status) {
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

    public void getInfoById() {
        try {//TODO: result
            Statement s = sql.getConn().createStatement();
            ResultSet rs = s.executeQuery("SELECT `sender`, `reported`, `worldname`, `x`, `y`, `z`, `reason` FROM `" + reporter.tableName + "` WHERE `id`='" + id + "' LIMIT 2;");
            while (rs.next()) {
                sender = rs.getString("sender");
                reported = rs.getString("reported");
                w = reporter.getServer().getWorld(rs.getString("worldname"));
                x = rs.getInt("x");
                y = rs.getInt("y");
                z = rs.getInt("z");
                reason = rs.getString("reason");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertReport() {
        try {
            Statement s = sql.getConn().createStatement();
            s.execute("INSERT INTO `" + reporter.tableName + "` (`sender`, `reported`, `x`, `y`, `z`, `reason`, `resolved`) VALUES ('" + sender + "', '" + reported + "', '" + w.getName() + "', '" + x + "', '" + y + "', '" + z + "', '" + reason + "', false);", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = s.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            reporter.log(2, "Failed to send report! " + ex.getMessage());
        }
    }

    public World getW() {
        return w;
    }

    public void updateStatus() {
        try {
            //TODO: result
            PreparedStatement ps = sql.getConn().prepareStatement("UPDATE `" + reporter.tableName + "` SET `resolved`=? WHERE `id`=?");
            ps.setBoolean(1, resolved);
            ps.setInt(2, id);
            ps.executeUpdate();
            reporter.log(1, id + ":" + resolved);
        } catch (SQLException ex) {
            reporter.log(2, "Failed to update report! " + ex.getMessage());
        }
    }
}
