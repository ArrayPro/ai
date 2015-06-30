package me.jrl1004.java.pathfinderapi;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class AStarNode {
	private final Vector	vec3;
	private final Vector	gridValue;

	private double			step_cost		= 1;
	private double			heuristic_cost	= 0;
	private double			total_cost		= 1;

	protected AStarNode(Vector vec3, Vector gridValue) {
		this.vec3 = vec3;
		this.gridValue = gridValue;
	}

	public Vector getVec3() {
		return this.vec3.clone();
	}

	public Vector getGridValue() {
		return this.gridValue.clone();
	}

	private void calculateCost(double step, Vector end) {
		this.step_cost = step;
		this.heuristic_cost = directCost(end);
		this.total_cost = this.step_cost + this.heuristic_cost;
	}

	private int directCost(Vector end) {
		int costX = Math.abs(getVec3().getBlockX() - end.getBlockX());
		int costY = Math.abs(getVec3().getBlockY() - end.getBlockY());
		int costZ = Math.abs(getVec3().getBlockZ() - end.getBlockZ());
		return costX + costY + costZ;
	}

	public double getTotalCost(double step, Vector end) {
		calculateCost(step, end);
		return getTotalCost();
	}

	public double getTotalCost() {
		return this.total_cost;
	}

	public boolean isWalkable(World world) {
		Material m = getVec3().toLocation(world).getBlock().getType();
		return m.isTransparent();
	}
}
