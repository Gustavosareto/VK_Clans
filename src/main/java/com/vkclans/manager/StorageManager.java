package com.vkclans.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.vkclans.model.Clan;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Gerencia armazenamento dos clãs em YAML ou Banco de Dados (Singleton)
 */
public class StorageManager {
    private static StorageManager instance;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("VKClans");
    private final File file = new File(plugin.getDataFolder(), "clans.yml");
    private YamlConfiguration config;
    private HikariDataSource dataSource;
    private boolean useDatabase;

    private StorageManager() {
        useDatabase = plugin.getConfig().getBoolean("database.enabled", false);
        if (useDatabase) {
            setupDatabase();
        }
    }

    public static StorageManager getInstance() {
        if (instance == null) instance = new StorageManager();
        return instance;
    }

    private void setupDatabase() {
        HikariConfig config = new HikariConfig();
        String type = plugin.getConfig().getString("database.type", "MYSQL");
        if ("MYSQL".equalsIgnoreCase(type)) {
            String host = plugin.getConfig().getString("database.mysql.host", "localhost");
            int port = plugin.getConfig().getInt("database.mysql.port", 3306);
            String database = plugin.getConfig().getString("database.mysql.database", "vkclans");
            String username = plugin.getConfig().getString("database.mysql.username", "root");
            String password = plugin.getConfig().getString("database.mysql.password", "");
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
        } else {
            String fileName = plugin.getConfig().getString("database.sqlite.file", "clans.db");
            File dbFile = new File(plugin.getDataFolder(), fileName);
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        }
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "CREATE TABLE IF NOT EXISTS clans (name VARCHAR(255) PRIMARY KEY, data TEXT)")) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    /**
     * Carrega clãs do armazenamento
     */
    public void loadClans() {
        if (useDatabase) {
            loadClansFromDB();
        } else {
            loadClansFromYAML();
        }
    }

    private void loadClansFromYAML() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar arquivo clans.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        ClanManager manager = ClanManager.getInstance();
        for (String key : config.getKeys(false)) {
            Clan clan = Clan.deserialize(config.getConfigurationSection(key));
            if (clan != null) manager.addClan(clan);
        }
        plugin.getLogger().info("Carregados " + manager.getAllClans().size() + " clãs do YAML.");
    }

    private void loadClansFromDB() {
        ClanManager manager = ClanManager.getInstance();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name, data FROM clans");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String data = rs.getString("data");
                YamlConfiguration tempConfig = new YamlConfiguration();
                tempConfig.loadFromString(data);
                Clan clan = Clan.deserialize(tempConfig);
                if (clan != null) manager.addClan(clan);
            }
        } catch (SQLException | org.bukkit.configuration.InvalidConfigurationException e) {
            plugin.getLogger().severe("Erro ao carregar clãs do DB: " + e.getMessage());
        }
        plugin.getLogger().info("Carregados " + manager.getAllClans().size() + " clãs do banco de dados.");
    }

    /**
     * Salva clãs no armazenamento (assíncrono se habilitado)
     */
    public void saveClans() {
        if (plugin.getConfig().getBoolean("performance.async-save", true)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, this::saveClansSync);
        } else {
            saveClansSync();
        }
    }

    /**
     * Salva clãs de forma síncrona
     */
    private void saveClansSync() {
        if (useDatabase) {
            saveClansToDB();
        } else {
            saveClansToYAML();
        }
    }

    private void saveClansToYAML() {
        if (config == null) config = new YamlConfiguration();
        ClanManager manager = ClanManager.getInstance();
        for (Clan clan : manager.getAllClans()) {
            config.set(clan.getName(), clan.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar clãs no YAML: " + e.getMessage());
        }
    }

    private void saveClansToDB() {
        ClanManager manager = ClanManager.getInstance();
        try (Connection conn = dataSource.getConnection()) {
            // Clear existing
            try (PreparedStatement clearStmt = conn.prepareStatement("DELETE FROM clans")) {
                clearStmt.execute();
            }
            // Insert all
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO clans (name, data) VALUES (?, ?)")) {
                for (Clan clan : manager.getAllClans()) {
                    YamlConfiguration tempConfig = new YamlConfiguration();
                    tempConfig.set("clan", clan.serialize());
                    stmt.setString(1, clan.getName());
                    stmt.setString(2, tempConfig.saveToString());
                    stmt.execute();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao salvar clãs no DB: " + e.getMessage());
        }
    }
    
    public void createBackup() {
        File backupDir = new File(plugin.getDataFolder(), "backups");
        backupDir.mkdirs();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        
        if (!useDatabase) {
            File backupFile = new File(backupDir, "clans_" + timestamp + ".yml");
            try {
                Files.copy(file.toPath(), backupFile.toPath());
                plugin.getLogger().info("Backup criado: " + backupFile.getName());
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar backup YAML: " + e.getMessage());
            }
        } else {
            // Para DB, exportar para arquivo (simplificado)
            File backupFile = new File(backupDir, "clans_db_" + timestamp + ".sql");
            // Implementar dump SQL se necessário
            plugin.getLogger().info("Backup DB não implementado ainda.");
        }
    }
}
