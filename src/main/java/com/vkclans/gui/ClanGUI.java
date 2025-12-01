package com.vkclans.gui;

import com.vkclans.manager.ClanManager;
import com.vkclans.manager.ConfigManager;
import com.vkclans.manager.InviteManager;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanRole;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Sistema de menus GUI do clan
 */
public class ClanGUI {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Abre o menu principal do clan
     */
    public static void openMainMenu(Player player) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        
        if (clan == null) {
            // Verifica se tem convites pendentes
            if (InviteManager.getInstance().hasAnyInvite(player.getUniqueId())) {
                openInvitesMenu(player);
            } else {
                MessageUtil.send(player, "clan-not-in-clan");
            }
            return;
        }
        
        ClanRole playerRole = clan.getMemberRole(player.getUniqueId());
        String title = MessageUtil.get("gui-main-title", null).replace("{clan}", clan.getName());
        Inventory inv = Bukkit.createInventory(null, 27, title);
        
        // Decoração
        fillBorder(inv);
        
        // Informações do Clan (slot 10)
        inv.setItem(10, createInfoItem(clan));
        
        // Membros (slot 12)
        inv.setItem(12, createItem(Material.SKULL_ITEM, (short) 3, 
            "§b§lMembros do Clã", 
            Arrays.asList(
                "§7Total: §f" + clan.getMembers().size(),
                "",
                "§eClique para ver a lista"
            )));
        
        // Convidar (slot 14) - só aparece se tiver permissão
        if (playerRole != null && playerRole.canInvite()) {
            inv.setItem(14, createItem(Material.PAPER, (short) 0,
                "§a§lConvidar Jogador",
                Arrays.asList(
                    "§7Convide novos membros",
                    "§7para o clã.",
                    "",
                    "§eClique para convidar"
                )));
        }
        
        // Base (slot 16)
        List<String> baseLore = new ArrayList<>();
        if (clan.getBase() != null) {
            baseLore.add("§7Base: §aDefinida");
            baseLore.add("");
            baseLore.add("§eClique para teleportar");
            if (playerRole != null && playerRole.canSetBase()) {
                baseLore.add("§eShift+Clique para redefinir");
            }
        } else {
            baseLore.add("§7Base: §cNão definida");
            if (playerRole != null && playerRole.canSetBase()) {
                baseLore.add("");
                baseLore.add("§eClique para definir");
            }
        }
        inv.setItem(16, createItem(Material.ENDER_PEARL, (short) 0, "§d§lBase do Clã", baseLore));
        
        // Configurações (slot 22) - só para líderes
        if (playerRole != null && playerRole.canPromote()) {
            inv.setItem(22, createItem(Material.REDSTONE_COMPARATOR, (short) 0,
                "§6§lGerenciar Cargos",
                Arrays.asList(
                    "§7Promova ou rebaixe",
                    "§7membros do clã.",
                    "",
                    "§eClique para gerenciar"
                )));
        }
        
        // Sair do Clan (slot 26)
        if (playerRole != ClanRole.DONO) {
            inv.setItem(26, createItem(Material.BARRIER, (short) 0,
                "§c§lSair do Clã",
                Arrays.asList(
                    "§7Clique para sair",
                    "§7do clã.",
                    "",
                    "§c⚠ Ação irreversível"
                )));
        } else {
            inv.setItem(26, createItem(Material.TNT, (short) 0,
                "§c§lDeletar Clã",
                Arrays.asList(
                    "§7Clique para deletar",
                    "§7o clã permanentemente.",
                    "",
                    "§c⚠ Ação irreversível"
                )));
        }
        
