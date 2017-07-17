package com.medievallords.carbyne.events.implementations.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.implementations.Race;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Dalton on 7/10/2017.
 */
public class RaceCommands extends BaseCommand {

    private Race race;

    public RaceCommands(Race race) {
        this.race = race;
    }

    @Command(name = "race")
    public void onCommand(CommandArgs cmd) {
        if (!race.isActive()) {
            MessageManager.sendMessage(cmd.getPlayer(), "&cThis event is not active!");
        }

        Player player = cmd.getPlayer();
        String[] args = cmd.getArgs();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                if (player.getInventory().firstEmpty() != 0) {
                    MessageManager.sendMessage(player, "&cYou need an empty inventory to join!");
                    return;
                }

                ItemStack[] armor = player.getInventory().getArmorContents();
                for (int i = 0; i < armor.length; i++)
                    if (armor[i].getType() != Material.AIR) {
                        MessageManager.sendMessage(player, "&cYou need an empty inventory to join!");
                        return;
                    }

                BukkitRunnable telTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        race.getWaitingTasks().remove(player);
                        player.getInventory().clear();
                        player.getActivePotionEffects().clear();
                        player.teleport(race.getEventSpawn());
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setFlying(false);
                        race.addPlayerToEvent(player);
                    }
                };
                MessageManager.sendMessage(player, "&cYou will be teleported to the event in 10 seconds!");
                race.getWaitingTasks().put(player, telTask);
                telTask.runTaskLater(Carbyne.getInstance(), 200L);
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (race.getParticipants().contains(player)) {
                    player.getInventory().clear();
                    race.removePlayerFromEvent(player);
                    player.teleport(race.getSpawn());
                }
            }
        }
    }

}
