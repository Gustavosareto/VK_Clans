package com.vkclans.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.vkclans.VKClans;

/**
 * Utilitário para mensagens com placeholders
 */
public class MessageUtil {
    private static final Map<String, String> messages = new HashMap<>();

    /**
     * Carrega mensagens do messages.yml
     */
    public static void loadMessages() {
        messages.clear();
        VKClans plugin = VKClans.getInstance();
        
        File file = new File(plugin.getDataFolder(), "messages.yml");
        
        // Se o arquivo não existir ou estiver vazio, cria um novo
        if (!file.exists() || file.length() == 0) {
            plugin.saveResource("messages.yml", true); // true = sobrescreve
            plugin.getLogger().info("[MessageUtil] Arquivo messages.yml criado/recriado!");
        }
        
        // Carrega o arquivo
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Se não conseguiu carregar do arquivo, tenta carregar do JAR diretamente
        if (config.getKeys(false).isEmpty()) {
            plugin.getLogger().warning("[MessageUtil] Arquivo vazio! Carregando do JAR...");
            InputStream stream = plugin.getResource("messages.yml");
            if (stream != null) {
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                config = YamlConfiguration.loadConfiguration(reader);
            }
        }
        
        // Carrega todas as mensagens
        int count = 0;
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            if (value != null && !value.isEmpty()) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', value));
                count++;
            }
        }
        
        plugin.getLogger().info("[MessageUtil] " + count + " mensagens carregadas!");
        
        if (!messages.containsKey("prefix")) {
            plugin.getLogger().severe("[MessageUtil] ERRO CRITICO: 'prefix' não encontrado! Delete a pasta VKClans e reinicie!");
        }
    }

    /**
     * Retorna mensagem formatada COM prefixo (para chat)
     */
    public static String get(String key, Map<String, String> placeholders) {
        String msg = messages.getOrDefault(key, "§cMensagem não encontrada: " + key);
        
        // Se a mensagem não foi encontrada, loga o erro
        if (!messages.containsKey(key)) {
            VKClans.getInstance().getLogger().warning("[MessageUtil] Chave não encontrada: " + key);
        }
        
        // Só adiciona prefixo se NÃO for título de GUI e se tiver prefixo
        if (!key.startsWith("gui-") && messages.containsKey("prefix")) {
            msg = messages.get("prefix") + msg;
        }
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return msg;
    }
    
    /**
     * Retorna mensagem formatada SEM prefixo (para GUIs e títulos)
     */
    public static String getRaw(String key, Map<String, String> placeholders) {
        String msg = messages.getOrDefault(key, "");
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return msg;
    }

    /**
     * Envia mensagem para jogador
     */
    public static void send(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(get(key, placeholders));
    }

    /**
     * Envia mensagem simples
     */
    public static void send(Player player, String key) {
        send(player, key, null);
    }

    /**
     * Envia mensagem sem prefixo
     */
    public static void sendRaw(Player player, String key) {
        String msg = messages.getOrDefault(key, "");
        player.sendMessage(msg);
    }
}
