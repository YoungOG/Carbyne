package com.medievallords.carbyne.conquerpoints.commands;

import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConquerPointCommand extends BaseCommand {

    @Command(name = "conquerpoint", aliases = {"cp", "conquer"}, permission = "carbyne.commands.conquer", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("new") || args[0].equalsIgnoreCase("add")) {
                Location pos1;
                Location pos2;

                try {
                    WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
                    Selection selection = worldEdit.getSelection(player);

                    double pos1x = selection.getMaximumPoint().getX();
                    double pos1y = selection.getMaximumPoint().getY();
                    double pos1z = selection.getMaximumPoint().getZ();
                    pos1 = new Location(Bukkit.getWorld((selection.getWorld() != null ? selection.getWorld().getName() : "world")), pos1x, pos1y, pos1z);

                    double pos2x = selection.getMinimumPoint().getX();
                    double pos2y = selection.getMinimumPoint().getY();
                    double pos2z = selection.getMinimumPoint().getZ();
                    pos2 = new Location(Bukkit.getWorld((selection.getWorld() != null ? selection.getWorld().getName() : "world")), pos2x, pos2y, pos2z);
                } catch (NullPointerException e) {
                    MessageManager.sendMessage(player, "&cInvalid WorldEdit Selection.");
                    return;
                }

                getConquerPointManager().addConquerPoint(new ConquerPoint(args[1], pos1, pos2));
                getConquerPointManager().saveControlPoints();
                getConquerPointManager().reloadConquerPoints();

                MessageManager.sendMessage(player, "&aSuccessfully created a new ConquerPoint with the ID &a\"&b" + args[1] + "&a\".");
            } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
                String id = args[1];
                ConquerPoint conquerPoint = null;

                for (ConquerPoint conquerPoint1 : getConquerPointManager().getConquerPoints()) {
                    if (conquerPoint1.getId().equalsIgnoreCase(id)) {
                        conquerPoint = conquerPoint1;
                    }
                }

                if (conquerPoint != null) {
                    conquerPoint.stopCapturing();

                    FileConfiguration controlpoints = getCarbyne().getConquerPointsFileConfiguration();
                    if (controlpoints.isSet("conquerpoints." + id)) {
                        controlpoints.set("conquerpoints." + id, null);
                    }

                    getConquerPointManager().removeConquerPoint(conquerPoint);
                    getConquerPointManager().saveControlPoints();
                    getConquerPointManager().reloadConquerPoints();

                    MessageManager.sendMessage(player, "&aSuccessfully removed a ConquerPoint with the ID \"&b" + id + "&a\".");
                }
            } else {
                MessageManager.sendMessage(player, "&cUsage: /conquer");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                MessageManager.sendMessage(player, "&aAvailable ConquerPoints:");

                JSONMessage message = JSONMessage.create("");

                for (int i = 0; i < getConquerPointManager().getConquerPoints().size(); i++) {
                    if (i < getConquerPointManager().getConquerPoints().size() - 1) {
                        ConquerPoint conquerPoint = getConquerPointManager().getConquerPoints().get(i);

                        message.then(conquerPoint.getId()).color(ChatColor.AQUA)
                                .tooltip(getMessageForConquerPoint(conquerPoint))
                                .then(", ").color(ChatColor.GRAY);
                    } else {
                        ConquerPoint conquerPoint = getConquerPointManager().getConquerPoints().get(i);

                        message.then(conquerPoint.getId()).color(ChatColor.AQUA)
                                .tooltip(getMessageForConquerPoint(conquerPoint));
                    }
                }

                message.send(player);
            } else {
                MessageManager.sendMessage(player, "&cUsage: /conquer");

            }
        } else {
            MessageManager.sendMessage(player, "&cUsage: /conquer");
        }
    }

    public JSONMessage getMessageForConquerPoint(ConquerPoint conquerPoint) {
        JSONMessage message = JSONMessage.create("");

        message.then(ChatColor.translateAlternateColorCodes('&', "&aId: &b" + conquerPoint.getId()));
        message.then("\n");
        message.then(ChatColor.translateAlternateColorCodes('&', "&aHolder: &b" + (conquerPoint.getHolder() != null ? (Bukkit.getPlayer(conquerPoint.getHolder()) != null ? Bukkit.getPlayer(conquerPoint.getHolder()).getName() : "none") : "none")) + "\n");
        message.then(ChatColor.translateAlternateColorCodes('&', "&aNation: &b" + (conquerPoint.getNation() != null ? conquerPoint.getNation().getName() : "none")) + "\n");
        message.then(ChatColor.translateAlternateColorCodes('&', "&aState: &b" + conquerPoint.getState()) + "\n");
        message.then(ChatColor.translateAlternateColorCodes('&', "&aTime: &b" + conquerPoint.getCaptureTime()));
        return message;
    }
}