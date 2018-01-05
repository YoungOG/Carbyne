package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.utils.*;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerListeners implements Listener {

    @Getter
    private static int voteCount = 0;
    private Carbyne main = Carbyne.getInstance();
    private String joinMessage;
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
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);

        if (!player.hasPlayedBefore())
            player.sendTitle(new Title.Builder().title(joinMessage).subtitle(subtitles[Maths.randomNumberBetween(subtitles.length, 0)]).stay(55).build());


//        try {
//            //Get the MapManager instance
//            MapManager mapManager = ((MapManagerPlugin) Bukkit.getPluginManager().getPlugin("MapManager")).getMapManager();
//
//            //Wrap the local file "myImage.png"
//            MapWrapper mapWrapper = mapManager.wrapImage(ImageIO.read(new URL("https://res.cloudinary.com/teepublic/image/private/s--s51yUeiA--/t_Preview/b_rgb:191919,c_limit,f_jpg,h_630,q_90,w_630/v1463091852/production/designs/510568_1.jpg")));
//            MapController mapController = mapWrapper.getController();
//
//            //Add "inventivetalent" as a viewer and send the content
//            mapController.addViewer(player);
//            mapController.sendContent(player);
//
//            //At this point, the player is able to see the image
//            //So we can show we can show it in ItemFrames
//            mapController.showInFrame(player, PlayerUtility.getFrame(new Location(Bukkit.getWorld("world"), -763.5, 105, 308.5)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, .1f);

        if (player != null) {
            voteCount++;

            if (voteCount % 15 == 0 && voteCount < 100)
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5&l" + voteCount + " &aconsecutive votes has been reached! Vote using &3/vote&a!");

            double random = Math.random();

            ItemStack reward;

            if (random <= 0.02)
                reward = main.getCrateManager().getKey("ObsidianKey").getItem().clone();
            else if (random <= 0.028)
                reward = main.getCrateManager().getKey("EmeraldKey").getItem().clone();
            else if (random <= 0.15)
                reward = main.getCrateManager().getKey("DiamondKey").getItem().clone();
            else if (random <= 0.25)
                reward = main.getCrateManager().getKey("GoldKey").getItem().clone();
            else
                reward = main.getCrateManager().getKey("IronKey").getItem().clone();

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

            if (anotherRandom <= 0.05)
                amount = 300;
            else if (anotherRandom <= 0.1)
                amount = 250;
            else if (anotherRandom <= 0.25)
                amount = 150;
            else
                amount = 75;

            Account.getAccount(player.getUniqueId()).setBalance(Account.getAccount(player.getUniqueId()).getBalance() + amount);

            MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received a " + reward.getItemMeta().getDisplayName() + "&a, and &c" + MessageManager.format(amount) + "&a! Vote using &3/vote&a!");
            MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received a " + reward.getItemMeta().getDisplayName() + "&a! Thank you for voting!");
        }

        if (voteCount >= 100) {
            voteCount = 0;

            ItemStack reward = main.getCrateManager().getKey("ObsidianKey").getItem().clone();

            double anotherRandom = Math.random();
            int amount;

            if (anotherRandom <= 0.05)
                amount = 300;
            else if (anotherRandom <= 0.1)
                amount = 250;
            else if (anotherRandom <= 0.25)
                amount = 150;
            else
                amount = 75;

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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ItemStack blood = new ItemBuilder(Material.INK_SACK).durability(1).build();
        ItemStack blood2 = new ItemBuilder(Material.REDSTONE).build();
        ItemStack bone = new ItemBuilder(Material.BONE).build();

        Player player = event.getEntity();
        player.getWorld().playSound(player.getLocation(), Sound.VILLAGER_HIT, 1, 0.15f);
        ParticleEffect.LAVA.display(0, 0, 0, 0, 2, player.getLocation(), 60, false);
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            Item item = player.getWorld().dropItem(player.getLocation(), blood);
            Item item2 = player.getWorld().dropItem(player.getLocation(), bone);
            Item item3 = player.getWorld().dropItem(player.getLocation(), blood2);
            item.setVelocity(new Vector(Maths.randomNumberBetween(0, 10) - 5, Maths.randomNumberBetween(0, 10) - 5, Maths.randomNumberBetween(0, 10) - 5).multiply(1.1));
            item2.setVelocity(new Vector(Maths.randomNumberBetween(0, 10) - 5, Maths.randomNumberBetween(0, 10) - 5, Maths.randomNumberBetween(0, 10) - 5).multiply(1.1));
            item3.setVelocity(new Vector(Maths.randomNumberBetween(0, 10) - 5, Maths.randomNumberBetween(0, 10) - 5, Maths.randomNumberBetween(0, 10) - 5).multiply(1.1));
            item.setPickupDelay(1000000000);
            item2.setPickupDelay(1000000000);
            item3.setPickupDelay(1000000000);
            items.add(item);
            items.add(item2);
            items.add(item3);
        }

        new BukkitRunnable() {
            private int i = 0;

            @Override
            public void run() {
                for (Item item : items)
                    if (item.isOnGround()) {
                        item.getWorld().playSound(item.getLocation(), Sound.LAVA_POP, 1, 1f);
                        i++;
                    }

                if (items.size() <= 0 || i >= 3)
                    cancel();
            }
        }.runTaskTimerAsynchronously(main, 0, 5);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Item item : items)
                    item.remove();
                items.clear();
            }
        }.runTaskLater(main, 150);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().equals(event.getTo().getWorld()) && event.getFrom().distance(event.getTo()) > 10) {
            event.getPlayer().playSound(event.getTo(), Sound.ENDERMAN_TELEPORT, .6f, 1);
        }
    }
}