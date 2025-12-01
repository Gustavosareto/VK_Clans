package com.vkclans.model;

import java.util.*;

/**
 * Representa uma missão do clã
 */
public class ClanMission {
    
    public enum MissionType {
        KILL_PLAYERS("Matar Jogadores", "kills"),
        MINE_BLOCKS("Minerar Blocos", "blocks"),
        WIN_WARS("Vencer Guerras", "wars"),
        DEPOSIT_MONEY("Depositar Dinheiro", "money");

        private final String displayName;
        private final String key;

        MissionType(String displayName, String key) {
            this.displayName = displayName;
            this.key = key;
        }

        public String getDisplayName() { return displayName; }
        public String getKey() { return key; }
    }

    private final String id;
    private final MissionType type;
    private final int targetAmount;
    private final int rewardPoints;
    private final double rewardMoney;
    private int currentProgress;
    private boolean completed;
    private long expirationTime;

    public ClanMission(String id, MissionType type, int targetAmount, int rewardPoints, double rewardMoney, long durationHours) {
        this.id = id;
        this.type = type;
        this.targetAmount = targetAmount;
        this.rewardPoints = rewardPoints;
        this.rewardMoney = rewardMoney;
        this.currentProgress = 0;
        this.completed = false;
        this.expirationTime = System.currentTimeMillis() + (durationHours * 60 * 60 * 1000);
    }

    // Para deserialização
    public ClanMission(String id, MissionType type, int targetAmount, int rewardPoints, 
                       double rewardMoney, int currentProgress, boolean completed, long expirationTime) {
        this.id = id;
        this.type = type;
        this.targetAmount = targetAmount;
        this.rewardPoints = rewardPoints;
        this.rewardMoney = rewardMoney;
        this.currentProgress = currentProgress;
        this.completed = completed;
        this.expirationTime = expirationTime;
    }

    public String getId() { return id; }
    public MissionType getType() { return type; }
    public int getTargetAmount() { return targetAmount; }
    public int getRewardPoints() { return rewardPoints; }
    public double getRewardMoney() { return rewardMoney; }
    public int getCurrentProgress() { return currentProgress; }
    public boolean isCompleted() { return completed; }
    public long getExpirationTime() { return expirationTime; }

    public void addProgress(int amount) {
        if (!completed) {
            this.currentProgress += amount;
            if (this.currentProgress >= targetAmount) {
                this.currentProgress = targetAmount;
                this.completed = true;
            }
        }
    }

    public double getProgressPercentage() {
        return (double) currentProgress / targetAmount * 100;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }

    public String getRemainingTimeFormatted() {
        long remaining = Math.max(0, expirationTime - System.currentTimeMillis());
        long hours = (remaining / 1000) / 3600;
        long minutes = ((remaining / 1000) % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type.name());
        map.put("targetAmount", targetAmount);
        map.put("rewardPoints", rewardPoints);
        map.put("rewardMoney", rewardMoney);
        map.put("currentProgress", currentProgress);
        map.put("completed", completed);
        map.put("expirationTime", expirationTime);
        return map;
    }

    public static ClanMission deserialize(Map<String, Object> map) {
        return new ClanMission(
            (String) map.get("id"),
            MissionType.valueOf((String) map.get("type")),
            ((Number) map.get("targetAmount")).intValue(),
            ((Number) map.get("rewardPoints")).intValue(),
            ((Number) map.get("rewardMoney")).doubleValue(),
            ((Number) map.get("currentProgress")).intValue(),
            (Boolean) map.get("completed"),
            ((Number) map.get("expirationTime")).longValue()
        );
    }
}
