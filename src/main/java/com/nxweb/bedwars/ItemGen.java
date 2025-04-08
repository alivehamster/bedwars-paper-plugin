package com.nxweb.bedwars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ItemGen {
    private final Location location;
    private final Plugin plugin;
    private final long intervalTicks;
    private BukkitTask itemTask;
    private BukkitTask particleTask;

    public ItemGen(Plugin plugin, World world, double x, double y, double z) {
        this.plugin = plugin;
        this.location = new Location(world, x, y, z);
        this.intervalTicks = 5 * 20L; // 5 seconds converted to ticks (20 ticks = 1 second)
    }
    
    /**
     * Starts the item generator.
     */
    public void start() {
        // Cancel any existing task
        if (itemTask != null || particleTask != null) {
            stop();
        }
        
        // Create a new repeating task for items
        itemTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnIronIngot();
            }
        }.runTaskTimer(plugin, 0L, intervalTicks);
        
        // Create a new repeating task for particles
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnParticles();
            }
        }.runTaskTimer(plugin, 0L, 5L); // Run every 5 ticks (1/4 second)
    }
    
    /**
     * Stops the item generator.
     */
    public void stop() {
        if (itemTask != null && !itemTask.isCancelled()) {
            itemTask.cancel();
            itemTask = null;
        }
        
        if (particleTask != null && !particleTask.isCancelled()) {
            particleTask.cancel();
            particleTask = null;
        }
    }
    
    /**
     * Spawns an iron ingot at the generator's location.
     */
    private void spawnIronIngot() {
        ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
        location.getWorld().dropItem(location, ironIngot);
    }
    
    /**
     * Spawns particles at the generator's location.
     */
    private void spawnParticles() {
        location.getWorld().spawnParticle(
            Particle.CRIT,
            location.clone().add(0, 0.5, 0), // Slightly above the spawn point
            10, // Number of particles
            0.2, 0.3, 0.2, // Spread in x, y, z directions
            0.01 // Particle speed
        );
    }
}
