package com.nxweb.bedwars;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegistry {
    public static LiteralCommandNode<CommandSourceStack> OpenStore(final Shop shop) {
        return Commands.literal("openStore")
                .requires(source -> source.getExecutor() instanceof Player && source.getSender().isOp()) // Requires operator level 2
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();

                    if (sender instanceof Player player) {
                            shop.openCustomInventory(player);
                            sender.sendPlainMessage("inventory opened");
                            // Perform player-specific actions
                    } else {
                        sender.sendMessage("Only players can use this command.");
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
