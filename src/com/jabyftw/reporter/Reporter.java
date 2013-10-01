package com.jabyftw.reporter;

import java.sql.Connection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Reporter extends JavaPlugin {
    FileConfiguration config;
    Connection c;
    MySQLCon MySQL;
    
    @Override
    public void onEnable() {
        config = getConfig();
        genConfig();
        MySQL = new MySQLCon(this, config.getString("mySQL.username"), config.getString("mySQL.password"),
                config.getString("mySQL.host"), config.getInt("mySQL.port"), config.getString("mySQL.database"));
        c = MySQL.startConn();
    }
    
    @Override
    public void onDisable() {
        MySQL.closeConn();
    }
    
    void genConfig() {
        config.addDefault("mySQL.username", "root");
        config.addDefault("mySQL.password", "123");
        config.addDefault("mySQL.host", "localhost");
        config.addDefault("mySQL.port", 3306);
        config.addDefault("mySQL.database", "minecraft");
        config.options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
    }
}
