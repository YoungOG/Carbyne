package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Dalton on 6/26/2017.
 */
public class RulesCommand extends BaseCommand
{

    private HashMap<String, List<String>> rules;

    public RulesCommand()
    {
        rules = new HashMap<>();

        FileConfiguration fc = Carbyne.getInstance().getRulesFileCongfiguration();
        ConfigurationSection cs = fc.getConfigurationSection("Rules");

        for(String section : cs.getKeys(false))
        {
            try {
                rules.put(section, fc.getStringList("Rules." + section));
            } catch (NullPointerException ex) {
                Carbyne.instance.getLogger().log(Level.WARNING, "Failed to load rules section " + section + "!");
            }
        }

    }

    @Command(name = "rules", inGameOnly = true)
    public void onCommand(CommandArgs cmdArgs)
    {
        String[] args = cmdArgs.getArgs();
        Player sender = cmdArgs.getPlayer();

        if(args.length == 0)
        {
            MessageManager.sendMessage(sender, "&6&m»----------------------------«");
            MessageManager.sendMessage(sender, "&a&lRules (/rules <section>)");
            for(String sectionName : rules.keySet())
                MessageManager.sendMessage(sender, "&b" + sectionName);
            MessageManager.sendMessage(sender, "&6&m»----------------------------«");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < args.length; i++)
            if(!(i+1 < args.length))
                sb.append(args[i]);
            else
                sb.append(args[i] + " ");

        if(rules.containsKey(sb.toString()))
            displayRules(sb.toString(), sender);
        else
            MessageManager.sendMessage(sender, "&cThe rules section specified does not exist!");
    }

    /**
     * PRECONDITION: rules HashMap contains the key section!
     * @param section section key used to pull information from the hashmap
     */
    public void displayRules(String section, Player player)
    {
        MessageManager.sendMessage(player, "&6&m»----------------------------«");
        MessageManager.sendMessage(player, "&a&l" + section);
        for(String value : rules.get(section))
            MessageManager.sendMessage(player, "&b" + value);
        MessageManager.sendMessage(player, "&6&m»----------------------------«");
    }

}
