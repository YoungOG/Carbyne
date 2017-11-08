package com.medievallords.carbyne.spellmenu;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class SpellMenuCommand extends BaseCommand {

    @Command(name = "spells", aliases = {"magic"}, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        getSpellMenuManager().openSpellsMenu(player);
    }
}
