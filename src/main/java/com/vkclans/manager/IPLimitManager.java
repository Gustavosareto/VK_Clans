package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gerencia limite de clãs por IP (anti multi-conta)
 */
public class IPLimitManager {
    private static IPLimitManager instance;
    
    // Map de IP -> lista de UUIDs de jogadores
    private final Map<String, Set<UUID>> ipPlayers = new HashMap<>();
    // Map de UUID -> IP
    private final Map<UUID, String> playerIPs = new HashMap<>();
    
    private IPLimitManager() {
        loadData();
    }
    
    public static IPLimitManager getInstance() {
        if (instance == null) instance = new IPLimitManager();
        return instance;
    }
    
    /**
     * Registra o IP de um jogador
     */
    public void registerPlayer(Player player) {
        String ip = getPlayerIP(player);
        UUID uuid = player.getUniqueId();
        
        // Remove do IP antigo se mudou
        String oldIP = playerIPs.get(uuid);
        if (oldIP != null && !oldIP.equals(ip)) {
            Set<UUID> oldPlayers = ipPlayers.get(oldIP);
            if (oldPlayers != null) {
                oldPlayers.remove(uuid);
            }
        }
        
        // Registra no novo IP
        playerIPs.put(uuid, ip);
        ipPlayers.computeIfAbsent(ip, k -> new HashSet<>()).add(uuid);
        
        saveData();
    }
    
    /**
     * Verifica se o jogador pode criar/entrar em um clã
     * Retorna true se pode, false se atingiu o limite
     */
    public boolean canJoinClan(Player player, String clanName) {
        if (!ConfigManager.getInstance().isLimiteIPAtivo()) {
            return true;
        }
        
        int maxPerIP = ConfigManager.getInstance().getMaxClasPerIP();
        String ip = getPlayerIP(player);
        
        Set<UUID> playersOnIP = ipPlayers.getOrDefault(ip, new HashSet<>());
        
        // Conta quantos clãs diferentes os jogadores deste IP estão
        Set<String> clansOnIP = new HashSet<>();
        for (UUID uuid : playersOnIP) {
            if (uuid.equals(player.getUniqueId())) continue; // Ignora o próprio jogador
            
            Clan clan = ClanManager.getInstance().getPlayerClan(uuid);
            if (clan != null) {
                clansOnIP.add(clan.getName());
            }
        }
        
        // Se o clã já está na lista deste IP, permite
        if (clansOnIP.contains(clanName)) {
            return true;
        }
        
        // Verifica se atingiu o limite
        return clansOnIP.size() < maxPerIP;
    }
    
    /**
     * Verifica se pode criar clã
     */
    public boolean canCreateClan(Player player) {
        if (!ConfigManager.getInstance().isLimiteIPAtivo()) {
            return true;
        }
        
        int maxPerIP = ConfigManager.getInstance().getMaxClasPerIP();
        String ip = getPlayerIP(player);
        
        Set<UUID> playersOnIP = ipPlayers.getOrDefault(ip, new HashSet<>());
        
        // Conta quantos clãs diferentes os jogadores deste IP estão
        Set<String> clansOnIP = new HashSet<>();
        for (UUID uuid : playersOnIP) {
            Clan clan = ClanManager.getInstance().getPlayerClan(uuid);
            if (clan != null) {
                clansOnIP.add(clan.getName());
            }
        }
        
        return clansOnIP.size() < maxPerIP;
    }
    
    /**
     * Retorna lista de jogadores no mesmo IP
     */
    public Set<UUID> getPlayersOnSameIP(Player player) {
        String ip = getPlayerIP(player);
        return ipPlayers.getOrDefault(ip, new HashSet<>());
    }
    
    private String getPlayerIP(Player player) {
        if (player.getAddress() == null) return "unknown";
        return player.getAddress().getAddress().getHostAddress();
    }
    
    private void loadData() {
        File file = new File(VKClans.getInstance().getDataFolder(), "ips.yml");
        if (!file.exists()) return;
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String uuidStr : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                String ip = config.getString(uuidStr);
                
                playerIPs.put(uuid, ip);
                ipPlayers.computeIfAbsent(ip, k -> new HashSet<>()).add(uuid);
            } catch (IllegalArgumentException ignored) {}
        }
    }
    
    private void saveData() {
        File file = new File(VKClans.getInstance().getDataFolder(), "ips.yml");
        YamlConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<UUID, String> entry : playerIPs.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar IPs: " + e.getMessage());
        }
    }
}
