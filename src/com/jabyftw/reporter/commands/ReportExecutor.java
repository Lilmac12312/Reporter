package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import com.jabyftw.reporter.tasks.RemoveFromListTask;
import java.util.ArrayList;
import java.util.List;
import me.muizers.Notifications.Notification;
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

    private final Reporter reporter;
    private final MySQLCon sql;
    public List<Player> alreadyReport = new ArrayList<Player>();

    public ReportExecutor(Reporter plugin, MySQLCon sql) {
        this.reporter = plugin;
        this.sql = sql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) { // just /report
            return false;
        } else {

            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.YELLOW + "Report'em all Plugin: Command list");
                if (sender.hasPermission("reporter.report")) {
                    sender.sendMessage(ChatColor.GOLD + "/report (name) (reason)");
                }
                if (sender.hasPermission("reporter.report.info")) {
                    sender.sendMessage(ChatColor.GOLD + "/report info (id)");
                }
                if (sender.hasPermission("reporter.list")) {
                    sender.sendMessage(ChatColor.GOLD + "/reporter list");
                }
                if (sender.hasPermission("reporter.tp")) {
                    sender.sendMessage(ChatColor.GOLD + "/reporter tp (id)");
                }
                if (sender.hasPermission("reporter.reopen")) {
                    sender.sendMessage(ChatColor.GOLD + "/reporter reopen (id)");
                }
                if (sender.hasPermission("reporter.close")) {
                    sender.sendMessage(ChatColor.GOLD + "/reporter close (id) (result)");
                }
                return true;

            } else if (args[0].equalsIgnoreCase("info")) {
                if (sender.hasPermission("reporter.report.info")) {
                    if (args.length > 1) {
                        int id = Integer.parseInt(args[1]);
                        reporter.log(1, "ID: " + id + " | Args: " + args[0] + args[1]);
                        try {
                            Report r = new Report(reporter, sql, id);
                            if (r.getSender().equals(sender.getName()) || sender.hasPermission("reporter.report.info.others")) {
                                sender.sendMessage(reporter.getLang("lang.reporterInfo").replaceAll("%sender", r.getSender()).replaceAll("%reported", r.getReported()).replaceAll("%X", Integer.toString(r.getX())).replaceAll("%Y", Integer.toString(r.getY())).replaceAll("%Z", Integer.toString(r.getZ())));
                                sender.sendMessage(reporter.getLang("lang.reason").replaceAll("%reason", r.getReason()));
                                if (r.isResolved()) {
                                    sender.sendMessage(reporter.getLang("lang.resolvedYes").replaceAll("%result", r.getResult()).replaceAll("%owner", r.getResolver()));
                                } else {
                                    sender.sendMessage(reporter.getLang("lang.resolvedNo"));
                                }
                                return true;

                            } else {
                                sender.sendMessage(reporter.getLang("lang.cantSeeOtherPlayerReport"));
                                return true;
                            }
                        } catch (NullPointerException npe) {
                            sender.sendMessage(reporter.getLang("lang.invalidId"));
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    sender.sendMessage(reporter.getLang("lang.noPermission"));
                    return true;
                }
            } else {
                if (sender.hasPermission("reporter.report")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (args.length > 1) {
                            if (alreadyReport.contains(p)) {
                                sender.sendMessage(reporter.getLang("lang.alreadyReported").replaceAll("%time", reporter.config.getString("Config.reportDelayInMinutes")));
                                return true;
                            } else {
                                Location loc = p.getLocation();
                                String reason = reporter.combineSplit(1, args);
                                Report r = new Report(reporter, sql, sender.getName(), args[0], loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), reason, false, null, null);
                                r.insertReport();
                                reporter.reports.add(r);
                                alreadyReport.add(p);
                                reporter.getServer().getScheduler().runTaskLater(reporter, new RemoveFromListTask(this, p), reporter.config.getInt("Config.reportDelayInMinutes") * 20 * 60);
                                sender.sendMessage(reporter.getLang("lang.reportSent"));
                                if (sender.hasPermission("reporter.report.info")) {
                                    sender.sendMessage(reporter.getLang("lang.reportInfo").replaceAll("%id", Integer.toString(r.getId())));
                                }
                                for(Player player : reporter.getServer().getOnlinePlayers()) {
                                    if(player.hasPermission("reporter.report.notify")) {
                                        player.sendMessage(reporter.getLang("lang.reportNotification").replaceAll("%id", Integer.toString(r.getId())).replaceAll("%sender", r.getSender()));
                                    }
                                }
                                if (reporter.notifications != null) {
                                    Notification notf = new Notification("New report from " + sender.getName() + " @ " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ(), "Reported: " + r.getReported() + " | Reason: " + reason);
                                    reporter.notifications.showNotification(notf);
                                }
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        sender.sendMessage(reporter.getLang("lang.onlyIngame"));
                        return true;
                    }
                } else {
                    sender.sendMessage(reporter.getLang("lang.noPermission"));
                    return true;
                }
            }
        }
    }
}
