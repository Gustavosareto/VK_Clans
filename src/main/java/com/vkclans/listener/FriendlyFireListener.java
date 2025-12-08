package com.vkclans.listener;

import com.vkclans.manager.*;
import com.vkclans.model.Clan;
import com.vkclans.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Listener para controlar Friendly Fire entre membros de clã e aliados
 */
public class FriendlyFireListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Verifica se são dois jogadores
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Mesmos jogadores
        if (victim.equals(attacker)) return;

        ClanManager cm = ClanManager.getInstance();
        Clan victimClan = cm.getPlayerClan(victim.getUniqueId());
        Clan attackerClan = cm.getPlayerClan(attacker.getUniqueId());

        // Se algum não tem clã, permite dano normal
        if (victimClan == null || attackerClan == null) return;

        ConfigManager config = ConfigManager.getInstance();

        // MESMO CLÃ
        if (victimClan.getName().equals(attackerClan.getName())) {
            // Verifica se estão em guerra (permite friendly fire em guerra se configurado)
            boolean inWar = WarManager.getInstance().getActiveWar(victimClan.getName()) != null;
            
            if (inWar && config.isFriendlyFireEmGuerra()) {
                // Permite dano durante guerra
                return;
            }

            // Verifica configuração normal
            if (!config.isFriendlyFirePermitido()) {
                event.setCancelled(true);
                MessageUtil.send(attacker, "friendly-fire-clan");
                return;
            }
        }

        // ALIADOS
        if (AllyManager.getInstance().areAllies(victimClan.getName(), attackerClan.getName())) {
            if (!config.isFriendlyFireAliados()) {
                event.setCancelled(true);
                MessageUtil.send(attacker, "friendly-fire-ally");
            }
        }
    }
}
