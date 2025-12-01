package com.vkclans.listener;

import com.vkclans.manager.*;
import com.vkclans.model.Clan;
import com.vkclans.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener para mortes de jogadores e sistema de combate
 */
public class PlayerDeathListener implements Listener {
    
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        
        // Marca ambos em combate
        CooldownManager.getInstance().tagCombat(victim.getUniqueId());
        CooldownManager.getInstance().tagCombat(attacker.getUniqueId());
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        Clan victimClan = ClanManager.getInstance().getPlayerClan(victim.getUniqueId());
        Clan killerClan = killer != null ? ClanManager.getInstance().getPlayerClan(killer.getUniqueId()) : null;
        
        boolean needsSave = false;
        ConfigManager config = ConfigManager.getInstance();
        
        // Processa morte da vítima
        if (victimClan != null) {
            victimClan.addDeath();
            int pointsLost = config.getPontosPorMorte();
            victimClan.removePoints(pointsLost);
            needsSave = true;
            
            Map<String, String> ph = new HashMap<>();
            ph.put("player", victim.getName());
            ph.put("points", String.valueOf(pointsLost));
            MessageUtil.send(victim, "clan-death", ph);
        }
        
        // Processa kill do assassino
        if (killerClan != null && killer != null) {
            killerClan.addKill();
            int pointsGained = config.getPontosPorKill();
            
            // Bônus por matar membro de outro clan
            if (victimClan != null && !victimClan.equals(killerClan)) {
                pointsGained += config.getBonusClanRival();
            }
            
            killerClan.addPoints(pointsGained);
            needsSave = true;
            
            // Adiciona kill ao ranking semanal
            if (config.isRankingSemanalAtivo()) {
                WeeklyRankingManager.getInstance().addKill(killerClan.getName());
            }
            
            // Verifica conquistas
            if (config.isConquistasAtivo()) {
                AchievementManager.getInstance().checkAchievements(killerClan);
            }
            
            Map<String, String> ph = new HashMap<>();
            ph.put("killer", killer.getName());
            ph.put("victim", victim.getName());
            ph.put("clan", killerClan.getName());
            ph.put("points", String.valueOf(pointsGained));
            MessageUtil.send(killer, "clan-kill", ph);
        }
        
        // Remove tag de combate da vítima
        CooldownManager.getInstance().removeCombatTag(victim.getUniqueId());
        
        // Salva apenas uma vez
        if (needsSave) {
            StorageManager.getInstance().saveClans();
        }
    }
}
