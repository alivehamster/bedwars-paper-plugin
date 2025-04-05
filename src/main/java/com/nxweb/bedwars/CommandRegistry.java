package com.nxweb.bedwars;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
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

    public static LiteralCommandNode<CommandSourceStack> giveSpecialBed() {
        return Commands.literal("giveSpecialBed")
                .requires(source -> source.getExecutor() instanceof Player && source.getSender().isOp())
                .executes(ctx -> {

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
