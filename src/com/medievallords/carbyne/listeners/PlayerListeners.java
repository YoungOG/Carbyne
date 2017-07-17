package com.medievallords.carbyne.listeners;

import com.keenant.tabbed.tablist.TitledTabList;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            } catch (Exception ignored) {
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
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() != Material.AIR) {
                ItemStack item = event.getPlayer().getItemInHand();
                switch (item.getType()) {
                    case TRAPPED_CHEST:
                    case CHEST:
                    case HOPPER:
                    case DISPENSER:
                    case DROPPER:
                    case FURNACE:
                    case BREWING_STAND:
                        net.minecraft.server.v1_8_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
                        if (itemStack.getTag() != null) {
                            event.setCancelled(true);
                            itemStack.setTag(null);
                            event.getPlayer().setItemInHand(CraftItemStack.asCraftMirror(itemStack));
                        }
                }
            }
        }
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getVote().getUsername());

        if (player != null) {
            Double random = Math.random();

            if (random <= 0.05) {
                ItemStack reward = main.getCrateManager().getKey("ObsidianKey").getItem().clone();
                player.getInventory().addItem(reward);
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a! Vote using &3/vote&a!");
                MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
            } else if (random <= 0.1) {
                ItemStack reward = main.getCrateManager().getKey("EmeraldKey").getItem().clone();
                player.getInventory().addItem(reward);
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a! Vote using &3/vote&a!");
                MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
            } else if (random <= 0.15) {
                ItemStack reward = main.getCrateManager().getKey("DiamondKey").getItem().clone();
                player.getInventory().addItem(reward);
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a! Vote using &3/vote&a!");
                MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
            } else if (random <= 0.2) {
                ItemStack reward = main.getCrateManager().getKey("GoldKey").getItem().clone();
                player.getInventory().addItem(reward);
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a! Vote using &3/vote&a!");
                MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
            } else {
                ItemStack reward = main.getCrateManager().getKey("IronKey").getItem().clone();
                player.getInventory().addItem(reward);
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a! Vote using &3/vote&a!");
                MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
            }
        }
    }
}
