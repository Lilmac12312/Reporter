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
    private String Formatting;
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
                        Report r = new Report(reporter, sql, id);
                        if (r.getSender().equals(sender.getName()) || sender.hasPermission("reporter.report.info.others")) {
                            if (!r.isNull()) {
                                sender.sendMessage(ChatColor.GOLD + "Reporter: " + ChatColor.YELLOW + r.getSender() + ChatColor.GRAY + " | " + ChatColor.GOLD + "Reported: " + ChatColor.YELLOW + r.getReported() + ChatColor.GRAY + " | " + ChatColor.GOLD + "Coords: " + ChatColor.YELLOW + r.getX() + ", " + r.getY() + ", " + r.getZ());
                                sender.sendMessage(ChatColor.GOLD + "Reason: " + ChatColor.YELLOW + r.getReason());
                                if (r.isResolved()) {
                                    sender.sendMessage(ChatColor.GOLD + "Resolved: " + ChatColor.YELLOW + "yes" + ChatColor.GRAY + " | " + ChatColor.GOLD + "Result: " + ChatColor.YELLOW + r.getResult() + ChatColor.GRAY + " | " + ChatColor.GOLD + "Resolved by: " + ChatColor.YELLOW + r.getResolver());
                                } else {
                                    sender.sendMessage(ChatColor.GOLD + "Resolved: " + ChatColor.DARK_RED + "no");
                                }
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid ID!");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You cant see a report of another player!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /reporter info (id)");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                    return true;
                }
            } else {
                if (sender.hasPermission("reporter.report")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (args.length > 1) {
                            if (alreadyReport.contains(p)) {
                                sender.sendMessage(ChatColor.RED + "You already reported in the past " + ChatColor.DARK_RED + reporter.config.getInt("Config.reportDelayInMinutes") + ChatColor.RED + " minutes!");
                                return true;
                            } else {
                                Location loc = p.getLocation();
                                String reason = reporter.combineSplit(1, args);
                                Report r = new Report(reporter, sql, sender.getName(), args[0], loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), reason, false, null, null);
                                r.insertReport();
                                reporter.reports.add(r);
                                alreadyReport.add(p);
                                reporter.getServer().getScheduler().runTaskLater(reporter, new RemoveFromListTask(this, p), reporter.config.getInt("Config.reportDelayInMinutes") * 20 * 60);
                                sender.sendMessage(ChatColor.YELLOW + "Report sent! " + ChatColor.GOLD + "Our mods will take care of your issue!");
                                if (sender.hasPermission("reporter.report.info")) {
                                    sender.sendMessage(ChatColor.GOLD + "You can watch your report by using " + ChatColor.RED + "/report info " + r.getId());
                                }
                                if (reporter.notifications != null) {
                                    Notification notf = new Notification("New report from " + sender.getName() + " @ " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ(), "Reason: " + reason);
                                    reporter.notifications.showNotification(notf);
                                }
                                return true;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Usage: /reporter (name) (reason)");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "This command cant be executed on Console!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                    return true;
                }
            }
        }
    }
}
