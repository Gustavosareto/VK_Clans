package com.vkclans.manager;

import com.vkclans.VKClans;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia todas as configurações do plugin
 */
public class ConfigManager {
    private static ConfigManager instance;
    private final VKClans plugin;
    private FileConfiguration config;

    // ==================== CONFIGURAÇÕES GERAIS ====================
    private int nomeMin;
    private int nomeMax;
    private int tagMin;
    private int tagMax;
    private boolean permitirCoresNome;
    private int limiteMembros;

    // ==================== BLACKLIST ====================
    private List<String> blacklistNomes;
    private List<String> blacklistTags;

    // ==================== CONVITES ====================
    private int conviteExpiracao;
    private int maxConvitesPendentes;

    // ==================== BASE ====================
    private int baseCooldown;
    private int baseTempoEspera;
    private boolean baseCancelarAoMover;
    private boolean baseBloquearEmCombate;
    private int tempoCombate;
    private List<String> baseMundosPermitidos;
    private List<String> baseMundosBloqueados;

    // ==================== PONTOS ====================
    private int pontosPorKill;
    private int pontosPorMorte;
    private int bonusClanRival;

    // ==================== MENU ====================
    private String somAbrir;
    private String somClicar;
    private int membrosPorPagina;

    // ==================== GUERRA ====================
    private int guerraDuracao;
    private int guerraCooldown;
    private int guerraRecompensaPontos;
    private int guerraRecompensaExp;
    private double guerraRecompensaDinheiro;

    // ==================== NÍVEL ====================
    private int nivelMaximo;
    private int nivelExpBase;
    private double nivelExpMultiplier;
    private int nivelBonusMembros;
    private int nivelHomesBase;
    private int nivelBonusHomes;
    private int nivelReducaoCooldown;
    private double nivelCustoBase;
    private double nivelCustoMultiplier;

    // ==================== BANCO ====================
    private double bancoDepositoMinimo;
    private double bancoSaqueMinimo;
    private double bancoTaxaSaque;

    // ==================== MISSÕES ====================
    private int missaoDuracao;
    private int missaoMaxAtivas;

    // ==================== CHAT ====================
    private String chatFormat;
    private boolean chatPrefixEnabled;

    // ==================== ALIANÇAS ====================
    private boolean aliancasAtivado;
    private int maxAliados;
    private int tempoConviteAlianca;
    private boolean friendlyFireAliados;

    // ==================== IP LIMIT ====================
    private boolean limiteIPAtivo;
    private int maxClasPerIP;

    // ==================== RANKING SEMANAL ====================
    private boolean rankingSemanalAtivo;

    // ==================== CONQUISTAS ====================
    private boolean conquistasAtivo;

    // ==================== FRIENDLY FIRE ====================
    private boolean friendlyFirePermitido;
    private boolean friendlyFireEmGuerra;

