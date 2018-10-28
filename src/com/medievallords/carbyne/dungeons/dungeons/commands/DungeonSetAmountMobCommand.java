package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.dungeons.MobData;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class DungeonSetAmountMobCommand extends BaseCommand {

    @Command(name = "dungeon.setmobamount", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon setmobamount <dungeon> <spawnerType> <amount>");
            return;
        }

        Dungeon dungeon = StaticClasses.dungeonHandler.getDungeon(args[0]);
        if (dungeon == null) {
            MessageManager.sendMessage(player, "&cCould not find a dungeon with that name.");
            return;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageManager.sendMessage(player, "&cCould not find that spawner type.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player, "&cThe amount must be a number.");
            return;
        }

        MobData mobData;

        if (!dungeon.getMobs().containsKey(entityType)) {
            mobData = new MobData(new HashMap<>(), 1);
            mobData.setAmount(amount);
            dungeon.getMobs().put(entityType, mobData);
        } else {
            mobData = dungeon.getMobs().get(entityType);
            mobData.setAmount(amount);
        }

        ConfigurationSection mobSection = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName() + ".Mobs");
        if (mobSection == null) {
            mobSection = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName()).createSection("Mobs");
        }

        if (mobSection.getConfigurationSection(entityType.name()) != null) {
            ConfigurationSection currentSection = mobSection.getConfigurationSection(entityType.name());
            currentSection.set("Amount", amount);
        } else {
            ConfigurationSection newSection = mobSection.createSection(entityType.name());
            newSection.set("Amount", amount);
        }

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            MessageManager.sendMessage(player, "&aYou have set the max amount to &7" + amount);
        } catch (IOException e) {
            MessageManager.sendMessage(player, "&cFailed to set amount.");
            e.printStackTrace();
        }
    }
}
