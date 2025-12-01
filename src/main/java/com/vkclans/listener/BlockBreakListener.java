package com.vkclans.listener;

import com.vkclans.api.VKClansAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Listener para rastrear blocos minerados (missões)
 */
public class BlockBreakListener implements Listener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        VKClansAPI.getInstance().registerBlocksMined(player.getUniqueId(), 1);
    }
}
