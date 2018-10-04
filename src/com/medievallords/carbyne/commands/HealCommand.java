package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerHealth;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class HealCommand extends BaseCommand {

    @Command(name = "heal", permission = "carbyne.command.heal", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());

            player.setFoodLevel(20);
            player.setFireTicks(0);
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
            playerHealth.setHealth(playerHealth.getMaxHealth(), player);
            MessageManager.sendMessage(player, "&aYou have been healed.");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageManager.sendMessage(sender, "&cThat player could not be found.");
                return;
            }

            for (PotionEffect potionEffect : target.getActivePotionEffects())
                target.removePotionEffect(potionEffect.getType());

            target.setFoodLevel(20);
            target.setFireTicks(0);
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(target.getUniqueId());
            playerHealth.setHealth(playerHealth.getMaxHealth(), target);
            MessageManager.sendMessage(target, "&aYou have been healed.");
            MessageManager.sendMessage(sender, "&aYou have healed &5" + target.getName() + "&a.");
        } else
            MessageManager.sendMessage(sender, "&cUsage: /heal <player>");
    }
}
