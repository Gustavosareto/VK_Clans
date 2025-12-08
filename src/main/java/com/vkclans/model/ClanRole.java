package com.vkclans.model;

/**
 * Representa os cargos disponíveis em um clã
 */
public enum ClanRole {
    DONO("Dono", 4, "§4"),
    SUB_DONO("Sub-Dono", 3, "§c"),
    ADMINISTRADOR("Administrador", 2, "§6"),
    MEMBRO("Membro", 1, "§7");

    private final String displayName;
    private final int level;
    private final String color;

    ClanRole(String displayName, int level, String color) {
        this.displayName = displayName;
        this.level = level;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getColor() {
        return color;
    }

    public String getColoredName() {
        return color + displayName;
    }

    /**
     * Verifica se este cargo pode executar ação sobre outro cargo
     */
    public boolean canManage(ClanRole other) {
        return this.level > other.level;
    }

    /**
     * Verifica se pode convidar jogadores
     */
    public boolean canInvite() {
        return this.level >= ADMINISTRADOR.level;
    }

    /**
     * Verifica se pode expulsar jogadores
     */
    public boolean canKick() {
        return this.level >= ADMINISTRADOR.level;
    }

    /**
     * Verifica se pode setar a base
     */
    public boolean canSetBase() {
        return this.level >= SUB_DONO.level;
    }

    /**
     * Verifica se pode promover/rebaixar jogadores
     */
    public boolean canPromote() {
        return this.level >= SUB_DONO.level;
    }

    /**
     * Verifica se pode deletar o clan
     */
    public boolean canDelete() {
        return this == DONO;
    }

    /**
     * Verifica se pode gerenciar membros em geral (promover, rebaixar, gerenciar banco, etc)
     */
    public boolean canManageMembers() {
        return this.level >= SUB_DONO.level;
    }

    /**
     * Retorna o próximo cargo (promoção)
     */
    public ClanRole getNextRole() {
        switch (this) {
            case MEMBRO: return ADMINISTRADOR;
            case ADMINISTRADOR: return SUB_DONO;
            case SUB_DONO: return DONO;
            default: return null;
        }
    }

    /**
     * Retorna o cargo anterior (rebaixamento)
     */
    public ClanRole getPreviousRole() {
        switch (this) {
            case DONO: return SUB_DONO;
            case SUB_DONO: return ADMINISTRADOR;
            case ADMINISTRADOR: return MEMBRO;
            default: return null;
        }
    }
}
