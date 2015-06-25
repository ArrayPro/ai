package me.jrl1004.java.pathfinder.swarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.jrl1004.java.pathfinder.main.PathfinderMain;
import net.minecraft.server.v1_8_R2.AttributeInstance;
import net.minecraft.server.v1_8_R2.EntityInsentient;
import net.minecraft.server.v1_8_R2.GenericAttributes;
import net.minecraft.server.v1_8_R2.PathEntity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitRunnable;

public class SwarmManager implements Listener {

    private static final Random rand = new Random();

    private static final List<String> spawnedByPlugin = new ArrayList<String>();

    public SwarmManager() {
        new BukkitRunnable() {

            public void run() {
                grouping();
            }

        }.runTaskTimer(PathfinderMain.instance, 5, 0);
    }

    private void grouping() {
        List<Integer> ac = new ArrayList<Integer>();
        for (World w : Bukkit.getWorlds())
            for (Entity e : w.getEntities()) {
                if (!ac.contains(e.getEntityId()) && e instanceof Creature) {
                    List<Integer> i = new ArrayList<Integer>();
                    for (Entity e2 : e.getNearbyEntities(8, 8, 8)) {
                        if (e2.getType() == e.getType()) {
                            if (e2 instanceof Creature)
                                i.add(e2.getEntityId());
                            ac.add(e2.getEntityId());
                        }
                    }
                    int[] ids = new int[i.size()];
                    int c = 0;
                    for (Integer in : i) {
                        ids[c] = in.intValue();
                        c++;
                    }
                    Arrays.sort(ids);

                    if (ids.length > 0) {
                        Entity r = getEntityById(ids[0]);
                        if (r instanceof Creature) {
                            Creature leader = (Creature) getEntityById(ids[0]);

                            for (Entity e2 : e.getNearbyEntities(8, 8, 8)) {
                                if (e2.getType() == leader.getType()) {
                                    if (e2 instanceof Creature) {
                                        Creature cr = (Creature) e2;
                                        Location loc = leader.getLocation();
                                        if (loc.distance(e2.getLocation()) > 3) {
                                            mobWalk(getBlockBehind(loc).getLocation(), cr, 0.03d * loc.distance(e2.getLocation()));
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }
    }

    private void mobWalk(final Location loc, final LivingEntity pet, final double speed) {
        if (!pet.isValid() || pet.isDead()) {
            return;
        }
        net.minecraft.server.v1_8_R2.Entity pett = ((CraftEntity) pet).getHandle();
        ((EntityInsentient) pett).getNavigation().a(2);
        Object petf = ((CraftEntity) pet).getHandle();
        Location targetLocation = loc;
        PathEntity path;
        path = ((EntityInsentient) petf).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        if (path != null) {
            ((EntityInsentient) petf).getNavigation().a(path, 1.0D);
            ((EntityInsentient) petf).getNavigation().a(2.0D);
        }
        AttributeInstance attributes = ((EntityInsentient) ((CraftEntity) pet).getHandle()).getAttributeInstance(GenericAttributes.d);
        attributes.setValue(speed);
    }

    private Entity getEntityById(int id) {
        for (World w : Bukkit.getWorlds())
            for (Entity e : w.getEntities()) {
                if (e.getEntityId() == id) {
                    return e;
                }
            }
        return null;
    }

    private Block getBlockBehind(Location loc) {
        World world = loc.getWorld();
        Block behind = loc.getBlock();
        int direction = (int) loc.getYaw();

        if (direction < 0) {
            direction += 360;
            direction = (direction + 45) / 90;
        } else {
            direction = (direction + 45) / 90;
        }

        switch (direction) {
        case 1:
            behind = world.getBlockAt(behind.getX() + 1, behind.getY(), behind.getZ());
            break;
        case 2:
            behind = world.getBlockAt(behind.getX(), behind.getY(), behind.getZ() + 1);
            break;
        case 3:
            behind = world.getBlockAt(behind.getX() - 1, behind.getY(), behind.getZ());
            break;
        case 4:
            behind = world.getBlockAt(behind.getX(), behind.getY(), behind.getZ() - 1);
            break;
        case 0:
            behind = world.getBlockAt(behind.getX(), behind.getY(), behind.getZ() - 1);
            break;
        default:
            break;
        }
        return behind;
    }

    private static final EntityType[] blockedMultiply = { EntityType.SQUID, EntityType.BAT };

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {

        for (EntityType t : blockedMultiply)
            if (e.getEntity().getType().equals(t))
                return;

        if (e.getSpawnReason() == SpawnReason.CUSTOM)
            return;

        Location loc = e.getEntity().getLocation();
        int spawnedMobs = 0;
        for (int x = -15; x < 15; x++) {
            for (int z = -15; z < 15; z++) {
                if (spawnedMobs > 3)
                    return;
                Location l2 = loc.clone().add(x, 0, z);
                if (rand.nextInt(160) == rand.nextInt(160)) {
                    spawnedMobs++;
                    Location l3 = getHighestBlock(l2);
                    Entity en = loc.getWorld().spawnEntity(l3, e.getEntity().getType());
                }
            }
        }
    }

    private Location getHighestBlock(Location loc) {
        double y = 0;
        while (y <= 256 && (loc.clone().add(0, y, 0).getBlock().getType() != Material.AIR && loc.clone().add(0, y, 0).getBlock().getType() != Material.GRASS)) {
            y++;
        }
        return loc.clone().add(0, y, 0);
    }

}
