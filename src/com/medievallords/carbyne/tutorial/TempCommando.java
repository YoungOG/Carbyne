package com.medievallords.carbyne.tutorial;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class TempCommando extends BaseCommand {

    @Command(name = "tut")
    public void a(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        getCarbyne().getTutorialManager().startTutorial(player);
    }
}
