package me.jrl1004.java.pathfinder;

import org.bukkit.Location;

public class JPathfinder {

	private JPath	directions;

	public JPathfinder(Location start, Location end) {
		directions = new JPath(start, end, 0, null);
	}

	public JPath getPath() {
		return directions;
	}

}
