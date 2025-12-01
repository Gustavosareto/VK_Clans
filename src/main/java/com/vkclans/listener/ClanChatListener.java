package com.vkclans.listener;

import com.vkclans.manager.ChatManager;
import com.vkclans.manager.ClanManager;
import com.vkclans.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener para o sistema de chat do clã
 */
public class ClanChatListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        // Verifica se está com chat do clã ativado
        if (ChatManager.getInstance().isClanChatEnabled(player.getUniqueId())) {
            Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
            if (clan != null) {
                event.setCancelled(true);
                // Envia mensagem para o clã de forma síncrona
                org.bukkit.Bukkit.getScheduler().runTask(
                    com.vkclans.VKClans.getInstance(),
                    () -> ChatManager.getInstance().sendClanMessage(player, event.getMessage())
                );
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ChatManager.getInstance().handlePlayerQuit(event.getPlayer().getUniqueId());
    }
}
