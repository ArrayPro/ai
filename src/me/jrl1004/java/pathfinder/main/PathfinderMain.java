package me.jrl1004.java.pathfinder.main;

import java.util.Set;
import java.util.Stack;

import me.jrl1004.java.pathfinder.Pathfinder;
import me.jrl1004.java.pathfinder.Pathfinder.Path;

import org.bukkit.Material;
import org.bukkit.block.Block;
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

        Stack<Path> path = p.getPath();

        Block b = pl.getLocation().getBlock();

        while (!path.isEmpty()) {
            Path pa = path.pop();
            for (int i = 0; i < pa.moves; i++) {
                b = b.getRelative(pa.direction);
                b.setType(Material.SPONGE);
            }
        }

        return false;
    }
}
