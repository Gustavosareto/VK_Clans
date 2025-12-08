package com.vkclans.model;

import org.bukkit.entity.Player;

import com.vkclans.manager.ClanManager;
import com.vkclans.manager.ConfigManager;
import com.vkclans.util.ValidationUtil;

/**
 * Factory para criação de clãs com validações
 */
public class ClanFactory {
    
    /**
     * Cria um novo clã com validações
     */
    public static Clan createClan(String name, String tag, Player creator) throws IllegalArgumentException {
        ConfigManager config = ConfigManager.getInstance();
        
        // Validações básicas
        if (!ValidationUtil.isValidClanName(name, config.getNomeMin(), config.getNomeMax())) {
            throw new IllegalArgumentException("Nome inválido");
        }
        
        String cleanTag = tag.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "").toUpperCase();
        if (!ValidationUtil.isValidClanTag(cleanTag, config.getTagMin(), config.getTagMax())) {
            throw new IllegalArgumentException("Tag inválida");
        }
        
        // Verifica se já existe
        ClanManager manager = ClanManager.getInstance();
        if (manager.getClanByName(name) != null) {
            throw new IllegalArgumentException("Nome já existe");
        }
        if (manager.getClanByTag(cleanTag) != null) {
            throw new IllegalArgumentException("Tag já existe");
        }
        
        // Cria o clã
        Clan clan = new Clan(name, tag, creator.getUniqueId());
        
        return clan;
    }
}