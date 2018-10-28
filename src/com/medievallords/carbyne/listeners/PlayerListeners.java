package com.medievallords.carbyne.listeners;

import com.destroystokyo.paper.Title;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.objects.Account;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerListeners implements Listener {

    @Getter
    private static int voteCount = 0;
    private String joinMessage;
    private String[] subtitles;
    //@Getter
    //private static List<String> chunksUpdated = new ArrayList<>();

    public PlayerListeners() {
        joinMessage = ChatColor.translateAlternateColorCodes('&', Carbyne.getInstance().getConfig().getString("JoinMessage"));
        List<String> initSubs = Carbyne.getInstance().getConfig().getStringList("JoinMessageSubtitles");
        subtitles = initSubs.toArray(new String[0]);

        if (subtitles.length < 1 || subtitles[0] == null)
            subtitles = new String[]{};

        for (int i = 0; i < subtitles.length; i++)
            subtitles[i] = ChatColor.translateAlternateColorCodes('&', subtitles[i]);
    }

//    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent event) {
//        if (!chunksUpdated.contains(event.getWorld().getName() + "," + event.getChunk().getX() + "," + event.getChunk().getZ())) {
//            chunksUpdated.add(event.getWorld().getName() + "," + event.getChunk().getX() + "," + event.getChunk().getZ());
//            for (BlockState blockState : event.getChunk().getTileEntities()) {
//                if (blockState instanceof ContainerBlock) {
//                    ContainerBlock block = (ContainerBlock) blockState;
//
//                    for (int x = 0; x < block.getInventory().getSize(); x++) {
//                        ItemStack itemStack = block.getInventory().getItem(x);
//                        if (itemStack == null) {
//                            continue;
//                        }
//
//                        CarbyneGear gear = StaticClasses.gearManager.getCarbyneGear(itemStack);
//                        if (gear != null && (gear.getState() == GearState.HIDDEN || gear.getState() == GearState.CONCEALED)) {
//                            block.getInventory().setItem(x, null);
//                        }
//
//                        switch (itemStack.getType()) {
//                            case DRAGON_EGG:
//                            case GOLD_NUGGET:
//                            case GOLD_INGOT:
//                            case GOLD_BLOCK:
//                                block.getInventory().setItem(x, null);
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);

        if (!player.hasPlayedBefore())
            player.sendTitle(new Title.Builder().title(joinMessage).subtitle(subtitles[Maths.randomNumberBetween(subtitles.length, 0)]).stay(55).build());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        for (ItemStack itemStack : event.getInventory().getContents())
            if (itemStack != null && itemStack.getMaxStackSize() == 1 && itemStack.getAmount() > 1)
                itemStack.setAmount(1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (ItemStack itemStack : event.getInventory().getContents())
            if (itemStack != null && itemStack.getMaxStackSize() == 1 && itemStack.getAmount() > 1)
                itemStack.setAmount(1);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                switch (item.getType()) {
                    case TRAPPED_CHEST:
                    case CHEST:
                    case HOPPER:
                    case DISPENSER:
                    case DROPPER:
                    case FURNACE:
                    case BREWING_STAND:
                        net.minecraft.server.v1_12_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
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
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, .1f);
            voteCount++;

            if (voteCount % 15 == 0 && voteCount < 500)
                MessageManager.broadcastMessage("&f[&3Voting&f]: &5&l" + voteCount + " &aconsecutive votes has been reached! Vote using &3/vote&a!");

            StaticClasses.profileManager.getProfile(player.getUniqueId()).setKitPoints(StaticClasses.profileManager.getProfile(player.getUniqueId()).getKitPoints() + 1);

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

            MessageManager.broadcastMessage("&f[&3Voting&f]: &5" + player.getName() + " &ahas voted and has received &c1 KitPoint&a, and &c" + MessageManager.format(amount) + "&a! Vote using &3/vote&a!");
            MessageManager.sendMessage(player, "&f[&3Voting&f]: &aYou have received 1 KitPoint&a! Thank you for voting!");
        }

        if (voteCount >= 500) {
            voteCount = 0;

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

            for (Player online : PlayerUtility.getOnlinePlayers())
                StaticClasses.profileManager.getProfile(online.getUniqueId()).setKitPoints(StaticClasses.profileManager.getProfile(online.getUniqueId()).getKitPoints() + 1);

            MessageManager.broadcastMessage("&f[&3Voting&f]: &5&l500 &aconsecutive votes has been reached, everyone online gets &c1 KitPoint&a&a, and &c" + MessageManager.format(amount) + "&a! Vote using &3/vote&a!");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ItemStack blood = new ItemBuilder(Material.INK_SACK).durability(1).build();
        ItemStack blood2 = new ItemBuilder(Material.REDSTONE).build();
        ItemStack bone = new ItemBuilder(Material.BONE).build();

        Player player = event.getEntity();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 0.15f);
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
                        item.getWorld().playSound(item.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1f);
                        i++;
                    }

                if (items.size() <= 0 || i >= 3)
                    cancel();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 5);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Item item : items)
                    item.remove();
                items.clear();
            }
        }.runTaskLater(Carbyne.getInstance(), 150);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!StaticClasses.staffManager.isVanished(event.getPlayer()))
            if (event.getFrom().getWorld().equals(event.getTo().getWorld()) && event.getFrom().distance(event.getTo()) > 10) {
                event.getPlayer().playSound(event.getTo(), Sound.ENTITY_ENDERMEN_TELEPORT, .6f, 1);
                event.getPlayer().playSound(event.getFrom(), Sound.ENTITY_ENDERMEN_TELEPORT, .6f, 1);
            }
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        event.getPlayer().setAllowFlight(false);
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        if (StaticClasses.gamemodeManager.getFlyPlayers().contains(player) || StaticClasses.gamemodeManager.getGmPlayers().contains(player))
            return;

        PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
        if (playerHealth.getStamina() >= 15 && playerHealth.isSkillsToggled()) {
            Board board = Board.getByPlayer(player.getUniqueId());

            if (board != null) {
                BoardCooldown skillCooldown = board.getCooldown("skill");

                if (skillCooldown == null) {
                    playerHealth.setStamina(playerHealth.getStamina() - 15);
                    event.setCancelled(true);
                    player.setAllowFlight(false);

                    float hForce = 15 / 10.0F;
                    float vForce = 12 / 10.0F;

                    Vector direction = player.getLocation().getDirection();
                    Vector forward = direction.multiply(3);

                    if (playerHealth.isSprintToggled())
                        forward.multiply(4.5);

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
                        all.playSound(all.getLocation(), Sound.ENTITY_HORSE_JUMP, 3.0F, 0.533F);
                }
            } else
                event.setCancelled(true);
        } else
            event.setCancelled(true);
    }

    @EventHandler
    public void onSprintToggle(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {

            if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
                return;

            if (StaticClasses.gamemodeManager.getFlyPlayers().contains(event.getPlayer()) || StaticClasses.gamemodeManager.getGmPlayers().contains(event.getPlayer()))
                return;

            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(event.getPlayer().getUniqueId());

            if (System.currentTimeMillis() - playerHealth.getSprintCombo() <= 1000 && playerHealth.isSkillsToggled()) {
                if (playerHealth.getStamina() > 6 && !playerHealth.isSprintToggled()) {
                    if (!event.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
                        playerHealth.setSprintToggled(true);
                        event.getPlayer().setWalkSpeed(0.4f);
                        playerHealth.setSprintCombo(0);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!event.getPlayer().isOnline())
                                    cancel();

                                if (playerHealth.isSprintToggled())
                                    ParticleEffect.SMOKE_LARGE.display(0.0F, 0.0F, 0.0F, 0.03F, 2, event.getPlayer().getLocation().subtract(0.0, 0.1, 0.0), 30, false);
                                else
                                    cancel();
                            }
                        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);

                        MessageManager.sendMessage(event.getPlayer(), "&aSuper Sprint has been enabled!");

                    } else {
                        MessageManager.sendMessage(event.getPlayer(), "&cYou cannot use Super Sprint while you have speed.");
                    }
                } else if (playerHealth.isSprintToggled()) {
                    playerHealth.setSprintToggled(false);
                    event.getPlayer().setWalkSpeed(0.2f);
                    playerHealth.setSprintCombo(0);
                    MessageManager.sendMessage(event.getPlayer(), "&cSuper Sprint has been disabled!");
                }
            } else {
                playerHealth.setSprintCombo(System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onPiledriveCombo(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            if (player.getInventory().getItemInMainHand().getType().toString().contains("SWORD") || player.getInventory().getItemInMainHand().getType().toString().contains("AXE") || player.getInventory().getItemInMainHand().getType().toString().contains("HOE")) {
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());

                if (playerHealth.getStamina() >= 60 && playerHealth.isSkillsToggled()) {
                    Board board = Board.getByPlayer(player.getUniqueId());

                    if (board != null) {
                        BoardCooldown skillCooldown = board.getCooldown("skill");

                        if (skillCooldown == null) {
                            if (!playerHealth.isPiledriveBoolReady()) {
                                if (System.currentTimeMillis() - playerHealth.getPiledriveCombo() <= 1000) {
                                    playerHealth.setPiledriveReady(3);
                                    playerHealth.setPiledriveCombo(0);
                                    playerHealth.setPiledriveBoolReady(true);
                                    MessageManager.sendMessage(player, "&aReady to piledrive! Damage an enemy to initiate!");
                                } else
                                    playerHealth.setPiledriveCombo(System.currentTimeMillis());
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
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(damager.getUniqueId());

//            if (StaticClasses.duelManager.getDuelFromUUID(damaged.getUniqueId()) != null) {
//                if (playerHealth.isPiledriveBoolReady() && playerHealth.getStamina() >= 60)
//                    pileDrive(damaged, damager);
//            } else {

            TownBlock townBlock = TownyUniverse.getTownBlock(damaged.getLocation());

            if (townBlock != null) {
                if (townBlock.getPermissions().pvp)
                    if (playerHealth.isPiledriveBoolReady() && playerHealth.getStamina() >= 60)
                        pileDrive(damaged, damager);
            } else {
                if (playerHealth.isPiledriveBoolReady() && playerHealth.getStamina() >= 60)
                    pileDrive(damaged, damager);
            }
            //}
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
        PlayerHealth damagerPlayer = PlayerHealth.getPlayerHealth(damager.getUniqueId());
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.CONFUSION, 100, 2);
        PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.BLINDNESS, 60, 2);
        PotionEffect potionEffect3 = new PotionEffect(PotionEffectType.SLOW, 80, 2);
        damaged.addPotionEffect(potionEffect);
        damaged.addPotionEffect(potionEffect2);
        damaged.addPotionEffect(potionEffect3);
        damagerPlayer.setPiledriveReady(0);
        damagerPlayer.setPiledriveBoolReady(false);
        damagerPlayer.setStamina(damagerPlayer.getStamina() - 60);
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect2 = FireworkEffect.builder().withColor(Color.ORANGE).trail(true).withFade(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
        InstantFirework.spawn(damaged.getLocation(), effect);
        InstantFirework.spawn(damaged.getLocation(), effect2, effect);
        PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(damaged.getUniqueId());
        playerHealth.setHealth(playerHealth.getHealth() - (playerHealth.getMaxHealth() * 0.02));

        new BoardCooldown(Board.getByPlayer(damager.getUniqueId()), "skill", 10.0D);

        damaged.sendTitle(new Title.Builder()
                .title(ChatColor.translateAlternateColorCodes('&', "&cYou have been piledriven!")).stay(200)
                .subtitle(ChatColor.translateAlternateColorCodes('&', "&4Press SHIFT to counter!")).stay(200)
                .build());

        if (clear(damaged)) {
            Chicken chicken = damaged.getWorld().spawn(damaged.getLocation(), Chicken.class);
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 100000));
            chicken.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 100000));
            chicken.setPassenger(damaged);
            chicken.setMaxHealth(1000.0);
            chicken.setHealth(1000.0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!chicken.isDead()) {
                        chicken.eject();
                        chicken.setHealth(0);
                    }
                }
            }.runTaskLater(Carbyne.getInstance(), 200L);
        }
    }

    @EventHandler
    public void vehicleDismountEvent(VehicleExitEvent event) {
        if (event.getExited().getType().equals(EntityType.CHICKEN))
            event.getExited().remove();
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