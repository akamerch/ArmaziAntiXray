package dev.armazi.antixray.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.armazi.antixray.ArmaziAntiXray;
import dev.armazi.antixray.config.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

/**
 * Database management with HikariCP connection pooling
 */
public class DatabaseManager {
    
    private final ArmaziAntiXray plugin;
    private final ConfigManager configManager;
    private HikariDataSource dataSource;

    public DatabaseManager(ArmaziAntiXray plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void initialize() throws SQLException {
        FileConfiguration config = configManager.getConfig("config.yml");
        String type = config.getString("database.type", "SQLITE");
        
        if ("SQLITE".equalsIgnoreCase(type)) {
            initializeSQLite();
        } else if ("MYSQL".equalsIgnoreCase(type)) {
            initializeMySQL();
        }
        
        createTables();
    }

    private void initializeSQLite() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + 
            plugin.getDataFolder().getAbsolutePath() + "/data.db");
        config.setMaximumPoolSize(5);
        
        this.dataSource = new HikariDataSource(config);
        plugin.getPluginLogger().info("SQLite database initialized");
    }

    private void initializeMySQL() throws SQLException {
        FileConfiguration config = configManager.getConfig("config.yml");
        HikariConfig hikariConfig = new HikariConfig();
        
        hikariConfig.setJdbcUrl("jdbc:mysql://" + 
            config.getString("database.mysql.host", "localhost") + ":" +
            config.getInt("database.mysql.port", 3306) + "/" +
            config.getString("database.mysql.database", "antixray"));
        
        hikariConfig.setUsername(config.getString("database.mysql.username", "root"));
        hikariConfig.setPassword(config.getString("database.mysql.password", "password"));
        hikariConfig.setMaximumPoolSize(config.getInt("database.mysql.max-pool-size", 10));
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        
        this.dataSource = new HikariDataSource(hikariConfig);
        plugin.getPluginLogger().info("MySQL database initialized");
    }

    private void createTables() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS violations (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    player_uuid VARCHAR(36) NOT NULL,
                    player_name VARCHAR(32) NOT NULL,
                    violation_type VARCHAR(50) NOT NULL,
                    suspicion_score DOUBLE NOT NULL,
                    timestamp BIGINT NOT NULL,
                    details TEXT
                )
                """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mining_logs (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    player_uuid VARCHAR(36) NOT NULL,
                    player_name VARCHAR(32) NOT NULL,
                    block_type VARCHAR(50) NOT NULL,
                    x INT NOT NULL,
                    y INT NOT NULL,
                    z INT NOT NULL,
                    world VARCHAR(50) NOT NULL,
                    timestamp BIGINT NOT NULL
                )
                """);
            
            plugin.getPluginLogger().info("Database tables created");
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}