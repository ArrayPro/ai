package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import me.jrl1004.java.pathfinder.main.PathfinderMain;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class JPath {

	public boolean		terminate	= false;

	private final JPath	master;

	public BlockFace	direction;
	public int			moves;

	private BlockFace	lastDirection;

	private final Location	startLoc, endLoc, localEnd;
	private JPath			nextPath;
	private final int		recursion;

	JPath(Location startLoc, Location endLoc, int recursion, JPath master) {
		// Initiation
		this.master = master;
		direction = null;
		moves = Integer.MIN_VALUE;
		this.startLoc = startLoc;
		this.endLoc = endLoc;
		nextPath = null; // Not needed until we calculate this path
		this.recursion = recursion;

		if (master != null)
			if (master.terminate) {
				localEnd = startLoc;
				return;
			}

		// Calculations
		TreeMap<Integer, BlockFace> paths = getDirectionsByLength();
		moves = paths.lastKey();
		direction = paths.get(moves);
		lastDirection = getReverse();

		// Find the end of the path
		if (moves > 0) {
			Block block = startLoc.getBlock();
			for (int i = 0; i < moves; i++)
				block = block.getRelative(direction);
			localEnd = block.getLocation();
		} else {
			localEnd = startLoc;
		}

		if (recursion >= PathfinderMain.maxRecursions) {
			return;
		}

		if (!reachesDestination()) {

			if (isBlocked()) {
				System.out.println("it's blocked :(");
				return;
			}

			if (isIntercepted()) {
				// Calculations
				paths = getDirectionsByLength();
				moves = paths.lastKey();
				if (moves == 0) {
					nextPath = new JPath(endLoc, localEnd, recursion + 1, master == null ? this : master);
					// calculateWorkaroundAsynchronously();
				} else
					nextPath = new JPath(localEnd, endLoc, recursion + 1, master == null ? this : master);
			} else {
				nextPath = new JPath(localEnd, endLoc, recursion + 1, master == null ? this : master);
			}

		} else {
			if (master != null)
				master.terminate = true;
		}

	}

	public boolean reachesDestination() {
		if (localEnd == null)
			return false;
		return localEnd.equals(endLoc);
	}

	private TreeMap<Integer, BlockFace> getDirectionsByLength() {
		System.out.println("-----------");
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
			if (!PathfinderMain.safeBlocks.contains(b.getType()) || moves >= max) {
				run = false;
			} else {
				moves++;
			}
		}
		System.out.println(blockface.toString() + " has a pathable distance of " + moves);
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

	private boolean isIntercepted() {
		Block block = localEnd.getBlock();
		List<BlockFace> arr = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
		for (BlockFace b : arr) {
			if (PathfinderMain.safeBlocks.contains(block.getRelative(b).getType()))
				return true;
		}
		return false;
	}

	private boolean isBlocked() {
		int freePaths = 5;
		Block block = localEnd.getBlock();
		ArrayList<BlockFace> arr = new ArrayList<BlockFace>(Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN));
		arr.remove(lastDirection);
		for (BlockFace b : arr) {
			if (!PathfinderMain.safeBlocks.contains(block.getRelative(b).getType()))
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

	private void calculateWorkaroundAsynchronously() {

	}
}
