package me.jrl1004.java.ai.silverfish;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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


}
