package com.vkclans.manager;

import com.vkclans.model.Clan;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;

/**
 * Gerencia armazenamento dos clãs em YAML (Singleton)
 */
public class StorageManager {
    private static StorageManager instance;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("VKClans");
    private final File file = new File(plugin.getDataFolder(), "clans.yml");
    private YamlConfiguration config;

    private StorageManager() {}

    public static StorageManager getInstance() {
        if (instance == null) instance = new StorageManager();
        return instance;
    }

    /**
     * Carrega clãs do arquivo YAML
     */
    public void loadClans() {
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
        plugin.getLogger().info("Carregados " + manager.getAllClans().size() + " clãs.");
    }

    /**
     * Salva clãs no arquivo YAML
     */
    public void saveClans() {
        config = new YamlConfiguration();
        for (Clan clan : ClanManager.getInstance().getAllClans()) {
            config.createSection(clan.getName(), clan.serialize());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar clans.yml: " + e.getMessage());
        }
    }
}