    private ConfigManager() {
        this.plugin = VKClans.getInstance();
    }

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    /**
     * Recarrega as configurações
     */
    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadValues();
    }

    private void loadValues() {
        // Gerais
        nomeMin = config.getInt("geral.nome-min", 3);
        nomeMax = config.getInt("geral.nome-max", 16);
        tagMin = config.getInt("geral.tag-min", 2);
        tagMax = config.getInt("geral.tag-max", 4);
        permitirCoresNome = config.getBoolean("geral.permitir-cores-nome", false);
        limiteMembros = config.getInt("geral.max-membros", 50);

        // Blacklist
        blacklistNomes = config.getStringList("blacklist.nomes");
        blacklistTags = config.getStringList("blacklist.tags");

        // Convites
        conviteExpiracao = config.getInt("convites.expiracao", 60);
        maxConvitesPendentes = config.getInt("convites.max-pendentes", 5);

        // Base
        baseCooldown = config.getInt("base.cooldown", 30);
        baseTempoEspera = config.getInt("base.tempo-espera", 3);
        baseCancelarAoMover = config.getBoolean("base.cancelar-ao-mover", true);
        baseBloquearEmCombate = config.getBoolean("base.bloquear-em-combate", true);
        tempoCombate = config.getInt("base.tempo-combate", 10);
        baseMundosPermitidos = config.getStringList("base.mundos-permitidos");
        baseMundosBloqueados = config.getStringList("base.mundos-bloqueados");

        // Pontos
        pontosPorKill = config.getInt("pontos.pontos-por-kill", 10);
        pontosPorMorte = config.getInt("pontos.pontos-por-morte", 5);
        bonusClanRival = config.getInt("pontos.bonus-clan-rival", 5);

        // Menu
        somAbrir = config.getString("menu.som-abrir", "CLICK");
        somClicar = config.getString("menu.som-clicar", "CLICK");
        membrosPorPagina = config.getInt("menu.membros-por-pagina", 21);

        // Guerra
        guerraDuracao = config.getInt("guerra.duracao", 30);
        guerraCooldown = config.getInt("guerra.cooldown", 60);
        guerraRecompensaPontos = config.getInt("guerra.recompensa-pontos", 100);
        guerraRecompensaExp = config.getInt("guerra.recompensa-exp", 50);
        guerraRecompensaDinheiro = config.getDouble("guerra.recompensa-dinheiro", 1000);

        // Nível
        nivelMaximo = config.getInt("nivel.maximo", 10);
        nivelExpBase = config.getInt("nivel.exp-base", 100);
        nivelExpMultiplier = config.getDouble("nivel.exp-multiplier", 1.5);
        nivelBonusMembros = config.getInt("nivel.bonus-membros-por-nivel", 5);
        nivelHomesBase = config.getInt("nivel.homes-base", 1);
        nivelBonusHomes = config.getInt("nivel.bonus-homes-por-nivel", 1);
        nivelReducaoCooldown = config.getInt("nivel.reducao-cooldown-por-nivel", 2);
        nivelCustoBase = config.getDouble("nivel.custo-upgrade-base", 5000);
        nivelCustoMultiplier = config.getDouble("nivel.custo-upgrade-multiplier", 2.0);

        // Banco
        bancoDepositoMinimo = config.getDouble("banco.deposito-minimo", 100);
        bancoSaqueMinimo = config.getDouble("banco.saque-minimo", 100);
        bancoTaxaSaque = config.getDouble("banco.taxa-saque", 0);

        // Missões
        missaoDuracao = config.getInt("missoes.duracao-horas", 24);
        missaoMaxAtivas = config.getInt("missoes.max-ativas", 3);

        // Chat
        chatFormat = config.getString("chat.format", "§7[§6{tag}§7] §e{role} §f{player}§7: §f{message}");
        chatPrefixEnabled = config.getBoolean("chat.prefix-enabled", true);

        // Alianças
        aliancasAtivado = config.getBoolean("aliancas.ativado", true);
        maxAliados = config.getInt("aliancas.max-aliados", 3);
        tempoConviteAlianca = config.getInt("aliancas.tempo-convite", 60);
        friendlyFireAliados = config.getBoolean("aliancas.friendly-fire-aliados", false);

        // IP Limit
        limiteIPAtivo = config.getBoolean("ip-limit.ativado", true);
        maxClasPerIP = config.getInt("ip-limit.max-clas-por-ip", 2);

        // Ranking Semanal
        rankingSemanalAtivo = config.getBoolean("ranking-semanal.ativado", true);

        // Conquistas
        conquistasAtivo = config.getBoolean("conquistas.ativado", true);

        // Friendly Fire
        friendlyFirePermitido = config.getBoolean("friendly-fire.permitido", false);
        friendlyFireEmGuerra = config.getBoolean("friendly-fire.permitido-em-guerra", false);
    }

    // ==================== GETTERS GERAIS ====================
    public int getNomeMin() { return nomeMin; }
    public int getNomeMax() { return nomeMax; }
    public int getTagMin() { return tagMin; }
    public int getTagMax() { return tagMax; }
    public boolean isPermitirCoresNome() { return permitirCoresNome; }
    public int getLimiteMembros() { return limiteMembros; }

    // ==================== BLACKLIST ====================
    public boolean isNomeBlacklisted(String nome) {
        String nomeLower = nome.toLowerCase();
        for (String blacklisted : blacklistNomes) {
            if (nomeLower.contains(blacklisted.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isTagBlacklisted(String tag) {
        String tagLower = org.bukkit.ChatColor.stripColor(tag).toLowerCase();
        for (String blacklisted : blacklistTags) {
            if (tagLower.equalsIgnoreCase(blacklisted)) {
                return true;
            }
        }
        return false;
    }

    // ==================== GETTERS CONVITES ====================
    public int getConviteExpiracao() { return conviteExpiracao; }
    public int getMaxConvitesPendentes() { return maxConvitesPendentes; }

    // ==================== GETTERS BASE ====================
    public int getBaseCooldown() { return baseCooldown; }
    public int getBaseTempoEspera() { return baseTempoEspera; }
    public boolean isBaseCancelarAoMover() { return baseCancelarAoMover; }
    public boolean isBaseBloquearEmCombate() { return baseBloquearEmCombate; }
    public int getTempoCombate() { return tempoCombate; }
    
    public boolean isMundoPermitidoParaBase(String worldName) {
        // Se o mundo esta na lista de bloqueados, nao permite
        for (String blocked : baseMundosBloqueados) {
            if (blocked.equalsIgnoreCase(worldName)) {
                return false;
            }
        }
        
        // Se a lista de permitidos esta vazia, permite todos (exceto bloqueados)
        if (baseMundosPermitidos.isEmpty()) {
            return true;
        }
        
        // Verifica se esta na lista de permitidos
        for (String allowed : baseMundosPermitidos) {
            if (allowed.equalsIgnoreCase(worldName)) {
                return true;
            }
        }
        
        return false;
    }

    // ==================== GETTERS PONTOS ====================
    public int getPontosPorKill() { return pontosPorKill; }
    public int getPontosPorMorte() { return pontosPorMorte; }
    public int getBonusClanRival() { return bonusClanRival; }

    // ==================== GETTERS MENU ====================
    public String getSomAbrir() { return somAbrir; }
    public String getSomClicar() { return somClicar; }
    public int getMembrosPorPagina() { return membrosPorPagina; }

    // ==================== GETTERS GUERRA ====================
    public int getGuerraDuracao() { return guerraDuracao; }
    public int getGuerraCooldown() { return guerraCooldown; }
    public int getGuerraRecompensaPontos() { return guerraRecompensaPontos; }
    public int getGuerraRecompensaExp() { return guerraRecompensaExp; }
    public double getGuerraRecompensaDinheiro() { return guerraRecompensaDinheiro; }

    // ==================== GETTERS NÍVEL ====================
    public int getNivelMaximo() { return nivelMaximo; }
    public int getNivelExpBase() { return nivelExpBase; }
    public double getNivelExpMultiplier() { return nivelExpMultiplier; }
    public int getNivelBonusMembros() { return nivelBonusMembros; }
    public int getNivelHomesBase() { return nivelHomesBase; }
    public int getNivelBonusHomes() { return nivelBonusHomes; }
    public int getNivelReducaoCooldown() { return nivelReducaoCooldown; }
    public double getNivelCustoBase() { return nivelCustoBase; }
    public double getNivelCustoMultiplier() { return nivelCustoMultiplier; }

    // ==================== GETTERS BANCO ====================
    public double getBancoDepositoMinimo() { return bancoDepositoMinimo; }
    public double getBancoSaqueMinimo() { return bancoSaqueMinimo; }
    public double getBancoTaxaSaque() { return bancoTaxaSaque; }

    // ==================== GETTERS MISSÕES ====================
    public int getMissaoDuracao() { return missaoDuracao; }
    public int getMissaoMaxAtivas() { return missaoMaxAtivas; }

    // ==================== GETTERS CHAT ====================
    public String getChatFormat() { return chatFormat; }
    public boolean isChatPrefixEnabled() { return chatPrefixEnabled; }

    // ==================== GETTERS ALIANÇAS ====================
    public boolean isAliancasAtivado() { return aliancasAtivado; }
    public int getMaxAliados() { return maxAliados; }
    public int getTempoConviteAlianca() { return tempoConviteAlianca; }
    public boolean isFriendlyFireAliados() { return friendlyFireAliados; }

    // ==================== GETTERS IP LIMIT ====================
    public boolean isLimiteIPAtivo() { return limiteIPAtivo; }
    public int getMaxClasPerIP() { return maxClasPerIP; }

    // ==================== GETTERS RANKING SEMANAL ====================
    public boolean isRankingSemanalAtivo() { return rankingSemanalAtivo; }

    // ==================== GETTERS CONQUISTAS ====================
    public boolean isConquistasAtivo() { return conquistasAtivo; }

    // ==================== GETTERS FRIENDLY FIRE ====================
    public boolean isFriendlyFirePermitido() { return friendlyFirePermitido; }
    public boolean isFriendlyFireEmGuerra() { return friendlyFireEmGuerra; }
}
