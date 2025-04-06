package com.nxweb.bedwars;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lol.pyr.znpcsplus.api.NpcApi;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.npc.NpcType;
import lol.pyr.znpcsplus.util.NpcLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandRegistry {
    // List of all available bed colors in Minecraft
    private static final List<String> BED_COLORS = Arrays.asList(
        "RED", "BLACK", "BLUE", "BROWN", "CYAN", "GRAY", "GREEN", 
        "LIGHT_BLUE", "LIGHT_GRAY", "LIME", "MAGENTA", "ORANGE", 
        "PINK", "PURPLE", "WHITE", "YELLOW"
    );
    
    public static LiteralCommandNode<CommandSourceStack> createShop(final NpcApi npcApi, final NpcType NpcType) {
        return Commands.literal("createShop")
                .requires(source -> source.getExecutor() instanceof Player && source.getSender().isOp() )
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getExecutor();

                    // Count existing shop NPCs
                    long shopCount = npcApi.getNpcRegistry().getAll().stream()
                            .filter(npc -> npc.getId().startsWith("bedwars-shop"))
                            .count();

                    // Create new NPC with incremented number
                    NpcEntry playerNpc = npcApi.getNpcRegistry().create(
                            "bedwars-shop" + (shopCount + 1), // Incremented NPC ID
                            player.getWorld(),
                            NpcType,
                            new NpcLocation(
                                player.getLocation().getX(),
                                player.getLocation().getY(),
                                player.getLocation().getZ(),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch()
                            )
                    );
                    playerNpc.enableEverything();

                    player.sendMessage(Component.text("Use ZNPCsPlus commands to edit the NPC: ")
                            .append(Component.text("https://github.com/Pyrbu/ZNPCsPlus/wiki/Commands")
                            .clickEvent(ClickEvent.openUrl("https://github.com/Pyrbu/ZNPCsPlus/wiki/Commands"))));

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> giveTeamBed(NamespacedKey key) {
        return Commands.literal("giveTeamBed")
                .requires(source -> source.getExecutor() instanceof Player && source.getSender().isOp())
                .then(Commands.argument("color", StringArgumentType.word())
                    .suggests((context, builder) -> {
                        // Add all bed color suggestions
                        for (String color : BED_COLORS) {
                            if (color.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                builder.suggest(color.toLowerCase());
                            }
                        }
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        Player player = (Player) ctx.getSource().getExecutor();
                        String colorName = StringArgumentType.getString(ctx, "color").toUpperCase();
                        
                        try {
                            // Try to construct bed material from color name
                            Material bedMaterial = Material.valueOf(colorName + "_BED");
                            
                            if(player != null) {
                                ItemStack item = ItemStack.of(bedMaterial);
                                item.editMeta(meta -> {
                                    meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
                                    
                                    meta.displayName(Component.text("Team Bed")
                                        .color(TextColor.color(0xFF55FF)));
                                });
                                player.getInventory().addItem(item);
                                player.sendMessage(Component.text("Team Bed (" + colorName + ")"));
                            }
                            
                            return Command.SINGLE_SUCCESS;
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(Component.text("Invalid bed color: " + colorName)
                                .color(TextColor.color(0xFF5555)));
                            return 0;
                        }
                    }))
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getExecutor();
                    if(player != null) {
                        ItemStack item = ItemStack.of(Material.RED_BED);
                        item.editMeta(meta -> {
                            // Ensure we're consistently using INTEGER
                            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
                            
                            // Add a display name to make it clear this is a special bed
                            meta.displayName(Component.text("Team Bed")
                                .color(TextColor.color(0xFF55FF)));
                        });
                        player.getInventory().addItem(item);
                        player.sendMessage(Component.text("Team Bed"));
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
