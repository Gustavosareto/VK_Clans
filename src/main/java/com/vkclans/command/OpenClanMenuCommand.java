package com.vkclans.command;

import com.vkclans.gui.ClanGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando para abrir o menu GUI do clã
 */
public class OpenClanMenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) {
                sender.sendMessage("Apenas jogadores podem usar este comando.");
            }
            return true;
        }
        Player player = (Player) sender;
        ClanGUI.openMainMenu(player);
        return true;
    }
}
