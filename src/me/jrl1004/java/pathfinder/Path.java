package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import me.jrl1004.java.pathfinder.main.PathfinderMain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Path {
	public BlockFace direction, lastDirection;
	public int moves;
	private final Location startLoc, endLoc, localEnd;
	private Path nextPath;

	Path(Location start, Location end, BlockFace LastDirection, int recursion) {
		// Initiation
		direction = null;
		moves = Integer.MIN_VALUE;
		this.startLoc = start;
		this.endLoc = end;
		nextPath = null; // Not needed until we calculate this path

		// Calculations
		TreeMap<Integer, BlockFace> paths = getDirectionsByLength();
		moves = paths.lastKey();
		direction = paths.get(moves);
		// Find the end of the path
		{
			Block block = startLoc.getBlock();
			for (int i = 0; i < moves; i++)
				block = block.getRelative(direction);
			localEnd = block.getLocation();
		}

		System.out.println("Path Created from " + startLoc.toVector() + " to " + localEnd.toVector());

		if (recursion >= PathfinderMain.maxRecursions) {
			System.out.println("Max recursions met");
			return;
		}
		if (isBlocked()) {
			System.out.println("Path blocked; Terminating after " + recursion + " iterations");
			return;
		}
		if (localEnd.equals(end)) {
			System.out.println("Destination reached");
			return;
		}
		nextPath = new Path(localEnd, end, getReverse(), recursion + 1);
	}

	private TreeMap<Integer, BlockFace> getDirectionsByLength() {
		BlockFace xFace = (endLoc.getBlockX() - startLoc.getBlockX() >= 0 ? BlockFace.EAST : BlockFace.WEST);
		int xDist = Math.abs(endLoc.getBlockX() - startLoc.getBlockX());
		xDist = getPathableDistance(xFace, xDist);

		BlockFace yFace = (endLoc.getBlockY() - startLoc.getBlockY() >= 0 ? BlockFace.UP : BlockFace.DOWN);
		int yDist = Math.abs(endLoc.getBlockY() - startLoc.getBlockY());
		yDist = getPathableDistance(yFace, yDist);

		BlockFace zFace = (endLoc.getBlockZ() - startLoc.getBlockZ() >= 0 ? BlockFace.SOUTH : BlockFace.NORTH);
		int zDist = Math.abs(endLoc.getBlockZ() - startLoc.getBlockZ());
		zDist = getPathableDistance(zFace, zDist);
		
		TreeMap<Integer, BlockFace> pathing = new TreeMap<Integer, BlockFace>();
		pathing.put(xDist, xFace);
		pathing.put(yDist, yFace);
		pathing.put(zDist, zFace);
		return pathing;
	}

	private int getPathableDistance(BlockFace blockface, int max) {
		Block b = startLoc.getBlock();
		int moves = 0;
		boolean run = true;
		while (run) {
			b = b.getRelative(blockface);
			if (b.getType() != Material.AIR || moves >= max) {
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
		if (moves == 0)
			return true;
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
