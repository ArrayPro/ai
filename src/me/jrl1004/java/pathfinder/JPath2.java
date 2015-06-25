package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;

import me.jrl1004.java.pathfinder.main.PathfinderMain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class JPath2 extends Thread {
    private JPath2 nextPath;
    private final Location start;
    private final Location end;
    private Location localEnd;
    private final int recur;

    public JPath2(Location start, Location end, int recur) {
        // Initialize
        nextPath = null;
        this.start = start;
        this.end = end;
        this.recur = recur;
        if (!start.getWorld().equals(end.getWorld())) {
            localEnd = start;
            return; // We cannot path between dimensions
        }
    }

    @Override
    public void run() {

        generateCurrentPath();
        countBlocksAlongPath();

        System.out.println("Path made with a magnitude of " + magnitude + " along the path " + direction);

        localEnd = this.start.clone().add(direction.multiply(magnitude));

        if (!atEnd() && recur < PathfinderMain.maxRecursions)
            nextPath = new JPath2(localEnd, end, recur + 1);
        new BukkitRunnable() {
            public void run() {
                final Location[] locations = getLocations(false);
                for (int i = 0; i < locations.length; i++)
                    locations[i].getBlock().setType(Material.SPONGE);
            }
        }.runTask(PathfinderMain.instance);
        super.run();
    }

    private Vector direction; // Which way are we going?
    private int magnitude; // How long are we going to follow this path?

    private void generateCurrentPath() {
        direction = new Vector();

        // Can we move on the X axis?
        int distanceX = end.getBlockX() - start.getBlockX();
        if (distanceX > 0)
            direction.setX(1);
        else if (distanceX < 0)
            direction.setX(-1);
        else
            direction.setX(0);

        // Can we move on the Y axis?
        int distanceY = end.getBlockY() - start.getBlockY();
        if (distanceY > 0)
            direction.setY(1);
        else if (distanceY < 0)
            direction.setY(-1);
        else
            direction.setY(0);

        // Can we move on the Z axis?
        int distanceZ = end.getBlockZ() - start.getBlockZ();
        if (distanceZ > 0)
            direction.setZ(1);
        else if (distanceZ < 0)
            direction.setZ(-1);
        else
            direction.setZ(0);

        System.out.println("direction = " + direction);

    }

    private void countBlocksAlongPath() {
        magnitude = 0;
        while (PathfinderMain.safeBlocks.contains(start.add(direction.clone().multiply(magnitude)).getBlock().getType()) && magnitude < 1000) {
            // System.out.println("Magnitude = " + magnitude);
            magnitude++;
        }
    }

    private boolean atEnd() {
        if (localEnd.getBlockX() != end.getBlockX())
            return false;
        if (localEnd.getBlockY() != end.getBlockY())
            return false;
        if (localEnd.getBlockZ() != end.getBlockZ())
            return false;
        return true;
    }

    // Getters for all the information
    public Location[] getLocations() {
        return getLocations(true);
    }

    public Location[] getLocations(boolean useChildren) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < magnitude; i++) {
            locations.add(start.add(direction.clone().multiply(magnitude)));
        }
        if (nextPath != null && useChildren)
            locations.addAll(Arrays.asList(nextPath.getLocations()));
        return locations.toArray(new Location[locations.size()]);
    }

}