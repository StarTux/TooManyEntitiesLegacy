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
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class TooManyEntitiesTask extends BukkitRunnable {
        TooManyEntitiesPlugin plugin;
        private CommandSender sender;
        private int limit;
        private double radius;
        private int checksPerTick;
        private LinkedList<Entity> entities = new LinkedList<Entity>();
        private Set<Entity> findings = new HashSet<Entity>();
        private EntityType filter = null;

        public TooManyEntitiesTask(TooManyEntitiesPlugin plugin, CommandSender sender,
                                   int limit, double radius, EntityType filter, int checksPerTick) {
                this.plugin = plugin;
                this.sender = sender;
                this.limit = limit;
                this.radius = radius;
                this.filter = filter;
                this.checksPerTick = checksPerTick;
        }

        @Override
        public void run() {
                for (int i = 0; i < checksPerTick; ++i) {
                        if (entities.isEmpty()) {
                                stop();
                                return;
                        } else {
                                Entity entity = entities.removeFirst();
                                if (!entity.isValid()) continue;
                                if (findings.contains(entity)) continue;
                                if (filter != null && entity.getType() != filter) continue;
                                List<Entity> tmp = entity.getNearbyEntities(radius, radius, radius);
                                List<Entity> nearby = new ArrayList<Entity>(tmp.size() + 1);
                                for (Entity e : tmp) {
                                        if (filter == null || e.getType() == filter) nearby.add(e);
                                }
                                nearby.add(entity);
                                if (nearby.size() > limit) {
                                        report(nearby);
                                        findings.add(entity);
                                        findings.addAll(nearby);
                                }
                        }
                }
        }

        public void report(List<Entity> entities) {
                Location loc = entities.get(0).getLocation();
                EntityType top = null;
                int max = 0;
                EnumMap<EntityType, Integer> entityCount = new EnumMap<EntityType, Integer>(EntityType.class);
                for (Entity entity : entities) {
                        int count = 1;
                        Integer tmp = entityCount.get(entity.getType());
                        if (tmp != null) count = tmp + 1;
                        entityCount.put(entity.getType(), count);
                        if (count > max) {
                                max = count;
                                top = entity.getType();
                                loc = entity.getLocation(loc);
                        }
                }
                sender.sendMessage(String.format("[TME] %s%d entities at %s:%d,%d,%d, mostly %s",
                                                 ChatColor.RED, entities.size(), loc.getWorld().getName(),
                                                 loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                                                 niceEntityName(top)));
        }

        public void init() {
                for (World world : plugin.getServer().getWorlds()) {
                        List<Entity> e = world.getEntities();
                        sender.sendMessage(world.getName() + ": " + e.size() + " entities");
                        entities.addAll(e);
                }
                sender.sendMessage("[TooManyEntities] Scanning");
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

        private static String niceEntityName(EntityType e) {
                return e.name().toLowerCase().replaceAll("_", " ");
        }
}
