package com.jabyftw.reporter.commands;

import com.jabyftw.reporter.MySQLCon;
import com.jabyftw.reporter.Report;
import com.jabyftw.reporter.Reporter;
import com.jabyftw.reporter.tasks.RemoveFromListTask;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) { // just /report
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (sender.hasPermission("reporter.report.list")) {
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

                } else {
                    sender.sendMessage(ChatColor.RED + "No reports found.");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                return true;
            }

        } else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GOLD + "/report (name) (reason)");
            sender.sendMessage(ChatColor.GOLD + "/report list");
            sender.sendMessage(ChatColor.GOLD + "/report tp (id)");
            sender.sendMessage(ChatColor.GOLD + "/report info (id)");
            sender.sendMessage(ChatColor.GOLD + "/report reopen (id)");
            sender.sendMessage(ChatColor.GOLD + "/report close (id) (result)");
            return true;

        } else if (args[0].equalsIgnoreCase("tp")) {
            if (sender.hasPermission("reporter.report.tp")) {
                if (sender instanceof Player) {
                    if (args.length > 1) {
                        int id = Integer.parseInt(args[1]);
                        Report r = new Report(reporter, sql, id);
                        Player p = (Player) sender;
                        p.teleport(new Location(r.getW(), r.getX(), r.getY(), r.getZ()));
                        p.sendMessage(ChatColor.GOLD + "Done!");
                        return true;
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

        } else if (args[0].equalsIgnoreCase("info")) {
            if (sender.hasPermission("reporter.report.info")) {
                if (args.length > 1) {
                    int id = Integer.parseInt(args[1]);
                    reporter.log(1, "ID: " + id + " | Args: " + args[0] + args[1]);
                    Report r = new Report(reporter, sql, id);
                    sender.sendMessage(ChatColor.GOLD + "Reporter: " + ChatColor.YELLOW + r.getSender() + ChatColor.GRAY + " | " + ChatColor.GOLD + "Reported: " + ChatColor.YELLOW + r.getReported() + ChatColor.GRAY + " | " + ChatColor.GOLD + "Coords: " + ChatColor.YELLOW + r.getX() + ", " + r.getY() + ", " + r.getZ());
                    sender.sendMessage(ChatColor.GOLD + "Reason: " + ChatColor.YELLOW + r.getReason());
                    if (r.isResolved()) {
                        sender.sendMessage(ChatColor.GOLD + "Resolved: " + ChatColor.YELLOW + "yes" + ChatColor.GRAY + " | " + ChatColor.GOLD + "Result: " + ChatColor.YELLOW + r.getResult());
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Resolved: " + ChatColor.DARK_RED + "no");
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /reporter info (id)");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                return true;
            }

        } else if (args[0].equalsIgnoreCase("close")) {
            if (sender.hasPermission("reporter.report.close")) {
                int id;
                String result;
                if (args.length > 2) {
                    id = Integer.parseInt(args[1]);
                    result = combineSplit(2, args);
                } else if (args.length > 1) {
                    id = Integer.parseInt(args[1]);
                    result = "didnt mentioned";
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /reporter close (id) [result]");
                    return true;
                }
                Report r = new Report(reporter, sql, id);
                if (r.isResolved()) {
                    sender.sendMessage(ChatColor.RED + "Report " + id + " is already closed!");
                    return true;
                } else {
                    r.setResolved(true);
                    r.setResult(result);
                    r.updateStatus();
                    reporter.reloadReports();
                    sender.sendMessage(ChatColor.GOLD + "Done!");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "You dont have permission to do that!");
                return true;
            }

        } else if (args[0].equalsIgnoreCase("reopen")) {
            if (sender.hasPermission("reporter.report.reopen")) {
                if (args.length > 1) {
                    int id = Integer.parseInt(args[1]);
                    Report r = new Report(reporter, sql, id);
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
                    sender.sendMessage(ChatColor.RED + "Usage: /reporter reopen (id)");
                    return true;
                }
            }

        } else {
            if (sender.hasPermission("reporter.report")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length > 1) {
                        if (alreadyReport.contains(p)) {
                            sender.sendMessage(ChatColor.RED + "You already reported in the past " + ChatColor.DARK_RED + reporter.reportDelay + ChatColor.RED + " minutes!");
                            return true;
                        } else {
                            Location loc = p.getLocation();
                            String reason = combineSplit(1, args);
                            Report r = new Report(reporter, sql, sender.getName(), args[0], loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), reason, false, null);
                            r.insertReport();
                            reporter.reports.add(r);
                            alreadyReport.add(p);
                            reporter.getServer().getScheduler().runTaskLater(reporter, new RemoveFromListTask(this, p), reporter.reportDelay * 20 * 60);
                            sender.sendMessage(ChatColor.YELLOW + "Report sent! " + ChatColor.GOLD + "Our mods will take care of your issue!");
                            sender.sendMessage(ChatColor.GOLD + "You can watch your report by using " + ChatColor.RED + "/report info " + r.getId());
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
        return false;
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
