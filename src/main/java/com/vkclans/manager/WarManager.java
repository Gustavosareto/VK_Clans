package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanWar;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gerencia guerras entre clãs (Singleton)
 */
public class WarManager {
    private static WarManager instance;
    private final Map<String, ClanWar> activeWars = new HashMap<>(); // warId -> ClanWar
    private final Map<String, List<String>> warHistory = new HashMap<>(); // clanName -> lista de warIds
    private final File file;
    private YamlConfiguration config;

    private WarManager() {
        this.file = new File(VKClans.getInstance().getDataFolder(), "wars.yml");
        loadWars();
        startWarChecker();
    }

    public static WarManager getInstance() {
        if (instance == null) instance = new WarManager();
        return instance;
    }

    /**
     * Inicia task que verifica guerras expiradas
     */
    private void startWarChecker() {
        Bukkit.getScheduler().runTaskTimer(VKClans.getInstance(), () -> {
            List<String> toEnd = new ArrayList<>();
            for (Map.Entry<String, ClanWar> entry : activeWars.entrySet()) {
                ClanWar war = entry.getValue();
                if (war.isActive() && war.isExpired()) {
                    toEnd.add(entry.getKey());
                }
            }
            for (String warId : toEnd) {
                endWar(warId);
            }
        }, 20L * 10, 20L * 10); // A cada 10 segundos
    }

    /**
     * Declara guerra entre dois clãs
     */
    public boolean declareWar(Clan attacker, Clan defender) {
        // Verifica se já existe guerra ativa entre eles
        if (getActiveWar(attacker.getName()) != null || getActiveWar(defender.getName()) != null) {
            return false;
        }

        int duration = ConfigManager.getInstance().getGuerraDuracao();
        ClanWar war = new ClanWar(attacker.getName(), defender.getName(), duration);
        String warId = attacker.getName() + "_vs_" + defender.getName() + "_" + System.currentTimeMillis();
        
        activeWars.put(warId, war);
        
        // Adiciona ao histórico
        warHistory.computeIfAbsent(attacker.getName(), k -> new ArrayList<>()).add(warId);
        warHistory.computeIfAbsent(defender.getName(), k -> new ArrayList<>()).add(warId);

        // Notifica todos os membros
        Map<String, String> ph = new HashMap<>();
        ph.put("clan1", attacker.getName());
        ph.put("clan2", defender.getName());
        ph.put("duration", String.valueOf(duration));

        notifyClanMembers(attacker, "war-declared", ph);
        notifyClanMembers(defender, "war-declared-against", ph);

        // Registra log
        LogManager.getInstance().log(attacker.getName(), null, "Sistema", 
            com.vkclans.model.ClanLog.LogType.WAR_DECLARE, defender.getName());

        saveWars();
        return true;
    }

    /**
     * Registra kill durante guerra
     */
    public void registerWarKill(String killerClan, String victimClan) {
        for (ClanWar war : activeWars.values()) {
            if (war.isActive() && war.involves(killerClan) && war.involves(victimClan)) {
                war.addKill(killerClan);
                
                // Notifica ambos os clãs
                Map<String, String> ph = new HashMap<>();
                ph.put("killer_clan", killerClan);
                ph.put("victim_clan", victimClan);
                ph.put("kills1", String.valueOf(war.getClan1Kills()));
                ph.put("kills2", String.valueOf(war.getClan2Kills()));
                ph.put("remaining", war.getRemainingTimeFormatted());

                Clan clan1 = ClanManager.getInstance().getClanByName(war.getClan1());
                Clan clan2 = ClanManager.getInstance().getClanByName(war.getClan2());
                
                if (clan1 != null) notifyClanMembers(clan1, "war-kill", ph);
                if (clan2 != null) notifyClanMembers(clan2, "war-kill", ph);
                
                saveWars();
                break;
            }
        }
    }

