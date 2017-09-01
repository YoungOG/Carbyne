package com.medievallords.carbyne.events.implementations.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.events.implementations.CliffClimb;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Dalton on 7/5/2017.
 */
public class CliffClimbCommands extends BaseCommand
{

    private CliffClimb cliffClimb;

    public CliffClimbCommands(CliffClimb cliffClimb)
    {
        this.cliffClimb = cliffClimb;
    }

    @Command(name="cliffclimb")
    public void onCommand(CommandArgs cmd)
    {
        Player player = cmd.getPlayer();
        String[] args = cmd.getArgs();

        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("join"))
            {
                Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
                if (profile.getActiveEvent() != null) {
                    MessageManager.sendMessage(player, "&cYou are already in an event!");
                    return;
                }
                if (!PlayerUtility.isInventoryEmpty(player))
                {
                    MessageManager.sendMessage(player, "&cYou need an empty inventory to join!");
                    return;
                }

                ItemStack[] armor = player.getInventory().getArmorContents();
                for(int i = 0; i < armor.length; i++)
                    if(armor[i].getType() != Material.AIR)
                    {
                        MessageManager.sendMessage(player, "&cYou need an empty inventory to join!");
                        return;
                    }

                BukkitRunnable telTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        cliffClimb.getWaitingTasks().remove(player);
                        player.getInventory().clear();
                        for (PotionEffect e : player.getActivePotionEffects()) player.removePotionEffect(e.getType());
                        player.teleport(cliffClimb.getCliffClimbSpawn());
                        player.setGameMode(GameMode.SURVIVAL);
                        player.setFlying(false);
                        player.setAllowFlight(true);
                        cliffClimb.addPlayerToEvent(player);
                    }
                };
                MessageManager.sendMessage(player, "&cYou will be teleported to the event in 10 seconds!");
                cliffClimb.getWaitingTasks().put(player, telTask);
                telTask.runTaskLater(Carbyne.getInstance(), 200L);
            }
            else if(args[0].equalsIgnoreCase("leave"))
            {
                if(cliffClimb.getParticipants().contains(player))
                {
                    player.getInventory().clear();
                    cliffClimb.removePlayerFromEvent(player);
                    player.teleport(cliffClimb.getSpawn());
                }
            }
        }
    }

}
