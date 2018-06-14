package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;
import org.spigotmc.event.entity.EntityDismountEvent;

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

            reward = main.getCrateManager().getKey("MysticalKey").getItem().clone();

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

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        Profile profile = main.getProfileManager().getProfile(player.getUniqueId());
        if (profile.getStamina() >= 15 && profile.isSkillsToggled()) {
            Board board = Board.getByPlayer(player);

            if (board != null) {
                BoardCooldown skillCooldown = board.getCooldown("skill");

                if (skillCooldown == null) {
                    profile.setStamina(profile.getStamina() - 15);
                    event.setCancelled(true);
                    player.setAllowFlight(false);

                    float hForce = 15 / 10.0F;
                    float vForce = 12 / 10.0F;

                    Vector direction = player.getLocation().getDirection();
                    Vector forward = direction.multiply(3);

                    if (profile.isSprintToggled())
                        forward.multiply(2.5);

                    Vector vector = player.getLocation().toVector().subtract(player.getLocation().add(0, 3, 0).toVector());
                    vector.add(forward);
                    vector.setY(5);
                    vector.normalize();
                    vector.multiply(hForce * 0.9);
                    vector.setY(vForce * 0.9);
                    player.setVelocity(vector);

                    new BoardCooldown(board, "skill", 10.0D);

                    ParticleEffect.CLOUD.display(0.0F, 0.0F, 0.0F, 0.004F, 100, player.getLocation().subtract(0.0, 0.1, 0.0), 15, false);

                    for (Player all : PlayerUtility.getPlayersInRadius(player.getLocation(), 15))
                        all.playSound(all.getLocation(), Sound.HORSE_JUMP, 3.0F, 0.533F);
                }
            }
        }
    }

    @EventHandler
    public void onSprintToggle(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            Profile profile = main.getProfileManager().getProfile(event.getPlayer().getUniqueId());
            if (System.currentTimeMillis() - profile.getSprintCombo() <= 1000) {
                if (profile.getStamina() > 6 && !profile.isSprintToggled()) {
                    profile.setSprintToggled(true);
                    event.getPlayer().setWalkSpeed(0.33f);
                    profile.setSprintCombo(0);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!event.getPlayer().isOnline())
                                cancel();

                            if (profile.isSprintToggled())
                                ParticleEffect.SMOKE_LARGE.display(0.0F, 0.0F, 0.0F, 0.03F, 2, event.getPlayer().getLocation().subtract(0.0, 0.1, 0.0), 30, false);
                            else
                                cancel();
                        }
                    }.runTaskTimerAsynchronously(main, 0, 1);

                    MessageManager.sendMessage(event.getPlayer(), "&aSuper Sprint has been enabled!");
                } else if (profile.isSprintToggled()) {
                    profile.setSprintToggled(false);
                    event.getPlayer().setWalkSpeed(0.2f);
                    profile.setSprintCombo(0);
                    MessageManager.sendMessage(event.getPlayer(), "&cSuper Sprint has been disabled!");
                }
            } else {
                profile.setSprintCombo(System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onPiledriveCombo(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            if (player.getItemInHand().getType().toString().contains("SWORD") || player.getItemInHand().getType().toString().contains("AXE") || player.getItemInHand().getType().toString().contains("HOE")) {
                Profile profile = main.getProfileManager().getProfile(player.getUniqueId());

                if (profile.getStamina() >= 60 && profile.isSkillsToggled()) {
                    Board board = Board.getByPlayer(player);

                    if (board != null) {
                        BoardCooldown skillCooldown = board.getCooldown("skill");

                        if (skillCooldown == null) {
                            if (!profile.isPiledriveBoolReady()) {
                                if (System.currentTimeMillis() - profile.getPiledriveCombo() <= 1000) {
                                    profile.setPiledriveReady(3);
                                    profile.setPiledriveCombo(0);
                                    profile.setPiledriveBoolReady(true);
                                    MessageManager.sendMessage(player, "&aReady to piledrive! Damage an enemy to initiate!");
                                } else {
                                    profile.setPiledriveCombo(System.currentTimeMillis());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPiledriver(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            Profile profile = main.getProfileManager().getProfile(damager.getUniqueId());

            if (main.getDuelManager().getDuelFromUUID(damaged.getUniqueId()) != null) {
                if (profile.isPiledriveBoolReady() && profile.getStamina() >= 60)
                    pileDrive(damaged, damager);
            } else {
                TownBlock townBlock = TownyUniverse.getTownBlock(damaged.getLocation());

                if (townBlock != null)
                    if (townBlock.getPermissions().pvp)
                        if (profile.isPiledriveBoolReady() && profile.getStamina() >= 60)
                            pileDrive(damaged, damager);
            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getDismounted() instanceof Chicken) {
                player.sendTitle(new Title.Builder()
                        .title("").stay(1)
                        .subtitle("").stay(1)
                        .build());
            }
        }
    }

    public void pileDrive(Player damaged, Player damager) {
        MessageManager.sendMessage(damager, "&aYou have piledrived &5" + damaged.getName() + "&a!");
        Profile damagerProfile = main.getProfileManager().getProfile(damager.getUniqueId());
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.CONFUSION, 140, 2);
        PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.BLINDNESS, 80, 2);
        PotionEffect potionEffect3 = new PotionEffect(PotionEffectType.SLOW, 100, 2);
        damaged.addPotionEffect(potionEffect);
        damaged.addPotionEffect(potionEffect2);
        damaged.addPotionEffect(potionEffect3);
        damagerProfile.setPiledriveReady(0);
        damagerProfile.setPiledriveBoolReady(false);
        damagerProfile.setStamina(damagerProfile.getStamina() - 60);
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect2 = FireworkEffect.builder().withColor(Color.ORANGE).trail(true).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        InstantFirework.spawn(damaged.getLocation(), effect);
        InstantFirework.spawn(damaged.getLocation(), effect2, effect);
        damaged.damage(10D);

        new BoardCooldown(Board.getByPlayer(damager), "skill", 10.0D);

        damaged.sendTitle(new Title.Builder()
                .title(ChatColor.translateAlternateColorCodes('&', "&cYou have been piledriven!")).stay(200)
                .subtitle(ChatColor.translateAlternateColorCodes('&', "&4Press SHIFT to counter!")).stay(200)
                .build());

        if (clear(damaged)) {
            Chicken chicken = damaged.getWorld().spawn(damaged.getLocation(), Chicken.class);
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 100000));
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 100000));
            chicken.setPassenger(damaged);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!chicken.isDead()) {
                        chicken.eject();
                        chicken.setHealth(0);
                    }
                }
            }.runTaskLater(main, 200L);
        }
    }

    public boolean clear(Player damaged) {
        int check = 0;

        if (!correctType(damaged.getLocation().clone().add(1, 0, 0).getBlock()))
            check += 1;
        if (!correctType(damaged.getLocation().clone().subtract(1, 0, 0).getBlock()))
            check += 1;
        if (!correctType(damaged.getLocation().clone().add(0, 0, 1).getBlock()))
            check += 1;
        if (!correctType(damaged.getLocation().clone().subtract(0, 0, 1).getBlock()))
            check += 1;
        if (!correctType(damaged.getLocation().clone().add(1, 0, 0).subtract(0, 0, 1).getBlock()))
            check += 10;
        if (!correctType(damaged.getLocation().clone().add(1, 0, 1).getBlock()))
            check += 10;
        if (!correctType(damaged.getLocation().clone().subtract(1, 0, 0).add(0, 0, 1).getBlock()))
            check += 1;
        if (!correctType(damaged.getLocation().clone().subtract(1, 0, 1).getBlock()))
            check += 10;

        return check == 0;
    }

    private boolean correctType(Block check) {
        return check.getType() == Material.AIR || check.getType() == Material.LONG_GRASS || check.getType() == Material.RED_ROSE || check.getType() == Material.YELLOW_FLOWER || check.getType() == Material.GRASS;
    }
}