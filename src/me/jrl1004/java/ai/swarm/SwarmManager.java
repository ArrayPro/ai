package me.jrl1004.java.ai.swarm;

import java.util.Random;

import me.jrl1004.java.ai.main.PathfinderMain;
import net.minecraft.server.v1_8_R2.EntityInsentient;
import net.minecraft.server.v1_8_R2.PathEntity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SwarmManager implements Listener {

	private static final Random	rand	= new Random();

	private void enterPack(Creature creature, double radius) {
		EntityType type = creature.getType();
		World world = creature.getWorld();
		Creature master = null;
		double distance = Double.MAX_VALUE;
		int packSize = 0;

		for (Entity entity : world.getEntitiesByClass(Creature.class)) {
			if (!(entity.getType() == type))
				continue;
			++packSize;
			double d = creature.getLocation().distance(entity.getLocation());
			if (d < distance) {
				distance = d;
				master = (Creature) entity;
			}
		}

		int reqiredMass = (creature.getWorld().getDifficulty().ordinal() * 5) + 5;

		if (packSize < reqiredMass) {
			spawnReinforcements(master, reqiredMass - packSize);
		}
	}

	private void mobWalk(final Location loc, final LivingEntity pet) {
		if (!pet.isValid())
			return;
		EntityInsentient pett = (EntityInsentient) ((CraftEntity) pet).getHandle();
		pett.getNavigation().a(2);
		PathEntity path = pett.getNavigation().a(loc.getX() + 1, loc.getY(), loc.getZ() + 1);
		if (path == null)
			return;
		pett.getNavigation().a(path, 1.0D);
		pett.getNavigation().a(2.0D);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (!(e.getEntity() instanceof Creature))
			return;
		enterPack((Creature) e.getEntity(), 32);
	}

	private void spawnReinforcements(Creature master, int amount) {
	    if(master == null) return;
		for (int i = 0; i < amount; i++) {
			Creature slave = (Creature) master.getWorld().spawnEntity(master.getLocation().add(Math.sin(rand.nextInt(360)), 1, Math.cos(rand.nextInt(360))), master.getType());
			setMaster(slave, master);
		}
	}

	private void setMaster(Creature slave, Entity master) {
		if (!master.isValid() || !slave.isValid())
			return;
		setPassiveWalk(slave, master);
	}

	private void setPassiveWalk(final Creature slave, final Entity master) {
		new BukkitRunnable() {
			public void run() {
				if (!slave.isValid() || !master.isValid()) {
					cancel();
					return;
				}
				double d = slave.getLocation().distance(master.getLocation());
				if (d >= 5)
					mobWalk(master.getLocation(), slave);
			}
		}.runTaskTimer(PathfinderMain.instance, 0, 20);
	}
}