        player.openInventory(inv);
        playSound(player, ConfigManager.getInstance().getSomAbrir());
    }
    
    /**
     * Abre o menu de membros com paginação
     */
    public static void openMembersMenu(Player player, int page) {
        Clan clan = ClanManager.getInstance().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            MessageUtil.send(player, "clan-not-in-clan");
            return;
        }
        
        ClanRole playerRole = clan.getMemberRole(player.getUniqueId());
        List<UUID> members = new ArrayList<>(clan.getMembers());
        
        // Ordena por cargo
        members.sort((a, b) -> {
            ClanRole roleA = clan.getMemberRole(a);
            ClanRole roleB = clan.getMemberRole(b);
            return Integer.compare(roleB.getLevel(), roleA.getLevel());
        });
        
        int perPage = ConfigManager.getInstance().getMembrosPorPagina();
        int totalPages = (int) Math.ceil((double) members.size() / perPage);
        page = Math.max(0, Math.min(page, totalPages - 1));
        
        String title = MessageUtil.get("gui-members-title", null) + " §7(" + (page + 1) + "/" + totalPages + ")";
        Inventory inv = Bukkit.createInventory(null, 36, title);
        
        // Adiciona membros
        int startIndex = page * perPage;
        int endIndex = Math.min(startIndex + perPage, members.size());
        int slot = 0;
        
        for (int i = startIndex; i < endIndex && slot < 27; i++) {
            UUID memberUUID = members.get(i);
            ClanRole role = clan.getMemberRole(memberUUID);
            Player memberPlayer = Bukkit.getPlayer(memberUUID);
            String memberName = memberPlayer != null ? memberPlayer.getName() : Bukkit.getOfflinePlayer(memberUUID).getName();
            boolean isOnline = memberPlayer != null && memberPlayer.isOnline();
            
            ItemStack skull = createSkull(memberName, role, isOnline, playerRole, clan.getLeader().equals(memberUUID));
            inv.setItem(slot++, skull);
        }
        
        // Navegação
        if (page > 0) {
            inv.setItem(27, createItem(Material.ARROW, (short) 0, "§e« Página Anterior", Collections.singletonList("§7Clique para voltar")));
        }
        
        inv.setItem(31, createItem(Material.BARRIER, (short) 0, "§cVoltar", Collections.singletonList("§7Voltar ao menu principal")));
        
        if (page < totalPages - 1) {
            inv.setItem(35, createItem(Material.ARROW, (short) 0, "§ePróxima Página »", Collections.singletonList("§7Clique para avançar")));
        }
        
        player.openInventory(inv);
    }
    
    /**
     * Abre o menu de convites pendentes
     */
    public static void openInvitesMenu(Player player) {
        Set<String> invites = InviteManager.getInstance().getInvites(player.getUniqueId());
        if (invites == null || invites.isEmpty()) {
            MessageUtil.send(player, "clan-invite-none");
            return;
        }
        
        String title = MessageUtil.get("gui-invite-title", null);
        int size = Math.min(54, ((invites.size() / 9) + 1) * 9);
        size = Math.max(9, size);
        Inventory inv = Bukkit.createInventory(null, size, title);
        
        int slot = 0;
        for (String clanName : invites) {
            Clan clan = ClanManager.getInstance().getClanByName(clanName);
            if (clan != null && slot < size) {
                inv.setItem(slot++, createItem(Material.BOOK, (short) 0,
                    "§a" + clan.getName() + " §7[" + clan.getTag() + "]",
                    Arrays.asList(
                        "§7Membros: §f" + clan.getMembers().size(),
                        "§7Pontos: §d" + clan.getPoints(),
                        "",
                        "§aClique para aceitar",
                        "§cShift+Clique para recusar"
                    )));
            }
        }
        
        player.openInventory(inv);
    }
    
    /**
     * Abre o menu de confirmação
     */
    public static void openConfirmMenu(Player player, String action, String target) {
        String title = MessageUtil.get("gui-confirm-title", null);
        Inventory inv = Bukkit.createInventory(null, 27, title);
        
        fillBorder(inv);
        
        // Info
        inv.setItem(4, createItem(Material.PAPER, (short) 0,
            "§e" + action,
            Arrays.asList("§7Alvo: §f" + target)));
        
        // Confirmar
        inv.setItem(11, createItem(Material.WOOL, (short) 5,
            "§a§lConfirmar",
            Collections.singletonList("§7Clique para confirmar")));
        
        // Cancelar
        inv.setItem(15, createItem(Material.WOOL, (short) 14,
            "§c§lCancelar",
            Collections.singletonList("§7Clique para cancelar")));
        
        player.openInventory(inv);
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private static ItemStack createInfoItem(Clan clan) {
        List<String> lore = new ArrayList<>();
        lore.add("§7Tag: §f[" + clan.getTag() + "]");
        lore.add("");
        
        Player leader = Bukkit.getPlayer(clan.getLeader());
        String leaderName = leader != null ? leader.getName() : Bukkit.getOfflinePlayer(clan.getLeader()).getName();
        lore.add("§7Dono: §f" + leaderName);
        lore.add("§7Membros: §f" + clan.getMembers().size());
        lore.add("§7Criado em: §f" + DATE_FORMAT.format(new Date(clan.getCreationDate())));
        lore.add("");
        lore.add("§7Kills: §a" + clan.getKills());
        lore.add("§7Deaths: §c" + clan.getDeaths());
        lore.add("§7KDR: §b" + String.format("%.2f", clan.getKDR()));
        lore.add("§7Pontos: §d" + clan.getPoints());
        
        return createItem(Material.BOOK, (short) 0, "§e§l" + clan.getName(), lore);
    }
    
    private static ItemStack createSkull(String playerName, ClanRole role, boolean online, ClanRole viewerRole, boolean isLeader) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(playerName);
        meta.setDisplayName(role.getColor() + playerName);
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Cargo: " + role.getColoredName());
        lore.add("§7Status: " + (online ? "§aOnline" : "§cOffline"));
        lore.add("");
        
        if (!isLeader && viewerRole != null && viewerRole.canManage(role)) {
            lore.add("§eClique esquerdo: §7Promover");
            lore.add("§eClique direito: §7Rebaixar");
            lore.add("§cShift+Clique: §7Expulsar");
        }
        
        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }
    
    private static ItemStack createItem(Material material, short data, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private static void fillBorder(Inventory inv) {
        ItemStack glass = createItem(Material.STAINED_GLASS_PANE, (short) 15, " ", null);
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25};
        for (int slot : borderSlots) {
            if (slot < inv.getSize()) {
                inv.setItem(slot, glass);
            }
        }
    }
    
    private static void playSound(Player player, String sound) {
        if (sound == null || sound.equalsIgnoreCase("NONE")) return;
        try {
            player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(sound), 1.0f, 1.0f);
        } catch (Exception ignored) {}
    }
}
