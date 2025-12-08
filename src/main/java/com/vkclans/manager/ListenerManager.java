package com.vkclans.manager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Manager para registrar e cancelar listeners dinamicamente
 */
public class ListenerManager {
    private static ListenerManager instance;
    private final Plugin plugin;
    
    private ListenerManager(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public static ListenerManager getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new ListenerManager(plugin);
        }
        return instance;
    }
    
    /**
     * Registra um listener
     */
    public void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
    
    /**
     * Cancela um listener (não diretamente suportado pelo Bukkit, mas pode desregistrar handlers)
     * Nota: Bukkit não permite cancelar listeners individuais facilmente; use para controle lógico.
     */
    public void unregisterListener(Listener listener) {
        // Implementação limitada; em prática, listeners são permanentes até reload
        // Pode usar para flags de ativação/desativação
    }
}