package com.vkclans.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.vkclans.VKClans;
import com.vkclans.gui.ClanGUI;
import com.vkclans.manager.ClanManager;
import com.vkclans.manager.ConfigManager;
import com.vkclans.manager.CooldownManager;
import com.vkclans.manager.InviteManager;
import com.vkclans.manager.StorageManager;
import com.vkclans.manager.TeleportManager;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanRole;
import com.vkclans.util.MessageUtil;

/**
 * Listener para interações com menus GUI do clã
 */
public class ClanGUIListener implements Listener {
    
    private static final Map<UUID, ChatAction> pendingChatActions = new HashMap<>();
    private static final Map<UUID, String> confirmActions = new HashMap<>();
    private static final Map<UUID, Integer> memberMenuPages = new HashMap<>();
    
    private enum ChatAction {
        INVITE_PLAYER,
        KICK_PLAYER
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        pendingChatActions.remove(uuid);
        confirmActions.remove(uuid);
        memberMenuPages.remove(uuid);
        TeleportManager.getInstance().cancelTeleport(uuid);
        CooldownManager.getInstance().clearPlayer(uuid);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        TeleportManager.getInstance().checkMovement(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title == null || title.isEmpty()) return;
        
        // Remove color codes para comparação mais fácil
        String cleanTitle = org.bukkit.ChatColor.stripColor(title).toLowerCase();
        
        // Identifica o tipo de menu por várias condições
        boolean isMainMenu = cleanTitle.contains("meu cl") && !cleanTitle.contains("membros") && !cleanTitle.contains("confirmar");
        boolean isMembersMenu = cleanTitle.contains("membros");
        boolean isInvitesMenu = cleanTitle.contains("convites");
        boolean isConfirmMenu = cleanTitle.contains("confirmar");
        boolean isClanCreation = cleanTitle.equals("criar clã");
        boolean isColorSelection = cleanTitle.equals("escolher cor da tag");
        
        // Se não for nenhum menu do clã, ignora
        if (!isMainMenu && !isMembersMenu && !isInvitesMenu && !isConfirmMenu && !isClanCreation && !isColorSelection) return;
        
        event.setCancelled(true);
        
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        
        String name = item.getItemMeta().getDisplayName();
        if (name == null) return;
        
        // Ignora vidro de decoração
        if (item.getType() == Material.STAINED_GLASS_PANE) return;
        
        if (isClanCreation) {
            handleClanCreationClick(player, event, item, name);
            return;
        } else if (isColorSelection) {
            handleColorSelectionClick(player, event, item, name);
            return;
        }
        
        if (isMainMenu) {
            handleMainMenuClick(player, event, item, name);
        } else if (isMembersMenu) {
            handleMembersMenuClick(player, event, item, name);
        } else if (isInvitesMenu) {
            handleInvitesMenuClick(player, event, item, name);
        } else if (isConfirmMenu) {
            handleConfirmMenuClick(player, event, item, name);
        }
    }
    
