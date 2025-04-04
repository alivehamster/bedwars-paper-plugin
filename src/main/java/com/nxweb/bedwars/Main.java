package com.nxweb.bedwars;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lol.pyr.znpcsplus.api.NpcApi;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.NpcType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public class Main extends JavaPlugin implements Listener{
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        Shop Shop = new Shop(this);

        Bukkit.getPluginManager().registerEvents(Shop, this);

        NpcApi npcApi = NpcApiProvider.get();
        NpcType playerNpcType = npcApi.getNpcTypeRegistry().getByName("player"); // Case-insensitive
        if (playerNpcType == null) {
            System.out.println("NPC type does not exist or is not available on this server version.");
            return;
        }


        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CommandRegistry.createShop(npcApi, playerNpcType));
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!").color(TextColor.color(0x13f832)));
    }
}
