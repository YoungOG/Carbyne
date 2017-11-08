package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Dalton on 8/28/2017.
 */
public class TownChatCommand extends BaseCommand {

    @Command(name = "townchat", aliases = {"tc"}, inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        Profile profile;

        if ((profile = getProfileManager().getProfile(player.getUniqueId())) == null) {
            MessageManager.sendMessage(player, "&cThere was an error");
            return;
        }

        profile.setProfileChatChannel((profile.getProfileChatChannel() != Profile.ProfileChatChannel.LOCAL ? Profile.ProfileChatChannel.LOCAL : Profile.ProfileChatChannel.GLOBAL));
        MessageManager.sendMessage(player, "&bTown chat toggled " + (profile.getProfileChatChannel() == Profile.ProfileChatChannel.LOCAL ? "&aon" : "&coff"));
    }

}
