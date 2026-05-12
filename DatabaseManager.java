package dev.armazi.antixray.config;

import dev.armazi.antixray.ArmaziAntiXray;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all configuration files for ArmaziAntiXray
 */
public class ConfigManager {
    
    private final ArmaziAntiXray plugin;
    private final Map<String, FileConfiguration> configs;
    private final File dataFolder;

    public ConfigManager(ArmaziAntiXray plugin) throws IOException {
        this.plugin = plugin;
        this.configs = new HashMap<>();
        this.dataFolder = plugin.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        loadAllConfigs();
    }

    private void loadAllConfigs() throws IOException {
        loadConfig("config.yml", getDefaultConfig());
        loadConfig("messages.yml", getDefaultMessages());
        loadConfig("modes.yml", getDefaultModes());
        loadConfig("worlds.yml", getDefaultWorlds());
        loadConfig("punishments.yml", getDefaultPunishments());
        loadConfig("detections.yml", getDefaultDetections());
    }

    private void loadConfig(String filename, String defaultContent) throws IOException {
        File file = new File(dataFolder, filename);

        if (!file.exists()) {
            Files.write(file.toPath(), defaultContent.getBytes(StandardCharsets.UTF_8));
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(filename, config);
    }

    public FileConfiguration getConfig(String filename) {
        return configs.get(filename);
    }

    public void reloadAllConfigs() throws IOException {
        configs.clear();
        loadAllConfigs();
    }

    private String getDefaultConfig() {
        return """
                # ArmaziAntiXray Configuration
                # Enterprise-Grade Anti-Xray Protection System
                
                # Global settings
                global:
                  enabled: true
                  debug: false
                  async-enabled: true
                  
                # Cache settings
                cache:
                  max-chunks: 10000
                  max-players: 5000
                  ttl-minutes: 30
                  
                # Detection settings
                detection:
                  enabled: true
                  sensitivity: MEDIUM
                  min-confidence: 75
                  
                # Database settings
                database:
                  type: SQLITE
                  sqlite:
                    path: plugins/ArmaziAntiXray/data.db
                  mysql:
                    host: localhost
                    port: 3306
                    database: antixray
                    username: root
                    password: password
                    max-pool-size: 10
                    
                # Discord webhook
                discord:
                  enabled: false
                  webhook-url: ""
                  alert-alerts: true
                  alert-punishments: true
                  """;
    }

    private String getDefaultMessages() {
        return """
                # Messages Configuration
                # All messages include their own prefix
                
                # Command messages
                commands:
                  no-permission: "&#ff0000&lArmaziAntiXray &8» &#ff5555You do not have permission for this command."
                  reload-success: "&#00ff00&lArmaziAntiXray &8» &#55ff55Configuration reloaded successfully."
                  invalid-usage: "&#ff0000&lArmaziAntiXray &8» &#ff5555Invalid usage: %usage%"
                  player-not-found: "&#ff0000&lArmaziAntiXray &8» &#ff5555Player '%player%' not found."
                  
                # Alert messages
                alerts:
                  xray-detected: "&#ff5555⚠ &lXray Detected &8» &#ffaa00%player% may be using Xray! &#55ff55Confidence: %confidence%%%"
                  suspicious-mining: "&#ffaa00⚠ &lSuspicious Mining &8» &#ffaa00%player% - Score: %score%"
                  punishment-issued: "&#ff0000⚠ &lPunishment &8» &#ffaa00%player% has been %punishment% for Xray abuse."
                  """;
    }

    private String getDefaultModes() {
        return """
                # Anti-Xray Modes Configuration
                
                modes:
                  obfuscation:
                    enabled: true
                    dynamic: true
                    randomize: true
                    exposure-check: true
                    
                  fake-ore:
                    enabled: true
                    percentage: 15
                    randomize: true
                    psychological-effect: true
                    
                  packet:
                    enabled: true
                    async: true
                    cache-enabled: true
                    compression-optimized: true
                    
                  detection:
                    enabled: true
                    advanced: true
                    ai-patterns: true
                    """;
    }

    private String getDefaultWorlds() {
        return """
                # Per-World Configuration
                
                worlds:
                  default:
                    enabled: true
                    mode: ADVANCED
                    fake-ores: true
                    """;
    }

    private String getDefaultPunishments() {
        return """
                # Punishment System Configuration
                
                punishments:
                  level-1:
                    command: "warn %player% Suspicious mining detected"
                    broadcast: false
                    
                  level-2:
                    command: "kick %player% Xray detection triggered"
                    broadcast: true
                    
                  level-3:
                    command: "tempban %player% 7d Xray abuse"
                    broadcast: true
                  """;
    }

    private String getDefaultDetections() {
        return """
                # Detection System Configuration
                
                detection:
                  impossible-paths:
                    enabled: true
                    weight: 25
                    
                  ore-ratio:
                    enabled: true
                    weight: 20
                    
                  mining-speed:
                    enabled: true
                    weight: 15
                    
                  pattern-analysis:
                    enabled: true
                    weight: 40
                  """;
    }
}