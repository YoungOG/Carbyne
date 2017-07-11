package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.crates.keys.Key;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by WE on 2017-07-03.
 */
public class VotingKeyCommand extends BaseCommand implements Listener{

    private HashMap<Key, Double> keyChances = new HashMap<>();
    private HashMap<UUID, Item> pickups = new HashMap<>();

    public VotingKeyCommand() {
        super();
        load();
    }

    public void load() {

        keyChances.clear();

        ConfigurationSection section = getCarbyne().getConfig().getConfigurationSection("votingkeys");
        if (section == null) {
            getCarbyne().getConfig().createSection("votingkeys");
            getCarbyne().getConfig().set("votingkeys.keys", new ArrayList<String>());
            return;
        }

        for (String s : section.getStringList("keys")) {
            String[] keys = s.split(",");

            try {
                Key key = getCrateManager().getKey(keys[0]);
                if (key == null) {
                    System.out.println("Could not find a key with the name" + keys[0]);
                    return;
                }

                double chance = Double.parseDouble(keys[1]);
                keyChances.put(key, chance);
            } catch (NumberFormatException e) {
                System.out.println("Could not load voting key " + s);
            }
        }

        for (Key key : keyChances.keySet()) {
            System.out.println("Key chances: " + key.getDisplayName() + ", " + keyChances.get(key));
        }
    }


    @Command(name = "votingkey", permission = "carbyne.administrator")
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "Usage: /votingkey <player>");
            return;
        }

        Player player = Bukkit.getServer().getPlayer(args[0]);
        if (player == null) {
            MessageManager.sendMessage(sender, "[VotingKey] Could not find that player");
            return;
        }

        for (Key key : keyChances.keySet()) {
            double chance = keyChances.get(key);
            if (Math.random() < chance) {
                if (player.getInventory().firstEmpty() == -1) {
                    Item item = player.getWorld().dropItemNaturally(player.getLocation(), key.getItem());
                    pickups.put(player.getUniqueId(), item);
                    MessageManager.sendMessage(player, "&6You got lucky and got a &a" + key.getDisplayName() + ". &6It was dropped on the ground since your inventory was full.");
                    MessageManager.sendMessage(player, "&aOnly you can pick it up.");

                } else {
                    player.getInventory().addItem(key.getItem());
                    MessageManager.sendMessage(player, "&6You got lucky and got a &a" + key.getDisplayName());
                }
                break;
            }
        }

    }

    @Command(name = "votingkey.reload", permission = "carbyne.administrator")
    public void onReload(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        getCarbyne().reloadConfig();
        getCarbyne().saveConfig();

        load();

        MessageManager.sendMessage(sender, "KeyChances reloaded");
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (pickups.containsValue(event.getItem())) {
            if (pickups.containsKey(event.getPlayer().getUniqueId()) && pickups.get(event.getPlayer().getUniqueId()).equals(event.getItem())) {

            } else {
                event.setCancelled(true);
                MessageManager.sendMessage(event.getPlayer(), "&cYou can not pickup this item");

            }

        }
    }
}
