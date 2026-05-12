package dev.armazi.antixray;

import dev.armazi.antixray.config.ConfigManager;
import dev.armazi.antixray.core.AntiXrayManager;
import dev.armazi.antixray.database.DatabaseManager;
import dev.armazi.antixray.listeners.ChunkListener;
import dev.armazi.antixray.listeners.PlayerListener;
import dev.armazi.antixray.listeners.MiningListener;
import dev.armazi.antixray.commands.AntiXrayCommandManager;
import dev.armazi.antixray.utils.Logger;
import dev.armazi.antixray.cache.CacheManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ArmaziAntiXray - Enterprise-Grade Anti-Xray Protection System
 * Main plugin entry point for Paper/Spigot/Purpur 1.21+
 */
public class ArmaziAntiXray extends JavaPlugin {

    private static ArmaziAntiXray instance;
    
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private CacheManager cacheManager;
    private AntiXrayManager antiXrayManager;
    private ScheduledExecutorService asyncExecutor;
    
    private Logger pluginLogger;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize logger
        pluginLogger = new Logger(this);
        pluginLogger.printBanner();
        
        try {
            // Load configuration
            this.configManager = new ConfigManager(this);
            pluginLogger.info("✓ Configuration loaded successfully");
            
            // Initialize cache system
            this.cacheManager = new CacheManager();
            pluginLogger.info("✓ Cache system initialized");
            
            // Initialize database
            this.databaseManager = new DatabaseManager(this, configManager);
            databaseManager.initialize();
            pluginLogger.info("✓ Database connected");
            
            // Initialize async executor
            this.asyncExecutor = Executors.newScheduledThreadPool(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread t = new Thread(r, "ArmaziAntiXray-Async");
                    t.setDaemon(true);
                    return t;
                }
            );
            pluginLogger.info("✓ Async executor initialized with " + 
                Runtime.getRuntime().availableProcessors() + " threads");
            
            // Initialize core anti-xray system
            this.antiXrayManager = new AntiXrayManager(this);
            antiXrayManager.initialize();
            pluginLogger.info("✓ Anti-Xray core initialized");
            
            // Register listeners
            registerListeners();
            pluginLogger.info("✓ Event listeners registered");
            
            // Register commands
            registerCommands();
            pluginLogger.info("✓ Commands registered");
            
            pluginLogger.success("ArmaziAntiXray v" + getDescription().getVersion() + 
                " enabled successfully!");
            
        } catch (Exception e) {
            pluginLogger.error("Fatal error during startup!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (pluginLogger == null) return;
        
        pluginLogger.info("Shutting down ArmaziAntiXray...");
        
        try {
            // Shutdown async executor
            if (asyncExecutor != null) {
                asyncExecutor.shutdownNow();
                pluginLogger.info("✓ Async executor shut down");
            }
            
            // Close database
            if (databaseManager != null) {
                databaseManager.shutdown();
                pluginLogger.info("✓ Database connection closed");
            }
            
            // Clear cache
            if (cacheManager != null) {
                cacheManager.clearAll();
                pluginLogger.info("✓ Cache cleared");
            }
            
            pluginLogger.success("ArmaziAntiXray shut down safely");
            
        } catch (Exception e) {
            pluginLogger.error("Error during shutdown!");
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChunkListener(antiXrayManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MiningListener(antiXrayManager), this);
    }

    private void registerCommands() {
        AntiXrayCommandManager commandManager = new AntiXrayCommandManager(this);
        commandManager.register();
    }

    public static ArmaziAntiXray getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public AntiXrayManager getAntiXrayManager() {
        return antiXrayManager;
    }

    public ScheduledExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public Logger getPluginLogger() {
        return pluginLogger;
    }
}