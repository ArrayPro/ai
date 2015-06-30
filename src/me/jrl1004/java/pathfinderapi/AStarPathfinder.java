package me.jrl1004.java.pathfinderapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

public class AStarPathfinder {

	private final World		world;
	private AStarNode[][][]	grid;
	private Vector			nativeLowest;

	protected AStarPathfinder(World world) {
		this.world = world;
	}

	public boolean pathFromStartToEnd(Vector start, Vector end, @Nullable CommandSender pathRequestor) {
		float parseTime = System.nanoTime();
		if (!new AStarNode(start, null).isWalkable(world) || !new AStarNode(end, null).isWalkable(world)) return false;
		loadGridIntoBuffer(start, end);
		List<AStarNode> openList = new ArrayList<AStarNode>();
		List<AStarNode> closedList = new ArrayList<AStarNode>();
		AStarNode current = new AStarNode(nativeLowest.clone().add(new Vector(-1, -1, -1)), new Vector(-1, -1, -1));
		current.getTotalCost(5, end);
		openList.add(getInGridRaw(start));
		while (!openList.isEmpty()) {
			current = getNodeWithLowestTotal(openList, end.clone(), current.getTotalCost());
			if (current.equals(end)) {
				break;
			}
			else {
				openList.remove(current);
				closedList.add(current);
				for (AStarNode aStarNode : getAdjacentNodes(current)) {
					if (closedList.contains(aStarNode) || openList.contains(aStarNode)) continue;
					openList.add(aStarNode);
				}
			}
		}
		AStarNode[] path = getPath(openList, closedList, start, end);
		if(path.length == 0) return false;
		parseTime = (float) ((System.nanoTime() - parseTime) * 1.0e-9);
		if (pathRequestor == null) {
			Bukkit.getPluginManager().callEvent(new PathLocatedEvent(world, start, end, path, parseTime));
		}
		else {
			Bukkit.getPluginManager().callEvent(new PathLocatedEvent(world, pathRequestor, start, end, path, parseTime));
		}
		return true;
	}

	private void loadGridIntoBuffer(Vector start, Vector end) {
		int x0 = Math.abs(end.getBlockX() - start.getBlockX()) + 1;
		int y0 = Math.abs(end.getBlockY() - start.getBlockY()) + 1;
		int z0 = Math.abs(end.getBlockZ() - start.getBlockZ()) + 1;
		grid = new AStarNode[x0][y0][z0];
		nativeLowest = calculateNativeLowest(start, end);
		for (int x = 0; x < x0; x++) {
			for (int y = 0; y < y0; y++) {
				for (int z = 0; z < z0; z++) {
					AStarNode node = new AStarNode(nativeLowest.clone().add(new Vector(x, y, z)), new Vector(x, y, z));
					grid[x][y][z] = node;
				}
			}
		}
	}

	private AStarNode getNodeWithLowestTotal(List<AStarNode> openList, Vector end, double lastCost) {
		AStarNode node = openList.get(0);
		double min = Double.MAX_VALUE;
		for (AStarNode aStarNode : openList) {
			if (aStarNode.getTotalCost(lastCost, end) <= min) {
				min = aStarNode.getTotalCost();
				node = aStarNode;
			}
		}
		return node;
	}

	private Vector calculateNativeLowest(Vector a, Vector b) {
		Vector rVec = new Vector();
		rVec.setX(Math.min(a.getX(), b.getX()));
		rVec.setY(Math.min(a.getY(), b.getY()));
		rVec.setZ(Math.min(a.getZ(), b.getZ()));
		return rVec;
	}

	private AStarNode getInGrid(Vector parent) {
		int gridX = parent.getBlockX();
		int gridY = parent.getBlockY();
		int gridZ = parent.getBlockZ();
		try {
			return grid[gridX][gridY][gridZ];
		} catch (ArrayIndexOutOfBoundsException exc) {
			return null; // That value is not indexed
		}
	}

	private AStarNode getInGridRaw(Vector parent) {
		Vector check = parent.clone().subtract(nativeLowest.clone());
		return getInGrid(check);
	}

	private AStarNode getRelativeInGrid(AStarNode parent, BlockFace relative) {
		Vector check = parent.getGridValue().add(new Vector(relative.getModX(), relative.getModY(), relative.getModZ()));
		return getInGrid(check);
	}

	private final List<BlockFace>	directions	= Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

	private ArrayList<AStarNode> getAdjacentNodes(AStarNode a) {
		ArrayList<AStarNode> list = new ArrayList<AStarNode>();
		for (BlockFace bf : directions) {
			AStarNode node = getRelativeInGrid(a, bf);
			if (node != null && node.isWalkable(world)) {
				list.add(getRelativeInGrid(a, bf));
			}
		}
		return list;
	}

	private AStarNode[] getPath(List<AStarNode> openList, List<AStarNode> closedList, Vector start, Vector end) {
		HashSet<AStarNode> allNodes = new HashSet<AStarNode>();
		allNodes.addAll(openList);
		allNodes.addAll(closedList);
		openList.clear();
		closedList.clear();
		int max = allNodes.size();
		System.out.println("Checking " + max + " nodes for a viable path");
		int curr = 0;
		boolean foundStart = false;
		AStarNode current = getInGridRaw(end);
		List<AStarNode> pathNodes = new ArrayList<AStarNode>();
		while (!foundStart && curr < max) {
			++curr;
			ArrayList<AStarNode> adj = getAdjacentNodes(current);
			for (AStarNode aStarNode : adj) {
				if (aStarNode.getVec3().equals(start))
					foundStart = true;
				if (allNodes.contains(aStarNode)) {
					if (aStarNode.getTotalCost() <= current.getTotalCost()) {
						current = aStarNode;
						System.out.println("Adding node. Current iteration = " + curr);
						pathNodes.add(aStarNode);
						break;
					}
					else
						System.out.println(aStarNode.getTotalCost() + " > " + current.getTotalCost());
				}
				else
					System.out.println("Node not in list");
			}
		}

		return pathNodes.toArray(new AStarNode[pathNodes.size()]);
	}
}
