package com.nxweb.bedwars;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is the custom inventory
        if (event.getView().title().equals(Component.text("Shop"))) {
            event.setCancelled(true); // Prevent item movement

            // Handle clicks on specific slots
            if (event.getSlot() == 4) { // Center slot
                Player player = (Player) event.getWhoClicked();
                PlayerInventory inventory = player.getInventory();
                ItemStack gold = new ItemStack(Material.GOLD_INGOT, 4);

                if (inventory.containsAtLeast(gold, 4)) {
                    inventory.removeItem(gold);
                    inventory.addItem(new ItemStack(Material.DIAMOND, 1)); // Give the player a diamond
                    player.sendMessage(Component.text("You clicked the diamond and spent 4 gold!"));
                } else {
                    player.sendMessage(Component.text("You need 4 gold to click this item!"));
                }
            }
        }
    }
}
