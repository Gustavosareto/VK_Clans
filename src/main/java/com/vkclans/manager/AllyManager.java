package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gerencia o sistema de alianças entre clãs
 */
public class AllyManager {
    private static AllyManager instance;
    
    // Map de clã -> lista de aliados
    private final Map<String, Set<String>> allies = new HashMap<>();
    // Map de clã -> convites de aliança pendentes
    private final Map<String, Set<String>> pendingAlliances = new HashMap<>();
    
    private AllyManager() {
        loadAllies();
    }
    
    public static AllyManager getInstance() {
        if (instance == null) instance = new AllyManager();
        return instance;
    }
    
    /**
     * Envia convite de aliança
     */
    public boolean sendAllyRequest(String fromClan, String toClan) {
        if (areAllies(fromClan, toClan)) return false;
        if (hasPendingRequest(fromClan, toClan)) return false;
        
        pendingAlliances.computeIfAbsent(toClan, k -> new HashSet<>()).add(fromClan);
        return true;
    }
    
    /**
     * Aceita convite de aliança
     */
    public boolean acceptAllyRequest(String clan, String fromClan) {
        Set<String> pending = pendingAlliances.get(clan);
        if (pending == null || !pending.contains(fromClan)) return false;
        
        // Verifica limite de aliados
        int maxAllies = ConfigManager.getInstance().getMaxAliados();
        if (getAllies(clan).size() >= maxAllies || getAllies(fromClan).size() >= maxAllies) {
            return false;
        }
        
        pending.remove(fromClan);
        
        // Adiciona aliança mútua
        allies.computeIfAbsent(clan, k -> new HashSet<>()).add(fromClan);
        allies.computeIfAbsent(fromClan, k -> new HashSet<>()).add(clan);
        
        saveAllies();
        return true;
    }
    
    /**
     * Recusa convite de aliança
     */
    public boolean denyAllyRequest(String clan, String fromClan) {
        Set<String> pending = pendingAlliances.get(clan);
        if (pending == null) return false;
        return pending.remove(fromClan);
    }
    
    /**
     * Remove aliança
     */
    public boolean removeAlly(String clan1, String clan2) {
        boolean removed = false;
        
        Set<String> allies1 = allies.get(clan1);
        if (allies1 != null) removed = allies1.remove(clan2);
        
        Set<String> allies2 = allies.get(clan2);
        if (allies2 != null) allies2.remove(clan1);
        
        if (removed) saveAllies();
        return removed;
    }
    
    /**
     * Verifica se dois clãs são aliados
     */
    public boolean areAllies(String clan1, String clan2) {
        Set<String> clanAllies = allies.get(clan1);
        return clanAllies != null && clanAllies.contains(clan2);
    }
    
    /**
     * Verifica se há convite pendente
     */
    public boolean hasPendingRequest(String fromClan, String toClan) {
        Set<String> pending = pendingAlliances.get(toClan);
        return pending != null && pending.contains(fromClan);
    }
    
    /**
     * Retorna lista de aliados
     */
    public Set<String> getAllies(String clan) {
        return allies.getOrDefault(clan, new HashSet<>());
    }
    
    /**
     * Retorna convites pendentes
     */
    public Set<String> getPendingRequests(String clan) {
        return pendingAlliances.getOrDefault(clan, new HashSet<>());
    }
    
    /**
     * Remove todas as alianças de um clã (quando deletado)
     */
    public void removeAllAlliances(String clan) {
        Set<String> clanAllies = allies.remove(clan);
        if (clanAllies != null) {
            for (String ally : clanAllies) {
                Set<String> allyAllies = allies.get(ally);
                if (allyAllies != null) allyAllies.remove(clan);
            }
        }
        pendingAlliances.remove(clan);
        saveAllies();
    }
    
    private void loadAllies() {
        File file = new File(VKClans.getInstance().getDataFolder(), "allies.yml");
        if (!file.exists()) return;
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String clan : config.getKeys(false)) {
            List<String> allyList = config.getStringList(clan);
            allies.put(clan, new HashSet<>(allyList));
        }
    }
    
    private void saveAllies() {
        File file = new File(VKClans.getInstance().getDataFolder(), "allies.yml");
        YamlConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<String, Set<String>> entry : allies.entrySet()) {
            config.set(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar alianças: " + e.getMessage());
        }
    }
}
