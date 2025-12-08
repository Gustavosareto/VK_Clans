package com.vkclans.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gerencia cooldowns de teleporte e estado de combate
 */
public class CooldownManager {
    private static CooldownManager instance;
    
    private final Map<UUID, Long> teleportCooldowns = new HashMap<>();
    private final Map<UUID, Long> combatTags = new HashMap<>();
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>(); // type -> {player -> expiration}
    
    private CooldownManager() {}
    
    public static CooldownManager getInstance() {
        if (instance == null) instance = new CooldownManager();
        return instance;
    }
    
    /**
     * Define cooldown genérico para jogador
     */
    public void setCooldown(UUID player, String type, long durationMs) {
        cooldowns.computeIfAbsent(type, k -> new HashMap<>())
            .put(player, System.currentTimeMillis() + durationMs);
    }
    
    /**
     * Verifica se jogador está em cooldown de um tipo
     */
    public boolean hasCooldown(UUID player, String type) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns == null) return false;
        Long expiration = typeCooldowns.get(player);
        if (expiration == null) return false;
        return System.currentTimeMillis() < expiration;
    }
    
    /**
     * Retorna tempo restante do cooldown em ms
     */
    public long getRemainingCooldown(UUID player, String type) {
        Map<UUID, Long> typeCooldowns = cooldowns.get(type);
        if (typeCooldowns == null) return 0;
        Long expiration = typeCooldowns.get(player);
        if (expiration == null) return 0;
        long remaining = expiration - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }
    
    /**
     * Define cooldown de teleporte para jogador
     */
    public void setTeleportCooldown(UUID player, int seconds) {
        teleportCooldowns.put(player, System.currentTimeMillis() + (seconds * 1000L));
    }
    
    /**
     * Verifica se jogador está em cooldown
     */
    public boolean isOnCooldown(UUID player) {
        Long cooldown = teleportCooldowns.get(player);
        if (cooldown == null) return false;
        return System.currentTimeMillis() < cooldown;
    }
    
    /**
     * Retorna tempo restante do cooldown em segundos
     */
    public int getRemainingCooldown(UUID player) {
        Long cooldown = teleportCooldowns.get(player);
        if (cooldown == null) return 0;
        long remaining = cooldown - System.currentTimeMillis();
        return remaining > 0 ? (int) (remaining / 1000) : 0;
    }
    
    /**
     * Marca jogador como em combate
     */
    public void tagCombat(UUID player) {
        int combatTime = ConfigManager.getInstance().getTempoCombate();
        combatTags.put(player, System.currentTimeMillis() + (combatTime * 1000L));
    }
    
    /**
     * Verifica se jogador está em combate
     */
    public boolean isInCombat(UUID player) {
        Long tag = combatTags.get(player);
        if (tag == null) return false;
        if (System.currentTimeMillis() >= tag) {
            combatTags.remove(player);
            return false;
        }
        return true;
    }
    
    /**
     * Remove tag de combate
     */
    public void removeCombatTag(UUID player) {
        combatTags.remove(player);
    }
    
    /**
     * Limpa dados do jogador
     */
    public void clearPlayer(UUID player) {
        teleportCooldowns.remove(player);
        combatTags.remove(player);
    }
}
