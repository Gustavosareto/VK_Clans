package com.vkclans.api;

import com.vkclans.manager.*;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanMission;
import com.vkclans.model.ClanRole;
import com.vkclans.model.ClanWar;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * API pública do VKClans para integração com outros plugins
 * 
 * Exemplo de uso:
 * <pre>
 * VKClansAPI api = VKClansAPI.getInstance();
 * Clan clan = api.getPlayerClan(player.getUniqueId());
 * if (clan != null) {
 *     api.addPoints(clan.getName(), 100);
 * }
 * </pre>
 */
public class VKClansAPI {
    private static VKClansAPI instance;

    private VKClansAPI() {}

    public static VKClansAPI getInstance() {
        if (instance == null) instance = new VKClansAPI();
        return instance;
    }

    // ==================== CLAN QUERIES ====================

    /**
     * Obtém o clã de um jogador
     * @param playerUUID UUID do jogador
     * @return Clan ou null se não tiver clã
     */
    public Clan getPlayerClan(UUID playerUUID) {
        return ClanManager.getInstance().getPlayerClan(playerUUID);
    }

    /**
     * Obtém clã por nome
     * @param name Nome do clã
     * @return Clan ou null
     */
    public Clan getClanByName(String name) {
        return ClanManager.getInstance().getClanByName(name);
    }

    /**
     * Obtém clã por tag
     * @param tag Tag do clã
     * @return Clan ou null
     */
    public Clan getClanByTag(String tag) {
        return ClanManager.getInstance().getClanByTag(tag);
    }

    /**
     * Obtém todos os clãs
     * @return Coleção de clãs
     */
    public Collection<Clan> getAllClans() {
        return ClanManager.getInstance().getAllClans();
    }

    /**
     * Verifica se jogador está em um clã
     */
    public boolean hasClan(UUID playerUUID) {
        return getPlayerClan(playerUUID) != null;
    }

    /**
     * Verifica se dois jogadores estão no mesmo clã
     */
    public boolean areInSameClan(UUID player1, UUID player2) {
        Clan clan1 = getPlayerClan(player1);
        Clan clan2 = getPlayerClan(player2);
        return clan1 != null && clan1.equals(clan2);
    }

    // ==================== CLAN INFO ====================

    /**
     * Obtém o cargo de um membro
     */
    public ClanRole getMemberRole(UUID playerUUID) {
        Clan clan = getPlayerClan(playerUUID);
        return clan != null ? clan.getMemberRole(playerUUID) : null;
    }

    /**
     * Verifica se jogador é líder do clã
     */
    public boolean isClanLeader(UUID playerUUID) {
        Clan clan = getPlayerClan(playerUUID);
        return clan != null && clan.getLeader().equals(playerUUID);
    }

    /**
     * Obtém nível do clã
     */
    public int getClanLevel(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getLevel() : 0;
    }

    /**
     * Obtém pontos do clã
     */
    public int getClanPoints(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getPoints() : 0;
    }

    // ==================== POINTS & ECONOMY ====================

    /**
     * Adiciona pontos ao clã
     */
    public void addPoints(String clanName, int amount) {
        Clan clan = getClanByName(clanName);
        if (clan != null) {
            clan.addPoints(amount);
            StorageManager.getInstance().saveClans();
        }
    }

    /**
     * Remove pontos do clã
     */
    public void removePoints(String clanName, int amount) {
        Clan clan = getClanByName(clanName);
        if (clan != null) {
            clan.removePoints(amount);
            StorageManager.getInstance().saveClans();
        }
    }

    /**
     * Obtém saldo do banco do clã
     */
    public double getClanBankBalance(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getBankBalance() : 0;
    }

    /**
     * Adiciona dinheiro ao banco do clã
     */
    public void addBankBalance(String clanName, double amount) {
        Clan clan = getClanByName(clanName);
        if (clan != null) {
            clan.addBankBalance(amount);
            StorageManager.getInstance().saveClans();
        }
    }

    /**
     * Remove dinheiro do banco do clã
     */
    public void removeBankBalance(String clanName, double amount) {
        Clan clan = getClanByName(clanName);
        if (clan != null) {
            clan.removeBankBalance(amount);
            StorageManager.getInstance().saveClans();
        }
    }

    // ==================== LEVEL & EXPERIENCE ====================

    /**
     * Adiciona experiência ao clã
     */
    public void addExperience(String clanName, int amount) {
        Clan clan = getClanByName(clanName);
        if (clan != null) {
            LevelManager.getInstance().addExperience(clan, amount);
        }
    }

