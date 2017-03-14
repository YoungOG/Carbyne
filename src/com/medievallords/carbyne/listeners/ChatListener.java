package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.JSONMessage;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Created by Calvin on 1/9/2017
 * for the Carbyne-Gear project.
 */
public class ChatListener implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        JSONMessage newMessage = JSONMessage.create("");

        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

            if (resident.hasTown()) {
                Town town = resident.getTown();

                newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f["));

                if (resident.hasNation()) {
                    Nation nation = resident.getTown().getNation();

                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&6" + nation.getName()));

                    if (getNationMessagePart(nation) != null) {
                        newMessage.tooltip(getNationMessagePart(nation));
                    }

                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f:"));
                }

                newMessage.then(ChatColor.translateAlternateColorCodes('&', "&3" + town.getName()));

                if (getTownMessagePart(town) != null) {
                    newMessage.tooltip(getTownMessagePart(town));
                }

                newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f]"));
            }
        } catch (NotRegisteredException e1) {
            e1.printStackTrace();
        }

        String message = event.getMessage();

        String prefix = PermissionsEx.getUser(event.getPlayer()).getGroups()[0].getPrefix();

        newMessage.then(" ").then(ChatColor.translateAlternateColorCodes('&', player.getDisplayName()));

        if (getPlayerMessagePart(player) != null) {
            newMessage.tooltip(getPlayerMessagePart(player));
        }

        newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f: ") + message);

        for (Player players : PlayerUtility.getOnlinePlayers()) {
            newMessage.send(players);
        }

        event.setCancelled(true);
    }

    public JSONMessage getTownMessagePart(Town town) {
        try {
            JSONMessage message = JSONMessage.create("");

            message.then(ChatColor.translateAlternateColorCodes('&', "&aBoard: &b" + town.getTownBoard()) + "\n");
            message.then(ChatColor.translateAlternateColorCodes('&', "&aTown Size: &b" + town.getTownBlocks().size() + " / " + TownySettings.getMaxTownBlocks(town)
                    + (TownySettings.isSellingBonusBlocks() ? " [Bought: " + town.getPurchasedBlocks() + "/" + TownySettings.getMaxPurchedBlocks() + "]" : "")
                    + ((town.getBonusBlocks() > 0) ? " [Bonus: " + town.getBonusBlocks() + "]" : "")
                    + ((TownySettings.getNationBonusBlocks(town) > 0) ? " [NationBonus: " + TownySettings.getNationBonusBlocks(town) : ""))
                    + (town.isPublic() ? " [Home: " + (town.hasHomeBlock() ? town.getHomeBlock().getCoord().toString() : "None") + "]" : ""));

            return message;
        } catch (TownyException ignored) {}

        return null;
    }

    public JSONMessage getNationMessagePart(Nation nation) {
        JSONMessage message = JSONMessage.create("");

        message.then(ChatColor.translateAlternateColorCodes('&', "&aBank: &b" + nation.getHoldingFormattedBalance() + " &8| &aDaily Upkeep: &4" + TownySettings.getNationUpkeepCost(nation) + "\n"));

        return message;
    }

    public JSONMessage getPlayerMessagePart(Player player) {
        JSONMessage message = JSONMessage.create("");

        message.then(ChatColor.translateAlternateColorCodes('&', "&aMore coming soon"));

        return message;
    }
}
