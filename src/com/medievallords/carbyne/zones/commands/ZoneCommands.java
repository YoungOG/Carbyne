package com.medievallords.carbyne.zones.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.region.RegionUser;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.NumberUtil;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.medievallords.carbyne.zones.Zone;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZoneCommands extends BaseCommand {

    @Command(name = "zone", permission = "carbyne.administrator", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 4) {
                    MessageManager.sendMessage(player, "&cUsage: /zone create <name> <maxMobs> <minDistance>");
                    return;
                }

                RegionUser user = RegionUser.getRegionUser(player);

                if (user == null || user.getSelection() == null || user.getSelection().getLocation1() == null || user.getSelection().getLocation2() == null) {
                    MessageManager.sendMessage(player, "&cYou need to select a region to be able to make a zone.");
                    return;
                }

                getZoneManager().createZone(args[1], user.getSelection(), Integer.parseInt(args[2]), Double.parseDouble(args[3]));
                MessageManager.sendMessage(player, "&A zone has successfully been created.");
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length != 4) {
                    MessageManager.sendMessage(player, "&cUsage: /zone set <zone> <displayName/maxMobs/minDistance> <value>");
                    return;
                }

                Zone zone = getZoneManager().getZone(args[1]);

                if (zone == null) {
                    MessageManager.sendMessage(player, "&cA zone with that name could not be found.");
                    return;
                }

                ConfigurationSection cs = Carbyne.getInstance().getZonesFileConfiguration();
                cs = cs.getConfigurationSection(zone.getName());

                switch (args[2].toLowerCase()) {
                    case "displayname":
                        zone.setDisplayName(args[3]);
                        cs.set("DisplayName", args[3]);

                        MessageManager.sendMessage(player, "&aSuccessfully set the DisplayName for &5" + zone.getName() + " &ato &b\"" + args[3] + "\"&a.");
                        break;
                    case "maxmobs":
                        if (!NumberUtil.isInt(args[3])) {
                            MessageManager.sendMessage(player, "&cPlease enter an integer for the input.");
                            return;
                        }

                        zone.setMaxMobs(Integer.parseInt(args[3]));
                        cs.set("MaxMobs", Integer.parseInt(args[3]));

                        MessageManager.sendMessage(player, "&aSuccessfully set the MaxMobs for &5" + zone.getName() + " &ato &b\"" + args[3] + "\"&a.");
                        break;
                    case "mindistance":
                        if (!NumberUtil.isInt(args[3])) {
                            MessageManager.sendMessage(player, "&cPlease enter an integer for the input.");
                            return;
                        }

                        zone.setMinDistance(Integer.parseInt(args[3]));
                        cs.set("MinDistance", args[3]);
                        MessageManager.sendMessage(player, "&aSuccessfully set the MinDistance for &5" + zone.getName() + " &ato &b\"" + args[3] + "\"&a.");
                        break;
                }

                try {
                    Carbyne.getInstance().getZonesFileConfiguration().save(Carbyne.getInstance().getZonesFile());
                    Carbyne.getInstance().setZonesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getZonesFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("addmob")) {
                if (args.length != 4) {
                    MessageManager.sendMessage(player, "&cUsage: /zone addmob <zone> <mobname> <chance>");
                    return;
                }

                Zone zone = getZoneManager().getZone(args[1]);

                if (zone == null) {
                    MessageManager.sendMessage(player, "&cA zone with that name could not be found.");
                    return;
                }

                for (MythicMob mob : zone.getMobs().keySet())
                    if (mob.getInternalName().equalsIgnoreCase(args[2])) {
                        MessageManager.sendMessage(player, "&cThis zone already contains that mob.");
                        return;
                    }

                MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(args[2]);

                if (mob == null) {
                    MessageManager.sendMessage(player, "&cThat is not a valid MythicMob");
                    return;
                }

                if (!NumberUtil.isInt(args[3])) {
                    MessageManager.sendMessage(player, "&cPlease enter an integer for the input.");
                    return;
                }

                zone.getMobs().put(mob, Integer.parseInt(args[3]));

                ConfigurationSection cs = Carbyne.getInstance().getZonesFileConfiguration();
                cs = cs.getConfigurationSection(zone.getName());

                if (cs.contains(zone.getName() + ".Mobs")) {
                    Map<MythicMob, Integer> mobs = zone.getMobs();
                    List<String> mobLines = cs.getStringList(zone.getName() + ".Mobs");

                    for (MythicMob mob1 : mobs.keySet())
                        mobLines.add(mob1.getInternalName() + "," + mobs.get(mob1));

                    cs.set(zone.getName() + ".Mobs", mobLines);
                }

                try {
                    Carbyne.getInstance().getZonesFileConfiguration().save(Carbyne.getInstance().getZonesFile());
                    Carbyne.getInstance().setZonesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getZonesFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MessageManager.sendMessage(player, "&aSuccessfully added the mob &b\"" + mob.getInternalName() + "\" &awith the chance &b\"" + args[3] + "\" &ato &5" + zone.getName() + " &azone.");
            } else if (args[0].equalsIgnoreCase("delmob")) {
                if (args.length != 4) {
                    MessageManager.sendMessage(player, "&cUsage: /zone addmob <zone> <mobname>");
                    return;
                }

                Zone zone = getZoneManager().getZone(args[1]);

                if (zone == null) {
                    MessageManager.sendMessage(player, "&cA zone with that name could not be found.");
                    return;
                }

                boolean contains = true;

                for (MythicMob mob : zone.getMobs().keySet())
                    if (mob.getInternalName().equalsIgnoreCase(args[2]))
                        contains = false;

                if (!contains) {
                    MessageManager.sendMessage(player, "&cThis zone does not contain that mob.");
                    return;
                }

                MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(args[2]);

                if (mob == null) {
                    MessageManager.sendMessage(player, "&cThat is not a valid MythicMob");
                    return;
                }

                zone.getMobs().remove(mob);

                ConfigurationSection cs = Carbyne.getInstance().getZonesFileConfiguration();
                cs = cs.getConfigurationSection(zone.getName());

                if (cs.contains(zone.getName() + ".Mobs")) {
                    Map<MythicMob, Integer> mobs = zone.getMobs();
                    List<String> mobLines = cs.getStringList(zone.getName() + ".Mobs");

                    for (MythicMob mob1 : mobs.keySet())
                        mobLines.add(mob1.getInternalName() + "," + mobs.get(mob1));

                    cs.set(zone.getName() + ".Mobs", mobLines);
                }

                try {
                    Carbyne.getInstance().getZonesFileConfiguration().save(Carbyne.getInstance().getZonesFile());
                    Carbyne.getInstance().setZonesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getZonesFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MessageManager.sendMessage(player, "&aSuccessfully removed the mob &b\"" + mob.getInternalName() + "\" &afrom &5" + zone.getName() + " &azone.");
            } else if (args[0].equalsIgnoreCase("setSelection")) {
                if (args.length != 2) {
                    MessageManager.sendMessage(player, "&cUsage: /zone setSelection <name>");
                    return;
                }

                RegionUser user = RegionUser.getRegionUser(player);

                if (user == null || user.getSelection() == null || user.getSelection().getLocation1() == null || user.getSelection().getLocation2() == null) {
                    MessageManager.sendMessage(player, "&cYou need to select a region to be able to set the selection.");
                    return;
                }

                Zone zone = getZoneManager().getZone(args[1]);

                if (zone == null) {
                    MessageManager.sendMessage(player, "&cA zone with that name could not be found.");
                    return;
                }

                zone.setSelection(user.getSelection());

                ConfigurationSection cs = Carbyne.getInstance().getZonesFileConfiguration();
                cs = cs.getConfigurationSection(zone.getName());

                if (cs.contains(zone.getName() + ".Loccation1"))
                    cs.set("Location1", LocationSerialization.serializeLocation(user.getSelection().location1));

                if (cs.contains(zone.getName() + ".Loccation2"))
                    cs.set("Location2", LocationSerialization.serializeLocation(user.getSelection().location2));

                try {
                    Carbyne.getInstance().getZonesFileConfiguration().save(Carbyne.getInstance().getZonesFile());
                    Carbyne.getInstance().setZonesFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getZonesFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MessageManager.sendMessage(player, "&aSuccessfully set the Selection for &5" + zone.getName() + "&a.");
            } else if (args[0].equalsIgnoreCase("list")) {
                if (getZoneManager().getZones().size() <= 0) {
                    MessageManager.sendMessage(player, "&cThere are no available zones to display.");
                    return;
                }

                MessageManager.sendMessage(player, "&aAvailable Zones:");

                JSONMessage message = JSONMessage.create("");

                for (int i = 0; i < getZoneManager().getZones().size(); i++) {
                    if (i < getZoneManager().getZones().size() - 1) {
                        Zone zone = getZoneManager().getZones().get(i);

                        message.then(zone.getName()).color(ChatColor.AQUA)
                                .tooltip(getMessageForZone(zone))
                                .then(", ").color(ChatColor.GRAY);
                    } else {
                        Zone zone = getZoneManager().getZones().get(i);

                        message.then(zone.getName()).color(ChatColor.AQUA)
                                .tooltip(getMessageForZone(zone));
                    }
                }

                message.send(player);
            } else if (args[0].equalsIgnoreCase("reload")) {
                getZoneManager().reload();
                MessageManager.sendMessage(player, "&aZones have been reloaded");
            }
        } else {
            MessageManager.sendMessage(player, "&7&m-------&r&7 [ &aZones &7] &m-------");
            MessageManager.sendMessage(player, "&a/zone create [name] [maxMobs] [minDistance] &7- Creates a new zone.");
            MessageManager.sendMessage(player, "&a/zone set [zone] [displayName/maxMobs/minDistance] [value] &7- Edits values for the zone.");
            MessageManager.sendMessage(player, "&a/zone addMob [name] [mob] [chance] &7- Adds a mob to the zone.");
            MessageManager.sendMessage(player, "&a/zone delMob [name] [mob] &7- Deletes a mob from the zone.");
            MessageManager.sendMessage(player, "&a/zone setSelection [name] &7- Sets a new selection.");
            MessageManager.sendMessage(player, "&a/zone list &7- Lists all zones.");
        }
    }

    public JSONMessage getMessageForZone(Zone zone) {
        JSONMessage message2 = JSONMessage.create("");

        message2.then(ChatColor.translateAlternateColorCodes('&', "&aZone Name&7: &b" + zone.getName()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aZone Displayname&7: &b" + zone.getDisplayName()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aZone Selection&7:") + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', "   &7Point 1 - (World&b: "
                + zone.getSelection().getLocation1().getWorld().getName()
                + "&7, X&b: " + zone.getSelection().getLocation1().getBlockX()
                + "&7, Y&b: " + zone.getSelection().getLocation1().getBlockY()
                + "&7, Z&b: " + zone.getSelection().getLocation1().getBlockZ() + "&7)") + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', "   &7Point 2 - (World&b: "
                + zone.getSelection().getLocation2().getWorld().getName()
                + "&7, X&b: " + zone.getSelection().getLocation2().getBlockX()
                + "&7, Y&b: " + zone.getSelection().getLocation2().getBlockY()
                + "&7, Z&b: " + zone.getSelection().getLocation2().getBlockZ() + "&7)") + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aMin Distance&7: &b" + zone.getMinDistance()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aRunning&7: &b" + zone.isRun()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aCooldown&7: &b" + zone.getCooldown()) + "\n");
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aPlayers&7(&b" + zone.getPlayersInZone().size() + "&7):" + (zone.getPlayersInZone().size() <= 0 ? " &bnone" : "")) + "\n");
        if (zone.getPlayersInZone().size() > 0) {
            int id = 0;

            for (UUID uuid : zone.getPlayersInZone()) {
                Player player = Bukkit.getPlayer(uuid);

                if (player == null)
                    continue;

                id++;
                message2.then(ChatColor.translateAlternateColorCodes('&', "   &7" + id + ". &b" + player.getName()) + "\n");
            }
        }
        message2.then(ChatColor.translateAlternateColorCodes('&', " &aMobs&7(&b" + zone.getAmountOfMobs() + "&7/&b" + zone.getMaxMobs() + "&7):") + "\n");
        if (zone.getMobs().keySet().size() > 0) {
            int id = 0;

            for (MythicMob mob : zone.getMobs().keySet()) {
                if (mob == null)
                    continue;

                id++;
                message2.then(ChatColor.translateAlternateColorCodes('&', "   &b" + id + "&7. &b" + mob.getInternalName() + "&7, &b" + zone.getMobs().get(mob)) + "\n");
            }
        }
        return message2;
    }
}
