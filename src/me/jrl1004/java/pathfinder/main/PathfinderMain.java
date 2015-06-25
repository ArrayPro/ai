package me.jrl1004.java.pathfinder.main;

import java.util.Set;

import me.jrl1004.java.pathfinder.Path;
import me.jrl1004.java.pathfinder.Pathfinder;
import me.jrl1004.java.pathfinder.silverfish.SilverfishTracker;
import me.jrl1004.java.pathfinder.swarm.SwarmManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PathfinderMain extends JavaPlugin {

    public static final int maxRecursions = 100;

    public static PathfinderMain instance;

    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new SilverfishTracker(), this);
        Bukkit.getPluginManager().registerEvents(new SwarmManager(), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("killall")) {
            for (World w : Bukkit.getWorlds())
                for (Entity e : w.getEntities())
                    if(!(e instanceof Player))
                    e.remove();
            return false;
        }

        if (!(sender instanceof Player))
            return false;
        
        Player pl = (Player) sender;
        Block b = pl.getTargetBlock((Set<Material>) null, 500);
        if (args.length >= 3) {
            Location l1 = pl.getLocation();
            l1.setX(Integer.parseInt(args[0]));
            l1.setY(Integer.parseInt(args[1]));
            l1.setZ(Integer.parseInt(args[2]));
            b = l1.getBlock();
        }
        // b.setType(Material.DIAMOND_BLOCK);
        Pathfinder p = new Pathfinder(pl.getLocation().getBlock().getLocation(), b.getLocation());

        Path path = p.getPath();

        Location[] locations = path.getPathLocations();
        for (int i = 0; i < locations.length; i++)
            locations[i].getBlock().setType(Material.SPONGE);
        return false;
    }
}
