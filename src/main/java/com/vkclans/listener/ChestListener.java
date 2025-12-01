package com.vkclans.listener;

import com.vkclans.manager.ChestManager;
import com.vkclans.manager.ClanManager;
import com.vkclans.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener para o sistema de baú compartilhado
 */
public class ChestListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.startsWith("§6Baú do Clã ")) return;
        
        Player player = (Player) event.getWhoClicked();
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            event.setCancelled(true);
            return;
        }
        
        // Log de itens adicionados/removidos
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        
        if (cursor != null && cursor.getType() != org.bukkit.Material.AIR) {
            // Colocando item no baú
            ChestManager.getInstance().logItemAdd(player, clan, cursor);
        }
        if (current != null && current.getType() != org.bukkit.Material.AIR && event.isShiftClick()) {
            // Pegando item do baú com shift
            ChestManager.getInstance().logItemRemove(player, clan, current);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if (!title.startsWith("§6Baú do Clã ")) return;
        
        Player player = (Player) event.getPlayer();
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) return;
        
        // Salva conteúdo do baú
        ChestManager.getInstance().saveChestContents(clan.getName(), event.getInventory().getContents());
    }
}
