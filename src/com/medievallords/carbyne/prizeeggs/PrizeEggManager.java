package com.medievallords.carbyne.prizeeggs;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.ParticleEffect;
import com.medievallords.carbyne.utils.StaticClasses;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class PrizeEggManager {

    private final Set<Location> altarLocations = new HashSet<>();
    private final Set<Integer> altarIDS = new HashSet<>();
    private final ArrayList<Reward> rewards = new ArrayList<>();
    private int rewardsAmount = 0, taskId = 0;

    public PrizeEggManager() {
        load();

        runAltarCheck();
    }

    public void reload() {
        try {
            Carbyne.getInstance().setPrizeEggFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getPrizeEggFile()));
            Carbyne.getInstance().getPrizeEggFileConfiguration().save(Carbyne.getInstance().getPrizeEggFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    private void load() {
        rewards.clear();
        altarLocations.clear();

        for (int i : altarIDS)
            Bukkit.getScheduler().cancelTask(i);

        altarIDS.clear();

        ConfigurationSection section = Carbyne.getInstance().getPrizeEggFileConfiguration();

        for (String locationString : section.getStringList("Locations"))
            altarLocations.add(LocationSerialization.deserializeLocation(locationString));

        this.rewardsAmount = section.getInt("RewardsAmount");

        ConfigurationSection rewardsSection = section.getConfigurationSection("Rewards");
        if (rewardsSection == null) {
            rewardsSection = section.createSection("Rewards");

            try {
                Carbyne.getInstance().getPrizeEggFileConfiguration().save(Carbyne.getInstance().getPrizeEggFile());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        for (String rewardId : rewardsSection.getKeys(false)) {
            int itemId = rewardsSection.getInt(rewardId + ".ItemID");
            int itemData = rewardsSection.getInt(rewardId + ".ItemData");
            int amount = rewardsSection.getInt(rewardId + ".Amount");
            String displayName = rewardsSection.getString(rewardId + ".DisplayName");
            String gearCode = "";
            List<String> lore = rewardsSection.getStringList(rewardId + ".Lore");
            List<String> enchantments = rewardsSection.getStringList(rewardId + ".Enchantments");
            List<String> commands = rewardsSection.getStringList(rewardId + ".Commands");
            boolean displayItemOnly = rewardsSection.getBoolean(rewardId + ".DisplayItem");
            int chance = rewardsSection.getInt(rewardId + ".Chance");
            int slot = rewardsSection.getInt(rewardId + ".Slot");

            if (displayName != null) {
                if (displayName.contains("randomgear")) {
                    gearCode = displayName;
                    displayName = "&6Randomly Selected Gear";
                    lore.clear();
                    lore.add("&eGives a random carbyne gear item.");
                } else {
                    if (StaticClasses.gearManager.getCarbyneGear(displayName) != null) {
                        gearCode = displayName;
                        displayName = StaticClasses.gearManager.getCarbyneGear(gearCode).getDisplayName();
                    }
                }
            }

            Reward reward = new Reward(Integer.valueOf(rewardId), itemId, itemData, amount, gearCode);
            reward.setDisplayName(displayName);
            reward.setLore(lore);
            for (String s : enchantments) {
                String[] args = s.split(",");
                reward.getEnchantments().put(Enchantment.getByName(args[0]), Integer.valueOf(args[1]));

            }

            reward.setCommands(commands);
            reward.setDisplayItemOnly(displayItemOnly);
            reward.setChance(chance);
            reward.setSlot(slot);
            rewards.add(reward);
        }

        runAltarEffect();
    }

    public List<Reward> rollRewards() {
        ArrayList<Reward> rewards = new ArrayList<>();

        int totalPercentage = 0;

        for (Reward reward : this.rewards)
            totalPercentage += reward.getChance();

        for (int a = 0; a < rewardsAmount; a++) {
            double random = Maths.randomNumberBetween(totalPercentage, 0);
            double last = 0;
            for (Reward reward : this.rewards) {
                double value = reward.getChance();

                if (random >= last && random < value + last) {

                    rewards.add(reward);
                    break;
                } else
                    last = value + last;
            }
        }

        return rewards;

    }

    public void knockbackPlayer(Player player, Location relative) {
        org.bukkit.util.Vector nv = player.getLocation().clone().subtract(relative).toVector().multiply(2);
        player.setVelocity(nv);
    }

    private void runAltarCheck() {
//        new BukkitRunnable() {
//            final ChatColor[] colors = {
//                    ChatColor.DARK_RED,
//                    ChatColor.RED,
//                    ChatColor.GOLD,
//                    ChatColor.YELLOW,
//                    ChatColor.DARK_GREEN,
//                    ChatColor.GREEN,
//                    ChatColor.AQUA,
//                    ChatColor.DARK_AQUA,
//                    ChatColor.BLUE,
//                    ChatColor.LIGHT_PURPLE,
//                    ChatColor.DARK_PURPLE
//            };
//
//            private int index = 0;
//            private boolean reverse = true;
//
//            @Override
//            public void run() {
//                if (reverse) {
//                    index++;
//                } else {
//                    index--;
//                }
//
//                if (index >= 10) {
//                    reverse = false;
//                }
//
//                if (index <= 0) {
//                    reverse = true;
//                }
//
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    for (int i = 0; i < player.getInventory().getSize(); i++) {
//                        ItemStack itemStack = player.getInventory().getItem(i);
//                        if (itemStack == null) {
//                            continue;
//                        }
//
//                        if (itemStack.getType() != Material.DRAGON_EGG) {
//                            continue;
//                        }
//
//                        String name = "PRIZED EGG";
//
//                        // P R I Z E D   E G G
//                        // 0 1 2 3 4 5 6 7 8 9
//
//                        String total = "&r&d&l" + name.substring(0, index) + "&b&l" + name.substring(index, name.length());
//
//                        //Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', total));
//
//                        ItemStack itemStack1 = new ItemBuilder(itemStack).name(total).build();
//                        player.getInventory().setItem(i, itemStack1);
//                    }
//                }
//            }
//        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 3L);
//
//        /*new BukkitRunnable() {
//            final ChatColor[] colors = {
//                    ChatColor.DARK_RED,
//                    ChatColor.RED,
//                    ChatColor.GOLD,
//                    ChatColor.YELLOW,
//                    ChatColor.DARK_GREEN,
//                    ChatColor.GREEN,
//                    ChatColor.AQUA,
//                    ChatColor.DARK_AQUA,
//                    ChatColor.BLUE,
//                    ChatColor.LIGHT_PURPLE,
//                    ChatColor.DARK_PURPLE
//            };
//
//            private int index = 0;
//            private int prevIndex = 0;
//
//            public int getI() {
//                index--;
//                if (index < 0) {
//                    index = colors.length - 1;
//                }
//
//                return index;
//            }
//
//            @Override
//            public void run() {
//                prevIndex++;
//                if (prevIndex >= colors.length) {
//                    prevIndex = 0;
//                }
//
//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    for (int i = 0; i < player.getInventory().getSize(); i++) {
//                        ItemStack itemStack = player.getInventory().getItem(i);
//                        if (itemStack == null) {
//                            continue;
//                        }
//
//                        if (itemStack.getType() != Material.DRAGON_EGG) {
//                            continue;
//                        }
//
//                        index = prevIndex;
//
//                        String name = colors[getI()]
//                                + "P" + colors[getI()]
//                                + "R" + colors[getI()]
//                                + "I" + colors[getI()]
//                                + "Z" + colors[getI()]
//                                + "E" + colors[getI()]
//                                + "D§r"
//                                + " " + colors[getI()]
//                                + "E" + colors[getI()]
//                                + "G" + colors[getI()]
//                                + "G";
//
//                        ItemStack itemStack1 = new ItemBuilder(itemStack).name(name).build();
//                        player.getInventory().setItem(i, itemStack1);
//                    }
//                }
//            }
//        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 3L);*/

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location altarLoc : altarLocations)
                    for (Player player : altarLoc.getWorld().getPlayers()) {
                        if (player.getLocation().getWorld().equals(altarLoc.getWorld())) {

                            double distance = player.getLocation().distance(altarLoc.clone().add(0.5, 0.3, 0.5));

                            if (distance <= 1)
                                knockbackPlayer(player, altarLoc);
                        }
                    }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 10L);
    }

    private void runAltarEffect() {
        for (Location location : altarLocations) {
            altarIDS.add(Bukkit.getScheduler().scheduleAsyncRepeatingTask(Carbyne.getInstance(), new Runnable() {
                private double theta = 0, radius = 2;
                private Location loc = location.clone().add(0.5, 2.5, 0.5);
                private final ParticleEffect.OrdinaryColor purple = new ParticleEffect.OrdinaryColor(244, 66, 244);
                private final ParticleEffect.OrdinaryColor black = new ParticleEffect.OrdinaryColor(0, 0, 0);

                @Override
                public void run() {
                    theta += Math.PI / 16;

                    double x = Math.cos(theta) * radius;
                    double y = Math.cos(theta) * radius;
                    double z = Math.sin(theta) * radius;

                    loc.add(x, y, z);
                    ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                    loc.subtract(x, 0, z);
                    loc.subtract(x, 0, z);
                    ParticleEffect.REDSTONE.display(black, loc, 40, false);
                    loc.add(x, -y, z);
                }
            }, 0, 1));
        }
    }
}
