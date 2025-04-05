package com.nxweb.bedwars;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shop implements Listener {
    private final Inventory customInventory;

    public Shop() {
        // Create a custom inventory with 9 slots and a title
        customInventory = Bukkit.createInventory(null, 9, Component.text("Shop"));

        // Add an item to the inventory
        ItemStack item = new ItemStack(Material.DIAMOND);
        customInventory.setItem(4, item); // Add it to the center slot (index 4)
    }

    // Open the custom inventory for a player (can be triggered elsewhere in your code)
    public void openCustomInventory(Player player) {
        player.openInventory(customInventory);
    }

}
