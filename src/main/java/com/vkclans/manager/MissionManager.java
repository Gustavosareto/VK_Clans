package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanLog;
import com.vkclans.model.ClanMission;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gerencia missões dos clãs (Singleton)
 */
public class MissionManager {
    private static MissionManager instance;
    private final Map<String, List<ClanMission>> clanMissions = new HashMap<>(); // clanName -> missions
    private final File file;
    private YamlConfiguration config;
    private static final int MAX_ACTIVE_MISSIONS = 3;

    private MissionManager() {
        this.file = new File(VKClans.getInstance().getDataFolder(), "missions.yml");
        loadMissions();
        startMissionChecker();
    }

    public static MissionManager getInstance() {
        if (instance == null) instance = new MissionManager();
        return instance;
    }

    /**
     * Inicia task que verifica missões expiradas e gera novas
     */
    private void startMissionChecker() {
        // A cada 5 minutos
        Bukkit.getScheduler().runTaskTimer(VKClans.getInstance(), () -> {
            for (Clan clan : ClanManager.getInstance().getAllClans()) {
                List<ClanMission> missions = getMissions(clan.getName());
                
                // Remove missões expiradas
                missions.removeIf(ClanMission::isExpired);
                
                // Gera novas missões se necessário
                while (missions.size() < MAX_ACTIVE_MISSIONS) {
                    ClanMission newMission = generateRandomMission();
                    missions.add(newMission);
                }
            }
            saveMissions();
        }, 20L * 60 * 5, 20L * 60 * 5);
    }

    /**
     * Gera uma missão aleatória
     */
    public ClanMission generateRandomMission() {
        ConfigManager config = ConfigManager.getInstance();
        ClanMission.MissionType[] types = ClanMission.MissionType.values();
        ClanMission.MissionType type = types[ThreadLocalRandom.current().nextInt(types.length)];

        int targetAmount;
        int rewardPoints;
        double rewardMoney;
        long durationHours = config.getMissaoDuracao();

        switch (type) {
            case KILL_PLAYERS:
                targetAmount = ThreadLocalRandom.current().nextInt(5, 21);
                rewardPoints = targetAmount * 10;
                rewardMoney = targetAmount * 100;
                break;
            case MINE_BLOCKS:
                targetAmount = ThreadLocalRandom.current().nextInt(100, 501);
                rewardPoints = targetAmount / 10;
                rewardMoney = targetAmount * 5;
                break;
            case WIN_WARS:
                targetAmount = ThreadLocalRandom.current().nextInt(1, 4);
                rewardPoints = targetAmount * 50;
                rewardMoney = targetAmount * 500;
                break;
            case DEPOSIT_MONEY:
                targetAmount = ThreadLocalRandom.current().nextInt(1000, 10001);
                rewardPoints = targetAmount / 100;
                rewardMoney = targetAmount * 0.1;
                break;
            default:
                targetAmount = 10;
                rewardPoints = 50;
                rewardMoney = 500;
        }

        String id = type.getKey() + "_" + System.currentTimeMillis();
        return new ClanMission(id, type, targetAmount, rewardPoints, rewardMoney, durationHours);
    }

    /**
     * Obtém missões ativas de um clã
     */
    public List<ClanMission> getMissions(String clanName) {
        return clanMissions.computeIfAbsent(clanName, k -> {
            List<ClanMission> missions = new ArrayList<>();
            for (int i = 0; i < MAX_ACTIVE_MISSIONS; i++) {
                missions.add(generateRandomMission());
            }
            return missions;
        });
    }

    /**
     * Adiciona progresso a missões de um tipo
     */
    public void addProgress(String clanName, ClanMission.MissionType type, int amount) {
        List<ClanMission> missions = getMissions(clanName);
        
        for (ClanMission mission : missions) {
            if (mission.getType() == type && !mission.isCompleted() && !mission.isExpired()) {
                boolean wasComplete = mission.isCompleted();
                mission.addProgress(amount);
                
                // Completou a missão agora
                if (!wasComplete && mission.isCompleted()) {
                    completeMission(clanName, mission);
                }
            }
        }
        
        saveMissions();
    }

    /**
     * Completa uma missão e distribui recompensas
     */
    private void completeMission(String clanName, ClanMission mission) {
        Clan clan = ClanManager.getInstance().getClanByName(clanName);
        if (clan == null) return;

        // Adiciona recompensas
        clan.addPoints(mission.getRewardPoints());
        clan.addBankBalance(mission.getRewardMoney());

        // Notifica membros
        Map<String, String> ph = new HashMap<>();
        ph.put("mission", mission.getType().getDisplayName());
        ph.put("points", String.valueOf(mission.getRewardPoints()));
        ph.put("money", BankManager.getInstance().formatMoney(mission.getRewardMoney()));

        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtil.send(p, "mission-completed", ph);
            }
        }

        // Registra log
        LogManager.getInstance().log(clanName, null, "Sistema",
            ClanLog.LogType.MISSION_COMPLETE, mission.getType().getDisplayName());

        StorageManager.getInstance().saveClans();
    }

    /**
     * Formata missões para exibição
     */
    public List<String> formatMissions(String clanName) {
        List<String> lines = new ArrayList<>();
        List<ClanMission> missions = getMissions(clanName);

        for (int i = 0; i < missions.size(); i++) {
            ClanMission mission = missions.get(i);
            String status = mission.isCompleted() ? "§a✓" : mission.isExpired() ? "§c✗" : "§e⏳";
            String progress = String.format("§7[§a%d§7/§e%d§7]", 
                mission.getCurrentProgress(), mission.getTargetAmount());
            
            lines.add(String.format("%s §f%s %s §7- §6%dp §7+ §a%s §7(Expira: %s)",
                status,
                mission.getType().getDisplayName(),
                progress,
                mission.getRewardPoints(),
                BankManager.getInstance().formatMoney(mission.getRewardMoney()),
                mission.getRemainingTimeFormatted()
            ));
        }

        return lines;
    }

    @SuppressWarnings("unchecked")
    public void loadMissions() {
        if (!file.exists()) return;
        config = YamlConfiguration.loadConfiguration(file);

        for (String clanName : config.getKeys(false)) {
            List<ClanMission> missions = new ArrayList<>();
            List<Map<?, ?>> missionList = config.getMapList(clanName);

            for (Map<?, ?> map : missionList) {
                Map<String, Object> typedMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    typedMap.put(entry.getKey().toString(), entry.getValue());
                }
                try {
                    ClanMission mission = ClanMission.deserialize(typedMap);
                    if (!mission.isExpired()) {
                        missions.add(mission);
                    }
                } catch (Exception e) {
                    // Ignora missões inválidas
                }
            }

            clanMissions.put(clanName, missions);
        }
    }

    public void saveMissions() {
        config = new YamlConfiguration();
        for (Map.Entry<String, List<ClanMission>> entry : clanMissions.entrySet()) {
            List<Map<String, Object>> missionList = new ArrayList<>();
            for (ClanMission mission : entry.getValue()) {
                missionList.add(mission.serialize());
            }
            config.set(entry.getKey(), missionList);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar missions.yml: " + e.getMessage());
        }
    }
}
