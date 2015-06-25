package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import me.jrl1004.java.pathfinder.main.PathfinderMain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

public class Path {

	public boolean		terminate	= false;

	private final Path	master;

	public BlockFace	direction, lastDirection;
	public int			moves;

	private final Location	startLoc, endLoc, localEnd;
	private Path			nextPath;
	private final int		recursion;

	Path(Location startLoc, Location endLoc, BlockFace LastDirection, int recursion, Path master) {
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

		// Find the end of the path
		{
			Block block = startLoc.getBlock();
			for (int i = 0; i < moves; i++)
				block = block.getRelative(direction);
			localEnd = block.getLocation();
		}
		if (recursion >= PathfinderMain.maxRecursions) {
			System.out.println("Max recursions met");
			return;
		}
		if (isBlocked()) {
			System.out.println("Path blocked; Terminating after " + recursion + " iterations");
			return;
		}
		if (reachesDestination()) {
			System.out.println("Destination reached");
			if (master != null)
				master.terminate = true;
			return;
		}
		if (!isIntercepted()) {
			nextPath = new Path(localEnd, endLoc, getReverse(), recursion + 1, master == null ? this : master);
		} else {
			nextPath = calculateWorkaroundAsynchronously(localEnd, endLoc);
		}
	}

	public boolean reachesDestination() {
		if (localEnd == null)
			return false;
		if (isBlocked())
			return false;
		return localEnd.equals(endLoc);
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

	private boolean isIntercepted() {
		if (moves == 0)
			return true;
		int freePaths = 5;
		Block block = localEnd.getBlock();
		List<BlockFace> arr = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
		arr.remove(lastDirection);
		for (BlockFace b : arr) {
			if (block.getRelative(b).getType() != Material.AIR)
				freePaths--;
		}
		return freePaths > 0;
	}

	private boolean isBlocked() {
		if (moves == 0)
			return true;
		int freePaths = 5;
		Block block = localEnd.getBlock();
		List<BlockFace> arr = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
		arr.remove(lastDirection);
		for (BlockFace b : arr) {
			if (block.getRelative(b).getType() != Material.AIR)
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

	private synchronized Path calculateWorkaroundAsynchronously(Location start, Location end) {
		if (master != null)
			if (master.terminate)
				return null;
		final List<Path> paths = new ArrayList<Path>();
		List<BlockFace> dir = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
		dir.remove(lastDirection);
		final Path me = this;
		for (final BlockFace b : dir) {
			if (localEnd.getBlock().getRelative(b).getType() == Material.AIR)
				new BukkitRunnable() {
					public void run() {
						paths.add(new Path(localEnd.getBlock().getRelative(b).getLocation(), endLoc, lastDirection, recursion + 1, master == null ? me : master));
					}
				}.runTaskAsynchronously(PathfinderMain.instance);
		}
		Path path = null;
		int moves = Integer.MAX_VALUE;
		System.out.println(paths.size());
		if (paths.isEmpty()) {
			return path;
		}
		for (Path p : paths) {
			if (p.isBlocked())
				continue;
			int mvs = p.getPathLocations().length;
			if (mvs < moves) {
				moves = mvs;
				path = p;
			}
		}
		return path;
	}
}
