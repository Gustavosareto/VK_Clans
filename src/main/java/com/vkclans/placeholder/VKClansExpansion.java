package com.vkclans.placeholder;

import com.vkclans.VKClans;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * Expansão do PlaceholderAPI para VKClans
 */
public class VKClansExpansion extends PlaceholderExpansion {

    private final VKClans plugin;

    public VKClansExpansion(VKClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "vkclans";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        return VKClansPlaceholder.onRequest(player, identifier);
    }
}
