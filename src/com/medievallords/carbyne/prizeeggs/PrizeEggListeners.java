package com.medievallords.carbyne.prizeeggs;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.gear.types.CarbyneGear;
import com.medievallords.carbyne.gear.types.GearState;
import com.medievallords.carbyne.utils.*;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PrizeEggListeners implements Listener {

    private final ParticleEffect.OrdinaryColor purple = new ParticleEffect.OrdinaryColor(102, 0, 51);
    private HashMap<UUID, UUID> playerPickups = new HashMap<>();

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        if (!event.getBlock().getType().equals(Material.getMaterial(StaticClasses.gearManager.getPrizeEggId())))
            return;

        ItemStack itemStack = event.getItemInHand();
        if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            event.setCancelled(true);
            event.getPlayer().setItemInHand(null);
            MessageManager.sendStaffMessage(Bukkit.getConsoleSender(), "" + event.getPlayer().getName() + " tried to use a fake prize egg.");
            return;
        }

        String loreOne = itemStack.getItemMeta().getLore().get(0);
        if (loreOne == null)
            return;

        if (!HiddenStringUtils.extractHiddenString(loreOne).equals(StaticClasses.gearManager.getPrizeEggCode())) {
            event.setCancelled(true);
            event.getPlayer().setItemInHand(null);
            MessageManager.sendStaffMessage(Bukkit.getConsoleSender(), "" + event.getPlayer().getName() + " tried to use a fake prize egg.");
            return;
        }

        Location placedLocation = event.getBlock().getLocation();
        if (!StaticClasses.prizeEggManager.getAltarLocations().contains(placedLocation)) {
            event.setCancelled(true);
            return;
        }


        event.setCancelled(true);

        if (itemStack.getAmount() <= 1)
            event.getPlayer().setItemInHand(null);
        else {
            itemStack.setAmount(itemStack.getAmount() - 1);
            event.getPlayer().setItemInHand(itemStack);
        }


        event.getBlock().setType(Material.AIR);
        List<Reward> rewards = StaticClasses.prizeEggManager.rollRewards();

        placedLocation.add(0.5, 0.2, 0.5);

        FallingBlock fallingBlock = placedLocation.getWorld().spawnFallingBlock(placedLocation, Material.DRAGON_EGG, (byte) 0);
        fallingBlock.setVelocity(new Vector(0, 0.9, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fallingBlock.isDead()) {
                    cancel();
                    return;
                }

                ParticleEffect.REDSTONE.display(purple, fallingBlock.getLocation(), 40, false);
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                Location center = fallingBlock.getLocation();
                int max = FireworkEffect.Type.values().length;
                FireworkEffect.Type type = FireworkEffect.Type.values()[Maths.randomNumberBetween(max, 0)];
                if (type == FireworkEffect.Type.CREEPER)
                    type = FireworkEffect.Type.BALL_LARGE;

                InstantFirework.spawn(center, FireworkEffect.builder().with(type)
                        .withColor(Color.fromRGB(Maths.randomNumberBetween(256, 0), Maths.randomNumberBetween(256, 0), Maths.randomNumberBetween(256, 0)))
                        .withFade(Color.fromRGB(Maths.randomNumberBetween(256, 0), Maths.randomNumberBetween(256, 0), Maths.randomNumberBetween(256, 0)))
                        .build());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Reward reward : rewards) {
                            ItemStack itemStack = reward.getItem(false);
                            CarbyneGear gear = StaticClasses.gearManager.getCarbyneGear(itemStack);
                            if (gear != null) {
                                if (gear.getState() == GearState.HIDDEN) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8[&d&lPrize Egg&8] &a" + event.getPlayer().getName() + " &7has found " + itemStack.getItemMeta().getDisplayName() + " &7in a prize egg!"));
                                    if (Cooldowns.tryCooldown(event.getPlayer().getUniqueId(), "prizeEgg:effect", 5000))
                                        runRareEffect(center, getFireworkEffect());
                                }

                                StringUtils.logToFile("[Carbyne PB] " + new Date().toString() + " --> " + event.getPlayer().getName() + " has found " + itemStack.getItemMeta().getDisplayName(), "carbyneItemLog.txt");
                            }

                            Item item = center.getWorld().dropItemNaturally(center, itemStack);
                            playerPickups.put(item.getUniqueId(), event.getPlayer().getUniqueId());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (item.isOnGround() || item.isDead()) {
                                        cancel();
                                        return;
                                    }

                                    item.setVelocity(item.getVelocity().setY(-0.2));

                                    ParticleEffect.FLAME.display(0, 0, 0, 0, 1, item.getLocation(), 40, false);
                                }
                            }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);
                        }
                    }
                }.runTaskLater(Carbyne.getInstance(), 9);

                fallingBlock.remove();
            }
        }.runTaskLater(Carbyne.getInstance(), 15);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPickup(PlayerPickupItemEvent event) {
        if (!playerPickups.containsKey(event.getItem().getUniqueId()))
            return;

        if (!event.getPlayer().getUniqueId().equals(playerPickups.get(event.getItem().getUniqueId()))) {
            event.setCancelled(true);
            return;
        }

        playerPickups.remove(event.getItem().getUniqueId());
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent event) {
        if (!playerPickups.containsKey(event.getEntity().getUniqueId()))
            return;

        playerPickups.remove(event.getEntity().getUniqueId());
    }

    private void runRareEffect(final Location centerLocation, final FireworkEffect[] effects) {
        new BukkitRunnable() {
            private double t = 0;
            private int times = 0;

            @Override
            public void run() {
                t = t + 0.35;
                final double x = Math.sin(t) * 5;
                final double z = Math.cos(t) * 5;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        centerLocation.add(x, 0, z);
                        InstantFirework.spawn(centerLocation, effects);
                        centerLocation.subtract(x, 0, z);
                    }
                }.runTask(Carbyne.getInstance());

                times++;
                if (times > 60)
                    this.cancel();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);
    }

    public FireworkEffect[] getFireworkEffect() {
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(false).withColor(Color.PURPLE).withFade(Color.BLACK).with(FireworkEffect.Type.BURST).build();
        FireworkEffect effect1 = FireworkEffect.builder().trail(false).flicker(false).withColor(Color.PURPLE).withFade(Color.BLACK).with(FireworkEffect.Type.BALL).build();
        return new FireworkEffect[]{
                effect, effect1
        };
    }
}
