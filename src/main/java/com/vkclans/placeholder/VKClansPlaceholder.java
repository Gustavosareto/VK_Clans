package com.vkclans.placeholder;

import com.vkclans.manager.*;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanRole;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Classe de placeholders para integração com PlaceholderAPI
 * 
 * Placeholders disponíveis:
 * %vkclans_clan% - Nome do clã
 * %vkclans_tag% - Tag colorida do clã (com cores)
 * %vkclans_tag_raw% - Tag sem cores
 * %vkclans_tag_colored% - Tag colorida do clã (alias)
 * %vkclans_role% - Cargo no clã
 * %vkclans_role_colored% - Cargo colorido
 * %vkclans_kills% - Kills do clã
 * %vkclans_deaths% - Deaths do clã
 * %vkclans_kdr% - KDR do clã
 * %vkclans_points% - Pontos do clã
 * %vkclans_level% - Nível do clã
 * %vkclans_members% - Quantidade de membros
 * %vkclans_max_members% - Máximo de membros
 * %vkclans_bank% - Saldo do banco
 * %vkclans_wars_won% - Guerras vencidas
 * %vkclans_wars_lost% - Guerras perdidas
 * %vkclans_weekly_kills% - Kills semanais
 * %vkclans_has_clan% - Se tem clã (true/false)
 * %vkclans_in_war% - Se está em guerra (true/false)
 * %vkclans_allies% - Quantidade de aliados
 */
public class VKClansPlaceholder {
    
    /**
     * Processa um placeholder
     */
    public static String onRequest(Player player, String identifier) {
        if (player == null) return "";
        
        UUID uuid = player.getUniqueId();
        Clan clan = ClanManager.getInstance().getPlayerClan(uuid);
        
        switch (identifier.toLowerCase()) {
            case "has_clan":
                return String.valueOf(clan != null);
                
            case "clan":
                return clan != null ? clan.getName() : "";
                
            case "tag":
                // Retorna a tag colorida por padrão
                return clan != null ? clan.getColoredTag() : "";
                
            case "tag_raw":
                // Retorna a tag sem cores
                return clan != null ? clan.getTag() : "";
                
            case "tag_colored":
                return clan != null ? clan.getColoredTag() : "";
                
            case "role":
                if (clan == null) return "";
                ClanRole role = clan.getMemberRole(uuid);
                return role != null ? role.getDisplayName() : "";
                
            case "role_colored":
                if (clan == null) return "";
                ClanRole roleC = clan.getMemberRole(uuid);
                return roleC != null ? roleC.getColoredName() : "";
                
            case "kills":
                return clan != null ? String.valueOf(clan.getKills()) : "0";
                
            case "deaths":
                return clan != null ? String.valueOf(clan.getDeaths()) : "0";
                
            case "kdr":
                if (clan == null) return "0.00";
                int deaths = clan.getDeaths();
                double kdr = deaths > 0 ? (double) clan.getKills() / deaths : clan.getKills();
                return String.format("%.2f", kdr);
                
            case "points":
                return clan != null ? String.valueOf(clan.getPoints()) : "0";
                
            case "level":
                return clan != null ? String.valueOf(clan.getLevel()) : "0";
                
            case "members":
                return clan != null ? String.valueOf(clan.getMembers().size()) : "0";
                
            case "max_members":
                if (clan == null) return "0";
                int base = ConfigManager.getInstance().getLimiteMembros();
                int bonus = clan.getLevel() * ConfigManager.getInstance().getNivelBonusMembros();
                return String.valueOf(base + bonus);
                
            case "bank":
                if (clan == null) return "0";
                return String.format("%.2f", BankManager.getInstance().getBalance(clan));
                
            case "wars_won":
                return clan != null ? String.valueOf(clan.getWins()) : "0";
                
            case "wars_lost":
                return clan != null ? String.valueOf(clan.getLosses()) : "0";
                
            case "weekly_kills":
                if (clan == null) return "0";
                return String.valueOf(WeeklyRankingManager.getInstance().getWeeklyKills(clan.getName()));
                
            case "in_war":
                if (clan == null) return "false";
                return String.valueOf(WarManager.getInstance().getActiveWar(clan.getName()) != null);
                
            case "allies":
                if (clan == null) return "0";
                return String.valueOf(AllyManager.getInstance().getAllies(clan.getName()).size());
                
            case "weekly_reset":
                return WeeklyRankingManager.getInstance().getFormattedTimeUntilReset();
                
            default:
                return null;
        }
    }
}
