/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2012 StarTux
 *
 * This file is part of TooManyEntities.
 *
 * TooManyEntities is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later
 * version.
 *
 * TooManyEntities is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with TooManyEntities.  If not, see
 * <http://www.gnu.org/licenses/>.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.winthier.toomanyentities;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class TooManyEntitiesPlugin extends JavaPlugin {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String token, String args[]) {
                if (args.length >= 1 && args.length <= 4 && args[0].equals("scan")) {
                        int limit = 100;
                        double radius = 1.0;
                        if (args.length >= 2) {
                                try {
                                        limit = Integer.parseInt(args[1]);
                                } catch (NumberFormatException nfe) {
                                        sender.sendMessage("" + ChatColor.RED + "Number expected: " + args[1]);
                                        return true;
                                }
                                if (limit < 0) {
                                        sender.sendMessage("" + ChatColor.RED + "Limit must be positive");
                                        return true;
                                }
                        }
                        if (args.length >= 3) {
                                try {
                                        radius = Double.parseDouble(args[2]);
                                } catch (NumberFormatException nfe) {
                                        sender.sendMessage("" + ChatColor.RED + "Number expected: " + args[2]);
                                        return true;
                                }
                                if (radius < 0.0) {
                                        sender.sendMessage("" + ChatColor.RED + "Radius must be positive");
                                        return true;
                                }
                        }
                        EntityType filter = null;
                        if (args.length >= 4) {
                                try {
                                        filter = EntityType.valueOf(args[3].toUpperCase().replaceAll("-", "_"));
                                } catch (IllegalArgumentException iae) {}
                                if (filter == null) {
                                        sender.sendMessage("" + ChatColor.RED + "Unknown entity type: " + args[3]);
                                        return true;
                                }
                        }
                        TooManyEntitiesTask task = new TooManyEntitiesTask(this, sender, limit, radius, filter, 300);
                        task.init();
                        task.start();
                        return true;
                }
                if (args.length == 1 && args[0].equals("sweep")) {
                        SweepTask task = new SweepTask(this, sender, 500);
                        task.init();
                        task.start();
                        return true;
                }
                return false;
        }
}
