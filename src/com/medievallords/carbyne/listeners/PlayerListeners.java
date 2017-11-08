package com.medievallords.carbyne.listeners;

import com.keenant.tabbed.tablist.TitledTabList;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.*;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.List;
import java.util.Map;

/**
 * Created by Dalton on 6/22/2017.
 */
public class PlayerListeners implements Listener {

    private Carbyne main = Carbyne.getInstance();

    @Getter
    private static int voteCount = 0;

    private String joinMessage, tablistHeader, tablistFooter;
    private String[] subtitles;

    public PlayerListeners() {
        joinMessage = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("JoinMessage"));

        if (joinMessage == null)
            joinMessage = ChatColor.translateAlternateColorCodes('&', "&5Medieval Lords");

        List<String> initSubs = Carbyne.getInstance().getConfig().getStringList("JoinMessageSubtitles");
        subtitles = initSubs.toArray(new String[initSubs.size()]);

        if (subtitles.length < 1 || subtitles[0] == null)
            subtitles = new String[]{};

        for (int i = 0; i < subtitles.length; i++)
            subtitles[i] = ChatColor.translateAlternateColorCodes('&', subtitles[i]);

        tablistHeader = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistHeader"));

        if (tablistHeader == null)
            tablistHeader = "";

        tablistFooter = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("TablistFooter"));

        if (tablistFooter == null)
            tablistFooter = "";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore())
            player.sendTitle(new Title.Builder().title(joinMessage).subtitle(subtitles[Maths.randomNumberBetween(subtitles.length, 0)]).stay(55).build());

        TitledTabList tabList = main.getTabbed().newTitledTabList(player);
        tabList.setHeaderFooter(tablistHeader, tablistFooter);
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
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
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
            voteCount++;

            for (Player online : PlayerUtility.getOnlinePlayers()) {
                Profile profile = main.getProfileManager().getProfile(online.getUniqueId());

                profile.setShowVoteCount(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (Cooldowns.tryCooldown(profile.getUniqueId(), "ShowVoteCD", 5000))
                            profile.setShowVoteCount(false);
                    }
                }.runTaskLaterAsynchronously(main, 5 * 20L);
            }

            if (voteCount % 15 == 0 && voteCount < 100) {
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5&l" + voteCount + " &aconsecutive votes has been reached! Vote using &3/vote&a!");
            }

            double random = Math.random();

            ItemStack reward;

            if (random <= 0.02) {
                reward = main.getCrateManager().getKey("ObsidianKey").getItem().clone();
            } else if (random <= 0.028) {
                reward = main.getCrateManager().getKey("EmeraldKey").getItem().clone();
            } else if (random <= 0.15) {
                reward = main.getCrateManager().getKey("DiamondKey").getItem().clone();
            } else if (random <= 0.25) {
                reward = main.getCrateManager().getKey("GoldKey").getItem().clone();
            } else {
                reward = main.getCrateManager().getKey("IronKey").getItem().clone();
            }

            Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(player.getInventory(), reward);

            if (leftovers.values().size() > 0) {
                MessageManager.sendMessage(player, "&cThis item could not fit in your inventory, and was dropped to the ground.");

                for (ItemStack itemStack : leftovers.values()) {
                    Item item = player.getWorld().dropItem(player.getEyeLocation(), itemStack);
                    item.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(1));
                }

                return;
            }

            double anotherRandom = Math.random();
            int amount;

            if (anotherRandom <= 0.05) {
                amount = 300;
            } else if (anotherRandom <= 0.1) {
                amount = 250;
            } else if (anotherRandom <= 0.25) {
                amount = 150;
            } else {
                amount = 75;
            }

            Account.getAccount(player.getUniqueId()).setBalance(Account.getAccount(player.getUniqueId()).getBalance() + amount);

            MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a, and &c" + MessageManager.format(amount) + "&a! Vote using &3/vote&a!");
            MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
        }

        if (voteCount >= 100) {
            voteCount = 0;

            ItemStack reward = main.getCrateManager().getKey("ObsidianKey").getItem().clone();

            double anotherRandom = Math.random();
            int amount;

            if (anotherRandom <= 0.05) {
                amount = 300;
            } else if (anotherRandom <= 0.1) {
                amount = 250;
            } else if (anotherRandom <= 0.25) {
                amount = 150;
            } else {
                amount = 75;
            }

            for (Player online : PlayerUtility.getOnlinePlayers()) {
                Map<Integer, ItemStack> leftovers = InventoryWorkaround.addItems(online.getInventory(), reward);

                if (leftovers.values().size() > 0) {
                    MessageManager.sendMessage(online, "&cThis item could not fit in your inventory, and was dropped to the ground.");

                    for (ItemStack itemStack : leftovers.values()) {
                        Item item = online.getWorld().dropItem(online.getEyeLocation(), itemStack);
                        item.setVelocity(online.getEyeLocation().getDirection().normalize().multiply(1));
                    }

                    return;
                }

                Account.getAccount(online.getUniqueId()).setBalance(Account.getAccount(online.getUniqueId()).getBalance() + amount);
            }

            MessageManager.broadcastMessage("&f[&3Voting&f]: &5&l100 &aconsecutive votes has been reached, everyone online gets 1 " + reward.getItemMeta().getDisplayName() + "&a, and &c" + MessageManager.format(amount) + "&a! Vote using &3/vote&a!");
        }
    }
}
