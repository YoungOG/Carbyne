package com.medievallords.carbyne.conquerpoints.commands;

import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ConquerPointCommand extends BaseCommand {

    @Command(name = "conquerpoint", inGameOnly = true, aliases = {"controlpoint, cp"})
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") && player.isOp()) {
                Location pos1;
                Location pos2;
                try {
                    LocalSession session = WorldEdit.getInstance().getSession(player.getName());
                    com.sk89q.worldedit.world.World world = session.getSelectionWorld();
                    Region region = session.getSelection(world);
                    String worldName = region.getWorld().getName();
                    World selWorld = Bukkit.getWorld(worldName);
                    double pos1x = region.getMaximumPoint().getX();
                    double pos1y = region.getMaximumPoint().getY();
                    double pos1z = region.getMaximumPoint().getZ();
                    pos1 = new Location(selWorld, pos1x, pos1y, pos1z);
                    double pos2x = region.getMinimumPoint().getX();
                    double pos2y = region.getMinimumPoint().getY();
                    double pos2z = region.getMinimumPoint().getZ();
                    pos2 = new Location(selWorld, pos2x, pos2y, pos2z);
                } catch (IncompleteRegionException | NullPointerException e) {
                    return;
                }

                String name = args[1];

                ConquerPoint newCP = new ConquerPoint(name, pos1, pos2);
                StaticClasses.conquerPointManager.getConquerPoints().add(newCP);
                StaticClasses.conquerPointManager.saveControlPoints();

                MessageManager.sendMessage(player, "&aYou have created a new Conquer Point named \"&b" + newCP.getName() + "&a\".");
            }
        }
    }
}
