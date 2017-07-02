package com.medievallords.carbyne.bounty;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Dalton on 6/28/2017.
 */
public class BountyCommand extends BaseCommand
{

    private BountyManager bountyManager = Carbyne.getInstance().getBountyManager();

    @Command(name = "bounty")
    public void onCommand(CommandArgs cmdArgs)
    {
        String[] args = cmdArgs.getArgs();
        Player sender = cmdArgs.getPlayer();

        if(args.length == 0)
        {
            if(bountyManager.getBounties().size() <= 0)
            {
                MessageManager.sendMessage(sender, "&cThere are no bounties currently!");
                return;
            }

            MessageManager.sendMessage(sender, "&4&lWANTED DEAD");
            for(UUID key : bountyManager.getBounties().keySet())
            {
                Double bounty = bountyManager.getBounties().get(key);
                Player hunted = Bukkit.getPlayer(key);
                MessageManager.sendMessage(sender, "&c" + hunted.getName() + " &f| " + "&2" + bounty + " &f| ");
            }
        }

        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("put")) {
                if(args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        MessageManager.sendMessage(sender, "&cPlayer not found!");
                        return;
                    }

                    double bounty;
                    try {
                        bounty = new Double(args[2]);
                    } catch (Exception ex) {
                        return;
                    }

                    if(bounty <= 0)
                    {
                        MessageManager.sendMessage(sender, "&cBounty value must be greater than zero!");
                        return;
                    }

                    if (!getMarketManager().withdraw(sender.getUniqueId(), bounty))
                    {
                        MessageManager.sendMessage(sender, "&cYou do not have that much credits!");
                        return;
                    }

                    if(bountyManager.getBounties().containsKey(player.getUniqueId()))
                        bountyManager.getBounties().put(player.getUniqueId(), bountyManager.getBounties().get(player.getUniqueId()) + bounty);
                    else
                        bountyManager.getBounties().put(player.getUniqueId(), bounty);

                    MessageManager.sendMessage(sender, "&2Successfully placed bounty on " + player.getName() + ". The new bounty on " + player.getName() + " is " + bountyManager.getBounties().get(player.getUniqueId()) + ".");
                    return;
                }
                else {
                    MessageManager.sendMessage(sender, "&c/bounty put name amount");
                }
            }
            else {
                Player player = Bukkit.getPlayer(args[0]);
                if(player == null) {
                    MessageManager.sendMessage(sender, "&cPlayer not found!");
                    return;
                }
                if (bountyManager.getBounties().containsKey(player.getUniqueId()))
                    MessageManager.sendMessage(sender, "&c" + player.getName() + " is wanted dead for " + bountyManager.getBounties().get(player.getUniqueId()) + "!");
                else
                    MessageManager.sendMessage(sender, "&cThis player does not have a bounty!");
                return;
            }
        }
    }

}
