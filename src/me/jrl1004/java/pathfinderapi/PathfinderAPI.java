package me.jrl1004.java.pathfinderapi;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PathfinderAPI extends JavaPlugin {
	
	private HashMap<String, AStarPathfinder> pathfinders;

	@Override
	public void onEnable() {
		this.pathfinders = new HashMap<String, AStarPathfinder>();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		super.onDisable();
	}

	public PathfinderAPI getAPI() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("PathfinderAPI");
		if (plugin instanceof PathfinderAPI) return (PathfinderAPI) plugin;
		return null;
	}
	
	public void callPathEvent(final Location start, final Location end, @Nullable final CommandSender pathRequestor) {
		World world = start.getWorld();
		AStarPathfinder finderTemp = pathfinders.get(world.getName());
		if(finderTemp == null) {
			finderTemp = new AStarPathfinder(world);
			pathfinders.put(world.getName(), finderTemp);
		}
		final AStarPathfinder finder = finderTemp;
		new BukkitRunnable() {
			public void run() {
				finder.pathFromStartToEnd(start.toVector(), end.toVector(), pathRequestor);
				cancel();
			}
		}.runTaskAsynchronously(this);
	}
}