    private void handleMainMenuClick(Player player, InventoryClickEvent event, ItemStack item, String name) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.closeInventory();
            return;
        }
        
        playClickSound(player);
        Material type = item.getType();
        
        // Identifica pela combinação de material + nome
        if (type == Material.BOOK && !name.contains("Log")) {
            // Informações do clã
            player.closeInventory();
            player.performCommand("clan info");
            
        } else if (type == Material.SKULL_ITEM || name.contains("Membros")) {
            // Lista de membros
            memberMenuPages.put(player.getUniqueId(), 0);
            ClanGUI.openMembersMenu(player, 0);
            
        } else if (type == Material.PAPER || name.contains("Convidar")) {
            // Convidar jogador
            player.closeInventory();
            pendingChatActions.put(player.getUniqueId(), ChatAction.INVITE_PLAYER);
            MessageUtil.send(player, "clan-invite-in-gui");
            
        } else if (type == Material.ENDER_PEARL || name.contains("Base")) {
            // Base do clã
            ClanRole role = clan.getMemberRole(player.getUniqueId());
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                if (role != null && role.canSetBase()) {
                    player.closeInventory();
                    player.performCommand("clan setbase");
                } else {
                    MessageUtil.send(player, "no-permission");
                }
            } else {
                player.closeInventory();
                player.performCommand("clan base");
            }
            
        } else if (type == Material.REDSTONE_COMPARATOR || name.contains("Gerenciar") || name.contains("Cargos")) {
            // Gerenciar cargos
            memberMenuPages.put(player.getUniqueId(), 0);
            ClanGUI.openMembersMenu(player, 0);
            
        } else if (type == Material.BARRIER || name.contains("Sair")) {
            // Sair do clã
            confirmActions.put(player.getUniqueId(), "leave");
            ClanGUI.openConfirmMenu(player, "Sair do Clã", clan.getName());
            
        } else if (type == Material.TNT || name.contains("Deletar")) {
            // Deletar clã
            confirmActions.put(player.getUniqueId(), "delete");
            ClanGUI.openConfirmMenu(player, "Deletar Clã", clan.getName());
        }
    }
    
    private void handleMembersMenuClick(Player player, InventoryClickEvent event, ItemStack item, String name) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.closeInventory();
            return;
        }
        
        playClickSound(player);
        Material type = item.getType();
        int currentPage = memberMenuPages.getOrDefault(player.getUniqueId(), 0);
        
        if (type == Material.ARROW) {
            // Navegação
            if (name.contains("Anterior") || name.contains("«")) {
                memberMenuPages.put(player.getUniqueId(), currentPage - 1);
                ClanGUI.openMembersMenu(player, currentPage - 1);
            } else if (name.contains("Próxima") || name.contains("»")) {
                memberMenuPages.put(player.getUniqueId(), currentPage + 1);
                ClanGUI.openMembersMenu(player, currentPage + 1);
            }
            return;
        }
        
        if (type == Material.BARRIER || name.contains("Voltar")) {
            ClanGUI.openMainMenu(player);
            return;
        }
        
        if (type == Material.SKULL_ITEM) {
            // Clique em um membro
            String memberName = name.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "").trim();
            ClanRole playerRole = clan.getMemberRole(player.getUniqueId());
            if (playerRole == null) return;
            
            // Tenta encontrar o jogador pelo nome
            UUID targetUUID = null;
            for (UUID uuid : clan.getMembers()) {
                String offlineName = Bukkit.getOfflinePlayer(uuid).getName();
                if (offlineName != null && offlineName.equalsIgnoreCase(memberName)) {
                    targetUUID = uuid;
                    break;
                }
            }
            
            if (targetUUID == null) return;
            
            ClanRole targetRole = clan.getMemberRole(targetUUID);
            if (targetRole == null) return;
            
            // Não pode gerenciar a si mesmo
            if (targetUUID.equals(player.getUniqueId())) {
                MessageUtil.send(player, "no-permission");
                return;
            }
            
            // Não pode gerenciar o dono
            if (targetRole == ClanRole.DONO) return;
            
            // Verifica permissão
            if (!playerRole.canManage(targetRole)) {
                MessageUtil.send(player, "no-permission");
                return;
            }
            
            String targetName = Bukkit.getOfflinePlayer(targetUUID).getName();
            
            if (event.getClick() == ClickType.LEFT) {
                // Promover
                if (clan.promoteMember(targetUUID)) {
                    StorageManager.getInstance().saveClans();
                    ClanRole newRole = clan.getMemberRole(targetUUID);
                    Map<String, String> ph = new HashMap<>();
                    ph.put("player", targetName);
                    ph.put("role", newRole.getDisplayName());
                    MessageUtil.send(player, "clan-promote-success", ph);
                    
                    Player target = Bukkit.getPlayer(targetUUID);
                    if (target != null) {
                        MessageUtil.send(target, "clan-promoted", ph);
                    }
                    ClanGUI.openMembersMenu(player, currentPage);
                }
                
            } else if (event.getClick() == ClickType.RIGHT) {
                // Rebaixar
                if (clan.demoteMember(targetUUID)) {
                    StorageManager.getInstance().saveClans();
                    ClanRole newRole = clan.getMemberRole(targetUUID);
                    Map<String, String> ph = new HashMap<>();
                    ph.put("player", targetName);
                    ph.put("role", newRole.getDisplayName());
                    MessageUtil.send(player, "clan-demote-success", ph);
                    
                    Player target = Bukkit.getPlayer(targetUUID);
                    if (target != null) {
                        MessageUtil.send(target, "clan-demoted", ph);
                    }
                    ClanGUI.openMembersMenu(player, currentPage);
                }
                
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                // Expulsar
                confirmActions.put(player.getUniqueId(), "kick:" + targetUUID.toString());
                ClanGUI.openConfirmMenu(player, "Expulsar Membro", targetName);
            }
        }
    }
    
    private void handleInvitesMenuClick(Player player, InventoryClickEvent event, ItemStack item, String name) {
        playClickSound(player);
        
        // Extrai nome do clan removendo cores e a tag
        String cleanName = name.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");
        String clanName = cleanName.split(" \\[")[0].trim();
        
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            // Recusar
            player.closeInventory();
            InviteManager.getInstance().removeInvite(player.getUniqueId(), clanName);
            MessageUtil.send(player, "clan-invite-declined");
        } else {
            // Aceitar
            player.closeInventory();
            player.performCommand("clan aceitar");
        }
    }
    
    private void handleConfirmMenuClick(Player player, InventoryClickEvent event, ItemStack item, String name) {
        String action = confirmActions.remove(player.getUniqueId());
        playClickSound(player);
        
        Material type = item.getType();
        
        // Identifica por material (lã verde = confirmar, lã vermelha = cancelar)
        boolean isConfirm = (type == Material.WOOL && item.getDurability() == 5) || name.contains("Confirmar");
        boolean isCancel = (type == Material.WOOL && item.getDurability() == 14) || name.contains("Cancelar");
        
        if (isConfirm && action != null) {
            player.closeInventory();
            
            if (action.equals("leave")) {
                player.performCommand("clan sair");
                
            } else if (action.equals("delete")) {
                player.performCommand("clan deletar confirmar");
                
            } else if (action.startsWith("kick:")) {
                UUID targetUUID = UUID.fromString(action.substring(5));
                Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
                
                if (clan != null && clan.isMember(targetUUID)) {
                    String targetName = Bukkit.getOfflinePlayer(targetUUID).getName();
                    if (targetName == null) targetName = "Jogador";
                    
                    clan.removeMember(targetUUID);
                    ClanManager.getInstance().removeMemberFromIndex(targetUUID);
                    StorageManager.getInstance().saveClans();
                    
                    Map<String, String> ph = new HashMap<>();
                    ph.put("player", targetName);
                    ph.put("clan", clan.getName());
                    MessageUtil.send(player, "clan-kick-success", ph);
                    
                    Player target = Bukkit.getPlayer(targetUUID);
                    if (target != null) {
                        MessageUtil.send(target, "clan-kicked", ph);
                    }
                }
            }
            
        } else if (isCancel) {
            player.closeInventory();
            ClanGUI.openMainMenu(player);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        ChatAction action = pendingChatActions.remove(uuid);
        if (action == null) return;
        
        event.setCancelled(true);
        String message = event.getMessage().trim();
        
        // Cancelar se digitar "cancelar"
        if (message.equalsIgnoreCase("cancelar") || message.equalsIgnoreCase("cancel")) {
            Bukkit.getScheduler().runTask(VKClans.getInstance(), () -> {
                player.sendMessage("§cAção cancelada.");
            });
            return;
        }
        
        // Executa na main thread
        Bukkit.getScheduler().runTask(VKClans.getInstance(), () -> {
            if (action == ChatAction.INVITE_PLAYER) {
                player.performCommand("clan convidar " + message);
            } else if (action == ChatAction.KICK_PLAYER) {
                player.performCommand("clan kick " + message);
            }
        });
    }
    
    private void playClickSound(Player player) {
        String sound = ConfigManager.getInstance().getSomClicar();
        if (sound == null || sound.equalsIgnoreCase("NONE")) return;
        try {
            player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(sound), 1.0f, 1.0f);
        } catch (Exception ignored) {}
    }
    
    private void handleClanCreationClick(Player player, InventoryClickEvent event, ItemStack item, String name) {
        playClickSound(player);
        Material type = item.getType();
        int slot = event.getSlot();
        
        ClanCreationListener listener = ClanCreationListener.getInstance();
        
        if (slot == 2) { // Definir Nome
            player.closeInventory();
            listener.startNameCapture(player);
        } else if (slot == 4) { // Definir Tag
            player.closeInventory();
            listener.startTagCapture(player);
        } else if (slot == 6) { // Escolher Cor
            ClanGUI.openColorSelectionMenu(player);
        } else if (slot == 22) { // Confirmar
            if (listener.hasAllData(player.getUniqueId())) {
                listener.createClan(player);
            } else {
                player.sendMessage("§cFaltam configurações: " + 
                    (listener.getPendingName(player.getUniqueId()) == null ? "Nome " : "") +
                    (listener.getPendingTag(player.getUniqueId()) == null ? "Tag " : "") +
                    (listener.getPendingColor(player.getUniqueId()) == null ? "Cor " : ""));
            }
        } else if (slot == 26) { // Cancelar
            listener.clearPendingData(player.getUniqueId());
            player.closeInventory();
            player.sendMessage("§cCriação de clã cancelada.");
        }
    }
    
    private void handleColorSelectionClick(Player player, InventoryClickEvent event, ItemStack item, String name) {
        playClickSound(player);
        int slot = event.getSlot();
        
        if (slot == 26) { // Voltar
            ClanGUI.openClanCreationMenu(player);
            return;
        }
        
        String color = null;
        if (slot >= 10 && slot <= 16) {
            switch (slot) {
                case 10: color = "&c"; break;
                case 11: color = "&6"; break;
                case 12: color = "&e"; break;
                case 13: color = "&a"; break;
                case 14: color = "&b"; break;
                case 15: color = "&d"; break;
                case 16: color = "&f"; break;
            }
        }
        
        if (color != null) {
            ClanCreationListener.getInstance().setPendingColor(player.getUniqueId(), color);
            ClanGUI.openClanCreationMenu(player);
        }
    }
}
