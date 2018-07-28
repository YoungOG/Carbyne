package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.listeners.CooldownListeners;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

/**
 * Created by William on 7/11/2017.
 */
public class SetDamageCommand extends BaseCommand implements Listener {

    private HashMap<Player, Double> damage = new HashMap<>();

    @Command(name = "setdamage", aliases = {"sda"}, permission = "carbyne.commands.damage", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("off")) {
                damage.remove(player);
                MessageManager.sendMessage(player, "&aDamage off");
                return;
            }

            try {
                damage.put(player, Double.parseDouble(args[0]));
                MessageManager.sendMessage(player, "&aDamage has been set to " + damage.get(player));
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cDamage must be a number");
            }
        } else {
            MessageManager.sendMessage(player, "&cUsage /setdamage <damage>");
        }
    }

    @Command(name = "setpotioncooldown", permission = "carbyne.administrator")
    public void onComma(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();

        if (args.length == 1) {
            double cooldown = 15d;
            try {
                cooldown = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(commandArgs.getSender(), "&cYou need to specify a number (double).");
                return;
            }

            CooldownListeners.POTION_COOLDOWN = cooldown;
            MessageManager.sendMessage(commandArgs.getSender(), "&cThe cooldown is now: &7" + CooldownListeners.POTION_COOLDOWN);
        } else {
            MessageManager.sendMessage(commandArgs.getSender(), "&cThe cooldown is currently: &7" + CooldownListeners.POTION_COOLDOWN);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (damage.containsKey(player)) {
                double damageAmount = damage.get(player);
                event.setDamage(damageAmount);
            }
        }
    }
}