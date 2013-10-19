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
public class ReportListExecutor implements CommandExecutor {

    private Reporter reporter;

    public ReportListExecutor(Reporter plugin) {
        this.reporter = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("reporter.listreports")) {
            sender.sendMessage("ID | Sender | Reported | X, Y, Z | Reason");
            int i = 0;
            for (Report r : reporter.reports) {
                while (i < 9) { // max number of report lines
                    if (!r.isResolved()) {
                        i++;
                        sender.sendMessage(r.getId() + " | " + r.getSender() + " | " + r.getReported() + " | " + r.getX() + ", " + r.getY() + ", " + r.getZ() + " | " + r.getReason());
                    }
                }
            }
            return true;
        } else {
            sender.sendMessage("You dont have permission to do that!");
            return false;
        }
    }

}
