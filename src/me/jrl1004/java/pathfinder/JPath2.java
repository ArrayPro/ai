package me.jrl1004.java.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;

import me.jrl1004.java.pathfinder.main.PathfinderMain;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class JPath2 {
	private JPath2			nextPath;
	private final Location	start;
	private final Location	end;
	private final Location	localEnd;

	public JPath2(Location start, Location end, int recur) {
		// Initialize
		nextPath = null;
		this.start = start;
		this.end = end;
		if (!start.getWorld().equals(end.getWorld())) {
			localEnd = start;
			return; // We cannot path between dimensions
		}

		generateCurrentPath();
		countBlocksAlongPath();

		localEnd = this.start.clone().add(direction.multiply(magnitude));

		if (!atEnd() && recur < PathfinderMain.maxRecursions)
			nextPath = new JPath2(localEnd, end, recur + 1);
	}

	private Vector	direction;	// Which way are we going?
	private int		magnitude;	// How long are we going to follow this path?

	private void generateCurrentPath() {
		direction = new Vector();

		//Can we move on the X axis?
		int distanceX = start.getBlockX() - end.getBlockX();
		if (distanceX > 0)
			direction.setX(1);
		else if (distanceX < 0)
			direction.setX(-1);
		else
			direction.setX(0);

		//Can we move on the Y axis?
		int distanceY = start.getBlockY() - end.getBlockY();
		if (distanceY > 0)
			direction.setY(1);
		else if (distanceY < 0)
			direction.setY(-1);
		else
			direction.setY(0);

		//Can we move on the Z axis?
		int distanceZ = start.getBlockZ() - end.getBlockZ();
		if (distanceZ > 0)
			direction.setZ(1);
		else if (distanceZ < 0)
			direction.setZ(-1);
		else
			direction.setZ(0);

	}

	private void countBlocksAlongPath() {
		Block activeBlock = start.clone().getBlock();
		magnitude = 0;
		while (PathfinderMain.safeBlocks.contains(activeBlock.getType())) {
			activeBlock = activeBlock.getRelative(direction.getBlockX(), direction.getBlockY(), direction.getBlockZ());
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

	//Getters for all the information
	public Location[] getLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < magnitude; i++) {
			locations.add(start.add(direction.multiply(magnitude)));
		}
		if (nextPath != null)
			locations.addAll(Arrays.asList(nextPath.getLocations()));
		return locations.toArray(new Location[locations.size()]);
	}

}