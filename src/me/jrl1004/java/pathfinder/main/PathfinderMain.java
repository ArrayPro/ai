package me.jrl1004.java.pathfinder.main;

import java.util.Set;

import me.jrl1004.java.pathfinder.Path;
import me.jrl1004.java.pathfinder.Pathfinder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PathfinderMain extends JavaPlugin {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player pl = (Player) sender;

		Pathfinder p = new Pathfinder(pl.getLocation(), pl.getTargetBlock((Set<Material>) null, 50).getLocation());

		Path path = p.getPath();

		Location[] locations = path.getPathLocations();
		for (int i = 0; i < locations.length; i++)
			locations[i].getBlock().setType(Material.SPONGE);
		return false;
	}
}
