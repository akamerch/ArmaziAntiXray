package dev.armazi.antixray.core;

import dev.armazi.antixray.ArmaziAntiXray;
import dev.armazi.antixray.detection.DetectionEngine;
import dev.armazi.antixray.obfuscation.ObfuscationEngine;
import dev.armazi.antixray.packets.PacketModifier;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core Anti-Xray manager orchestrating all systems
 */
public class AntiXrayManager {
    
    private final ArmaziAntiXray plugin;
    private final DetectionEngine detectionEngine;
    private final ObfuscationEngine obfuscationEngine;
    private final PacketModifier packetModifier;
    private final Map<UUID, PlayerXrayProfile> playerProfiles;
    private final Set<UUID> bypassedPlayers;

    public AntiXrayManager(ArmaziAntiXray plugin) {
        this.plugin = plugin;
        this.detectionEngine = new DetectionEngine(plugin);
        this.obfuscationEngine = new ObfuscationEngine(plugin);
        this.packetModifier = new PacketModifier(plugin);
        this.playerProfiles = new ConcurrentHashMap<>();
        this.bypassedPlayers = ConcurrentHashMap.newKeySet();
    }

    public void initialize() {
        detectionEngine.initialize();
        obfuscationEngine.initialize();
        packetModifier.initialize();
    }

    public void processChunk(Chunk chunk) {
        if (chunk == null || !isWorldEnabled(chunk.getWorld())) return;
        
        plugin.getAsyncExecutor().execute(() -> {
            try {
                obfuscationEngine.obfuscateChunk(chunk);
                packetModifier.modifyChunkPacket(chunk);
            } catch (Exception e) {
                plugin.getPluginLogger().error("Error processing chunk: " + e.getMessage());
            }
        });
    }

    public void processPlayerMining(Player player, org.bukkit.block.Block block) {
        if (isBypassed(player)) return;
        
        PlayerXrayProfile profile = getOrCreateProfile(player);
        profile.recordMine(block);
        
        plugin.getAsyncExecutor().execute(() -> {
            double suspicionScore = detectionEngine.calculateSuspicionScore(profile);
            if (suspicionScore > plugin.getConfigManager().getConfig("config.yml")
                    .getDouble("detection.min-confidence", 75)) {
                handleDetection(player, suspicionScore);
            }
        });
    }

    private void handleDetection(Player player, double suspicionScore) {
        PlayerXrayProfile profile = playerProfiles.get(player.getUniqueId());
        
        plugin.getPluginManager().callEvent(
            new XrayDetectionEvent(player, profile, suspicionScore)
        );
        
        // Alert staff
        alertStaff(player, suspicionScore, profile);
        
        // Execute punishment if confidence is high enough
        if (suspicionScore > 90) {
            executePunishment(player, profile);
        }
    }

    private void alertStaff(Player player, double suspicionScore, PlayerXrayProfile profile) {
        String message = plugin.getConfigManager().getConfig("messages.yml")
            .getString("alerts.xray-detected")
            .replace("%player%", player.getName())
            .replace("%confidence%", String.format("%.0f", suspicionScore));
        
        // Convert to Adventure component and send
        // TODO: Implement with Adventure API
    }

    private void executePunishment(Player player, PlayerXrayProfile profile) {
        // Get punishment level based on profile
        int level = determinePunishmentLevel(profile);
        
        String command = plugin.getConfigManager().getConfig("punishments.yml")
            .getString("punishments.level-" + level + ".command", "")
            .replace("%player%", player.getName());
        
        if (!command.isEmpty()) {
            plugin.getServer().dispatchCommand(
                plugin.getServer().getConsoleSender(), 
                command
            );
        }
    }

    private int determinePunishmentLevel(PlayerXrayProfile profile) {
        if (profile.getViolationCount() >= 3) return 3;
        if (profile.getViolationCount() >= 2) return 2;
        return 1;
    }

    private PlayerXrayProfile getOrCreateProfile(Player player) {
        return playerProfiles.computeIfAbsent(player.getUniqueId(), 
            uuid -> new PlayerXrayProfile(player));
    }

    private boolean isWorldEnabled(World world) {
        return plugin.getConfigManager().getConfig("worlds.yml")
            .getBoolean("worlds.default.enabled", true);
    }

    public boolean isBypassed(Player player) {
        return bypassedPlayers.contains(player.getUniqueId()) || 
               player.hasPermission("armaziantixray.bypass");
    }

    public void setBypassed(Player player, boolean bypassed) {
        if (bypassed) {
            bypassedPlayers.add(player.getUniqueId());
        } else {
            bypassedPlayers.remove(player.getUniqueId());
        }
    }

    public PlayerXrayProfile getProfile(Player player) {
        return playerProfiles.get(player.getUniqueId());
    }

    public void removeProfile(Player player) {
        playerProfiles.remove(player.getUniqueId());
    }
}