package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import com.jabyftw.reporter.tasks.RemoveFromListTask;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
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
    public List<Player> alreadyReport = new ArrayList<Player>();

    public ReportExecutor(Reporter plugin, MySQLCon sql) {
        this.reporter = plugin;
        this.sql = sql;
    }
    
    /*
    TODO: make ALL commands like:
    /report tp (id)
    /report set (id) boolean
    /report list
    /report (name) (reason)
    */

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("reporter.report")) {
                if (args.length >= 2) {
                    Player p = (Player) sender;
                    if (alreadyReport.contains(p)) {
                        sender.sendMessage(ChatColor.RED + "You already reported in the past " + ChatColor.DARK_RED + reporter.reportDelay + " minutes!");
                        return true;
                    } else {
                        Location loc = p.getLocation();
                        String reason = combineSplit(1, args);
                        Report r = new Report(reporter, sql, sender.getName().toLowerCase(), args[0].toLowerCase(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), reason, false);
                        r.insertReport();
                        reporter.reports.add(r);
                        alreadyReport.add(p);
                        sender.sendMessage(ChatColor.AQUA + "Report sent! " + ChatColor.BLUE + "Our mods will take care of your issue!");
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
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You dont have permission!");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You are not a player!");
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
