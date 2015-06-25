package me.jrl1004.java.pathfinder;

import org.bukkit.Location;

public class JPathfinder {

	private JPath2	directions;

	public JPathfinder(Location start, Location end) {
		directions = new JPath2(start, end);
	}

	public JPath2 getPath() {
		return directions;
	}

}
