package me.jrl1004.java.pathfinder.silverfish;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class SilverfishTracker implements Listener {

    private static final Random rand = new Random();

    @EventHandler
    public void breakOre(BlockBreakEvent e) {
        Block b = e.getBlock();

        if (e.isCancelled())
            return;

        if (!(e.getPlayer().getGameMode() == GameMode.SURVIVAL || e.getPlayer().getGameMode() == GameMode.ADVENTURE))
            return;

        if (!b.getType().toString().contains("ORE"))
            return;

        for (int x = -5; x < 5; x++) {
            for (int y = -5; y < 5; y++) {
                for (int z = -5; z < 5; z++) {
                    Block b2 = b.getLocation().clone().add(x, y, z).getBlock();
                    if (b2.getType().equals(Material.STONE) && b2.getData() == (byte) 0) {
                        if (rand.nextInt(5) == rand.nextInt(5)) {
                            b2.setType(Material.MONSTER_EGGS);
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() != SpawnReason.SPAWNER_EGG) return;
        
        Location loc = e.getEntity().getLocation();
        for (int x = -10; x < 10; x++) {
                for (int z = -10; z < 10; z++) {
                    Location l2 = loc.clone().add(x, 0, z);
                    if (rand.nextInt(50) == rand.nextInt(50)) {
                        Location l3 = getHighestBlock(l2);
                        loc.getWorld().spawnEntity(l3, e.getEntity().getType());
                    }
            }
        }
    }

    public Location getHighestBlock(Location loc) {
        double y = 0;
        while (y <= 256 && (loc.clone().add(0, y, 0).getBlock().getType() != Material.AIR && loc.clone().add(0, y, 0).getBlock().getType() != Material.GRASS)) {
            y++;
        }
        return loc.clone().add(0, y, 0);
    }
}
