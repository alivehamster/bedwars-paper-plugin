package com.nxweb.bedwars;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ItemGen {
    private final Location location;
    private final Plugin plugin;
    private final long intervalTicks;
    private BukkitTask task;

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
        if (task != null) {
            stop();
        }
        
        // Create a new repeating task
        task = new BukkitRunnable() {
            @Override
            public void run() {
                spawnIronIngot();
            }
        }.runTaskTimer(plugin, 0L, intervalTicks);
    }
    
    /**
     * Stops the item generator.
     */
    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }
    
    /**
     * Spawns an iron ingot at the generator's location.
     */
    private void spawnIronIngot() {
        ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
        location.getWorld().dropItem(location, ironIngot);
    }
}
