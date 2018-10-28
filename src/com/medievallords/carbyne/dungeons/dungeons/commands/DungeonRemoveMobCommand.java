package com.medievallords.carbyne.dungeons.dungeons.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.dungeons.MobData;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DungeonRemoveMobCommand extends BaseCommand {

    @Command(name = "dungeon.removemob", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length != 3) {
            MessageManager.sendMessage(player, "&cUsage: &7/dungeon removemob <dungeon> <spawnerType> <mob>");
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

        MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(args[2]);
        if (mythicMob == null) {
            MessageManager.sendMessage(player, "&cCould not find that mythic mob.");
            return;
        }


        if (!dungeon.getMobs().containsKey(entityType)) {
            MessageManager.sendMessage(player, "&cThe dungeon does not have that mob for the specified spawner type.");
            return;
        }

        MobData mobData = dungeon.getMobs().get(entityType);
        mobData.getMobs().remove(mythicMob);

        List<String> mobList = new ArrayList<>();
        for (MythicMob mob : mobData.getMobs().keySet()) {
            mobList.add(mob.getInternalName() + "," + mobData.getMobs().get(mob));
        }

        ConfigurationSection mobSection = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName() + ".Mobs");
        if (mobSection == null) {
            mobSection = Carbyne.getInstance().getDungeonsFileConfiguration().getConfigurationSection("Dungeons." + dungeon.getName()).createSection("Mobs");
        }

        if (mobSection.getConfigurationSection(entityType.name()) != null) {
            ConfigurationSection currentSection = mobSection.getConfigurationSection(entityType.name());
            currentSection.set("List", mobList);
        } else {
            ConfigurationSection newSection = mobSection.createSection(entityType.name());
            newSection.set("List", mobList);
        }

        try {
            Carbyne.getInstance().getDungeonsFileConfiguration().save(Carbyne.getInstance().getDungeonsFile());
            MessageManager.sendMessage(player, "&aYou have removed a mob from the dungeon");
        } catch (IOException e) {
            MessageManager.sendMessage(player, "&cFailed to remove mob.");
            e.printStackTrace();
        }
    }
}
