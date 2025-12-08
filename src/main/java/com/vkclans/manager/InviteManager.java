package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Gerencia convites de clã com expiração (Singleton)
 */
public class InviteManager {
    private static InviteManager instance;
    private final Map<UUID, Map<String, Long>> invites = new HashMap<>(); // UUID -> {ClanName -> ExpirationTime}

    private InviteManager() {
        // Inicia task de limpeza de convites expirados
        startCleanupTask();
    }

    public static InviteManager getInstance() {
        if (instance == null) instance = new InviteManager();
        return instance;
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(VKClans.getInstance(), () -> {
            long now = System.currentTimeMillis();
            Iterator<Map.Entry<UUID, Map<String, Long>>> it = invites.entrySet().iterator();
            
            while (it.hasNext()) {
                Map.Entry<UUID, Map<String, Long>> entry = it.next();
                UUID playerUUID = entry.getKey();
                Map<String, Long> playerInvites = entry.getValue();
                
                Iterator<Map.Entry<String, Long>> inviteIt = playerInvites.entrySet().iterator();
                while (inviteIt.hasNext()) {
                    Map.Entry<String, Long> invite = inviteIt.next();
                    if (now >= invite.getValue()) {
                        // Convite expirou
                        inviteIt.remove();
                        Player player = Bukkit.getPlayer(playerUUID);
                        if (player != null) {
                            Map<String, String> ph = new HashMap<>();
                            ph.put("clan", invite.getKey());
                            MessageUtil.send(player, "clan-invite-expired", ph);
                        }
                    }
                }
                
                if (playerInvites.isEmpty()) {
                    it.remove();
                }
            }
        }, 20L * 10, 20L * 10); // A cada 10 segundos
    }

    /**
     * Envia convite para jogador com expiração
     */
    public void sendInvite(UUID player, String clanName) {
        int expirationSeconds = ConfigManager.getInstance().getConviteExpiracao();
        long expirationTime = System.currentTimeMillis() + (expirationSeconds * 1000L);
        
        invites.computeIfAbsent(player, k -> new HashMap<>()).put(clanName, expirationTime);
    }

    /**
     * Verifica se jogador tem convite para clã
     */
    public boolean hasInvite(UUID player, String clanName) {
        Map<String, Long> playerInvites = invites.get(player);
        if (playerInvites == null) return false;
        
        Long expiration = playerInvites.get(clanName);
        if (expiration == null) return false;
        
        if (System.currentTimeMillis() >= expiration) {
            playerInvites.remove(clanName);
            return false;
        }
        
        return true;
    }

    /**
     * Verifica se jogador possui ao menos um convite válido
     */
    public boolean hasAnyInvite(UUID player) {
        Map<String, Long> playerInvites = invites.get(player);
        if (playerInvites == null || playerInvites.isEmpty()) return false;
        
        long now = System.currentTimeMillis();
        return playerInvites.values().stream().anyMatch(exp -> now < exp);
    }

    /**
     * Retorna todos os convites válidos do jogador
     */
    public Set<String> getInvites(UUID player) {
        Map<String, Long> playerInvites = invites.get(player);
        if (playerInvites == null) return Collections.emptySet();
        
        long now = System.currentTimeMillis();
        Set<String> validInvites = new HashSet<>();
        
        for (Map.Entry<String, Long> entry : playerInvites.entrySet()) {
            if (now < entry.getValue()) {
                validInvites.add(entry.getKey());
            }
        }
        
        return validInvites;
    }

    /**
     * Aceita/Remove convite específico
     */
    public void acceptInvite(UUID player, String clanName) {
        Map<String, Long> playerInvites = invites.get(player);
        if (playerInvites != null) {
            playerInvites.remove(clanName);
        }
    }

    /**
     * Remove convite específico
     */
    public void removeInvite(UUID player, String clanName) {
        Map<String, Long> playerInvites = invites.get(player);
        if (playerInvites != null) {
            playerInvites.remove(clanName);
        }
    }

    /**
     * Remove todos convites do jogador
     */
    public void clearInvites(UUID player) {
        invites.remove(player);
    }
    
    /**
     * Remove convites para um clan específico (quando o clan é deletado)
     */
    public void removeInvitesForClan(String clanName) {
        for (Map<String, Long> playerInvites : invites.values()) {
            playerInvites.remove(clanName);
        }
    }
}
