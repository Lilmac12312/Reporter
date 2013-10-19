/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Rafael
 */
public class ReportStatusExecutor implements CommandExecutor {

    private Reporter reporter;
    private MySQLCon sql;

    public ReportStatusExecutor(Reporter plugin, MySQLCon sql) {
        this.reporter = plugin;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("reporter.setreportstatus")) {
            int id = Integer.parseInt(args[0]);
            boolean resolved = resolved(Integer.parseInt(args[1]));

            try {
                Statement s = sql.getConn().createStatement();
                ResultSet rs = s.executeQuery("SELECT `sender`, `reported`, `x`, `y`, `z`, `reason` FROM `" + reporter.tableName + "` WHERE `id`='" + id + "' LIMIT 30;");
                while (rs.next()) {
                    String rsender = rs.getString("sender");
                    String reported = rs.getString("reported");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    String reason = rs.getString("reason");
                    Report r = new Report(reporter, sql, id, rsender, reported, x, y, z, reason, resolved);
                    r.updateStatus();
                    reporter.reloadReports();
                }
                sender.sendMessage("Done!");
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } else {
            sender.sendMessage("You dont have permission for that!");
            return true;
        }
    }

    private boolean resolved(int args) {
        switch(args) {
            case 0:
                return false;
            case 1:
                return true;
        }
        return false;
    }

}
