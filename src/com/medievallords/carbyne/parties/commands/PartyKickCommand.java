package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-13.
 * for the Carbyne project.
 */
public class PartyKickCommand extends BaseCommand {

    @Command(name = "party.kick", inGameOnly = true, aliases = {"k"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Party party = getPartyManager().getParty(player.getUniqueId());

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        if (party == null) {
            MessageManager.sendMessage(player, "&cYou are not in a party.");
            return;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cOnly the leader can kick players.");
            return;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(player, "&cCould not find that player.");
            return;
        }

        if (target.equals(player)) {
            MessageManager.sendMessage(player, "&cYou cannot kick yourself. Use /party leave.");
            return;
        }

        if (getPartyManager().getParty(target.getUniqueId()) == null) {
            MessageManager.sendMessage(player, "&cThat player is not in your party.");
            return;
        }

        if (!getPartyManager().getParty(target.getUniqueId()).getUniqueId().equals(party.getUniqueId())) {
            MessageManager.sendMessage(player, "&cThat player is not in your party.");
            return;
        }

        party.getMembers().remove(target.getUniqueId());

        MessageManager.sendMessage(target, "&cYou have been kicked from the party.");

        party.sendAllMembersMessage("&b" + target.getName() + " &chas been kicked from the party.");
    }
}
