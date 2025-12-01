package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanLog;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Gerencia níveis dos clãs (Singleton)
 */
public class LevelManager {
    private static LevelManager instance;

    private LevelManager() {}

    public static LevelManager getInstance() {
        if (instance == null) instance = new LevelManager();
        return instance;
    }

    /**
     * Obtém o nível atual do clã
     */
    public int getLevel(Clan clan) {
        return clan.getLevel();
    }

    /**
     * Obtém a experiência atual do clã
     */
    public int getExperience(Clan clan) {
        return clan.getExperience();
    }

    /**
     * Obtém experiência necessária para próximo nível
     */
    public int getExpForNextLevel(int currentLevel) {
        ConfigManager config = ConfigManager.getInstance();
        int baseExp = config.getNivelExpBase();
        double multiplier = config.getNivelExpMultiplier();
        return (int) (baseExp * Math.pow(multiplier, currentLevel - 1));
    }

    /**
     * Adiciona experiência ao clã
     */
    public void addExperience(Clan clan, int amount) {
        int newExp = clan.getExperience() + amount;
        clan.setExperience(newExp);

        // Verifica level up
        while (newExp >= getExpForNextLevel(clan.getLevel())) {
            newExp -= getExpForNextLevel(clan.getLevel());
            clan.setExperience(newExp);
            levelUp(clan);
        }
    }

    /**
     * Sobe o nível do clã
     */
    private void levelUp(Clan clan) {
        int newLevel = clan.getLevel() + 1;
        ConfigManager config = ConfigManager.getInstance();

        // Verifica nível máximo
        if (newLevel > config.getNivelMaximo()) {
            return;
        }

        clan.setLevel(newLevel);

        // Notifica membros
        Map<String, String> ph = new HashMap<>();
        ph.put("level", String.valueOf(newLevel));
        ph.put("clan", clan.getName());

        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtil.send(p, "clan-level-up", ph);
            }
        }

        // Registra log
        LogManager.getInstance().log(clan.getName(), null, "Sistema",
            ClanLog.LogType.CLAN_LEVEL_UP, "Nível " + newLevel);

        StorageManager.getInstance().saveClans();
    }

    /**
     * Obtém o limite de membros baseado no nível
     */
    public int getMemberLimit(Clan clan) {
        ConfigManager config = ConfigManager.getInstance();
        int baseLimit = config.getLimiteMembros();
        int bonusPerLevel = config.getNivelBonusMembros();
        return baseLimit + ((clan.getLevel() - 1) * bonusPerLevel);
    }

    /**
     * Obtém o limite de homes baseado no nível
     */
    public int getHomesLimit(Clan clan) {
        ConfigManager config = ConfigManager.getInstance();
        int baseLimit = config.getNivelHomesBase();
        int bonusPerLevel = config.getNivelBonusHomes();
        return baseLimit + ((clan.getLevel() - 1) * bonusPerLevel);
    }

    /**
     * Obtém redução de cooldown baseada no nível (em segundos)
     */
    public int getCooldownReduction(Clan clan) {
        ConfigManager config = ConfigManager.getInstance();
        int reductionPerLevel = config.getNivelReducaoCooldown();
        return (clan.getLevel() - 1) * reductionPerLevel;
    }

    /**
     * Obtém custo para upgrade manual
     */
    public double getUpgradeCost(Clan clan) {
        ConfigManager config = ConfigManager.getInstance();
        double baseCost = config.getNivelCustoBase();
        double multiplier = config.getNivelCustoMultiplier();
        return baseCost * Math.pow(multiplier, clan.getLevel() - 1);
    }

    /**
     * Tenta fazer upgrade do clã com dinheiro
     */
    public boolean upgradeWithMoney(Player player, Clan clan) {
        BankManager bank = BankManager.getInstance();
        if (!bank.isEnabled()) {
            MessageUtil.send(player, "bank-disabled");
            return false;
        }

        ConfigManager config = ConfigManager.getInstance();
        if (clan.getLevel() >= config.getNivelMaximo()) {
            MessageUtil.send(player, "clan-max-level");
            return false;
        }

        double cost = getUpgradeCost(clan);
        if (clan.getBankBalance() < cost) {
            Map<String, String> ph = new HashMap<>();
            ph.put("cost", bank.formatMoney(cost));
            ph.put("balance", bank.formatMoney(clan.getBankBalance()));
            MessageUtil.send(player, "clan-upgrade-insufficient", ph);
            return false;
        }

        clan.removeBankBalance(cost);
        int newLevel = clan.getLevel() + 1;
        clan.setLevel(newLevel);

        Map<String, String> ph = new HashMap<>();
        ph.put("level", String.valueOf(newLevel));
        ph.put("cost", bank.formatMoney(cost));

        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtil.send(p, "clan-upgrade-success", ph);
            }
        }

        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.CLAN_LEVEL_UP, "Comprou nível " + newLevel);

        StorageManager.getInstance().saveClans();
        return true;
    }

    /**
     * Obtém porcentagem de progresso para próximo nível
     */
    public double getProgressPercentage(Clan clan) {
        int currentExp = clan.getExperience();
        int needed = getExpForNextLevel(clan.getLevel());
        return (double) currentExp / needed * 100;
    }

    /**
     * Formata barra de progresso
     */
    public String getProgressBar(Clan clan) {
        double percentage = getProgressPercentage(clan);
        int filled = (int) (percentage / 10);
        int empty = 10 - filled;

        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < filled; i++) bar.append("█");
        bar.append("§7");
        for (int i = 0; i < empty; i++) bar.append("█");

        return bar.toString();
    }
}
