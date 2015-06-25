package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import me.jrl1004.java.pathfinder.utils.local.vector.VectorUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Path {
	public BlockFace direction;
	public int moves;
	private ArrayList<BlockFace> blocked;
	private Location startLoc;
	private Vector start, end, localEnd;
	private Path nextPath;
	private boolean PATH_BLOCKED;

	Path(Location startLoc, Vector end, BlockFace... blockedPaths) {
		// Initiation
		direction = BlockFace.SELF;
		moves = Integer.MIN_VALUE;
		this.startLoc = startLoc;
		this.start = startLoc.toVector();
		this.end = end;
		if (blockedPaths.length > 0)
			blocked = new ArrayList<BlockFace>(Arrays.asList(blockedPaths));
		else
			blocked = new ArrayList<BlockFace>();
		nextPath = null; // Not needed until we calculate this path
		PATH_BLOCKED = isBlocked();

		// Calculations
		TreeMap<BlockFace, Integer> paths = getDirectionsByLength();
		for (BlockFace b : paths.keySet()) {
			if (direction != BlockFace.SELF)
				continue;
			if (blocked.contains(b))
				continue;
			if (paths.get(b) > moves) {
				moves = paths.get(b);
				direction = b;
			}
		}
		Block block = startLoc.getBlock();
		for (int i = 0; i < moves; i++)
			block = block.getRelative(direction);
		blocked.clear();
		for (BlockFace face : BlockFace.values())
			if (block.getRelative(face).getType() != Material.AIR)
				blocked.add(face);
		blocked.add(getReverse());
		localEnd = block.getLocation().toVector();
		if (!VectorUtil.equals(localEnd, end) && !PATH_BLOCKED)
			nextPath = new Path(localEnd.toLocation(startLoc.getWorld()), end, blocked.toArray(new BlockFace[blocked.size()]));
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

	public Location[] getPathLocations() {
		ArrayList<Location> path = new ArrayList<Location>();
		Block block = startLoc.getBlock();
		for (int i = 0; i < moves; i++) {
			block = block.getRelative(direction);
			path.add(block.getLocation());
		}
		if (nextPath != null)
			path.addAll(Arrays.asList(nextPath.getPathLocations()));
		return path.toArray(new Location[path.size()]);
	}

	private boolean isBlocked() {
		boolean bool = false;
		for (BlockFace b : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
			if (blocked.contains(b))
				bool = true;
		}
		return bool;
	}

	private BlockFace getReverse() {
		switch (direction) {
		case NORTH:
			return BlockFace.SOUTH;
		case SOUTH:
			return BlockFace.NORTH;
		case EAST:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.EAST;
		case UP:
			return BlockFace.DOWN;
		case DOWN:
			return BlockFace.UP;
		default:
			return BlockFace.SELF;
		}
	}
}
