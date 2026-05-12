package dev.armazi.antixray.utils;

import dev.armazi.antixray.ArmaziAntiXray;

/**
 * Beautiful console logger with formatting
 */
public class Logger {
    
    private final ArmaziAntiXray plugin;
    private static final String PREFIX = "§b§lArmaziAntiXray §8»";

    public Logger(ArmaziAntiXray plugin) {
        this.plugin = plugin;
    }

    public void info(String message) {
        plugin.getLogger().info(stripColors(PREFIX + " §f" + message));
    }

    public void success(String message) {
        plugin.getLogger().info(stripColors(PREFIX + " §2✓ §a" + message));
    }

    public void error(String message) {
        plugin.getLogger().severe(stripColors(PREFIX + " §4✗ §c" + message));
    }

    public void warn(String message) {
        plugin.getLogger().warning(stripColors(PREFIX + " §6⚠ §e" + message));
    }

    public void printBanner() {
        plugin.getLogger().info("§3§m===========================================");
        plugin.getLogger().info("§b§lArmaziAntiXray v" + plugin.getDescription().getVersion());
        plugin.getLogger().info("§bEnterprise-Grade Anti-Xray Protection");
        plugin.getLogger().info("§3§m===========================================");
    }

    private String stripColors(String text) {
        return text.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "")
                  .replaceAll("&#[0-9a-fA-F]{6}", "");
    }
}