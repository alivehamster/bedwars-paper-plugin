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
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandRegistry {

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

    private static final List<String> BED_COLORS = Arrays.asList(
            "RED", "BLACK", "BLUE", "LIME",
            "PINK", "PURPLE", "WHITE", "YELLOW"
    );

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

    public static LiteralCommandNode<CommandSourceStack> startBedwars(Teams teams) {
        return Commands.literal("startBedwars")
                .requires(source-> source.getSender().isOp() )
                .executes(ctx -> {
                    teams.createTeamsForAllPlayers();

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> createItemgen(Plugin plugin) {
        return Commands.literal("createItemgen")
                .requires(source-> source.getSender().isOp() )
                .then(Commands.argument("x", IntegerArgumentType.integer())
                      .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                  .executes(ctx -> {
                                      Player player = (Player) ctx.getSource().getExecutor();
                                      int x = IntegerArgumentType.getInteger(ctx, "x");
                                      int y = IntegerArgumentType.getInteger(ctx, "y");
                                      int z = IntegerArgumentType.getInteger(ctx, "z");

                                      new ItemGen(
                                              plugin,
                                              player.getWorld(),
                                              x,
                                              y,
                                              z
                                      ).start();
                                      
                                      player.sendMessage(Component.text("Created ItemGen at coordinates: " + x + ", " + y + ", " + z));
                                      return Command.SINGLE_SUCCESS;
                                  })
                            )
                      )
                )
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getExecutor();

                    new ItemGen(
                            plugin,
                            player.getWorld(),
                            player.getLocation().getX(),
                            player.getLocation().getY(),
                            player.getLocation().getZ()
                    ).start();
                    
                    player.sendMessage(Component.text("Created ItemGen at your location"));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> setBorder() {
        return Commands.literal("setBorder")
                .requires(source-> source.getSender().isOp())
                .then(Commands.argument("x1", IntegerArgumentType.integer())
                      .then(Commands.argument("z1", IntegerArgumentType.integer())
                            .then(Commands.argument("x2", IntegerArgumentType.integer())
                                  .then(Commands.argument("z2", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            Player player = (Player) ctx.getSource().getExecutor();
                                            int x1 = IntegerArgumentType.getInteger(ctx, "x1");
                                            int z1 = IntegerArgumentType.getInteger(ctx, "z1");
                                            int x2 = IntegerArgumentType.getInteger(ctx, "x2");
                                            int z2 = IntegerArgumentType.getInteger(ctx, "z2");

                                            // Calculate center of the border
                                            double centerX = (x1 + x2) / 2.0;
                                            double centerZ = (z1 + z2) / 2.0;

                                            // Calculate the size (diameter) of the border
                                            // Using the maximum distance to ensure all points are covered
                                            double xSize = Math.abs(x2 - x1);
                                            double zSize = Math.abs(z2 - z1);
                                            double size = Math.max(xSize, zSize);

                                            // Set the world border
                                            player.getWorld().getWorldBorder().setCenter(centerX, centerZ);
                                            player.getWorld().getWorldBorder().setSize(size);

                                            player.sendMessage(Component.text("World border set with center at (" + 
                                                    centerX + ", " + centerZ + ") and size " + size)
                                                    .color(TextColor.color(0x55FF55)));

                                            return Command.SINGLE_SUCCESS;
                                        })
                                  )
                            )
                      )
                )
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getExecutor();
                    player.sendMessage(Component.text("Usage: /setBoarder <x1> <z1> <x2> <z2>")
                            .color(TextColor.color(0xFF5555)));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
