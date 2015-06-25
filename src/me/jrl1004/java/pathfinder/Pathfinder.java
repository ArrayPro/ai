package me.jrl1004.java.pathfinder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

public class Pathfinder {

    private Stack<Path> directions;

    public Pathfinder(Location start, Location end) {
        directions = new Stack<Path>();
        directions.push(new Path(start, end.toVector()));
    }

    public Stack<Path> getPath() {
        return directions;
    }

    protected class Path {
        private BlockFace direction;
        private List<BlockFace> blocked;
        private int moves;
        private Location startLoc;
        private Vector start, end, localEnd;

        Path(Location startLoc, Vector end, BlockFace... blockedPaths) {
            direction = null;
            moves = Integer.MAX_VALUE;
            this.startLoc = startLoc;
            this.start = startLoc.toVector();
            this.end = end;
            blocked = Arrays.asList(blockedPaths);
            TreeMap<BlockFace, Integer> paths = getDirectionsByLength();
            for (BlockFace b : paths.keySet()) {
                if (direction != null) continue;
                if (blocked.contains(b)) continue;
                if (paths.get(b) < moves) {
                    moves = paths.get(b);
                    direction = b;
                }
            }
            Block block = startLoc.getBlock();
            for (int i = 0; i < moves; i++) block = block.getRelative(direction);

            blocked.clear();
            for (BlockFace face : BlockFace.values())
                if (block.getRelative(face).getType() != Material.AIR) blocked.add(face);

            localEnd = block.getLocation().toVector();

            if (!localEnd.equals(end))
                directions.push(new Path(localEnd.toLocation(startLoc.getWorld()), end, blocked.toArray(new BlockFace[blocked.size()])));
        }

        private TreeMap<BlockFace, Integer> getDirectionsByLength() {
            BlockFace xFace = (end.getBlockX() - start.getX() >= 0 ? BlockFace.EAST : BlockFace.WEST);
            int xDist = getPathableDistance(xFace);

            BlockFace yFace = (end.getBlockY() - start.getY() >= 0 ? BlockFace.UP : BlockFace.DOWN);
            int yDist = getPathableDistance(yFace);

            BlockFace zFace = (end.getBlockZ() - start.getZ() >= 0 ? BlockFace.SOUTH : BlockFace.NORTH);
            int zDist = getPathableDistance(zFace);

            TreeMap<BlockFace, Integer> pathing = new TreeMap<BlockFace, Integer>();
            pathing.put(xFace, xDist);
            pathing.put(yFace, yDist);
            pathing.put(zFace, zDist);
            return pathing;
        }

        private int getPathableDistance(BlockFace blockface) {
            Block b = startLoc.getBlock();
            int moves = 0;
            boolean run = true;
            while (run) {
                b = b.getRelative(blockface);
                if (b.getType() != Material.AIR) {
                    run = false;
                } else {
                    moves++;
                }
            }
            return moves;
        }
    }
}
