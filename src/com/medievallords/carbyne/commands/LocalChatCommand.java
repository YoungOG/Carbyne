package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

/**
 * Created by Williams on 2017-08-08
 * for the Carbyne project.
 */
public class LocalChatCommand extends BaseCommand {


    @Command(name = "localchat", aliases = {"lc"}, inGameOnly = true)
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        Profile profile;

        if ((profile = getProfileManager().getProfile(player.getUniqueId())) == null) {
            MessageManager.sendMessage(player, "&cThere was an error");
            return;
        }

        profile.setProfileChatChannel((profile.getProfileChatChannel() != Profile.ProfileChatChannel.LOCAL ? Profile.ProfileChatChannel.LOCAL : Profile.ProfileChatChannel.GLOBAL));
        MessageManager.sendMessage(player, "&bLocal chat toggled " + (profile.getProfileChatChannel() == Profile.ProfileChatChannel.LOCAL ? "&aon" : "&coff"));
    }
}
