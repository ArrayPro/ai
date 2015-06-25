package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Path {
	public BlockFace direction, lastDirection;
	public int moves;
	private final Location startLoc, localEnd;
	private final Vector start, end;
	private Path nextPath;

	Path(Location startLoc, Vector end, BlockFace LastDirection) {
		// Initiation
		direction = BlockFace.SELF;
		moves = Integer.MIN_VALUE;
		this.startLoc = startLoc;
		this.start = startLoc.toVector();
		this.end = end;
		nextPath = null; // Not needed until we calculate this path

		// Calculations
		TreeMap<BlockFace, Integer> paths = getDirectionsByLength();
		for (BlockFace b : paths.keySet()) {
			if (direction == BlockFace.SELF)
				continue;
			if (paths.get(b) > moves) {
				moves = paths.get(b);
				direction = b;
			}
		}

		// Find the end of the path
		{
			Block block = startLoc.getBlock();
			for (int i = 0; i < moves; i++)
				block = block.getRelative(direction);
			localEnd = block.getLocation();
		}
		System.out.println("Local ending at " + localEnd);
		if (!localEnd.equals(end) && !isBlocked())
			nextPath = new Path(localEnd, end, getReverse());
	}

	private TreeMap<BlockFace, Integer> getDirectionsByLength() {
		BlockFace xFace = (end.getBlockX() - start.getBlockX() >= 0 ? BlockFace.EAST : BlockFace.WEST);
		int xDist = Math.abs(end.getBlockX() - start.getBlockX());
		xDist = getPathableDistance(xFace, xDist);

		BlockFace yFace = (end.getBlockY() - start.getBlockY() >= 0 ? BlockFace.UP : BlockFace.DOWN);
		int yDist = Math.abs(end.getBlockY() - start.getBlockY());
		yDist = getPathableDistance(yFace, yDist);

		BlockFace zFace = (end.getBlockZ() - start.getBlockZ() >= 0 ? BlockFace.SOUTH : BlockFace.NORTH);
		int zDist = Math.abs(end.getBlockZ() - start.getBlockZ());
		zDist = getPathableDistance(zFace, zDist);

		System.out.println("----------------------------------");

		TreeMap<BlockFace, Integer> pathing = new TreeMap<BlockFace, Integer>();
		pathing.put(xFace, xDist);
		pathing.put(yFace, yDist);
		pathing.put(zFace, zDist);
		return pathing;
	}

	private int getPathableDistance(BlockFace blockface, int max) {
		Block b = startLoc.clone().getBlock();
		int moves = 0;
		boolean run = true;
		while (run) {
			b = b.getRelative(blockface);
			if (b.getType() != Material.AIR || max >= 25) {
				run = false;
			} else {
				moves++;
			}
		}
		System.out.println(blockface.toString() + " has an availiable path! + (" + moves + " blocks)");
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
		int freePaths = 6;
		Block block = localEnd.getBlock();
		for (BlockFace b : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
			if (block.getRelative(b).getType() != Material.AIR || (b == lastDirection && block.getRelative(b).getType() == Material.AIR))
				freePaths--;
		}
		return freePaths == 0;
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
