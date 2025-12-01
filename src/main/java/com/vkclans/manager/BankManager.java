package com.vkclans.manager;

import com.vkclans.VKClans;
import com.vkclans.model.Clan;
import com.vkclans.model.ClanLog;
import com.vkclans.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Gerencia o banco de cada clã (Singleton)
 * Integra com Vault se disponível
 */
public class BankManager {
    private static BankManager instance;
    private net.milkbowl.vault.economy.Economy economy;
    private boolean vaultEnabled = false;

    private BankManager() {
        setupVault();
    }

    public static BankManager getInstance() {
        if (instance == null) instance = new BankManager();
        return instance;
    }

    private void setupVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            VKClans.getInstance().getLogger().info("Vault não encontrado. Sistema de banco desabilitado.");
            return;
        }

        org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = 
            Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        
        if (rsp == null) {
            VKClans.getInstance().getLogger().warning("Nenhum plugin de economia encontrado!");
            return;
        }

        economy = rsp.getProvider();
        vaultEnabled = true;
        VKClans.getInstance().getLogger().info("Vault integrado com sucesso!");
    }

    public boolean isEnabled() {
        return vaultEnabled;
    }

    /**
     * Deposita dinheiro no banco do clã
     */
    public boolean deposit(Player player, Clan clan, double amount) {
        if (!vaultEnabled) return false;
        if (amount <= 0) return false;

        if (!economy.has(player, amount)) {
            MessageUtil.send(player, "bank-insufficient-funds");
            return false;
        }

        economy.withdrawPlayer(player, amount);
        clan.addBankBalance(amount);

        Map<String, String> ph = new HashMap<>();
        ph.put("amount", formatMoney(amount));
        ph.put("balance", formatMoney(clan.getBankBalance()));
        ph.put("player", player.getName());
        
        MessageUtil.send(player, "bank-deposit-success", ph);

        // Registra log
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.BANK_DEPOSIT, formatMoney(amount));

        // Missão de depositar dinheiro
        MissionManager.getInstance().addProgress(clan.getName(),
            com.vkclans.model.ClanMission.MissionType.DEPOSIT_MONEY, (int) amount);

        StorageManager.getInstance().saveClans();
        return true;
    }

    /**
     * Saca dinheiro do banco do clã
     */
    public boolean withdraw(Player player, Clan clan, double amount) {
        if (!vaultEnabled) return false;
        if (amount <= 0) return false;

        if (clan.getBankBalance() < amount) {
            MessageUtil.send(player, "bank-insufficient-clan-funds");
            return false;
        }

        clan.removeBankBalance(amount);
        economy.depositPlayer(player, amount);

        Map<String, String> ph = new HashMap<>();
        ph.put("amount", formatMoney(amount));
        ph.put("balance", formatMoney(clan.getBankBalance()));
        ph.put("player", player.getName());
        
        MessageUtil.send(player, "bank-withdraw-success", ph);

        // Registra log
        LogManager.getInstance().log(clan.getName(), player.getUniqueId(), player.getName(),
            ClanLog.LogType.BANK_WITHDRAW, formatMoney(amount));

        StorageManager.getInstance().saveClans();
        return true;
    }

    /**
     * Obtém o saldo do banco do clã
     */
    public double getBalance(Clan clan) {
        return clan.getBankBalance();
    }
    
    /**
     * Obtém o saldo do banco do clã pelo nome
     */
    public double getBalance(String clanName) {
        Clan clan = ClanManager.getInstance().getClanByName(clanName);
        return clan != null ? clan.getBankBalance() : 0;
    }
    
    /**
     * Deposita dinheiro diretamente no banco (sem jogador/economia)
     */
    public void deposit(String clanName, double amount) {
        Clan clan = ClanManager.getInstance().getClanByName(clanName);
        if (clan != null && amount > 0) {
            clan.addBankBalance(amount);
            StorageManager.getInstance().saveClans();
        }
    }

    /**
     * Formata valor monetário (1000 -> 1K, 1000000 -> 1M)
     */
    public String formatMoney(double amount) {
        return formatMoneyShort(amount);
    }
    
    /**
     * Formata valor monetário de forma curta (1K, 1M, 1B)
     */
    public String formatMoneyShort(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("$%.2fB", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("$%.2fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("$%.2fK", amount / 1_000);
        } else {
            return String.format("$%.2f", amount);
        }
    }
    
    /**
     * Formata valor monetário de forma completa
     */
    public String formatMoneyFull(double amount) {
        if (vaultEnabled && economy != null) {
            return economy.format(amount);
        }
        return String.format("$%,.2f", amount);
    }

    /**
     * Obtém economia do Vault
     */
    public net.milkbowl.vault.economy.Economy getEconomy() {
        return economy;
    }
}
