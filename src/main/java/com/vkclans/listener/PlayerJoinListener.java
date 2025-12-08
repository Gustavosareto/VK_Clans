package com.vkclans.listener;

import com.vkclans.manager.ClanManager;
import com.vkclans.manager.IPLimitManager;
import com.vkclans.model.Clan;
import com.vkclans.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener para entrada de jogadores
 */
public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Registra IP do jogador
        IPLimitManager.getInstance().registerPlayer(player);
        
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan != null) {
            Map<String, String> ph = new HashMap<>();
            ph.put("player", player.getName());
            for (UUID uuid : clan.getMembers()) {
                Player p = player.getServer().getPlayer(uuid);
                if (p != null && !p.equals(player)) {
                    MessageUtil.send(p, "clan-member-joined", ph);
                }
            }
        }
    }
}
