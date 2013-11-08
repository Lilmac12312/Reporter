package com.jabyftw.reporter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.World;

/**
 * @author Rafael
 */
public final class Report {

    private final Reporter reporter;
    private final MySQLCon sql;
    private String sender, reported, reason, result, resolver;
    private int id, x, y, z;
    private boolean resolved, isNull;
    private World w;

    public Report(Reporter plugin, MySQLCon sql, int id) {
        this.reporter = plugin;
        this.id = id;
        this.sql = sql;
        getInfoById();
    }

    public Report(Reporter plugin, MySQLCon sql, String sender, String reported, World w, int x, int y, int z, String reason, boolean status, String result, String resolver) {
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
        this.resolver = resolver;
        this.isNull = false;
    }

    public Report(Reporter plugin, MySQLCon sql, int id, String sender, String reported, World w, int x, int y, int z, String reason, boolean status, String result, String resolver) {
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
        this.resolver = resolver;
        this.isNull = false;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
    
    public boolean isNull() {
        return isNull;
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
            ResultSet rs = s.executeQuery("SELECT `sender`, `reported`, `worldname`, `x`, `y`, `z`, `reason`, `resolved`, `result`, `resolver` FROM `" + reporter.config.getString("MySQL.table") + "` WHERE `id`=" + id + " LIMIT 2;");
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
                resolver = rs.getString("resolver");
                isNull = false;
                return;
            }
            isNull = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertReport() {
        try {
            Statement s = sql.getConn().createStatement();
            s.execute("INSERT INTO `reporter`(`sender`, `reported`, `worldname`, `x`, `y`, `z`, `reason`, `resolved`, `result`, `resolver`) VALUES ('" + sender + "','" + reported + "','" + w.getName() + "','" + x + "','" + y + "','" + z + "','" + reason + "'," + resolved + ",'" + result + "', '" + resolver + "')", Statement.RETURN_GENERATED_KEYS);
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
            s.executeUpdate("UPDATE `" + reporter.config.getString("MySQL.table") + "` SET `resolved`=" + resolved + ",`result`='" + result + "',`resolver`='" + resolver + "' WHERE `id`='" + id + "';");
            reporter.log(1, id + ":" + resolved + ":" + result + " by " + resolver);
        } catch (SQLException ex) {
            reporter.log(2, "Failed to update report! " + ex.getMessage());
        }
    }
}
