package com.vkclans.util;

import java.util.regex.Pattern;

/**
 * Utilitário para validações comuns do plugin
 */
public class ValidationUtil {
    
    private static Pattern namePattern;
    private static Pattern tagPattern;
    
    /**
     * Inicializa padrões de regex (chamado uma vez)
     */
    public static void initializePatterns(String nameRegex, String tagRegex) {
        namePattern = Pattern.compile(nameRegex);
        tagPattern = Pattern.compile(tagRegex);
    }
    
    /**
     * Valida nome de clã
     */
    public static boolean isValidClanName(String name, int minLength, int maxLength) {
        if (name == null || name.length() < minLength || name.length() > maxLength) {
            return false;
        }
        return namePattern != null ? namePattern.matcher(name).matches() : true;
    }
    
    /**
     * Valida tag de clã
     */
    public static boolean isValidClanTag(String tag, int minLength, int maxLength) {
        if (tag == null || tag.length() < minLength || tag.length() > maxLength) {
            return false;
        }
        return tagPattern != null ? tagPattern.matcher(tag).matches() : true;
    }
    
    /**
     * Verifica se string contém apenas caracteres permitidos (fallback)
     */
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9_]*$");
    }
}