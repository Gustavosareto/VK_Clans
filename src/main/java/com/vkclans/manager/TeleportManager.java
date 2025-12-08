package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gerencia teleportes com delay
 */
public class TeleportManager {
    private static TeleportManager instance;
    
    private final Map<UUID, TeleportTask> pendingTeleports = new HashMap<>();
    
    private TeleportManager() {}
    
    public static TeleportManager getInstance() {
        if (instance == null) instance = new TeleportManager();
        return instance;
    }
    
    /**
     * Inicia teleporte com delay
     */
    public void teleportWithDelay(Player player, Location destination, int delay, boolean cancelOnMove) {
        UUID uuid = player.getUniqueId();
        
        // Verifica se o destino é válido
        if (destination == null || destination.getWorld() == null) {
            MessageUtil.send(player, "clan-base-invalid");
            return;
        }
        
        // Cancela teleporte pendente se houver
        cancelTeleport(uuid);
        
        if (delay <= 0) {
            // Teleporte instantâneo
            player.teleport(destination);
            MessageUtil.send(player, "clan-base-tp");
            CooldownManager.getInstance().setTeleportCooldown(uuid, ConfigManager.getInstance().getBaseCooldown());
            return;
        }
        
        Location startLocation = player.getLocation().clone();
        
        TeleportTask task = new TeleportTask(player, destination, startLocation, delay, cancelOnMove);
        pendingTeleports.put(uuid, task);
        task.runTaskTimer(VKClans.getInstance(), 0L, 20L);
    }
    
    /**
     * Cancela teleporte pendente
     */
    public void cancelTeleport(UUID player) {
        TeleportTask task = pendingTeleports.remove(player);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Verifica se jogador tem teleporte pendente
     */
    public boolean hasPendingTeleport(UUID player) {
        return pendingTeleports.containsKey(player);
    }
    
    /**
     * Verifica se jogador se moveu (para cancelar teleporte)
     */
    public void checkMovement(Player player) {
        if (!ConfigManager.getInstance().isBaseCancelarAoMover()) return;
        
        UUID uuid = player.getUniqueId();
        TeleportTask task = pendingTeleports.get(uuid);
        
        if (task != null && task.hasMoved(player.getLocation())) {
            cancelTeleport(uuid);
            MessageUtil.send(player, "clan-base-cancelled");
        }
    }
    
    private class TeleportTask extends BukkitRunnable {
        private final Player player;
        private final Location destination;
        private final Location startLocation;
        private final boolean cancelOnMove;
        private int remainingSeconds;
        
        public TeleportTask(Player player, Location destination, Location startLocation, int delay, boolean cancelOnMove) {
            this.player = player;
            this.destination = destination;
            this.startLocation = startLocation;
            this.remainingSeconds = delay;
            this.cancelOnMove = cancelOnMove;
        }
        
        @Override
        public void run() {
            if (!player.isOnline()) {
                cancelTeleport(player.getUniqueId());
                return;
            }
            
            // Verifica se se moveu (se cancelOnMove estiver ativo)
            if (cancelOnMove && hasMoved(player.getLocation())) {
                cancelTeleport(player.getUniqueId());
                MessageUtil.send(player, "clan-base-cancelled");
                return;
            }
            
            if (remainingSeconds > 0) {
                Map<String, String> ph = new HashMap<>();
                ph.put("time", String.valueOf(remainingSeconds));
                MessageUtil.send(player, "clan-base-teleporting", ph);
                remainingSeconds--;
            } else {
                // Verifica se o destino ainda é válido
                if (destination == null || destination.getWorld() == null) {
                    MessageUtil.send(player, "clan-base-invalid");
                    pendingTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                
                // Teleporta
                player.teleport(destination);
                MessageUtil.send(player, "clan-base-tp");
                CooldownManager.getInstance().setTeleportCooldown(
                    player.getUniqueId(), 
                    ConfigManager.getInstance().getBaseCooldown()
                );
                pendingTeleports.remove(player.getUniqueId());
                cancel();
            }
        }
        
        public boolean hasMoved(Location current) {
            return startLocation.getBlockX() != current.getBlockX() ||
                   startLocation.getBlockY() != current.getBlockY() ||
                   startLocation.getBlockZ() != current.getBlockZ();
        }
    }
}
