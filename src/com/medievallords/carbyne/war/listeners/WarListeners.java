package com.medievallords.carbyne.war.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.war.WarManager;
import com.medievallords.carbyne.war.objects.WarNation;
import com.medievallords.carbyne.war.objects.WarPlot;
import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Williams on 2017-08-21
 * for the Carbyne project.
 */
public class WarListeners implements Listener {

    private Carbyne carbyne = Carbyne.getInstance();
    private WarManager warManager = carbyne.getWarManager();

    @EventHandler
    public void onPlotChange(PlayerChangePlotEvent event) {
        WarPlot warPlot;
        try {
            warPlot = carbyne.getWarManager().getWarPlot(event.getTo().getTownBlock());
        } catch (NotRegisteredException e) {
            return;
        }

        if (warPlot == null) {
            return;
        }

        event.getMoveEvent().setCancelled(true);

        if (warPlot.isStarted()) {
            if (warManager.getEnteringPlayers().containsKey(event.getPlayer().getUniqueId()) && warManager.getEnteringPlayers().get(event.getPlayer().getUniqueId()).equals(warPlot.getPlot())) {
                openJoinTeamGUI(event.getPlayer());
            } else {
                warManager.getEnteringPlayers().put(event.getPlayer().getUniqueId(), warPlot);
                openJoinTeamGUI(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!event.getInventory().getTitle().equals("§7§lChoose a side")) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        WarNation warNation = warManager.getWarNation(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
        if (warNation == null) {
            return;
        }

        WarPlot warPlot = warManager.getEnteringPlayers().get(player.getUniqueId());
        if (warPlot == null) {
            return;
        }

        if (warPlot.getAttacker().equals(warNation)) {
            warPlot.getAttackers().add(player.getUniqueId());
        } else {
            warPlot.getDefenders().add(player.getUniqueId());
        }
    }

    private void openJoinTeamGUI(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 9, "§7§lChoose a side");

        WarPlot warPlot = warManager.getEnteringPlayers().get(player.getUniqueId());
        if (warPlot == null) {
            return;
        }

        WarNation attacker = warPlot.getAttacker();
        WarNation defender = warPlot.getDefender();

        ItemStack attackerBanner = attacker.getBanner();
        ItemStack defenderBanner = attacker.getBanner();

        inventory.setItem(1, new ItemBuilder(attackerBanner).name("&c" + attacker.getNation().getName()).build());
        inventory.setItem(7, new ItemBuilder(defenderBanner).name("&b" + defender.getNation().getName()).build());

        player.openInventory(inventory);
    }
}
