package me.jrl1004.java.pathfinderapi;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class PathLocatedEvent extends Event {

	public final World			world;
	public final AStarNode[]	path;
	public final Vector			start;
	public final Vector			end;
	public final CommandSender	pathRequester;
	public final float			parseTime;

	protected PathLocatedEvent(World world, Vector start, Vector end, AStarNode[] path, float parseTime) {
		this.world = world;
		this.path = path;
		this.start = start;
		this.end = end;
		this.pathRequester = null;
		this.parseTime = parseTime;
	}

	protected PathLocatedEvent(World world, CommandSender pathRequestor, Vector start, Vector end, AStarNode[] path, float parseTime) {
		this.world = world;
		this.path = path;
		this.start = start;
		this.end = end;
		this.pathRequester = pathRequestor;
		this.parseTime = parseTime;
	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}

}
