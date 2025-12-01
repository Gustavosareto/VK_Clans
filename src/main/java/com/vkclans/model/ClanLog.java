package com.vkclans.model;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Representa um log de ação do clã
 */
public class ClanLog {
    
    public enum LogType {
        MEMBER_JOIN("entrou no clã"),
        MEMBER_LEAVE("saiu do clã"),
        MEMBER_KICK("foi expulso por"),
        MEMBER_INVITE("convidou"),
        MEMBER_PROMOTE("promoveu"),
        MEMBER_DEMOTE("rebaixou"),
        LEADER_TRANSFER("transferiu liderança para"),
        BASE_SET("definiu a base"),
        BANK_DEPOSIT("depositou"),
        BANK_WITHDRAW("sacou"),
        CHEST_ADD("adicionou item ao baú"),
        CHEST_REMOVE("removeu item do baú"),
        WAR_DECLARE("declarou guerra contra"),
        WAR_END("finalizou guerra contra"),
        CLAN_CREATE("criou o clã"),
        CLAN_LEVEL_UP("subiu de nível"),
        MISSION_COMPLETE("completou missão");

        private final String description;

        LogType(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    private final long timestamp;
    private final UUID player;
    private final String playerName;
    private final LogType type;
    private final String details;

    public ClanLog(UUID player, String playerName, LogType type, String details) {
        this.timestamp = System.currentTimeMillis();
        this.player = player;
        this.playerName = playerName;
        this.type = type;
        this.details = details;
    }

    // Para deserialização
    public ClanLog(long timestamp, UUID player, String playerName, LogType type, String details) {
        this.timestamp = timestamp;
        this.player = player;
        this.playerName = playerName;
        this.type = type;
        this.details = details;
    }

    public long getTimestamp() { return timestamp; }
    public UUID getPlayer() { return player; }
    public String getPlayerName() { return playerName; }
    public LogType getType() { return type; }
    public String getDetails() { return details; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedMessage() {
        if (details != null && !details.isEmpty()) {
            return String.format("[%s] %s %s %s", getFormattedDate(), playerName, type.getDescription(), details);
        }
        return String.format("[%s] %s %s", getFormattedDate(), playerName, type.getDescription());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("player", player.toString());
        map.put("playerName", playerName);
        map.put("type", type.name());
        map.put("details", details);
        return map;
    }

    public static ClanLog deserialize(Map<String, Object> map) {
        return new ClanLog(
            ((Number) map.get("timestamp")).longValue(),
            UUID.fromString((String) map.get("player")),
            (String) map.get("playerName"),
            LogType.valueOf((String) map.get("type")),
            (String) map.get("details")
        );
    }
}
