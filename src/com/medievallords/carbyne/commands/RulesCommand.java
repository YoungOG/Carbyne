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
public class RulesCommand extends BaseCommand {

    private HashMap<String, List<String>> rules;

    public RulesCommand() {
        load();
    }

    private void load() {
        rules = new HashMap<>();

        Carbyne.getInstance().setRulesFileCongfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getRulesFile()));
        FileConfiguration fc = Carbyne.getInstance().getRulesFileCongfiguration();
        ConfigurationSection cs = fc.getConfigurationSection("Rules");

        for (String section : cs.getKeys(false)) {
            try {
                rules.put(section, fc.getStringList("Rules." + section));
            } catch (NullPointerException ex) {
                Carbyne.instance.getLogger().log(Level.WARNING, "Failed to load rules section " + section + "!");
            }
        }
    }

    @Command(name = "rules", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player sender = commandArgs.getPlayer();

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "&6&m»----------------------------«");
            MessageManager.sendMessage(sender, "&5&lServer Rules");
            MessageManager.sendMessage(sender, "&7&nHover over a section to reveal the rules.");
            MessageManager.sendMessage(sender, "");
            for (String sectionName : rules.keySet()) {
                JSONMessage message = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&7- &a" + sectionName)).tooltip(ChatColor.translateAlternateColorCodes('&', getRules(sectionName)));
                message.send(sender);
            }
            MessageManager.sendMessage(sender, "&6&m»----------------------------«");
            return;
        }

        if (args.length == 1 && sender.hasPermission("carbyne.commands.rules") && args[0].equalsIgnoreCase("reload")) {
            load();
            MessageManager.sendMessage(sender, "&aThe rules have been reloaded!");
        }
    }

    /**
     * PRECONDITION: rules HashMap contains the key section!
     *
     * @param section section key used to pull information from the hashmap
     */
    public String getRules(String section) {
        StringBuilder val = new StringBuilder("&6&m»----------------------------«\n&a&lSection: &5" + section + "\n \n");
        int index = 1;
        for (String value : rules.get(section)) {
            String[] parse = value.replace("\\n", ":NEWLINE:").split(":NEWLINE:");
            val.append("&7").append(index).append(". &r").append(parse[0]).append("\n");

            if (parse.length > 0) {
                for (int i = 1; i < parse.length; i++) {
                    String parseVal = parse[i];
                    val.append("   ").append(parseVal).append("\n");
                }
            } else {
                val.append("&7").append(index).append(". &r").append(value.length() > 60 ? value.substring(0, 60) + "\n" + value.substring(60) : value).append("\n");
            }

            index++;
        }
        val.append("&6&m»----------------------------«");
        return val.toString();
    }
}