    /**
     * Obtém limite de membros baseado no nível
     */
    public int getMemberLimit(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? LevelManager.getInstance().getMemberLimit(clan) : 0;
    }

    // ==================== WAR SYSTEM ====================

    /**
     * Verifica se dois clãs estão em guerra
     */
    public boolean areAtWar(String clan1, String clan2) {
        return WarManager.getInstance().areAtWar(clan1, clan2);
    }

    /**
     * Obtém guerra ativa de um clã
     */
    public ClanWar getActiveWar(String clanName) {
        return WarManager.getInstance().getActiveWar(clanName);
    }

    // ==================== RANKING ====================

    /**
     * Obtém top clãs por pontos
     */
    public List<Clan> getTopClansByPoints(int limit) {
        return RankingManager.getInstance().getTopClans(RankingManager.RankingType.POINTS, limit);
    }

    /**
     * Obtém top clãs por kills
     */
    public List<Clan> getTopClansByKills(int limit) {
        return RankingManager.getInstance().getTopClans(RankingManager.RankingType.KILLS, limit);
    }

    /**
     * Obtém top clãs por vitórias
     */
    public List<Clan> getTopClansByWins(int limit) {
        return RankingManager.getInstance().getTopClans(RankingManager.RankingType.WINS, limit);
    }

    /**
     * Obtém posição do clã no ranking
     */
    public int getClanRankingPosition(String clanName, RankingManager.RankingType type) {
        Clan clan = getClanByName(clanName);
        return clan != null ? RankingManager.getInstance().getClanPosition(clan, type) : -1;
    }

    // ==================== MISSIONS ====================

    /**
     * Obtém missões ativas de um clã
     */
    public List<ClanMission> getActiveMissions(String clanName) {
        return MissionManager.getInstance().getMissions(clanName);
    }

    /**
     * Adiciona progresso a missões de um tipo
     */
    public void addMissionProgress(String clanName, ClanMission.MissionType type, int amount) {
        MissionManager.getInstance().addProgress(clanName, type, amount);
    }

    // ==================== CHAT ====================

    /**
     * Verifica se jogador está com chat do clã ativado
     */
    public boolean isClanChatEnabled(UUID playerUUID) {
        return ChatManager.getInstance().isClanChatEnabled(playerUUID);
    }

    /**
     * Envia mensagem no chat do clã
     */
    public void sendClanMessage(Player player, String message) {
        ChatManager.getInstance().sendClanMessage(player, message);
    }

    // ==================== STATISTICS ====================

    /**
     * Obtém kills do clã
     */
    public int getClanKills(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getKills() : 0;
    }

    /**
     * Obtém mortes do clã
     */
    public int getClanDeaths(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getDeaths() : 0;
    }

    /**
     * Obtém KDR do clã
     */
    public double getClanKDR(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getKDR() : 0;
    }

    /**
     * Obtém vitórias do clã
     */
    public int getClanWins(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getWins() : 0;
    }

    /**
     * Obtém derrotas do clã
     */
    public int getClanLosses(String clanName) {
        Clan clan = getClanByName(clanName);
        return clan != null ? clan.getLosses() : 0;
    }

    // ==================== EVENTS HOOKS ====================

    /**
     * Registra kill para estatísticas e missões
     */
    public void registerKill(UUID killerUUID, UUID victimUUID) {
        Clan killerClan = getPlayerClan(killerUUID);
        Clan victimClan = getPlayerClan(victimUUID);

        if (killerClan != null) {
            killerClan.addKill();
            MissionManager.getInstance().addProgress(killerClan.getName(), 
                ClanMission.MissionType.KILL_PLAYERS, 1);
        }
        if (victimClan != null) {
            victimClan.addDeath();
        }

        // Registra kill de guerra se aplicável
        if (killerClan != null && victimClan != null && !killerClan.equals(victimClan)) {
            WarManager.getInstance().registerWarKill(killerClan.getName(), victimClan.getName());
        }

        StorageManager.getInstance().saveClans();
    }

    /**
     * Registra blocos minerados para missões
     */
    public void registerBlocksMined(UUID playerUUID, int amount) {
        Clan clan = getPlayerClan(playerUUID);
        if (clan != null) {
            MissionManager.getInstance().addProgress(clan.getName(), 
                ClanMission.MissionType.MINE_BLOCKS, amount);
        }
    }
}
