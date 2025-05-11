package com.nxweb.bedwars;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lol.pyr.znpcsplus.api.NpcApi;
import lol.pyr.znpcsplus.api.NpcApiProvider;
import lol.pyr.znpcsplus.api.npc.NpcType;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;


public class Main extends JavaPlugin  {
    private Teams teams;
    private DatabaseManager db;

    @Override
    public void onEnable() {
        this.db = new DatabaseManager();
        try {
            this.db.connect();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Disable mob spawning in all worlds
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            getLogger().info("Disabled mob spawning in world: " + world.getName());
        }

        NamespacedKey key = new NamespacedKey(this, "bedwars");
        Shop shop = new Shop();
        this.teams = new Teams();

        Bukkit.getPluginManager().registerEvents(new Listeners(this, shop, key, this.teams), this);

        NpcApi npcApi = NpcApiProvider.get();
        NpcType playerNpcType = npcApi.getNpcTypeRegistry().getByName("player"); // Case-insensitive
        if (playerNpcType == null) {
            System.out.println("NPC type does not exist or is not available on this server version.");
            return;
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CommandRegistry.createShop(npcApi, playerNpcType));
            commands.registrar().register(CommandRegistry.giveTeamBed(key));
            commands.registrar().register(CommandRegistry.startBedwars(this.teams));
            commands.registrar().register(CommandRegistry.createItemgen(this));
            commands.registrar().register(CommandRegistry.setBorder());
        });
    }

    @Override
    public void onDisable() {
        this.teams.deleteAllTeams();
        // todo:

        // store locations of itemgen on shutdown
        // make a way to delete itemgen
        // 5 second death countdown
        // spawnpoint for teams
        // players join in spectator
        // start team takes players puts them in survival and takes them to the team spawn
    }
}
