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
 * Gerencia o sistema de conquistas/achievements do clã
 */
public class AchievementManager {
    private static AchievementManager instance;
    
    // Conquistas disponíveis
    public enum Achievement {
        FIRST_KILL("Primeira Sangue", "Conseguir a primeira kill do clã", 10, 100),
        KILLS_10("Assassinos", "Conseguir 10 kills", 25, 250),
        KILLS_50("Sanguinários", "Conseguir 50 kills", 50, 500),
        KILLS_100("Lendários", "Conseguir 100 kills", 100, 1000),
        KILLS_500("Imortais", "Conseguir 500 kills", 250, 2500),
        
        FIRST_WAR("Primeira Guerra", "Vencer a primeira guerra", 20, 200),
        WARS_5("Veteranos", "Vencer 5 guerras", 50, 500),
        WARS_10("Conquistadores", "Vencer 10 guerras", 100, 1000),
        WARS_25("Imperadores", "Vencer 25 guerras", 250, 2500),
        
        MEMBERS_10("Crescendo", "Ter 10 membros no clã", 15, 150),
        MEMBERS_25("Grande Família", "Ter 25 membros no clã", 30, 300),
        MEMBERS_50("Exército", "Ter 50 membros no clã", 50, 500),
        
        LEVEL_5("Evoluindo", "Alcançar nível 5", 50, 500),
        LEVEL_10("Máximo Poder", "Alcançar nível 10", 100, 1000),
        
        BANK_10K("Poupadores", "Ter 10.000 no banco", 25, 250),
        BANK_100K("Ricos", "Ter 100.000 no banco", 75, 750),
        BANK_1M("Milionários", "Ter 1.000.000 no banco", 200, 2000);
        
        private final String name;
        private final String description;
        private final int pointsReward;
        private final double moneyReward;
        
        Achievement(String name, String description, int pointsReward, double moneyReward) {
            this.name = name;
            this.description = description;
            this.pointsReward = pointsReward;
            this.moneyReward = moneyReward;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getPointsReward() { return pointsReward; }
        public double getMoneyReward() { return moneyReward; }
    }
    
    // Map de clã -> conquistas obtidas
    private final Map<String, Set<Achievement>> clanAchievements = new HashMap<>();
    
    private AchievementManager() {
        loadAchievements();
    }
    
    public static AchievementManager getInstance() {
        if (instance == null) instance = new AchievementManager();
        return instance;
    }
    
    /**
     * Verifica e concede conquistas baseado nas estatísticas do clã
     */
    public void checkAchievements(Clan clan) {
        Set<Achievement> obtained = clanAchievements.computeIfAbsent(clan.getName(), k -> new HashSet<>());
        
        // Verifica kills
        int kills = clan.getKills();
        checkAndGrant(clan, obtained, Achievement.FIRST_KILL, kills >= 1);
        checkAndGrant(clan, obtained, Achievement.KILLS_10, kills >= 10);
        checkAndGrant(clan, obtained, Achievement.KILLS_50, kills >= 50);
        checkAndGrant(clan, obtained, Achievement.KILLS_100, kills >= 100);
        checkAndGrant(clan, obtained, Achievement.KILLS_500, kills >= 500);
        
        // Verifica guerras
        int wars = clan.getWins();
        checkAndGrant(clan, obtained, Achievement.FIRST_WAR, wars >= 1);
        checkAndGrant(clan, obtained, Achievement.WARS_5, wars >= 5);
        checkAndGrant(clan, obtained, Achievement.WARS_10, wars >= 10);
        checkAndGrant(clan, obtained, Achievement.WARS_25, wars >= 25);
        
        // Verifica membros
        int members = clan.getMembers().size();
        checkAndGrant(clan, obtained, Achievement.MEMBERS_10, members >= 10);
        checkAndGrant(clan, obtained, Achievement.MEMBERS_25, members >= 25);
        checkAndGrant(clan, obtained, Achievement.MEMBERS_50, members >= 50);
        
        // Verifica nível
        int level = clan.getLevel();
        checkAndGrant(clan, obtained, Achievement.LEVEL_5, level >= 5);
        checkAndGrant(clan, obtained, Achievement.LEVEL_10, level >= 10);
        
        // Verifica banco
        double bank = BankManager.getInstance().getBalance(clan.getName());
        checkAndGrant(clan, obtained, Achievement.BANK_10K, bank >= 10000);
        checkAndGrant(clan, obtained, Achievement.BANK_100K, bank >= 100000);
        checkAndGrant(clan, obtained, Achievement.BANK_1M, bank >= 1000000);
    }
    
    private void checkAndGrant(Clan clan, Set<Achievement> obtained, Achievement achievement, boolean condition) {
        if (condition && !obtained.contains(achievement)) {
            grantAchievement(clan, achievement);
            obtained.add(achievement);
        }
    }
    
    private void grantAchievement(Clan clan, Achievement achievement) {
        // Adiciona recompensas
        clan.addPoints(achievement.getPointsReward());
        BankManager.getInstance().deposit(clan.getName(), achievement.getMoneyReward());
        
        // Notifica membros online
        Map<String, String> ph = new HashMap<>();
        ph.put("achievement", achievement.getName());
        ph.put("description", achievement.getDescription());
        ph.put("points", String.valueOf(achievement.getPointsReward()));
        ph.put("money", String.valueOf(achievement.getMoneyReward()));
        
        for (UUID uuid : clan.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                MessageUtil.send(player, "achievement-unlocked", ph);
            }
        }
        
        saveAchievements();
    }
    
    /**
     * Retorna conquistas obtidas pelo clã
     */
    public Set<Achievement> getAchievements(String clanName) {
        return clanAchievements.getOrDefault(clanName, new HashSet<>());
    }
    
    /**
     * Retorna todas as conquistas disponíveis
     */
    public Achievement[] getAllAchievements() {
        return Achievement.values();
    }
    
    /**
     * Remove conquistas de um clã (quando deletado)
     */
    public void removeAchievements(String clanName) {
        clanAchievements.remove(clanName);
        saveAchievements();
    }
    
    private void loadAchievements() {
        File file = new File(VKClans.getInstance().getDataFolder(), "achievements.yml");
        if (!file.exists()) return;
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (String clan : config.getKeys(false)) {
            Set<Achievement> achievements = new HashSet<>();
            for (String achName : config.getStringList(clan)) {
                try {
                    achievements.add(Achievement.valueOf(achName));
                } catch (IllegalArgumentException ignored) {}
            }
            clanAchievements.put(clan, achievements);
        }
    }
    
    private void saveAchievements() {
        File file = new File(VKClans.getInstance().getDataFolder(), "achievements.yml");
        YamlConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<String, Set<Achievement>> entry : clanAchievements.entrySet()) {
            List<String> names = new ArrayList<>();
            for (Achievement ach : entry.getValue()) {
                names.add(ach.name());
            }
            config.set(entry.getKey(), names);
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar conquistas: " + e.getMessage());
        }
    }
}
