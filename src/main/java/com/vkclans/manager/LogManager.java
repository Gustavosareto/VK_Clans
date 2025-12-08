package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.ClanLog;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gerencia logs de ações dos clãs (Singleton)
 */
public class LogManager {
    private static LogManager instance;
    private final Map<String, List<ClanLog>> clanLogs = new HashMap<>(); // clanName -> logs
    private final File file;
    private YamlConfiguration config;
    private static final int MAX_LOGS_PER_CLAN = 100;

    private LogManager() {
        this.file = new File(VKClans.getInstance().getDataFolder(), "logs.yml");
        loadLogs();
    }

    public static LogManager getInstance() {
        if (instance == null) instance = new LogManager();
        return instance;
    }

    /**
     * Registra uma ação no log do clã
     */
    public void log(String clanName, UUID player, String playerName, ClanLog.LogType type, String details) {
        ClanLog logEntry = new ClanLog(player, playerName, type, details);
        
        List<ClanLog> logs = clanLogs.computeIfAbsent(clanName, k -> new ArrayList<>());
        logs.add(0, logEntry); // Adiciona no início (mais recente primeiro)

        // Limita quantidade de logs
        while (logs.size() > MAX_LOGS_PER_CLAN) {
            logs.remove(logs.size() - 1);
        }

        saveLogs();
    }

    /**
     * Obtém logs de um clã
     */
    public List<ClanLog> getLogs(String clanName) {
        return clanLogs.getOrDefault(clanName, new ArrayList<>());
    }

    /**
     * Obtém logs de um clã com limite
     */
    public List<ClanLog> getLogs(String clanName, int limit) {
        List<ClanLog> logs = getLogs(clanName);
        return logs.subList(0, Math.min(limit, logs.size()));
    }

    /**
     * Obtém logs de um tipo específico
     */
    public List<ClanLog> getLogsByType(String clanName, ClanLog.LogType type) {
        List<ClanLog> result = new ArrayList<>();
        for (ClanLog log : getLogs(clanName)) {
            if (log.getType() == type) {
                result.add(log);
            }
        }
        return result;
    }

    /**
     * Obtém logs de um jogador específico
     */
    public List<ClanLog> getLogsByPlayer(String clanName, UUID player) {
        List<ClanLog> result = new ArrayList<>();
        for (ClanLog log : getLogs(clanName)) {
            if (log.getPlayer() != null && log.getPlayer().equals(player)) {
                result.add(log);
            }
        }
        return result;
    }

    /**
     * Limpa logs de um clã
     */
    public void clearLogs(String clanName) {
        clanLogs.remove(clanName);
        saveLogs();
    }

    /**
     * Formata logs para exibição
     */
    public List<String> formatLogs(String clanName, int limit) {
        List<String> formatted = new ArrayList<>();
        List<ClanLog> logs = getLogs(clanName, limit);
        
        for (ClanLog log : logs) {
            formatted.add(log.getFormattedMessage());
        }
        
        return formatted;
    }

    @SuppressWarnings("unchecked")
    public void loadLogs() {
        if (!file.exists()) return;
        config = YamlConfiguration.loadConfiguration(file);

        for (String clanName : config.getKeys(false)) {
            List<ClanLog> logs = new ArrayList<>();
            List<Map<?, ?>> logList = config.getMapList(clanName);
            
            for (Map<?, ?> map : logList) {
                Map<String, Object> typedMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    typedMap.put(entry.getKey().toString(), entry.getValue());
                }
                try {
                    logs.add(ClanLog.deserialize(typedMap));
                } catch (Exception e) {
                    // Ignora logs inválidos
                }
            }
            
            clanLogs.put(clanName, logs);
        }
    }

    public void saveLogs() {
        config = new YamlConfiguration();
        for (Map.Entry<String, List<ClanLog>> entry : clanLogs.entrySet()) {
            List<Map<String, Object>> logList = new ArrayList<>();
            for (ClanLog log : entry.getValue()) {
                logList.add(log.serialize());
            }
            config.set(entry.getKey(), logList);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar logs.yml: " + e.getMessage());
        }
    }
}
