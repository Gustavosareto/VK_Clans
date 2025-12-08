package com.vkclans.model;

import java.util.*;

/**
 * Representa uma guerra entre dois clãs
 */
public class ClanWar {
    private final String clan1;
    private final String clan2;
    private final long startTime;
    private final long endTime;
    private int clan1Kills;
    private int clan2Kills;
    private boolean active;
    private String winner;

    public ClanWar(String clan1, String clan2, long durationMinutes) {
        this.clan1 = clan1;
        this.clan2 = clan2;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + (durationMinutes * 60 * 1000);
        this.clan1Kills = 0;
        this.clan2Kills = 0;
        this.active = true;
        this.winner = null;
    }

    // Para deserialização
    public ClanWar(String clan1, String clan2, long startTime, long endTime, 
                   int clan1Kills, int clan2Kills, boolean active, String winner) {
        this.clan1 = clan1;
        this.clan2 = clan2;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clan1Kills = clan1Kills;
        this.clan2Kills = clan2Kills;
        this.active = active;
        this.winner = winner;
    }

    public String getClan1() { return clan1; }
    public String getClan2() { return clan2; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public int getClan1Kills() { return clan1Kills; }
    public int getClan2Kills() { return clan2Kills; }
    public boolean isActive() { return active; }
    public String getWinner() { return winner; }

    public void addKill(String clanName) {
        if (clanName.equals(clan1)) {
            clan1Kills++;
        } else if (clanName.equals(clan2)) {
            clan2Kills++;
        }
    }

    public int getKills(String clanName) {
        if (clanName.equals(clan1)) return clan1Kills;
        if (clanName.equals(clan2)) return clan2Kills;
        return 0;
    }

    public String getOpponent(String clanName) {
        if (clanName.equals(clan1)) return clan2;
        if (clanName.equals(clan2)) return clan1;
        return null;
    }

    public boolean involves(String clanName) {
        return clan1.equals(clanName) || clan2.equals(clanName);
    }

    public long getRemainingTime() {
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public String getRemainingTimeFormatted() {
        long remaining = getRemainingTime();
        long minutes = (remaining / 1000) / 60;
        long seconds = (remaining / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= endTime;
    }

    /**
     * Finaliza a guerra e determina o vencedor
     */
    public void end() {
        this.active = false;
        if (clan1Kills > clan2Kills) {
            this.winner = clan1;
        } else if (clan2Kills > clan1Kills) {
            this.winner = clan2;
        } else {
            this.winner = "EMPATE";
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("clan1", clan1);
        map.put("clan2", clan2);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("clan1Kills", clan1Kills);
        map.put("clan2Kills", clan2Kills);
        map.put("active", active);
        map.put("winner", winner);
        return map;
    }

    public static ClanWar deserialize(Map<String, Object> map) {
        return new ClanWar(
            (String) map.get("clan1"),
            (String) map.get("clan2"),
            ((Number) map.get("startTime")).longValue(),
            ((Number) map.get("endTime")).longValue(),
            ((Number) map.get("clan1Kills")).intValue(),
            ((Number) map.get("clan2Kills")).intValue(),
            (Boolean) map.get("active"),
            (String) map.get("winner")
        );
    }
}
