package com.jabyftw.reporter;

import com.jabyftw.reporter.commands.ReportExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Reporter extends JavaPlugin {

    MySQLCon sql;
    SQL sqlTable;

    private String username, password, url;
    private boolean debugEnabled;
    public String tableName;
    public int rowLimit;
    public List<Report> reports;

    @Override
    public void onEnable() {
        genConfig();

        sql = new MySQLCon(this, username, password, url);
        createTable();
        loadReports();
        
        getCommand("report").setExecutor(new ReportExecutor(this, sql));
    }

    @Override
    public void onDisable() {
        sql.closeConn();
    }

    private void genConfig() {
        FileConfiguration config = getConfig();
        config.addDefault("MySQL.username", "root");
        config.addDefault("MySQL.password", "123");
        config.addDefault("MySQL.reportLimitOnMySQLRequest", "30");
        config.addDefault("MySQL.url.host", "localhost");
        config.addDefault("MySQL.url.port", 3306);
        config.addDefault("MySQL.url.database", "minecraft");
        config.addDefault("MySQL.table", "reporter");
        config.addDefault("Config.debug", "false");
        config.options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
        url = "jdbc:mysql://" + config.getString("MySQL.url.host") + ":" + config.getInt("MySQL.url.port") + "/" + config.getString("MySQL.url.database");
        username = config.getString("MySQL.username");
        password = config.getString("MySQL.password");
        debugEnabled = config.getBoolean("Config.debug");
        tableName = config.getString("MySQL.table");
        rowLimit = config.getInt("MySQL.reportLimitOnMySQLRequest");
    }

    /*
     0 - normal
     1 - debug
     2 - warning
     */
    public void log(int i, String msg) {
        if (i == 0) {
            getLogger().log(Level.INFO, msg);
        } else if (i == 1 && debugEnabled) {
            getLogger().log(Level.OFF, "DEBUG: " + msg);
        } else if (i == 2) {
            getLogger().log(Level.WARNING, msg);
        }
    }

    private void createTable() {
        try {
            log(0, "Creating MySQL Table if not exists...");
            sql.getConn().createStatement().execute(sqlTable.createTable);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadReports() {
        try {
            Statement s = sql.getConn().createStatement();
            ResultSet rs = s.executeQuery(sqlTable.loadReport);
            while (rs.next()) {
                int id = rs.getInt("id");
                String sender = rs.getString("sender");
                String reported = rs.getString("reported");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String reason = rs.getString("reason");
                reports.add(new Report(this, sql, id, sender, reported, x, y, z, reason));
            }
            log(0, "Report list loaded.");
            log(1, "Report list: " + reports.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
