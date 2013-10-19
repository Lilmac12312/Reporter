/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Rafael
 */
public class ReportStatusExecutor implements CommandExecutor {

    private MySQLCon sql;
    private Reporter reporter;

    public ReportStatusExecutor(Reporter plugin, MySQLCon sql) {
        this.reporter = plugin;
        this.sql = sql;
    }

    // /reportstatus (id) (boolean resolved)
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("reporter.setreportstatus")) {
            for (Report r : reporter.reports) { // This can find more than one report, if admins use that on the wrong way
                int id = Integer.parseInt(args[1]);
                if (id == r.getId()) {
                    boolean resolved = Boolean.parseBoolean(args[2]);
                    r.setResolved(resolved);
                    if (resolved) {
                        reporter.reports.remove(r);
                    }
                    r.updateStatus();
                    sender.sendMessage("Status updated!");
                }
            }
            return true;
        } else {
            sender.sendMessage("You dont have permission for that!");
            return true;
        }
    }

}
