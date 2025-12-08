package com.vkclans.util;

import java.util.Map;

/**
 * Utilitário para aplicar placeholders em mensagens
 */
public class PlaceholderUtil {
    /**
     * Aplica placeholders no texto
     */
    public static String apply(String text, Map<String, String> placeholders) {
        if (placeholders == null) return text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }
}
