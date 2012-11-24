package com.gmail.goodfaceforradio.allowflight;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AllowFlight extends JavaPlugin implements Listener {

    // Executed when the plugin is enabled (automatically at server startup).
    public void onEnable() {
        // Register for events to receive calls to onProjectileHit.
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        player.setAllowFlight(true);
    }

}