    /**
     * Finaliza uma guerra
     */
    public void endWar(String warId) {
        ClanWar war = activeWars.get(warId);
        if (war == null || !war.isActive()) return;

        war.end();

        Clan clan1 = ClanManager.getInstance().getClanByName(war.getClan1());
        Clan clan2 = ClanManager.getInstance().getClanByName(war.getClan2());

        Map<String, String> ph = new HashMap<>();
        ph.put("clan1", war.getClan1());
        ph.put("clan2", war.getClan2());
        ph.put("kills1", String.valueOf(war.getClan1Kills()));
        ph.put("kills2", String.valueOf(war.getClan2Kills()));
        ph.put("winner", war.getWinner());

        ConfigManager config = ConfigManager.getInstance();
        
        // Distribui recompensas
        if (!war.getWinner().equals("EMPATE")) {
            Clan winner = ClanManager.getInstance().getClanByName(war.getWinner());
            Clan loser = war.getWinner().equals(war.getClan1()) ? clan2 : clan1;
            
            if (winner != null) {
                winner.addPoints(config.getGuerraRecompensaPontos());
                winner.addWin();
                winner.addLevel(config.getGuerraRecompensaExp());
                
                // Notifica vitória
                ph.put("points", String.valueOf(config.getGuerraRecompensaPontos()));
                notifyClanMembers(winner, "war-victory", ph);

                // Missão de vencer guerras
                MissionManager.getInstance().addProgress(winner.getName(), 
                    com.vkclans.model.ClanMission.MissionType.WIN_WARS, 1);
            }
            if (loser != null) {
                loser.addLoss();
                notifyClanMembers(loser, "war-defeat", ph);
            }
        } else {
            // Empate
            if (clan1 != null) notifyClanMembers(clan1, "war-draw", ph);
            if (clan2 != null) notifyClanMembers(clan2, "war-draw", ph);
        }

        // Registra log
        if (clan1 != null) {
            LogManager.getInstance().log(clan1.getName(), null, "Sistema", 
                com.vkclans.model.ClanLog.LogType.WAR_END, war.getClan2() + " - " + war.getWinner());
        }

        saveWars();
        StorageManager.getInstance().saveClans();
    }

    /**
     * Obtém guerra ativa de um clã
     */
    public ClanWar getActiveWar(String clanName) {
        for (ClanWar war : activeWars.values()) {
            if (war.isActive() && war.involves(clanName)) {
                return war;
            }
        }
        return null;
    }

    /**
     * Verifica se dois clãs estão em guerra
     */
    public boolean areAtWar(String clan1, String clan2) {
        for (ClanWar war : activeWars.values()) {
            if (war.isActive() && war.involves(clan1) && war.involves(clan2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtém histórico de guerras de um clã
     */
    public List<ClanWar> getWarHistory(String clanName) {
        List<ClanWar> history = new ArrayList<>();
        List<String> warIds = warHistory.get(clanName);
        if (warIds != null) {
            for (String id : warIds) {
                ClanWar war = activeWars.get(id);
                if (war != null && !war.isActive()) {
                    history.add(war);
                }
            }
        }
        return history;
    }

    private void notifyClanMembers(Clan clan, String messageKey, Map<String, String> placeholders) {
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtil.send(p, messageKey, placeholders);
            }
        }
    }

    public void loadWars() {
        if (!file.exists()) return;
        config = YamlConfiguration.loadConfiguration(file);
        
        if (config.contains("wars")) {
            for (String warId : config.getConfigurationSection("wars").getKeys(false)) {
                Map<String, Object> map = new HashMap<>();
                for (String key : config.getConfigurationSection("wars." + warId).getKeys(false)) {
                    map.put(key, config.get("wars." + warId + "." + key));
                }
                ClanWar war = ClanWar.deserialize(map);
                activeWars.put(warId, war);
                
                // Reconstrói histórico
                warHistory.computeIfAbsent(war.getClan1(), k -> new ArrayList<>()).add(warId);
                warHistory.computeIfAbsent(war.getClan2(), k -> new ArrayList<>()).add(warId);
            }
        }
    }

    public void saveWars() {
        config = new YamlConfiguration();
        for (Map.Entry<String, ClanWar> entry : activeWars.entrySet()) {
            for (Map.Entry<String, Object> data : entry.getValue().serialize().entrySet()) {
                config.set("wars." + entry.getKey() + "." + data.getKey(), data.getValue());
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar wars.yml: " + e.getMessage());
        }
    }
}
