package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

    public RulesCommand() {
        load();
    }

    private void load() {
        rules = new HashMap<>();

        Carbyne.getInstance().setRulesFileCongfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getRulesFile()));
        FileConfiguration fc = Carbyne.getInstance().getRulesFileCongfiguration();
        ConfigurationSection cs = fc.getConfigurationSection("Rules");

        for(String section : cs.getKeys(false)) {
            try {
                rules.put(section.toLowerCase(), fc.getStringList("Rules." + section));
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

        if (args.length == 1 && sender.hasPermission("carbyne.commands.rules") && args[0].equalsIgnoreCase("reload")) {
            load();
            MessageManager.sendMessage(sender, "&2Rules reloaded!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < args.length; i++)
            if(!(i+1 < args.length))
                sb.append(args[i]);
            else
                sb.append(args[i] + " ");

        if (rules.containsKey(sb.toString().toLowerCase()))
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
        for (String value : rules.get(section.toLowerCase())) {
            String[] parse = value.split("/:");
            if (parse.length == 2) {
                JSONMessage json = JSONMessage.create("");
                json.then(ChatColor.translateAlternateColorCodes('&', "&b" + parse[0])).tooltip(ChatColor.translateAlternateColorCodes('&', "&b" + parse[1]));
                json.send(player);
            } else MessageManager.sendMessage(player, "&b" + parse[0]);
        }
        MessageManager.sendMessage(player, "&6&m»----------------------------«");
    }

}
