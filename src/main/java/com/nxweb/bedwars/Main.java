package com.nxweb.bedwars;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lol.pyr.znpcsplus.api.NpcApi;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.NpcType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin  {
    @Override
    public void onEnable() {

        Shop shop = new Shop();

        Bukkit.getPluginManager().registerEvents(new Listeners(this, shop), this);

        NpcApi npcApi = NpcApiProvider.get();
        NpcType playerNpcType = npcApi.getNpcTypeRegistry().getByName("player"); // Case-insensitive
        if (playerNpcType == null) {
            System.out.println("NPC type does not exist or is not available on this server version.");
            return;
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CommandRegistry.createShop(npcApi, playerNpcType));
            commands.registrar().register(CommandRegistry.giveSpecialBed());
        });
    }
}
