package com.nxweb.bedwars;

import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.persistence.PersistentDataViewHolder;
import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
    private final NamespacedKey key;
    private final Teams teams;


    public Listeners(JavaPlugin plugin, Shop shop, NamespacedKey key, Teams teams) {
        this.plugin = plugin;
        this.shop = shop;
        this.key = key;
        this.teams = teams;
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
    public void onMultiBlockPlace(BlockMultiPlaceEvent event) {
        // This event is triggered for beds which are multi-block structures
        Block mainBlock = event.getBlock();

        if (mainBlock.getType().name().endsWith("_BED")) {
            Player player = event.getPlayer();
            ItemStack item = event.getItemInHand();

            // Check if the key exists before attempting to retrieve its value
            if (item.getPersistentDataContainer().has(key)) {
                Integer value = item.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                if(value != null) {
                    if (mainBlock.getState() instanceof Bed bed) {
                        String color = bed.getColor().toString();
                        bed.getPersistentDataContainer().set(key, PersistentDataType.STRING, color);
                        bed.update();
                        player.sendMessage(Component.text("You placed a team bed!").color(TextColor.color(0x13f832)));

                    }
                }
            }
        }
    }

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if the broken block is a bed
        if(block.getState() instanceof Bed bed) {
            String teamBedColor = bed.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            System.out.println(teamBedColor);
            
            // Use the mapping to get the correct NamedTextColor from bed color string
            if (teamBedColor != null) {
                NamedTextColor namedColor = teams.getTeamColorFromBedColor(teamBedColor);
                if (namedColor != null) {
                    // Find and remove the color from aliveTeams
                    for (int i = 0; i < teams.aliveTeams.length; i++) {
                        if (teams.aliveTeams[i] != null && teams.aliveTeams[i].equals(namedColor)) {
                            teams.aliveTeams[i] = null;
                            
                            // Broadcast bed broken message
                            Bukkit.getServer().sendMessage(
                                    Component.text("Team " + teamBedColor + "'s bed has been destroyed!")
                                            .color(TextColor.color(0xFF5555))
                            );
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        TextColor teamColor = teams.getPlayerTeamColor(player);
        
        if (teamColor != null) {
            // Check if player's team color is in aliveTeams
            boolean teamStillAlive = false;
            for (NamedTextColor aliveTeam : teams.aliveTeams) {
                if (aliveTeam != null && aliveTeam.equals(teamColor)) {
                    teamStillAlive = true;
                    break;
                }
            }
            
            // If team color not found in aliveTeams, set player to spectator mode
            if (!teamStillAlive) {
                // Schedule to run on the next tick to avoid issues during the death event
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                    player.getInventory().clear();
                    event.setCancelled(true);
                    player.sendMessage(Component.text("Your bed was destroyed! You are now a spectator.")
                            .color(TextColor.color(0xFF5555)));
                });
            }
        }
    }
}

