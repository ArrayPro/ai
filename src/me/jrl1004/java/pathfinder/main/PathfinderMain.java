package me.jrl1004.java.pathfinder.main;

import java.util.Set;

import me.jrl1004.java.pathfinder.Path;
import me.jrl1004.java.pathfinder.Pathfinder;
import me.jrl1004.java.pathfinder.silverfish.SilverfishTracker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PathfinderMain extends JavaPlugin {
    
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(new SilverfishTracker(), this);
    }

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player pl = (Player) sender;
		Block b = pl.getTargetBlock((Set<Material>) null, 100);
		b.setType(Material.DIAMOND_BLOCK);
		Pathfinder p = new Pathfinder(pl.getLocation(), b.getLocation());

		Path path = p.getPath();

		Location[] locations = path.getPathLocations();
		for (int i = 0; i < locations.length; i++)
			locations[i].getBlock().setType(Material.SPONGE);
		return false;
	}
}
