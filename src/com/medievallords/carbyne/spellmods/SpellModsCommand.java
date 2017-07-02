package com.medievallords.carbyne.spellmods;

import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by Dalton on 6/11/2017.
 */
public class SpellModsCommand extends BaseCommand
{

    @Command(name = "spells", aliases = { "magic" }, inGameOnly = true)
    public void onCommand(CommandArgs commandArgs)
    {
        Player player = commandArgs.getPlayer();
        Inventory gui = SpellModsListener.generateGUI(player);
        player.openInventory(gui);
    }

}
