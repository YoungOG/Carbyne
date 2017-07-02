package com.medievallords.carbyne.listeners;

import com.keenant.tabbed.tablist.TitledTabList;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.Maths;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.github.paperspigot.Title;

import java.util.List;
import java.util.UUID;

/**
 * Created by Dalton on 6/22/2017.
 */
public class PlayerListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();

    private String joinMessage, tablistHeader, tablistFooter;
    private String[] subtitles;

    public PlayerListeners() {
        joinMessage = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("JoinMessage"));

        if (joinMessage == null)
            joinMessage = ChatColor.translateAlternateColorCodes('&', "&5Medieval Lords");

        List<String> initSubs = Carbyne.getInstance().getConfig().getStringList("JoinMessageSubtitles");
        subtitles = initSubs.toArray(new String[initSubs.size()]);

        if (subtitles.length < 1 || subtitles[0] == null)
            subtitles = new String[]{ChatColor.translateAlternateColorCodes('&', "&eWelcome")};

        for (int i = 0; i < subtitles.length; i++)
            subtitles[i] = ChatColor.translateAlternateColorCodes('&', subtitles[i]);

        tablistHeader = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistHeader"));

        if (tablistHeader == null)
            tablistHeader = ChatColor.translateAlternateColorCodes('&', "&5Medieval Lords");

        tablistFooter = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistFooter"));

        if (tablistFooter == null)
            tablistFooter = ChatColor.translateAlternateColorCodes('&', "&5Welcome");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendTitle(new Title.Builder().title(joinMessage).subtitle(subtitles[Maths.randomNumberBetween(subtitles.length, 0)]).stay(55).build());

        TitledTabList tabList = main.getTabbed().newTitledTabList(player);
        tabList.setHeaderFooter(tablistHeader, tablistFooter);

        UUID id = event.getPlayer().getUniqueId();
        String currName = event.getPlayer().getName();

        if (Carbyne.getInstance().getProfileManager().getProfile(id) == null) {
            return;
        }

        String oldName = Carbyne.getInstance().getProfileManager().getProfile(id).getUsername();

        if (!oldName.equals(currName)) {
            try {
                Resident r = TownyUniverse.getDataSource().getResident(oldName);
                TownyUniverse.getDataSource().renamePlayer(r, currName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Carbyne.getInstance().getProfileManager().getProfile(id).setUsername(currName);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        for (ItemStack itemStack : event.getInventory().getContents()) {
            if (itemStack != null && itemStack.getMaxStackSize() == 1 && itemStack.getAmount() > 1) {
                itemStack.setAmount(1);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (ItemStack itemStack : event.getInventory().getContents()) {
            if (itemStack != null && itemStack.getMaxStackSize() == 1 && itemStack.getAmount() > 1) {
                itemStack.setAmount(1);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.TRAPPED_CHEST  || event.getBlock().getType() == Material.HOPPER || event.getBlock().getType() == Material.DISPENSER || event.getBlock().getType() == Material.DROPPER || event.getBlock().getType() == Material.BREWING_STAND || event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.FURNACE) {

        }
        Block block = event.getBlock();
        BlockState blockState = block.getState();

        switch (event.getBlock().getType()) {
            case TRAPPED_CHEST:
            case CHEST: {
                Chest chest = (Chest) blockState;
                chest.getInventory().clear();
                break;
            }

            case HOPPER: {
                Hopper hopper = (Hopper) blockState;
                hopper.getInventory().clear();
                break;
            }
            case DISPENSER: {
                Dispenser dispenser = (Dispenser) blockState;
                dispenser.getInventory().clear();
                break;
            }
            case DROPPER: {
                Dropper dropper = (Dropper) blockState;
                dropper.getInventory().clear();
                break;
            }
            case FURNACE: {
                Furnace furnace = (Furnace) blockState;
                furnace.getInventory().clear();
                break;
            }
            case BREWING_STAND: {
                BrewingStand brewingStand = (BrewingStand) blockState;
                brewingStand.getInventory().clear();
            }
        }
    }

    /*@EventHandler
    public void onNether(PlayerPortalEvent event) {
        if (!event.getPlayer().hasPermission("carbyne.enternether")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cYou cannot use this portal");
        }
    }*/
}
