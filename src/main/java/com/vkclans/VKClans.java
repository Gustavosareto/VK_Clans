package com.vkclans;

import com.vkclans.command.ClanCommand;
import com.vkclans.command.OpenClanMenuCommand;
import com.vkclans.listener.*;
import com.vkclans.manager.*;
import com.vkclans.placeholder.VKClansExpansion;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principal do plugin VKClans
 * Plugin de Clans avançado com sistemas completos
 */
public class VKClans extends JavaPlugin {
    private static VKClans instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Salva configs padrão
        saveDefaultConfig();
        saveResource("messages.yml", false);
        
        // Inicializa managers principais
        ConfigManager.getInstance().reload();
        MessageUtil.loadMessages();
        StorageManager.getInstance().loadClans();
        
        // Inicializa managers secundários
        InviteManager.getInstance();
        BankManager.getInstance();
        WarManager.getInstance();
        MissionManager.getInstance();
        LogManager.getInstance();
        ChestManager.getInstance();
        AllyManager.getInstance();
        WeeklyRankingManager.getInstance();
        AchievementManager.getInstance();
        IPLimitManager.getInstance();
        
        // Registra listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new ClanGUIListener(), this);
        getServer().getPluginManager().registerEvents(new ChestListener(), this);
        getServer().getPluginManager().registerEvents(new ClanChatListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new FriendlyFireListener(), this);
        
        // Registra comandos
        ClanCommand clanCommand = new ClanCommand();
        getCommand("clan").setExecutor(clanCommand);
        getCommand("clan").setTabCompleter(clanCommand);
        getCommand("clans").setExecutor(new OpenClanMenuCommand());
        
        // Registra PlaceholderAPI se disponível
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VKClansExpansion(this).register();
            getLogger().info("PlaceholderAPI integrado com sucesso!");
        }
        
        getLogger().info("========================================");
        getLogger().info("  VKClans v1.1.0 habilitado!");
        getLogger().info("  Sistemas: Guerra, Ranking, Banco,");
        getLogger().info("            Nivel, Bau, Chat, Missoes");
        getLogger().info("            Aliancas, Conquistas, Semanal");
        getLogger().info("            Limite IP, Friendly Fire");
        getLogger().info("            PlaceholderAPI Integrado");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        // Salva todos os dados
        StorageManager.getInstance().saveClans();
        WarManager.getInstance().saveWars();
        MissionManager.getInstance().saveMissions();
        LogManager.getInstance().saveLogs();
        ChestManager.getInstance().saveChests();
        
        getLogger().info("VKClans desabilitado!");
    }

    /**
     * Retorna a instância principal do plugin
     */
    public static VKClans getInstance() {
        return instance;
    }
    
    /**
     * Recarrega o plugin
     */
    public void reload() {
        reloadConfig();
        ConfigManager.getInstance().reload();
        MessageUtil.loadMessages();
        StorageManager.getInstance().loadClans();
        getLogger().info("VKClans recarregado!");
    }
}
