package me.jrl1004.java.pathfinder;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class Pathfinder {

	private Path	directions;

	public Pathfinder(Location start, Location end) {
		directions = new Path(start, end, BlockFace.SELF, 0, null);
	}

	public Path getPath() {
		return directions;
	}

}
