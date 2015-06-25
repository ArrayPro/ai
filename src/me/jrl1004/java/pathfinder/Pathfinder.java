package me.jrl1004.java.pathfinder;

import org.bukkit.Location;

public class Pathfinder {

	private Path directions;

	public Pathfinder(Location start, Location end) {
		directions = new Path(start, end.toVector());
	}

	public Path getPath() {
		return directions;
	}

}
