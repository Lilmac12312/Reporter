package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
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
public class ReporterExecutor implements CommandExecutor {
    
    private final Reporter reporter;
    private final MySQLCon sql;
    
    public ReporterExecutor(Reporter plugin, MySQLCon sql) {
        this.reporter = plugin;
        this.sql = sql;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            return false;
        } else {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("reporter.list")) {
                    if (reporter.reports.size() > 0) {
                        int i = 0;
                        sender.sendMessage(ChatColor.GOLD + "ID" + ChatColor.GRAY + " | " + ChatColor.GOLD + "Sender" + ChatColor.GRAY + " | " + ChatColor.GOLD + "Reported" + ChatColor.GRAY + " | " + ChatColor.GOLD + "X, Y, Z" + ChatColor.GRAY + " | " + ChatColor.GOLD + "Reason");
                        while (i < 10) {
                            for (Report r : reporter.reports) {
                                if (r.isResolved()) {
                                    i--;
                                    reporter.reports.remove(r); // Shouldnt be necessary... but...
                                } else {
                                    i++;
                                    sender.sendMessage(ChatColor.RED + "" + r.getId() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getSender() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getReported() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getX() + ", " + r.getY() + ", " + r.getZ() + ChatColor.GRAY + " | " + ChatColor.RED + "" + r.getReason());
                                }
                            }
                            return true;
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "No reports found.");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (sender.hasPermission("reporter.tp")) {
                    if (sender instanceof Player) {
                        if (args.length > 1) {
                            int id = Integer.parseInt(args[1]);
                            Report r = new Report(reporter, sql, id);
                            if (!r.isNull()) {
                                Player p = (Player) sender;
                                if (r.getW() == null) {
                                    p.sendMessage(ChatColor.RED + "World wasnt found!");
                                    return true;
                                }
                                Location loc = new Location(r.getW(), r.getX(), r.getY(), r.getZ());
                                try {
                                    p.teleport(loc);
                                    p.sendMessage(ChatColor.GOLD + "Done!");
                                    return true;
                                } catch (NullPointerException e) {
                                    p.sendMessage(ChatColor.DARK_RED + "Couldnt teleport! :/");
                                    return true;
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Invalid ID!");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /reporter tp (id)");
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
            } else if (args[0].equalsIgnoreCase("close")) {
                if (sender.hasPermission("reporter.close")) {
                    int id;
                    String result;
                    if (args.length > 2) {
                        id = Integer.parseInt(args[1]);
                        result = reporter.combineSplit(2, args);
                    } else if (args.length > 1) {
                        id = Integer.parseInt(args[1]);
                        result = "didnt mentioned";
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /reporter close (id) [result]");
                        return true;
                    }
                    Report r = new Report(reporter, sql, id);
                    if (!r.isNull()) {
                        if (r.isResolved()) {
                            sender.sendMessage(ChatColor.RED + "Report " + id + " is already closed!");
                            return true;
                        } else {
                            r.setResolved(true);
                            r.setResolver(sender.getName());
                            r.setResult(result);
                            r.updateStatus();
                            reporter.reloadReports();
                            sender.sendMessage(ChatColor.GOLD + "Done!");
                            if (reporter.notifications != null) {
                                Notification notf = new Notification("Closed report: " + r.getId() + "!", "Closed by: " + r.getResolver() + " | Result: " + r.getResult());
                                reporter.notifications.showNotification(notf);
                            }
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid ID!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                    return true;
                }
                
            } else if (args[0].equalsIgnoreCase("reopen")) {
                if (sender.hasPermission("reporter.reopen")) {
                    if (args.length > 1) {
                        int id = Integer.parseInt(args[1]);
                        Report r = new Report(reporter, sql, id);
                        if (!r.isNull()) {
                            if (!r.isResolved()) {
                                sender.sendMessage(ChatColor.RED + "Report " + id + " is already open!");
                                return true;
                            } else {
                                r.setResolved(false);
                                r.updateStatus();
                                reporter.reloadReports();
                                sender.sendMessage(ChatColor.GOLD + "Done!");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Invalid ID!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /reporter reopen (id)");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                    return true;
                }
            } else {
                return false;
            }
        }
    }
}
