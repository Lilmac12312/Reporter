package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Rafael
 */
public class ReportListExecutor implements CommandExecutor {

    private Reporter reporter;

    public ReportListExecutor(Reporter plugin) {
        this.reporter = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("reporter.listreports")) {
            int i = 0;
            if (reporter.reports.size() > 0) {
                sender.sendMessage(ChatColor.GOLD + "ID " + ChatColor.GRAY + "| " + ChatColor.GOLD + "Sender " + ChatColor.GRAY + "| " + ChatColor.GOLD + "Reported " + ChatColor.GRAY + "| " + ChatColor.GOLD + "X, Y, Z " + ChatColor.GRAY + "| " + ChatColor.GOLD + "Reason");
                while (i < 10) {
                    for (Report r : reporter.reports) {
                        if (!r.isResolved()) {
                            i++;
                            sender.sendMessage(ChatColor.RED + "" + r.getId() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getSender() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getReported() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getX() + ", " + r.getY() + ", " + r.getZ() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getReason());
                        }
                    }
                    return true;
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "No reports found");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You dont have permission to do that!");
            return false;
        }
    }

}
