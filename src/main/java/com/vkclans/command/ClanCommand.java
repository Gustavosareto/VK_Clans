package com.vkclans.command;

import com.vkclans.VKClans;
import com.vkclans.gui.ClanGUI;
import com.vkclans.manager.*;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanLog;
import com.vkclans.model.ClanRole;
import com.vkclans.model.ClanWar;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Executor do comando /clan e todos os subcomandos
 */
public class ClanCommand implements CommandExecutor, TabCompleter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final Set<UUID> deleteConfirmations = new HashSet<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String sub = args[0].toLowerCase();
        
        switch (sub) {
            // Comandos básicos
            case "criar": case "create": handleCreate(player, args); break;
            case "menu": handleMenu(player); break;
            case "convidar": case "invite": handleInvite(player, args); break;
            case "aceitar": case "accept": handleAccept(player, args); break;
            case "recusar": case "deny": handleDeny(player, args); break;
            case "sair": case "leave": handleLeave(player); break;
            case "info": handleInfo(player, args); break;
            case "base": case "home": handleBase(player); break;
            case "setbase": case "sethome": handleSetBase(player); break;
            case "kick": case "expulsar": handleKick(player, args); break;
            case "promover": case "promote": handlePromote(player, args); break;
            case "rebaixar": case "demote": handleDemote(player, args); break;
            case "transferir": case "transfer": handleTransfer(player, args); break;
            case "deletar": case "delete": handleDelete(player); break;
            case "membros": case "members": handleMembers(player); break;
            
            // Novos comandos avançados
            case "guerra": case "war": handleWar(player, args); break;
            case "top": case "ranking": handleRanking(player, args); break;
            case "banco": case "bank": handleBank(player, args); break;
            case "nivel": case "level": case "upgrade": handleLevel(player, args); break;
            case "bau": case "chest": handleChest(player); break;
            case "log": case "logs": case "historico": handleLog(player, args); break;
            case "chat": case "c": handleChat(player, args); break;
            case "missoes": case "missions": handleMissions(player); break;
            case "alianca": case "ally": case "aliado": handleAlly(player, args); break;
            case "semanal": case "weekly": handleWeeklyRanking(player); break;
            case "conquistas": case "achievements": handleAchievements(player); break;
            case "spy": handleSpy(player); break;
            case "reload": handleReload(player); break;
            case "ajuda": case "help": showHelp(player); break;
            
            default: showHelp(player);
        }
        return true;
    }
    
    // ==================== COMANDOS BÁSICOS ====================
    
    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("VKClans.criar")) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (ClanManager.getInstance().getPlayerClan(player.getUniqueId()) != null) {
            MessageUtil.send(player, "already-in-clan");
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage("§eUso: /clan criar <nome> <tag>");
            player.sendMessage("§7Dica: Use códigos de cor na tag! Ex: §a&a§eTAG §7ou §b&b§eTAG");
            return;
        }
        
        String name = args[1];
        // Junta todos os argumentos restantes para permitir espaços na tag com cores
        StringBuilder tagBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i > 2) tagBuilder.append(" ");
            tagBuilder.append(args[i]);
        }
        String tagInput = tagBuilder.toString();
        
        // Remove cores para validação de tamanho
        String tagClean = tagInput.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "").toUpperCase();
        
        ConfigManager config = ConfigManager.getInstance();
        
        if (name.length() < config.getNomeMin() || name.length() > config.getNomeMax()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("min", String.valueOf(config.getNomeMin()));
            ph.put("max", String.valueOf(config.getNomeMax()));
            MessageUtil.send(player, "clan-name-invalid", ph);
            return;
        }
        
        // Verifica blacklist de nome
        if (config.isNomeBlacklisted(name)) {
            MessageUtil.send(player, "clan-name-blacklisted");
            return;
        }
        
        // Valida o tamanho da tag SEM as cores
        if (tagClean.length() < config.getTagMin() || tagClean.length() > config.getTagMax()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("min", String.valueOf(config.getTagMin()));
            ph.put("max", String.valueOf(config.getTagMax()));
            MessageUtil.send(player, "clan-tag-invalid", ph);
            return;
        }
        
        // Verifica blacklist de tag
        if (config.isTagBlacklisted(tagClean)) {
            MessageUtil.send(player, "clan-tag-blacklisted");
            return;
        }
        
        // Verifica limite de IP
        if (!IPLimitManager.getInstance().canCreateClan(player)) {
            MessageUtil.send(player, "ip-limit-reached");
            return;
        }
        
        ClanManager manager = ClanManager.getInstance();
        
        if (manager.getClanByName(name) != null) {
            MessageUtil.send(player, "clan-name-taken");
            return;
        }
        
        // Verifica se a tag (sem cores) já existe
        if (manager.getClanByTag(tagClean) != null) {
            MessageUtil.send(player, "clan-tag-taken");
            return;
        }
        
        // Cria o clan com a tag colorida
        Clan clan = new Clan(name, tagInput, player.getUniqueId());
        manager.addClan(clan);
        
        LogManager.getInstance().log(name, player.getUniqueId(), player.getName(),
            ClanLog.LogType.CLAN_CREATE, null);
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("clan", name);
        ph.put("tag", clan.getColoredTag());
        MessageUtil.send(player, "clan-create-success", ph);
    }
    
    private void handleMenu(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            if (InviteManager.getInstance().hasAnyInvite(player.getUniqueId())) {
                ClanGUI.openInvitesMenu(player);
            } else {
                MessageUtil.send(player, "no-clan");
            }
            return;
        }
        ClanGUI.openMainMenu(player);
    }
    
    private void handleInvite(Player player, String[] args) {
        if (!player.hasPermission("VKClans.convidar")) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (!role.canInvite()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan convidar <jogador>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtil.send(player, "player-not-found");
            return;
        }
        
        if (ClanManager.getInstance().getPlayerClan(target.getUniqueId()) != null) {
            MessageUtil.send(player, "clan-invite-already-member");
            return;
        }
        
        if (InviteManager.getInstance().hasInvite(target.getUniqueId(), clan.getName())) {
            MessageUtil.send(player, "clan-invite-already-sent");
            return;
        }
        
        // Verifica limite de membros
        int limit = LevelManager.getInstance().getMemberLimit(clan);
        if (clan.getMembers().size() >= limit) {
            Map<String, String> ph = new HashMap<>();
            ph.put("max", String.valueOf(limit));
            MessageUtil.send(player, "clan-full", ph);
            return;
        }
        
        InviteManager.getInstance().sendInvite(target.getUniqueId(), clan.getName());
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.MEMBER_INVITE, target.getName());
        
        Map<String, String> ph = new HashMap<>();
        ph.put("player", target.getName());
        ph.put("clan", clan.getName());
        
        MessageUtil.send(player, "clan-invite-sent", ph);
        MessageUtil.send(target, "clan-invite-received", ph);
    }
    
    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan aceitar <clan>");
            return;
        }
        
        if (ClanManager.getInstance().getPlayerClan(player.getUniqueId()) != null) {
            MessageUtil.send(player, "already-in-clan");
            return;
        }
        
        String clanName = args[1];
        
        if (!InviteManager.getInstance().hasInvite(player.getUniqueId(), clanName)) {
            MessageUtil.send(player, "clan-invite-no-pending");
            return;
        }
        
        Clan clan = ClanManager.getInstance().getClanByName(clanName);
        if (clan == null) {
            MessageUtil.send(player, "clan-not-found");
            return;
        }
        
        // Verifica limite
        int limit = LevelManager.getInstance().getMemberLimit(clan);
        if (clan.getMembers().size() >= limit) {
            Map<String, String> ph = new HashMap<>();
            ph.put("max", String.valueOf(limit));
            MessageUtil.send(player, "clan-full", ph);
            return;
        }
        
        InviteManager.getInstance().removeInvite(player.getUniqueId(), clanName);
        clan.addMember(player.getUniqueId());
        ClanManager.getInstance().updateMemberIndex(clan);
        
        LogManager.getInstance().log(clanName, player.getUniqueId(), player.getName(),
            ClanLog.LogType.MEMBER_JOIN, null);
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("clan", clanName);
        ph.put("player", player.getName());
        
        MessageUtil.send(player, "clan-invite-accepted", ph);
        
        // Notifica membros
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && !p.equals(player)) {
                MessageUtil.send(p, "clan-new-member", ph);
            }
        }
    }
    
    private void handleDeny(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan recusar <clan>");
            return;
        }
        
        String clanName = args[1];
        
        if (!InviteManager.getInstance().hasInvite(player.getUniqueId(), clanName)) {
            MessageUtil.send(player, "clan-invite-no-pending");
            return;
        }
        
        InviteManager.getInstance().removeInvite(player.getUniqueId(), clanName);
        
        Map<String, String> ph = new HashMap<>();
        ph.put("clan", clanName);
        MessageUtil.send(player, "clan-invite-declined", ph);
    }
    
    private void handleLeave(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (clan.getLeader().equals(player.getUniqueId())) {
            MessageUtil.send(player, "clan-leave-leader");
            return;
        }
        
        clan.removeMember(player.getUniqueId());
        ClanManager.getInstance().removeMemberFromIndex(player.getUniqueId());
        ChatManager.getInstance().disableClanChat(player.getUniqueId());
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.MEMBER_LEAVE, null);
        
        StorageManager.getInstance().saveClans();
        
        MessageUtil.send(player, "clan-leave-success");
        
        Map<String, String> ph = new HashMap<>();
        ph.put("player", player.getName());
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                MessageUtil.send(p, "clan-leave-broadcast", ph);
            }
        }
    }
    
    private void handleInfo(Player player, String[] args) {
        Clan clan;
        if (args.length >= 2) {
            clan = ClanManager.getInstance().getClanByName(args[1]);
            if (clan == null) {
                clan = ClanManager.getInstance().getClanByTag(args[1].toUpperCase());
            }
        } else {
            clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        }
        
        if (clan == null) {
            MessageUtil.send(player, "clan-not-found");
            return;
        }
        
        String leaderName = Bukkit.getOfflinePlayer(clan.getLeader()).getName();
        int memberLimit = LevelManager.getInstance().getMemberLimit(clan);
        int expNeeded = LevelManager.getInstance().getExpForNextLevel(clan.getLevel());
        
        MessageUtil.sendRaw(player, "clan-info-header");
        
        Map<String, String> ph = new HashMap<>();
        ph.put("clan", clan.getName());
        ph.put("tag", clan.getTag());
        MessageUtil.send(player, "clan-info-name", ph);
        
        ph.clear();
        ph.put("level", String.valueOf(clan.getLevel()));
        ph.put("exp", String.valueOf(clan.getExperience()));
        ph.put("exp_needed", String.valueOf(expNeeded));
        MessageUtil.send(player, "clan-info-level", ph);
        
        ph.clear();
        ph.put("leader", leaderName != null ? leaderName : "Desconhecido");
        MessageUtil.send(player, "clan-info-leader", ph);
        
        ph.clear();
        ph.put("count", String.valueOf(clan.getMembers().size()));
        ph.put("max", String.valueOf(memberLimit));
        MessageUtil.send(player, "clan-info-members", ph);
        
        ph.clear();
        ph.put("kills", String.valueOf(clan.getKills()));
        ph.put("deaths", String.valueOf(clan.getDeaths()));
        ph.put("kdr", String.format("%.2f", clan.getKDR()));
        MessageUtil.send(player, "clan-info-stats", ph);
        
        ph.clear();
        ph.put("wins", String.valueOf(clan.getWins()));
        ph.put("losses", String.valueOf(clan.getLosses()));
        MessageUtil.send(player, "clan-info-wars", ph);
        
        ph.clear();
        ph.put("points", String.valueOf(clan.getPoints()));
        MessageUtil.send(player, "clan-info-points", ph);
        
        if (BankManager.getInstance().isEnabled()) {
            ph.clear();
            ph.put("balance", BankManager.getInstance().formatMoney(clan.getBankBalance()));
            MessageUtil.send(player, "clan-info-bank", ph);
        }
        
        ph.clear();
        ph.put("date", DATE_FORMAT.format(new Date(clan.getCreationDate())));
        MessageUtil.send(player, "clan-info-created", ph);
        
        MessageUtil.sendRaw(player, "clan-info-footer");
    }
    
    private void handleBase(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (clan.getBase() == null) {
            MessageUtil.send(player, "clan-base-no-base");
            return;
        }
        
        ConfigManager config = ConfigManager.getInstance();
        
        // Verifica combate
        if (config.isBaseBloquearEmCombate() && 
            CooldownManager.getInstance().isInCombat(player.getUniqueId())) {
            MessageUtil.send(player, "clan-base-combat");
            return;
        }
        
        // Verifica cooldown (com redução de nível)
        int cooldown = config.getBaseCooldown() - LevelManager.getInstance().getCooldownReduction(clan);
        cooldown = Math.max(0, cooldown);
        
        if (CooldownManager.getInstance().hasCooldown(player.getUniqueId(), "base")) {
            long remaining = CooldownManager.getInstance().getRemainingCooldown(player.getUniqueId(), "base");
            Map<String, String> ph = new HashMap<>();
            ph.put("seconds", String.valueOf(remaining / 1000));
            MessageUtil.send(player, "clan-base-cooldown", ph);
            return;
        }
        
        TeleportManager.getInstance().teleportWithDelay(player, clan.getBase(), 
            config.getBaseTempoEspera(), config.isBaseCancelarAoMover());
        CooldownManager.getInstance().setCooldown(player.getUniqueId(), "base", cooldown * 1000L);
    }
    
    private void handleSetBase(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (!role.canSetBase()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        // Verifica se o mundo e permitido
        String worldName = player.getWorld().getName();
        if (!ConfigManager.getInstance().isMundoPermitidoParaBase(worldName)) {
            MessageUtil.send(player, "clan-base-world-blocked");
            return;
        }
        
        clan.setBase(player.getLocation());
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.BASE_SET, null);
        
        StorageManager.getInstance().saveClans();
        MessageUtil.send(player, "clan-base-set");
    }
    
    private void handleKick(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (!role.canKick()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan kick <jogador>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        UUID targetUUID = target != null ? target.getUniqueId() : null;
        
        // Tenta buscar offline
        if (targetUUID == null) {
            for (UUID uuid : clan.getMembers()) {
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                if (name != null && name.equalsIgnoreCase(args[1])) {
                    targetUUID = uuid;
                    break;
                }
            }
        }
        
        if (targetUUID == null || !clan.isMember(targetUUID)) {
            MessageUtil.send(player, "player-not-in-clan");
            return;
        }
        
        ClanRole targetRole = clan.getMemberRole(targetUUID);
        if (targetRole.getLevel() >= role.getLevel()) {
            MessageUtil.send(player, "clan-kick-higher-role");
            return;
        }
        
        String targetName = Bukkit.getOfflinePlayer(targetUUID).getName();
        
        clan.removeMember(targetUUID);
        ClanManager.getInstance().removeMemberFromIndex(targetUUID);
        ChatManager.getInstance().disableClanChat(targetUUID);
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.MEMBER_KICK, targetName);
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("player", targetName);
        ph.put("kicker", player.getName());
        ph.put("clan", clan.getName());
        
        MessageUtil.send(player, "clan-kick-success", ph);
        
        if (target != null) {
            MessageUtil.send(target, "clan-kick-target", ph);
        }
        
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && !p.equals(player)) {
                MessageUtil.send(p, "clan-kick-broadcast", ph);
            }
        }
    }
    
    private void handlePromote(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ClanRole playerRole = clan.getMemberRole(player.getUniqueId());
        if (!playerRole.canManageMembers()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan promover <jogador> [cargo]");
            player.sendMessage("§7Cargos disponiveis: §fsubdono, admin, membro");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !clan.isMember(target.getUniqueId())) {
            MessageUtil.send(player, "player-not-in-clan");
            return;
        }
        
        // Se especificou um cargo
        if (args.length >= 3) {
            String cargoArg = args[2].toLowerCase();
            ClanRole newRole = null;
            
            switch (cargoArg) {
                case "subdono":
                case "sub-dono":
                case "co-leader":
                case "coleader":
                    newRole = ClanRole.SUB_DONO;
                    break;
                case "admin":
                case "administrador":
                case "officer":
                    newRole = ClanRole.ADMINISTRADOR;
                    break;
                case "membro":
                case "member":
                    newRole = ClanRole.MEMBRO;
                    break;
                default:
                    MessageUtil.send(player, "clan-role-invalid");
                    return;
            }
            
            // Verifica se pode definir esse cargo
            if (newRole.ordinal() <= playerRole.ordinal() && playerRole != ClanRole.DONO) {
                MessageUtil.send(player, "no-permission");
                return;
            }
            
            // Define o cargo diretamente
            clan.setMemberRole(target.getUniqueId(), newRole);
            
            LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
                ClanLog.LogType.MEMBER_PROMOTE, target.getName() + " -> " + newRole.getDisplayName());
            
            StorageManager.getInstance().saveClans();
            
            Map<String, String> ph = new HashMap<>();
            ph.put("player", target.getName());
            ph.put("role", newRole.getDisplayName());
            
            MessageUtil.send(player, "clan-role-set", ph);
            MessageUtil.send(target, "clan-promoted", ph);
            return;
        }
        
        // Promocao normal (sobe um cargo)
        if (!clan.promoteMember(target.getUniqueId())) {
            MessageUtil.send(player, "clan-promote-max");
            return;
        }
        
        ClanRole newRole = clan.getMemberRole(target.getUniqueId());
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.MEMBER_PROMOTE, target.getName() + " -> " + newRole.getDisplayName());
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("player", target.getName());
        ph.put("role", newRole.getDisplayName());
        
        MessageUtil.send(player, "clan-promote-success", ph);
        MessageUtil.send(target, "clan-promote-target", ph);
    }
    
    private void handleDemote(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (!role.canManageMembers()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan rebaixar <jogador>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !clan.isMember(target.getUniqueId())) {
            MessageUtil.send(player, "player-not-in-clan");
            return;
        }
        
        if (!clan.demoteMember(target.getUniqueId())) {
            MessageUtil.send(player, "clan-demote-min");
            return;
        }
        
        ClanRole newRole = clan.getMemberRole(target.getUniqueId());
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.MEMBER_DEMOTE, target.getName() + " -> " + newRole.getDisplayName());
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("player", target.getName());
        ph.put("role", newRole.getDisplayName());
        
        MessageUtil.send(player, "clan-demote-success", ph);
        MessageUtil.send(target, "clan-demote-target", ph);
    }
    
    private void handleTransfer(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (!clan.getLeader().equals(player.getUniqueId())) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§eUso: /clan transferir <jogador>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !clan.isMember(target.getUniqueId())) {
            MessageUtil.send(player, "player-not-in-clan");
            return;
        }
        
        clan.transferLeadership(target.getUniqueId());
        
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.LEADER_TRANSFER, target.getName());
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("player", target.getName());
        
        MessageUtil.send(player, "clan-transfer-success", ph);
        MessageUtil.send(target, "clan-transfer-target", ph);
        
        for (UUID uuid : clan.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && !p.equals(player) && !p.equals(target)) {
                MessageUtil.send(p, "clan-transfer-broadcast", ph);
            }
        }
    }
    
    private void handleDelete(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (!clan.getLeader().equals(player.getUniqueId())) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (!deleteConfirmations.contains(player.getUniqueId())) {
            deleteConfirmations.add(player.getUniqueId());
            MessageUtil.send(player, "clan-delete-confirm");
            
            Bukkit.getScheduler().runTaskLater(VKClans.getInstance(), () -> {
                deleteConfirmations.remove(player.getUniqueId());
            }, 20L * 30);
            return;
        }
        
        deleteConfirmations.remove(player.getUniqueId());
        
        String clanName = clan.getName();
        
        // Remove todos do clã
        for (UUID uuid : clan.getMembers()) {
            ClanManager.getInstance().removeMemberFromIndex(uuid);
            ChatManager.getInstance().disableClanChat(uuid);
        }
        
        ClanManager.getInstance().removeClan(clan);
        ChestManager.getInstance().removeChest(clanName);
        LogManager.getInstance().clearLogs(clanName);
        
        StorageManager.getInstance().saveClans();
        
        Map<String, String> ph = new HashMap<>();
        ph.put("clan", clanName);
        MessageUtil.send(player, "clan-delete-success", ph);
    }
    
    private void handleMembers(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        ClanGUI.openMembersMenu(player, 0);
    }
    
    // ==================== COMANDOS AVANÇADOS ====================
    
    private void handleWar(Player player, String[] args) {
        if (!player.hasPermission("VKClans.guerra")) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (!role.canManageMembers()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        if (args.length < 2) {
            // Mostra status da guerra atual
            ClanWar war = WarManager.getInstance().getActiveWar(clan.getName());
            if (war == null) {
                player.sendMessage("§eUso: /clan guerra <clan>");
                player.sendMessage("§7Seu clã não está em guerra.");
            } else {
                player.sendMessage("§c⚔ §4Guerra Ativa:");
                player.sendMessage("§e" + war.getClan1() + " §7(" + war.getClan1Kills() + ") vs (" + 
                    war.getClan2Kills() + ") §e" + war.getClan2());
                player.sendMessage("§7Tempo restante: §f" + war.getRemainingTimeFormatted());
            }
            return;
        }
        
        // Verifica se já está em guerra
        if (WarManager.getInstance().getActiveWar(clan.getName()) != null) {
            MessageUtil.send(player, "war-already-active");
            return;
        }
        
        Clan targetClan = ClanManager.getInstance().getClanByName(args[1]);
        if (targetClan == null) {
            targetClan = ClanManager.getInstance().getClanByTag(args[1].toUpperCase());
        }
        
        if (targetClan == null) {
            MessageUtil.send(player, "clan-not-found");
            return;
        }
        
        if (targetClan.equals(clan)) {
            player.sendMessage("§cVocê não pode declarar guerra contra seu próprio clã!");
            return;
        }
        
        if (WarManager.getInstance().getActiveWar(targetClan.getName()) != null) {
            MessageUtil.send(player, "war-target-in-war");
            return;
        }
        
        WarManager.getInstance().declareWar(clan, targetClan);
    }
    
    private void handleRanking(Player player, String[] args) {
        RankingManager.RankingType type = RankingManager.RankingType.POINTS;
        
        if (args.length >= 2) {
            try {
                type = RankingManager.RankingType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cTipos válidos: points, kills, wins, level, members, kdr");
                return;
            }
        }
        
        MessageUtil.sendRaw(player, "ranking-header");
        
        Map<String, String> ph = new HashMap<>();
        ph.put("type", type.getDisplayName());
        MessageUtil.send(player, "ranking-title", ph);
        
        List<String> ranking = RankingManager.getInstance().formatRanking(type, 10);
        for (String line : ranking) {
            player.sendMessage(line);
        }
        
        MessageUtil.sendRaw(player, "ranking-footer");
    }
    
    private void handleBank(Player player, String[] args) {
        if (!BankManager.getInstance().isEnabled()) {
            MessageUtil.send(player, "bank-disabled");
            return;
        }
        
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§6Banco do Cla: §a" + formatMoneyDisplay(clan.getBankBalance()));
            player.sendMessage("§eUso: /clan banco depositar <valor>");
            player.sendMessage("§eUso: /clan banco sacar <valor>");
            player.sendMessage("§7Dica: Use §e1k §7= 1.000, §e1m §7= 1.000.000, §e1b §7= 1.000.000.000");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        if (args.length < 3) {
            player.sendMessage("§eEspecifique um valor. Ex: §f100§e, §f1k§e, §f50k§e, §f1m");
            return;
        }
        
        double amount = parseFormattedMoney(args[2]);
        if (amount <= 0) {
            player.sendMessage("§cValor invalido. Use numeros ou formatos como: §f100§c, §f1k§c, §f50k§c, §f1m");
            return;
        }
        
        ConfigManager config = ConfigManager.getInstance();
        
        if (action.equals("depositar") || action.equals("deposit")) {
            if (amount < config.getBancoDepositoMinimo()) {
                Map<String, String> ph = new HashMap<>();
                ph.put("amount", formatMoneyDisplay(config.getBancoDepositoMinimo()));
                MessageUtil.send(player, "bank-minimum-deposit", ph);
                return;
            }
            BankManager.getInstance().deposit(player, clan, amount);
        } else if (action.equals("sacar") || action.equals("withdraw")) {
            ClanRole role = clan.getMemberRole(player.getUniqueId());
            if (!role.canManageMembers()) {
                MessageUtil.send(player, "no-permission");
                return;
            }
            
            if (amount < config.getBancoSaqueMinimo()) {
                Map<String, String> ph = new HashMap<>();
                ph.put("amount", formatMoneyDisplay(config.getBancoSaqueMinimo()));
                MessageUtil.send(player, "bank-minimum-withdraw", ph);
                return;
            }
            BankManager.getInstance().withdraw(player, clan, amount);
        }
    }
    
    /**
     * Parseia valores monetarios formatados (1k, 1m, 1b, etc)
     * @param input String com o valor (ex: "1k", "50k", "1.5m", "100")
     * @return valor em double, ou -1 se invalido
     */
    private double parseFormattedMoney(String input) {
        if (input == null || input.isEmpty()) return -1;
        
        input = input.toLowerCase().replace(",", ".").trim();
        
        try {
            double multiplier = 1;
            String numPart = input;
            
            if (input.endsWith("k")) {
                multiplier = 1_000;
                numPart = input.substring(0, input.length() - 1);
            } else if (input.endsWith("m")) {
                multiplier = 1_000_000;
                numPart = input.substring(0, input.length() - 1);
            } else if (input.endsWith("b")) {
                multiplier = 1_000_000_000;
                numPart = input.substring(0, input.length() - 1);
            } else if (input.endsWith("kk")) {
                multiplier = 1_000_000;
                numPart = input.substring(0, input.length() - 2);
            }
            
            double value = Double.parseDouble(numPart) * multiplier;
            return value > 0 ? value : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Formata valor monetario para exibicao (1000 -> 1k, 1000000 -> 1m)
     */
    private String formatMoneyDisplay(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.2fB", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.2fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.2fK", amount / 1_000);
        } else {
            return String.format("%.2f", amount);
        }
    }
    
    private void handleLevel(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        LevelManager level = LevelManager.getInstance();
        
        if (args.length < 2 || !args[1].equalsIgnoreCase("upgrade")) {
            player.sendMessage("§6Nível do Clã: §e" + clan.getLevel());
            player.sendMessage("§6Experiência: " + level.getProgressBar(clan) + " §7(" + 
                clan.getExperience() + "/" + level.getExpForNextLevel(clan.getLevel()) + ")");
            player.sendMessage("§6Limite de Membros: §f" + level.getMemberLimit(clan));
            
            if (BankManager.getInstance().isEnabled()) {
                player.sendMessage("§eCusto para upgrade: §a" + 
                    BankManager.getInstance().formatMoney(level.getUpgradeCost(clan)));
                player.sendMessage("§7Use /clan nivel upgrade para comprar");
            }
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        if (!role.canManageMembers()) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        level.upgradeWithMoney(player, clan);
    }
    
    private void handleChest(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        ChestManager.getInstance().openChest(player, clan);
        MessageUtil.send(player, "chest-opened");
    }
    
    private void handleLog(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        int limit = 10;
        if (args.length >= 2) {
            try {
                limit = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                limit = 10;
            }
        }
        
        MessageUtil.sendRaw(player, "log-header");
        MessageUtil.sendRaw(player, "log-title");
        
        List<String> logs = LogManager.getInstance().formatLogs(clan.getName(), limit);
        for (String log : logs) {
            player.sendMessage("§7" + log);
        }
        
        if (logs.isEmpty()) {
            player.sendMessage("§7Nenhum log encontrado.");
        }
        
        MessageUtil.sendRaw(player, "log-footer");
    }
    
    private void handleChat(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (args.length < 2) {
            // Toggle chat
            boolean enabled = ChatManager.getInstance().toggleClanChat(player);
            if (enabled) {
                MessageUtil.send(player, "chat-enabled");
            } else {
                MessageUtil.send(player, "chat-disabled");
            }
            return;
        }
        
        // Envia mensagem direta
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) message.append(" ");
            message.append(args[i]);
        }
        
        ChatManager.getInstance().sendClanMessage(player, message.toString());
    }
    
    private void handleMissions(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        player.sendMessage("§6§l Missoes do Cla:");
        player.sendMessage("");
        
        List<String> missions = MissionManager.getInstance().formatMissions(clan.getName());
        for (String mission : missions) {
            player.sendMessage(mission);
        }
    }
    
    // ==================== SISTEMA DE ALIANÇAS ====================
    
    private void handleAlly(Player player, String[] args) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (!ConfigManager.getInstance().isAliancasAtivado()) {
            player.sendMessage("§cSistema de aliancas desativado.");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("§6§l Sistema de Aliancas");
            player.sendMessage("§e/clan alianca convidar <clan> §7- Envia convite de alianca");
            player.sendMessage("§e/clan alianca aceitar <clan> §7- Aceita convite");
            player.sendMessage("§e/clan alianca recusar <clan> §7- Recusa convite");
            player.sendMessage("§e/clan alianca remover <clan> §7- Remove alianca");
            player.sendMessage("§e/clan alianca lista §7- Lista aliados");
            return;
        }
        
        String sub = args[1].toLowerCase();
        AllyManager allyManager = AllyManager.getInstance();
        
        if (sub.equals("lista") || sub.equals("list")) {
            Set<String> allies = allyManager.getAllies(clan.getName());
            if (allies.isEmpty()) {
                player.sendMessage("§7Seu cla nao possui aliados.");
            } else {
                player.sendMessage("§6Aliados: §f" + String.join(", ", allies));
            }
            
            Set<String> pending = allyManager.getPendingRequests(clan.getName());
            if (!pending.isEmpty()) {
                player.sendMessage("§eConvites pendentes: §f" + String.join(", ", pending));
            }
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage("§cUso: /clan alianca " + sub + " <clan>");
            return;
        }
        
        String targetClanName = args[2];
        Clan targetClan = ClanManager.getInstance().getClanByName(targetClanName);
        
        if (targetClan == null) {
            MessageUtil.send(player, "clan-not-found");
            return;
        }
        
        if (targetClan.getName().equals(clan.getName())) {
            player.sendMessage("§cVoce nao pode se aliar ao seu proprio cla!");
            return;
        }
        
        ClanRole role = clan.getMemberRole(player.getUniqueId());
        
        switch (sub) {
            case "convidar":
            case "invite":
                if (!role.canManageMembers()) {
                    MessageUtil.send(player, "no-permission");
                    return;
                }
                
                if (allyManager.areAllies(clan.getName(), targetClan.getName())) {
                    player.sendMessage("§cVoces ja sao aliados!");
                    return;
                }
                
                if (allyManager.getAllies(clan.getName()).size() >= ConfigManager.getInstance().getMaxAliados()) {
                    player.sendMessage("§cVoce atingiu o limite de aliados!");
                    return;
                }
                
                if (allyManager.sendAllyRequest(clan.getName(), targetClan.getName())) {
                    player.sendMessage("§aConvite de alianca enviado para §f" + targetClan.getName());
                    
                    // Notifica líderes do outro clã
                    for (UUID uuid : targetClan.getMembers()) {
                        ClanRole r = targetClan.getMemberRole(uuid);
                        if (r.canManageMembers()) {
                            Player p = Bukkit.getPlayer(uuid);
                            if (p != null && p.isOnline()) {
                                p.sendMessage("§eO cla §f" + clan.getName() + " §equer se aliar ao seu cla!");
                                p.sendMessage("§7Use §e/clan alianca aceitar " + clan.getName() + " §7para aceitar.");
                            }
                        }
                    }
                } else {
                    player.sendMessage("§cJa existe um convite pendente!");
                }
                break;
                
            case "aceitar":
            case "accept":
                if (!role.canManageMembers()) {
                    MessageUtil.send(player, "no-permission");
                    return;
                }
                
                if (allyManager.acceptAllyRequest(clan.getName(), targetClan.getName())) {
                    // Notifica ambos os clãs
                    for (UUID uuid : clan.getMembers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) p.sendMessage("§aAlianca formada com §f" + targetClan.getName() + "§a!");
                    }
                    for (UUID uuid : targetClan.getMembers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) p.sendMessage("§aAlianca formada com §f" + clan.getName() + "§a!");
                    }
                } else {
                    player.sendMessage("§cNao ha convite pendente deste cla ou limite de aliados atingido!");
                }
                break;
                
            case "recusar":
            case "deny":
                if (!role.canManageMembers()) {
                    MessageUtil.send(player, "no-permission");
                    return;
                }
                
                if (allyManager.denyAllyRequest(clan.getName(), targetClan.getName())) {
                    player.sendMessage("§cConvite de alianca recusado.");
                } else {
                    player.sendMessage("§cNao ha convite pendente deste cla!");
                }
                break;
                
            case "remover":
            case "remove":
                if (!role.canManageMembers()) {
                    MessageUtil.send(player, "no-permission");
                    return;
                }
                
                if (allyManager.removeAlly(clan.getName(), targetClan.getName())) {
                    // Notifica ambos os clãs
                    for (UUID uuid : clan.getMembers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) p.sendMessage("§cAlianca com §f" + targetClan.getName() + " §cfoi desfeita.");
                    }
                    for (UUID uuid : targetClan.getMembers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) p.sendMessage("§cAlianca com §f" + clan.getName() + " §cfoi desfeita.");
                    }
                } else {
                    player.sendMessage("§cVoces nao sao aliados!");
                }
                break;
                
            default:
                player.sendMessage("§cSubcomando invalido. Use /clan alianca para ver a ajuda.");
        }
    }
    
    // ==================== RANKING SEMANAL ====================
    
    private void handleWeeklyRanking(Player player) {
        if (!ConfigManager.getInstance().isRankingSemanalAtivo()) {
            player.sendMessage("§cRanking semanal desativado.");
            return;
        }
        
        WeeklyRankingManager manager = WeeklyRankingManager.getInstance();
        
        player.sendMessage("§6§l Ranking Semanal de Kills");
        player.sendMessage("§7Tempo restante: §e" + manager.getFormattedTimeUntilReset());
        player.sendMessage("");
        
        List<Map.Entry<String, Integer>> top = manager.getTopWeekly(10);
        
        if (top.isEmpty()) {
            player.sendMessage("§7Nenhum cla pontuou ainda esta semana.");
            return;
        }
        
        String[] medals = {"§6[1]", "§7[2]", "§c[3]"};
        int pos = 1;
        for (Map.Entry<String, Integer> entry : top) {
            String prefix = pos <= 3 ? medals[pos - 1] : "§8[" + pos + "]";
            player.sendMessage(prefix + " §f" + entry.getKey() + " §7- §a" + entry.getValue() + " kills");
            pos++;
        }
        
        // Mostra posição do clã do jogador
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan != null) {
            int myKills = manager.getWeeklyKills(clan.getName());
            player.sendMessage("");
            player.sendMessage("§eSeu cla: §f" + myKills + " kills");
        }
    }
    
    // ==================== CONQUISTAS ====================
    
    private void handleAchievements(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "no-clan");
            return;
        }
        
        if (!ConfigManager.getInstance().isConquistasAtivo()) {
            player.sendMessage("§cSistema de conquistas desativado.");
            return;
        }
        
        AchievementManager manager = AchievementManager.getInstance();
        Set<AchievementManager.Achievement> obtained = manager.getAchievements(clan.getName());
        AchievementManager.Achievement[] all = manager.getAllAchievements();
        
        player.sendMessage("§6§l Conquistas do Cla");
        player.sendMessage("§7Conquistadas: §a" + obtained.size() + "§7/§f" + all.length);
        player.sendMessage("");
        
        for (AchievementManager.Achievement ach : all) {
            boolean has = obtained.contains(ach);
            String status = has ? "§a[OK]" : "§c[X]";
            String color = has ? "§a" : "§7";
            player.sendMessage(status + " " + color + ach.getName() + " §8- §7" + ach.getDescription());
        }
    }
    
    private void handleSpy(Player player) {
        if (!player.hasPermission("VKClans.spy")) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        boolean enabled = ChatManager.getInstance().toggleSpy(player);
        if (enabled) {
            MessageUtil.send(player, "chat-spy-enabled");
        } else {
            MessageUtil.send(player, "chat-spy-disabled");
        }
    }
    
    private void handleReload(Player player) {
        if (!player.hasPermission("VKClans.reload")) {
            MessageUtil.send(player, "no-permission");
            return;
        }
        
        VKClans.getInstance().reload();
        MessageUtil.send(player, "reloaded");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§6§lVKClans §7- §fComandos");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e/clan criar <nome> <tag> §7- Cria um cla");
        player.sendMessage("§e/clan menu §7- Abre o menu do cla");
        player.sendMessage("§e/clan info [clan] §7- Informacoes do cla");
        player.sendMessage("§e/clan convidar <jogador> §7- Convida jogador");
        player.sendMessage("§e/clan aceitar/recusar <clan> §7- Gerencia convites");
        player.sendMessage("§e/clan sair §7- Sai do cla");
        player.sendMessage("§e/clan kick <jogador> §7- Expulsa membro");
        player.sendMessage("§e/clan promover/rebaixar <jogador> §7- Gerencia cargos");
        player.sendMessage("§e/clan base §7- Teleporta para a base");
        player.sendMessage("§e/clan setbase §7- Define a base do cla");
        player.sendMessage("§e/clan guerra <clan> §7- Declara guerra");
        player.sendMessage("§e/clan top [tipo] §7- Ranking de clas");
        player.sendMessage("§e/clan banco §7- Sistema de banco");
        player.sendMessage("§e/clan nivel §7- Sistema de nivel");
        player.sendMessage("§e/clan bau §7- Bau compartilhado");
        player.sendMessage("§e/clan chat [msg] §7- Chat do cla");
        player.sendMessage("§e/clan missoes §7- Missoes ativas");
        player.sendMessage("§e/clan alianca §7- Sistema de aliancas");
        player.sendMessage("§e/clan semanal §7- Ranking semanal");
        player.sendMessage("§e/clan conquistas §7- Conquistas do cla");
        player.sendMessage("§e/clan log §7- Historico de acoes");
        player.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    // ==================== TAB COMPLETER ====================
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subs = Arrays.asList(
                "criar", "menu", "info", "convidar", "aceitar", "recusar", 
                "sair", "kick", "promover", "rebaixar", "transferir", "deletar",
                "base", "setbase", "membros", "guerra", "top", "banco", 
                "nivel", "bau", "chat", "missoes", "log", "ajuda"
            );
            for (String sub : subs) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            
            if (sub.equals("convidar") || sub.equals("kick") || 
                sub.equals("promover") || sub.equals("rebaixar") || sub.equals("transferir")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(p.getName());
                    }
                }
            } else if (sub.equals("info") || sub.equals("guerra") || 
                       sub.equals("aceitar") || sub.equals("recusar")) {
                for (Clan clan : ClanManager.getInstance().getAllClans()) {
                    if (clan.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(clan.getName());
                    }
                }
            } else if (sub.equals("top") || sub.equals("ranking")) {
                for (RankingManager.RankingType type : RankingManager.RankingType.values()) {
                    if (type.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(type.name().toLowerCase());
                    }
                }
            } else if (sub.equals("banco")) {
                completions.add("depositar");
                completions.add("sacar");
            } else if (sub.equals("nivel")) {
                completions.add("upgrade");
            }
        }
        
        return completions;
    }
}
