package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.professions.alchemy.AlchemyTask;
import com.medievallords.carbyne.profiles.Profile;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
@Getter
public class AlchemyProfession extends Profession {

    private List<AlchemyTask> alchemyTasks = new ArrayList<>();

    public AlchemyProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrew(BrewEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.BREWING_STAND) {
            return;
        }

        BrewerInventory inventory = event.getContents();

        for (AlchemyTask task : alchemyTasks) {
            if (task.getInventory().equals(inventory)) {
                if (task.getTimeViewed() > 7 && task.getTimeViewed() < 340 && task.getItemsPutIn() > 1) {
                    Player player = task.getPlayer();

                    Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());

                    if (profile == null || profile.getProfession() == null || profile.getProfession() != this)
                        return;

                    if (inventory.getViewers().contains(player)) {
                        task.setTimeViewed(0);
                        task.setItemsPutIn(0);
                        task.setTimeNotViewed(0);
                    }

                    giveReward(player);
                }
            }
        }
    }

    @EventHandler
    public void putStuffIn(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null) {
            return;
        }

        if (inv.getType() != InventoryType.BREWING) {
            return;
        }

        BrewerInventory inventory = (BrewerInventory) inv;
        for (AlchemyTask task : alchemyTasks) {
            if (task.getInventory() == inventory && event.getWhoClicked().equals(task.getPlayer())) {
                if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_SOME) {
                    task.setItemsPutIn(task.getItemsPutIn() + 1);
                }
            }
        }

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING)
            return;

        if (!(event.getPlayer() instanceof Player))
            return;

        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());

        if (profile == null || profile.getProfession() == null || profile.getProfession() != this)
            return;

        AlchemyTask hasTask = getTask((BrewerInventory) event.getInventory());

        if (hasTask != null) {
            if (hasTask.getPlayer().getUniqueId().equals((event.getPlayer()).getUniqueId())) {
                return;
            }
        }

        AlchemyTask task = new AlchemyTask((BrewerInventory) event.getInventory(), (Player) event.getPlayer(), this);
        alchemyTasks.add(task);
        task.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }


    private AlchemyTask getTask(BrewerInventory inventory) {
        for (AlchemyTask task : alchemyTasks)
            if (task.getInventory().equals(inventory))
                return task;

        return null;
    }
}
