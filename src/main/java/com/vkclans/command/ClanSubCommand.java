package com.vkclans.command;

/**
 * Enum para tipos de subcomandos do /clan
 */
public enum ClanSubCommand {
    CRIAR("criar", "create"),
    MENU("menu"),
    CONVIDAR("convidar", "invite"),
    ACEITAR("aceitar", "accept"),
    RECUSAR("recusar", "deny"),
    SAIR("sair", "leave"),
    INFO("info"),
    BASE("base", "home"),
    SETBASE("setbase", "sethome"),
    KICK("kick", "expulsar"),
    PROMOVER("promover", "promote"),
    REBAIXAR("rebaixar", "demote"),
    TRANSFERIR("transferir", "transfer"),
    DELETAR("deletar", "delete"),
    MEMBROS("membros", "members"),
    GUERRA("guerra", "war"),
    STATS("stats");
    
    private final String primary;
    private final String alias;
    
    ClanSubCommand(String primary) {
        this(primary, null);
    }
    
    ClanSubCommand(String primary, String alias) {
        this.primary = primary;
        this.alias = alias;
    }
    
    public String getPrimary() {
        return primary;
    }
    
    public String getAlias() {
        return alias;
    }
    
    public boolean matches(String input) {
        return primary.equalsIgnoreCase(input) || (alias != null && alias.equalsIgnoreCase(input));
    }
    
    public static ClanSubCommand fromString(String input) {
        for (ClanSubCommand cmd : values()) {
            if (cmd.matches(input)) {
                return cmd;
            }
        }
        return null;
    }
}