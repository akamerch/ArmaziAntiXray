package dev.armazi.antixray.listeners;

import dev.armazi.antixray.ArmaziAntiXray;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player join/quit listener
 */
public class PlayerListener implements Listener {
    
    private final ArmaziAntiXray plugin;

    public PlayerListener(ArmaziAntiXray plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player profile
        plugin.getAntiXrayManager().getOrCreateProfile(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up player profile
        plugin.getAntiXrayManager().removeProfile(event.getPlayer());
    }
}