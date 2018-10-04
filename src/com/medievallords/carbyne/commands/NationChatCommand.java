package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.entity.Player;

/**
 * Created by Dalton on 8/29/2017.
 */
public class NationChatCommand extends BaseCommand {

    @Command(name = "nationchat", aliases = {"nc"}, inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

            if (!resident.hasNation()) {
                MessageManager.sendMessage(player, "&cYou must be in a nation to use this command.");
                return;
            }
        } catch (NotRegisteredException ignored) {
            MessageManager.sendMessage(player, "&cAn error occurred. Please contact an administrator.");
            return;
        }

        profile.setProfileChatChannel((profile.getProfileChatChannel() != Profile.ProfileChatChannel.NATION ? Profile.ProfileChatChannel.NATION : Profile.ProfileChatChannel.GLOBAL));
        MessageManager.sendMessage(player, "&bNation chat toggled " + (profile.getProfileChatChannel() == Profile.ProfileChatChannel.NATION ? "&aon" : "&coff"));
    }

}
