package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gerencia o ranking semanal de kills
 */
public class WeeklyRankingManager {
    private static WeeklyRankingManager instance;
    
    // Map de clã -> kills semanais
    private final Map<String, Integer> weeklyKills = new HashMap<>();
    private long weekStartTime;
    
    private WeeklyRankingManager() {
        loadWeeklyData();
        scheduleWeeklyReset();
    }
    
    public static WeeklyRankingManager getInstance() {
        if (instance == null) instance = new WeeklyRankingManager();
        return instance;
    }
    
    /**
     * Adiciona kill semanal
     */
    public void addKill(String clanName) {
        weeklyKills.merge(clanName, 1, Integer::sum);
        saveWeeklyData();
    }
    
    /**
     * Retorna kills semanais de um clã
     */
    public int getWeeklyKills(String clanName) {
        return weeklyKills.getOrDefault(clanName, 0);
    }
    
    /**
     * Retorna top clãs da semana
     */
    public List<Map.Entry<String, Integer>> getTopWeekly(int limit) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(weeklyKills.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }
    
    /**
     * Reseta o ranking semanal e distribui recompensas
     */
    public void resetWeekly() {
        if (weeklyKills.isEmpty()) {
            weekStartTime = System.currentTimeMillis();
            return;
        }
        
        // Pega top 3
        List<Map.Entry<String, Integer>> top = getTopWeekly(3);
        
        // Recompensas configuráveis
        int[] pointsRewards = {500, 300, 150};
        double[] moneyRewards = {5000, 3000, 1500};
        
        // Distribui recompensas
        for (int i = 0; i < top.size(); i++) {
            String clanName = top.get(i).getKey();
            Clan clan = ClanManager.getInstance().getClanByName(clanName);
            
            if (clan != null) {
                int points = pointsRewards[i];
                double money = moneyRewards[i];
                
                clan.addPoints(points);
                BankManager.getInstance().deposit(clanName, money);
                
                // Notifica membros
                Map<String, String> ph = new HashMap<>();
                ph.put("position", String.valueOf(i + 1));
                ph.put("kills", String.valueOf(top.get(i).getValue()));
                ph.put("points", String.valueOf(points));
                ph.put("money", String.valueOf(money));
                
                for (UUID uuid : clan.getMembers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        MessageUtil.send(player, "weekly-reward", ph);
                    }
                }
            }
        }
        
        // Broadcast top 3
        Bukkit.broadcastMessage("§6§l[Ranking Semanal] §eTop 3 Clas da semana:");
        for (int i = 0; i < top.size(); i++) {
            Bukkit.broadcastMessage("§e" + (i + 1) + ". §f" + top.get(i).getKey() + " §7- §a" + top.get(i).getValue() + " kills");
        }
        
        // Limpa e reinicia
        weeklyKills.clear();
        weekStartTime = System.currentTimeMillis();
        saveWeeklyData();
        
        StorageManager.getInstance().saveClans();
    }
    
    /**
     * Retorna tempo restante até o reset (em milissegundos)
     */
    public long getTimeUntilReset() {
        long weekDuration = 7L * 24L * 60L * 60L * 1000L; // 7 dias
        long elapsed = System.currentTimeMillis() - weekStartTime;
        return Math.max(0, weekDuration - elapsed);
    }
    
    /**
     * Retorna tempo restante formatado
     */
    public String getFormattedTimeUntilReset() {
        long remaining = getTimeUntilReset();
        long days = remaining / (24 * 60 * 60 * 1000);
        long hours = (remaining % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        
        return days + "d " + hours + "h " + minutes + "m";
    }
    
    private void scheduleWeeklyReset() {
        // Verifica a cada hora se precisa resetar
        Bukkit.getScheduler().runTaskTimer(VKClans.getInstance(), () -> {
            if (getTimeUntilReset() <= 0) {
                resetWeekly();
            }
        }, 20L * 60L * 60L, 20L * 60L * 60L); // A cada hora
    }
    
    private void loadWeeklyData() {
        File file = new File(VKClans.getInstance().getDataFolder(), "weekly.yml");
        if (!file.exists()) {
            weekStartTime = System.currentTimeMillis();
            return;
        }
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        weekStartTime = config.getLong("start-time", System.currentTimeMillis());
        
        if (config.contains("kills")) {
            for (String clan : config.getConfigurationSection("kills").getKeys(false)) {
                weeklyKills.put(clan, config.getInt("kills." + clan));
            }
        }
    }
    
    private void saveWeeklyData() {
        File file = new File(VKClans.getInstance().getDataFolder(), "weekly.yml");
        YamlConfiguration config = new YamlConfiguration();
        
        config.set("start-time", weekStartTime);
        for (Map.Entry<String, Integer> entry : weeklyKills.entrySet()) {
            config.set("kills." + entry.getKey(), entry.getValue());
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar ranking semanal: " + e.getMessage());
        }
    }
}
