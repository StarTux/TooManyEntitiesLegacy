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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.scheduler.BukkitRunnable;

public class SweepTask extends BukkitRunnable {
        private TooManyEntitiesPlugin plugin;
        private CommandSender sender;
        private int monstersPerTick;
        private LinkedList<Monster> monsters = new LinkedList<Monster>();

        public SweepTask(TooManyEntitiesPlugin plugin, CommandSender sender, int monstersPerTick) {
                this.plugin = plugin;
                this.sender = sender;
                this.monstersPerTick = monstersPerTick;
        }

        @Override
        public void run() {
                for (int i = 0; i < monstersPerTick; ++i) {
                        if (monsters.isEmpty()) {
                                stop();
                                return;
                        } else {
                                Monster monster = monsters.removeFirst();
                                if (monster.getCustomName() != null) continue;
                                if (!monster.getRemoveWhenFarAway()) continue;
                                monster.remove();
                        }
                }
        }

        public void init() {
                for (World world : plugin.getServer().getWorlds()) {
                        Collection<Monster> e = world.getEntitiesByClass(Monster.class);
                        sender.sendMessage(world.getName() + ": " + e.size() + " monsters");
                        monsters.addAll(e);
                }
                sender.sendMessage("[TooManyEntities] Sweeping monsters");
        }

        public void start() {
                runTaskTimer(plugin, 0L, 1L);
        }

        public void stop() {
                sender.sendMessage("[TooManyEntities] Done");
                try {
                        cancel();
                } catch (Exception e) {}
        }
}
