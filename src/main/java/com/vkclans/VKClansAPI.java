package com.vkclans;

import org.bukkit.entity.Player;

import com.vkclans.manager.ClanManager;
import com.vkclans.model.Clan;

/**
 * API pública para integração com outros plugins
 */
public class VKClansAPI {
    
    /**
     * Obtém o clã de um jogador
     */
    public static Clan getClan(Player player) {
        return ClanManager.getInstance().getPlayerClan(player.getUniqueId());
    }
    
    /**
     * Verifica se um jogador está em um clã
     */
    public static boolean hasClan(Player player) {
        return getClan(player) != null;
    }
    
    /**
     * Obtém o nome do clã de um jogador
     */
    public static String getClanName(Player player) {
        Clan clan = getClan(player);
        return clan != null ? clan.getName() : null;
    }
    
    /**
     * Obtém a tag do clã de um jogador
     */
    public static String getClanTag(Player player) {
        Clan clan = getClan(player);
        return clan != null ? clan.getTag() : null;
    }
}