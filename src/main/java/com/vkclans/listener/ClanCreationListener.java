package com.vkclans.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.vkclans.gui.ClanGUI;
import com.vkclans.manager.ClanManager;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanFactory;
import com.vkclans.util.ValidationUtil;

/**
 * Listener para criação interativa de clãs via GUI
 */
public class ClanCreationListener implements Listener {
    
    private static ClanCreationListener instance;
    
    private final Map<UUID, String> pendingClanName = new HashMap<>();
    private final Map<UUID, String> pendingClanTag = new HashMap<>();
    private final Map<UUID, String> pendingClanColor = new HashMap<>();
    private final Map<UUID, String> chatCaptureMode = new HashMap<>();
    private final Map<UUID, BukkitRunnable> timeoutTasks = new HashMap<>();
    
    public ClanCreationListener() {
        instance = this;
    }
    
    public static ClanCreationListener getInstance() {
        return instance;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!chatCaptureMode.containsKey(uuid)) {
            return;
        }
        
        event.setCancelled(true);
        String mode = chatCaptureMode.get(uuid);
        String input = event.getMessage().trim();
        
        // Cancelar se digitar "cancelar"
        if (input.equalsIgnoreCase("cancelar")) {
            clearPendingData(uuid);
            player.sendMessage("§cCriação de clã cancelada.");
            return;
        }
        
        boolean valid = false;
        String error = "";
        
        if ("name".equals(mode)) {
            if (ValidationUtil.isValidClanName(input, 3, 16)) {
                pendingClanName.put(uuid, input);
                valid = true;
            } else {
                error = "Nome inválido. Use 3-16 caracteres (letras, números, _).";
            }
        } else if ("tag".equals(mode)) {
            if (input.contains("&")) {
                error = "Tag não pode conter '&'. Digite novamente.";
            } else if (ValidationUtil.isValidClanTag(input, 2, 4)) {
                pendingClanTag.put(uuid, input);
                valid = true;
            } else {
                error = "Tag inválida. Use 2-4 caracteres (letras e números).";
            }
        }
        
        if (valid) {
            clearChatMode(uuid);
            ClanGUI.openClanCreationMenu(player);
            player.sendMessage("§aConfigurado com sucesso!");
        } else {
            player.sendMessage("§c" + error + " Digite novamente ou 'cancelar' para sair.");
        }
    }
    
    public void startNameCapture(Player player) {
        chatCaptureMode.put(player.getUniqueId(), "name");
        player.sendMessage("§eDigite no chat o nome do clã (3-16 caracteres, sem caracteres especiais). Ou 'cancelar' para sair.");
        scheduleTimeout(player.getUniqueId());
    }
    
    public void startTagCapture(Player player) {
        chatCaptureMode.put(player.getUniqueId(), "tag");
        player.sendMessage("§eDigite no chat a tag do clã (2-4 caracteres, sem &). Ou 'cancelar' para sair.");
        scheduleTimeout(player.getUniqueId());
    }
    
    public void setPendingColor(UUID uuid, String color) {
        pendingClanColor.put(uuid, color);
    }
    
    public boolean hasAllData(UUID uuid) {
        return pendingClanName.containsKey(uuid) && pendingClanTag.containsKey(uuid) && pendingClanColor.containsKey(uuid);
    }
    
    public void createClan(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            Clan clan = ClanFactory.createClan(pendingClanName.get(uuid), pendingClanColor.get(uuid) + pendingClanTag.get(uuid), player);
            ClanManager.getInstance().addClan(clan);
            clearPendingData(uuid);
            player.sendMessage("§aClã criado com sucesso!");
            ClanGUI.openMainMenu(player);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cErro: " + e.getMessage());
        }
    }
    
    public void clearPendingData(UUID uuid) {
        pendingClanName.remove(uuid);
        pendingClanTag.remove(uuid);
        pendingClanColor.remove(uuid);
        clearChatMode(uuid);
    }
    
    private void clearChatMode(UUID uuid) {
        chatCaptureMode.remove(uuid);
        if (timeoutTasks.containsKey(uuid)) {
            timeoutTasks.get(uuid).cancel();
            timeoutTasks.remove(uuid);
        }
    }
    
    private void scheduleTimeout(UUID uuid) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (chatCaptureMode.containsKey(uuid)) {
                    clearPendingData(uuid);
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendMessage("§cTempo esgotado. Criação de clã cancelada.");
                    }
                }
            }
        };
        task.runTaskLater(Bukkit.getPluginManager().getPlugin("VKClans"), 600L); // 30 segundos
        timeoutTasks.put(uuid, task);
    }
    
    // Getters para previews
    public String getPendingName(UUID uuid) {
        return pendingClanName.get(uuid);
    }
    
    public String getPendingTag(UUID uuid) {
        return pendingClanTag.get(uuid);
    }
    
    public String getPendingColor(UUID uuid) {
        return pendingClanColor.get(uuid);
    }
}