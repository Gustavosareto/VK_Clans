package com.vkclans.manager;

import com.vkclans.model.Clan;
import java.util.*;
import java.util.UUID;

/**
 * Gerencia os clãs do servidor (Singleton)
 */
public class ClanManager {
    private static ClanManager instance;
    private final Map<String, Clan> clans = new HashMap<>(); // Nome -> Clan
    private final Map<String, Clan> tags = new HashMap<>(); // Tag -> Clan
    private final Map<UUID, Clan> memberIndex = new HashMap<>(); // UUID -> Clan (índice para busca rápida)

    private ClanManager() {}

    public static ClanManager getInstance() {
        if (instance == null) instance = new ClanManager();
        return instance;
    }

    /**
     * Busca o clã de um jogador pelo UUID (O(1) ao invés de O(n))
     */
    public Clan getPlayerClan(UUID playerUUID) {
        return memberIndex.get(playerUUID);
    }

    /**
     * Atualiza o índice de membros para um clã
     */
    public void updateMemberIndex(Clan clan) {
        // Remove entradas antigas deste clã
        memberIndex.entrySet().removeIf(entry -> entry.getValue().equals(clan));
        // Adiciona novas entradas
        for (UUID member : clan.getMembers()) {
            memberIndex.put(member, clan);
        }
    }

    /**
     * Remove jogador do índice de membros
     */
    public void removeMemberFromIndex(UUID playerUUID) {
        memberIndex.remove(playerUUID);
    }

    /**
     * Adiciona um novo clã
     */
    public boolean addClan(Clan clan) {
        if (clans.containsKey(clan.getName()) || tags.containsKey(clan.getTag())) return false;
        clans.put(clan.getName(), clan);
        tags.put(clan.getTag(), clan);
        updateMemberIndex(clan);
        return true;
    }

    /**
     * Remove um clã
     */
    public void removeClan(Clan clan) {
        // Remove membros do índice
        for (UUID member : clan.getMembers()) {
            memberIndex.remove(member);
        }
        clans.remove(clan.getName());
        tags.remove(clan.getTag());
    }

    /**
     * Busca clã por nome
     */
    public Clan getClanByName(String name) {
        return clans.get(name);
    }

    /**
     * Busca clã por tag
     */
    public Clan getClanByTag(String tag) {
        return tags.get(tag);
    }

    /**
     * Retorna todos os clãs
     */
    public Collection<Clan> getAllClans() {
        return clans.values();
    }
}
