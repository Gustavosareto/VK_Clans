package com.vkclans.api;

/**
 * Interface para observers de eventos de clã
 */
public interface ClanEventObserver {
    void onClanCreated(String clanName, String tag);
    void onClanDeleted(String clanName);
    void onPlayerJoinedClan(String clanName, String playerName);
    void onPlayerLeftClan(String clanName, String playerName);
}