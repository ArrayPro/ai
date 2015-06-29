package me.jrl1004.java.ai.main;

import java.util.Arrays;
import java.util.List;

import me.jrl1004.java.ai.silverfish.SilverfishTracker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PathfinderMain extends JavaPlugin {

	public static final int				maxRecursions	= 100;

	public static PathfinderMain		instance;

	public static final List<Material>	safeBlocks		= Arrays.asList(Material.AIR, Material.LONG_GRASS, Material.DOUBLE_PLANT, Material.WATER, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM);

	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new SilverfishTracker(), this);
		// Bukkit.getPluginManager().registerEvents(new SwarmManager(), this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("killall"))
			for (World w : Bukkit.getWorlds())
				for (Entity e : w.getEntities())
					if (!(e instanceof Player))
						e.remove();
		return false;
	}
}
