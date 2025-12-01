package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Gerencia chat privado dos clãs (Singleton)
 */
public class ChatManager {
    private static ChatManager instance;
    private final Set<UUID> clanChatEnabled = new HashSet<>(); // Jogadores com chat do clã ativado
    private final Set<UUID> spyEnabled = new HashSet<>(); // Staff com spy ativado

    private ChatManager() {}

    public static ChatManager getInstance() {
        if (instance == null) instance = new ChatManager();
        return instance;
    }

    /**
     * Verifica se jogador está com chat do clã ativado
     */
    public boolean isClanChatEnabled(UUID player) {
        return clanChatEnabled.contains(player);
    }

    /**
     * Ativa/desativa chat do clã
     */
    public boolean toggleClanChat(Player player) {
        if (clanChatEnabled.contains(player.getUniqueId())) {
            clanChatEnabled.remove(player.getUniqueId());
            return false;
        } else {
            clanChatEnabled.add(player.getUniqueId());
            return true;
        }
    }

    /**
     * Desativa chat do clã
     */
    public void disableClanChat(UUID player) {
        clanChatEnabled.remove(player);
    }

    /**
     * Verifica se staff tem spy ativado
     */
    public boolean isSpyEnabled(UUID player) {
        return spyEnabled.contains(player);
    }

    /**
     * Ativa/desativa spy
     */
    public boolean toggleSpy(Player player) {
        if (spyEnabled.contains(player.getUniqueId())) {
            spyEnabled.remove(player.getUniqueId());
            return false;
        } else {
            spyEnabled.add(player.getUniqueId());
            return true;
        }
    }

    /**
     * Envia mensagem no chat do clã
     */
    public void sendClanMessage(Player sender, String message) {
        Clan clan = ClanManager.getInstance().getPlayerClan(sender.getUniqueId());
        if (clan == null) {
            MessageUtil.send(sender, "no-clan");
            return;
        }

        String formattedMessage = formatClanMessage(clan, sender, message);

        // Envia para membros do clã
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage(formattedMessage);
            }
        }

        // Envia para spies
        String spyMessage = "§8[SPY] " + formattedMessage;
        for (UUID uuid : spyEnabled) {
            Player spy = Bukkit.getPlayer(uuid);
            if (spy != null && !clan.isMember(uuid)) {
                spy.sendMessage(spyMessage);
            }
        }

        // Log no console
        VKClans.getInstance().getLogger().info("[Clan Chat] " + clan.getName() + " | " + sender.getName() + ": " + message);
    }

    /**
     * Formata mensagem do chat do clã
     */
    private String formatClanMessage(Clan clan, Player sender, String message) {
        String format = ConfigManager.getInstance().getChatFormat();
        return format
            .replace("{tag}", clan.getTag())
            .replace("{clan}", clan.getName())
            .replace("{role}", clan.getMemberRole(sender.getUniqueId()).getDisplayName())
            .replace("{player}", sender.getName())
            .replace("{message}", message);
    }

    /**
     * Envia mensagem de sistema para todo o clã
     */
    public void sendSystemMessage(Clan clan, String messageKey, Map<String, String> placeholders) {
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtil.send(p, messageKey, placeholders);
            }
        }
    }

    /**
     * Remove jogador do chat do clã quando sai
     */
    public void handlePlayerQuit(UUID player) {
        clanChatEnabled.remove(player);
        spyEnabled.remove(player);
    }
}
