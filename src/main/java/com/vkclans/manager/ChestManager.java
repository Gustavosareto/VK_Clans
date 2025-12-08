package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanLog;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Gerencia baú compartilhado dos clãs (Singleton)
 */
public class ChestManager {
    private static ChestManager instance;
    private final Map<String, ItemStack[]> clanChests = new HashMap<>(); // clanName -> contents
    private final File file;
    private YamlConfiguration config;
    private static final int CHEST_SIZE = 54; // Baú grande

    private ChestManager() {
        this.file = new File(VKClans.getInstance().getDataFolder(), "chests.yml");
        loadChests();
    }

    public static ChestManager getInstance() {
        if (instance == null) instance = new ChestManager();
        return instance;
    }

    /**
     * Abre o baú do clã para um jogador
     */
    public void openChest(Player player, Clan clan) {
        String title = "§6Baú do Clã " + clan.getName();
        Inventory inv = Bukkit.createInventory(null, CHEST_SIZE, title);
        
        ItemStack[] contents = clanChests.get(clan.getName());
        if (contents != null) {
            inv.setContents(contents);
        }

        player.openInventory(inv);
    }

    /**
     * Salva o conteúdo do baú
     */
    public void saveChestContents(String clanName, ItemStack[] contents) {
        clanChests.put(clanName, contents);
        saveChests();
    }

    /**
     * Obtém o conteúdo do baú
     */
    public ItemStack[] getChestContents(String clanName) {
        return clanChests.getOrDefault(clanName, new ItemStack[CHEST_SIZE]);
    }

    /**
     * Registra adição de item no log
     */
    public void logItemAdd(Player player, Clan clan, ItemStack item) {
        String itemDesc = formatItem(item);
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.CHEST_ADD, itemDesc);
    }

    /**
     * Registra remoção de item no log
     */
    public void logItemRemove(Player player, Clan clan, ItemStack item) {
        String itemDesc = formatItem(item);
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.CHEST_REMOVE, itemDesc);
    }

    private String formatItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return "Nenhum";
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName() 
            ? item.getItemMeta().getDisplayName() 
            : item.getType().name();
        return item.getAmount() + "x " + name;
    }

    /**
     * Remove baú do clã (quando clã é deletado)
     */
    public void removeChest(String clanName) {
        clanChests.remove(clanName);
        saveChests();
    }

    public void loadChests() {
        if (!file.exists()) return;
        config = YamlConfiguration.loadConfiguration(file);

        for (String clanName : config.getKeys(false)) {
            List<?> itemList = config.getList(clanName);
            if (itemList != null) {
                ItemStack[] contents = new ItemStack[CHEST_SIZE];
                for (int i = 0; i < Math.min(itemList.size(), CHEST_SIZE); i++) {
                    Object obj = itemList.get(i);
                    if (obj instanceof ItemStack) {
                        contents[i] = (ItemStack) obj;
                    }
                }
                clanChests.put(clanName, contents);
            }
        }
    }

    public void saveChests() {
        config = new YamlConfiguration();
        for (Map.Entry<String, ItemStack[]> entry : clanChests.entrySet()) {
            config.set(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        try {
            config.save(file);
        } catch (IOException e) {
            VKClans.getInstance().getLogger().severe("Erro ao salvar chests.yml: " + e.getMessage());
        }
    }
}
