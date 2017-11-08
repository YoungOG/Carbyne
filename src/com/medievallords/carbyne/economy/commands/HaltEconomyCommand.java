package com.mccritz.kmain.economy.commands;

import com.mccritz.kmain.economy.EconomyManager;
import com.mccritz.kmain.kMain;
import com.mccritz.kmain.utils.MessageManager;
import com.mccritz.kmain.utils.PlayerUtility;
import com.mccritz.kmain.utils.command.BaseCommand;
import com.mccritz.kmain.utils.command.CommandUsageBy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HaltEconomyCommand extends BaseCommand {

    private kMain main = kMain.getInstance();
    private EconomyManager economyManager = main.getEconomyManager();

    public HaltEconomyCommand() {
        super("halteconomy", "kmain.halteconomy", CommandUsageBy.ANYONE, "halteco");
        setUsage("&c/<command>");
        setMinArgs(0);
        setMaxArgs(0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (economyManager.isEconomyHalted()) {
            economyManager.setEconomyHalted(false);

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("kmain.halteconomy")) {
                    MessageManager.message(all, "&7The economy has been resumed by &c" + sender.getName() + "&7.");
                }
            }
        } else {
            economyManager.setEconomyHalted(true);

            for (Player all : PlayerUtility.getOnlinePlayers()) {
                if (all.hasPermission("kmain.halteconomy")) {
                    MessageManager.message(all, "&7The economy has been halted by &c" + sender.getName() + "&7.");
                }
            }
        }
    }
}
