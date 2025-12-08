package com.vkclans.manager;

import com.vkclans.model.Clan;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerencia ranking de clãs (Singleton)
 */
public class RankingManager {
    private static RankingManager instance;

    private RankingManager() {}

    public static RankingManager getInstance() {
        if (instance == null) instance = new RankingManager();
        return instance;
    }

    public enum RankingType {
        POINTS("Pontos"),
        KILLS("Kills"),
        WINS("Vitórias"),
        LEVEL("Nível"),
        MEMBERS("Membros"),
        KDR("KDR");

        private final String displayName;

        RankingType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * Obtém ranking de clãs por tipo
     */
    public List<Clan> getTopClans(RankingType type, int limit) {
        Collection<Clan> allClans = ClanManager.getInstance().getAllClans();
        
        Comparator<Clan> comparator;
        switch (type) {
            case KILLS:
                comparator = Comparator.comparingInt(Clan::getKills).reversed();
                break;
            case WINS:
                comparator = Comparator.comparingInt(Clan::getWins).reversed();
                break;
            case LEVEL:
                comparator = Comparator.comparingInt(Clan::getLevel).reversed();
                break;
            case MEMBERS:
                comparator = Comparator.comparingInt(c -> c.getMembers().size());
                comparator = comparator.reversed();
                break;
            case KDR:
                comparator = Comparator.comparingDouble(Clan::getKDR).reversed();
                break;
            case POINTS:
            default:
                comparator = Comparator.comparingInt(Clan::getPoints).reversed();
                break;
        }

        return allClans.stream()
            .sorted(comparator)
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Obtém a posição de um clã no ranking
     */
    public int getClanPosition(Clan clan, RankingType type) {
        List<Clan> ranking = getTopClans(type, Integer.MAX_VALUE);
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getName().equals(clan.getName())) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Formata ranking para exibição
     */
    public List<String> formatRanking(RankingType type, int limit) {
        List<String> lines = new ArrayList<>();
        List<Clan> top = getTopClans(type, limit);
        
        for (int i = 0; i < top.size(); i++) {
            Clan clan = top.get(i);
            String value;
            switch (type) {
                case KILLS: value = String.valueOf(clan.getKills()); break;
                case WINS: value = String.valueOf(clan.getWins()); break;
                case LEVEL: value = String.valueOf(clan.getLevel()); break;
                case MEMBERS: value = String.valueOf(clan.getMembers().size()); break;
                case KDR: value = String.format("%.2f", clan.getKDR()); break;
                case POINTS:
                default: value = String.valueOf(clan.getPoints()); break;
            }
            
            String medal = getMedal(i + 1);
            lines.add(String.format("%s §e#%d §f[%s] %s §7- §a%s", 
                medal, i + 1, clan.getTag(), clan.getName(), value));
        }
        
        return lines;
    }

    private String getMedal(int position) {
        switch (position) {
            case 1: return "§6🥇";
            case 2: return "§7🥈";
            case 3: return "§c🥉";
            default: return "§8•";
        }
    }
}
