package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-03-12
 * for the Carbyne project.
 */
public class PartyInviteCommand extends BaseCommand{

    @Command(name = "party.invite", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(player, "&cThat player could not be found.");
            return;
        }

        Party party = getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            MessageManager.sendMessage(player, "&cYou are not in a party.");
            return;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cOnly the leader can invite players to the party.");
            return;
        }

        if (getPartyManager().getParty(target.getUniqueId()) != null) {
            MessageManager.sendMessage(player, "&cThat player is already in party.");
            return;
        }

        if (party.getInvitedPlayers().contains(target.getUniqueId())) {
            MessageManager.sendMessage(player, "&cYou have already invited this player.");
            return;
        }

        party.getInvitedPlayers().add(target.getUniqueId());

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&b" + player.getName() + " &ahas invited you to join their party!\n&6Click Here &eto accept! This expires in &660 &eseconds."))
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick this to run\n&b/party join " + player.getName()))
                .runCommand("/party join " + player.getName())
                .send(target);

        MessageManager.sendMessage(player, "&aYou have invited &b" + target.getName() + " &ato join the party.");

        party.sendMembersMessage("&b" + player.getName() + " &ahas invited &b" + target.getName() + " &ato join the party.");

        //TODO: Make invitation last for 60 seconds.
    }
}
