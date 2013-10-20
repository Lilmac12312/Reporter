/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jabyftw.reporter.tasks;

import com.jabyftw.reporter.commands.ReportExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Rafael
 */
public class RemoveFromListTask extends BukkitRunnable {

    private ReportExecutor r;
    private Player p;

    public RemoveFromListTask(ReportExecutor rex, Player p) {
        this.r = rex;
        this.p = p;
    }

    @Override
    public void run() {
        if (r.alreadyReport.contains(p)) {
            r.alreadyReport.remove(p);
        }
    }
}
