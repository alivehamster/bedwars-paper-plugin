package com.nxweb.bedwars;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
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

        Store Store = new Store();

        Bukkit.getPluginManager().registerEvents(Store, this);


        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CommandRegistry.OpenStore(Store));
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!").color(TextColor.color(0x13f832)));
    }
}
