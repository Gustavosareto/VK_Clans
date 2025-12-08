package com.vkclans.manager;

import java.util.ArrayList;
import java.util.List;

import com.vkclans.api.ClanEventObserver;

/**
 * Manager para notificações de eventos de clã (Observer Pattern)
 */
public class ClanEventManager {
    private static ClanEventManager instance;
    private final List<ClanEventObserver> observers = new ArrayList<>();
    
    private ClanEventManager() {}
    
    public static ClanEventManager getInstance() {
        if (instance == null) {
            instance = new ClanEventManager();
        }
        return instance;
    }
    
    /**
     * Adiciona um observer
     */
    public void addObserver(ClanEventObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Remove um observer
     */
    public void removeObserver(ClanEventObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica criação de clã
     */
    public void notifyClanCreated(String clanName, String tag) {
        for (ClanEventObserver observer : observers) {
            observer.onClanCreated(clanName, tag);
        }
    }
    
    /**
     * Notifica exclusão de clã
     */
    public void notifyClanDeleted(String clanName) {
        for (ClanEventObserver observer : observers) {
            observer.onClanDeleted(clanName);
        }
    }
    
    /**
     * Notifica entrada de jogador
     */
    public void notifyPlayerJoined(String clanName, String playerName) {
        for (ClanEventObserver observer : observers) {
            observer.onPlayerJoinedClan(clanName, playerName);
        }
    }
    
    /**
     * Notifica saída de jogador
     */
    public void notifyPlayerLeft(String clanName, String playerName) {
        for (ClanEventObserver observer : observers) {
            observer.onPlayerLeftClan(clanName, playerName);
        }
    }
}