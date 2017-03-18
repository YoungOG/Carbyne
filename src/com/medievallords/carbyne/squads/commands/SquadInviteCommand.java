package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
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
public class SquadInviteCommand extends BaseCommand{

    @Command(name = "squad.invite", inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length != 1) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(player, "&cThat player could not be found.");
            return;
        }

        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        if (!squad.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cOnly the leader can invite players to the squad.");
            return;
        }

        if (getSquadManager().getSquad(target.getUniqueId()) != null) {
            MessageManager.sendMessage(player, "&cThat player is already in a squad.");
            return;
        }

        if (squad.getInvitedPlayers().contains(target.getUniqueId())) {
            MessageManager.sendMessage(player, "&cYou have already invited this player.");
            return;
        }

        squad.getInvitedPlayers().add(target.getUniqueId());

        JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&b" + player.getName() + " &ahas invited you to join their squad!\n&6Click Here &eto accept! This expires in &660 &eseconds."))
                .tooltip(ChatColor.translateAlternateColorCodes('&', "&aClick this to run\n&b/squad join " + player.getName()))
                .runCommand("/squad join " + player.getName())
                .send(target);

        MessageManager.sendMessage(player, "&aYou have invited &b" + target.getName() + " &ato join the squad.");

        squad.sendMembersMessage("&b" + player.getName() + " &ahas invited &b" + target.getName() + " &ato join the squad.");

        //TODO: Make invitation last for 60 seconds.
    }
}
