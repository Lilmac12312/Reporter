package com.jabyftw.reporter;

import com.jabyftw.reporter.commands.ReporterExecutor;
import com.jabyftw.reporter.commands.ReportExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import me.muizers.Notifications.Notifications;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Reporter extends JavaPlugin {

    MySQLCon sql;

    public List<Report> reports = new ArrayList<Report>();
    public FileConfiguration config;
    public Notifications notifications;

    @Override
    public void onEnable() {
        config = getConfig();
        genConfig();

        sql = new MySQLCon(this, config.getString("MySQL.username"), config.getString("MySQL.password"), "jdbc:mysql://" + config.getString("MySQL.url.host") + ":" + config.getInt("MySQL.url.port") + "/" + config.getString("MySQL.url.database"));
        createTable();
        loadReports();
        log(0, "Loaded " + reports.size() + " reports.");

        if (config.getBoolean("Config.useNotifications")) {
            notifications = (Notifications) getServer().getPluginManager().getPlugin("Notifications");
            if (notifications == null) {
                log(0, "Notifications plugin not found! But we're running!");
            } else {
                log(0, "Linked with Notifications! (;");
            }
        }

        getCommand("report").setExecutor(new ReportExecutor(this, sql));
        getCommand("reporter").setExecutor(new ReporterExecutor(this, sql));
    }

    @Override
    public void onDisable() {
        sql.closeConn();
    }

    private void genConfig() {
        config.addDefault("MySQL.username", "root");
        config.addDefault("MySQL.password", "123");
        config.addDefault("MySQL.table", "reporter");
        config.addDefault("MySQL.url.host", "localhost");
        config.addDefault("MySQL.url.port", 3306);
        config.addDefault("MySQL.url.database", "minecraft");
        config.addDefault("Config.reportDelayInMinutes", 30);
        config.addDefault("Config.useNotifications", true);
        config.addDefault("Config.debug", false);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }

    /*
     0 - normal
     1 - debug
     2 - warning
     */
    public void log(int i, String msg) {
        switch (i) {
            case 0:
                getLogger().log(Level.INFO, msg);
                break;
            case 1:
                if (config.getBoolean("Config.debug")) {
                    getLogger().log(Level.OFF, "DEBUG: " + msg);
                }
                break;
            case 2:
                getLogger().log(Level.WARNING, msg);
                break;
        }
    }

    // From: https://github.com/deathmarine/Ultrabans/blob/master/src/com/modcrafting/ultrabans/util/Formatting.java#L47
    // Sorry, I cant do that by myself now
    public String combineSplit(int startIndex, String[] args) {
        StringBuilder builder = new StringBuilder();
        if (args.length >= 1) {
            for (int i = startIndex; i < args.length; i++) {
                builder.append(args[i]);
                builder.append(" ");
            }

            if (builder.length() > 1) {
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            }
        }
        return null;
    }

    private void createTable() {
        try {
            log(0, "Creating MySQL Table if not exists...");
            Statement s = sql.getConn().createStatement();
            s.execute("CREATE TABLE IF NOT EXISTS `" + config.getString("MySQL.table") + "` (`id` int(11) NOT NULL AUTO_INCREMENT, `sender` varchar(32) NOT NULL, `reported` varchar(32) NOT NULL, `worldname` varchar(56) NOT NULL, `x` int(11) NOT NULL DEFAULT '0', `y` int(11) NOT NULL DEFAULT '0', `z` int(11) NOT NULL DEFAULT '0', `reason` text NOT NULL, `resolved` tinyint(1) NOT NULL DEFAULT '0', `result` text, `resolver` varchar(32) NOT NULL DEFAULT 'Nobody', PRIMARY KEY (`id`)) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;");
        } catch (SQLException ex) {
            log(2, "Cant create MySQL Table: " + ex.getMessage());
        }
    }

    private void loadReports() {
        try {
            Statement s = sql.getConn().createStatement();
            ResultSet rs = s.executeQuery("SELECT `id`, `sender`, `reported`, `worldname`, `x`, `y`, `z`, `reason` FROM `" + config.getString("MySQL.table") + "` WHERE `resolved`=FALSE LIMIT 30;"); // will only load non-resolved issues
            while (rs.next()) {
                int id = rs.getInt("id");
                String sender = rs.getString("sender");
                String reported = rs.getString("reported");
                World w = getServer().getWorld(rs.getString("worldname"));
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String reason = rs.getString("reason");
                log(1, "id: " + id);
                reports.add(new Report(this, sql, id, sender, reported, w, x, y, z, reason, false, null, null));
            }
        } catch (SQLException ex) {
            log(2, "Cant load Reporter's table: " + ex.getMessage());
        }
    }

    public void reloadReports() {
        reports.clear();
        loadReports();
    }
}
