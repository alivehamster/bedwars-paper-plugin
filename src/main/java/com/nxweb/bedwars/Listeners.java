package com.nxweb.bedwars;

import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Listeners implements Listener {

    private final JavaPlugin plugin;
    private final Shop shop;

    public Listeners(JavaPlugin plugin, Shop shop) {
        this.plugin = plugin;
        this.shop = shop;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!").color(TextColor.color(0x13f832)));
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

    @EventHandler
    public void onNpcInteract(NpcInteractEvent event) {
        if (event.getEntry().getId().startsWith("bedwars-shop")) {
            var player = event.getPlayer();
            // Schedule inventory opening to run on the main server thread
            Bukkit.getScheduler().runTask(plugin, () -> shop.openCustomInventory(player));
        }
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        
        // Check if the broken block is a bed
        if (block.getType().name().endsWith("_BED")) {

        }
    }
}
