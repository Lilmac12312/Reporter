package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import com.jabyftw.reporter.tasks.RemoveFromListTask;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Rafael
 */
public class ReportExecutor implements CommandExecutor {

    private Reporter reporter;
    private MySQLCon sql;
    private String Formatting;
    public List<Player> alreadyReport;

    public ReportExecutor(Reporter plugin, MySQLCon sql) {
        this.reporter = plugin;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { //TODO: color
        if (sender instanceof Player) {
            if (sender.hasPermission("reporter.report")) {
                if (args.length > 3) {
                    Player p = (Player) sender;
                    if (alreadyReport.contains(p)) {
                        sender.sendMessage("You already reported in the past " + reporter.reportDelay + " minutes!");
                        return true;
                    } else {
                        Location loc = p.getLocation();
                        String reason = combineSplit(2, args);
                        Report r = new Report(reporter, sql, sender.getName().toLowerCase(), args[1].toLowerCase(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), reason, false);
                        r.sendReport();
                        if(!reporter.reloading) { // if its reloading, the report is already sent and will appear on the next list
                            reporter.reports.add(r);
                        }
                        alreadyReport.add(p);
                        sender.sendMessage("Report sent! Our mods will take care of your issue!");
                        reporter.getServer().getScheduler().runTaskLater(reporter, new RemoveFromListTask(this, p), reporter.reportDelay * 20 * 60); // time in minutes * 20 * 60 = time in minutes in ticks
                        return true;
                    }
                } else {
                    /*
                     args[0] = report
                     args[1] = name
                     args[2] = reason
                     3 args minimum
                     */
                    sender.sendMessage("Use: /report (name) (reason)");
                    return true;
                }
            } else {
                sender.sendMessage("You dont have permission!");
                return true;
            }
        } else {
            sender.sendMessage("You are not a player!");
            return true;
        }
    }

    // From: https://github.com/deathmarine/Ultrabans/blob/master/src/com/modcrafting/ultrabans/util/Formatting.java#L47
    // Sorry, I cant do that by myself now
    private String combineSplit(int startIndex, String[] args) {
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

}
