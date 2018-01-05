package com.medievallords.carbyne.crates;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.crates.rewards.Reward;
import com.medievallords.carbyne.crates.rewards.RewardGenerator;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.Maths;
import com.medievallords.carbyne.utils.ParticleEffect;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

@Getter
@Setter
public class Crate {

    private Carbyne main = Carbyne.getInstance();
    private CrateManager crateManager = main.getCrateManager();

    private String name;
    private Location location;
    private ArrayList<Reward> rewards = new ArrayList<>();
    private HashMap<UUID, Inventory> crateOpeners = new HashMap<>();
    private HashMap<UUID, Integer> crateOpenersAmount = new HashMap<>();
    private ArrayList<UUID> editors = new ArrayList<>();
    private int rewardsAmount, progressIncreaseP, progressIncreaseM;

    public Crate(String name) {
        this.name = name;
    }

    public void save(FileConfiguration crateFileConfiguration) {
        ConfigurationSection configurationSection = crateFileConfiguration.getConfigurationSection("Crates");

        if (!configurationSection.isSet(name))
            configurationSection.createSection(name);

        if (!configurationSection.isSet(name + ".Locations"))
            configurationSection.createSection(name + ".Location");

        if (!configurationSection.isSet(name + ".Rewards"))
            configurationSection.createSection(name + ".Rewards");

        if (!configurationSection.isSet(name + ".RewardsAmount"))
            configurationSection.createSection(".RewardsAmount");

        if (location != null)
            configurationSection.set(name + ".Location", LocationSerialization.serializeLocation(location));

        if (rewardsAmount > 0)
            configurationSection.set(name + ".RewardsAmount", rewardsAmount);

        if (!configurationSection.isSet(name + ".ProgressIncreaseP"))
            configurationSection.set(name + ".ProgressIncreaseP", progressIncreaseP);

        if (!configurationSection.isSet(name + ".ProgressIncreaseM"))
            configurationSection.set(name + ".ProgressIncreaseM", progressIncreaseM);

        try {
            crateFileConfiguration.save(main.getCrateFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save the crate " + name + "!");
        }
    }

    public void editRewards(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Crate");

        for (Reward reward : getRewards()) {
            ItemStack itemStack = reward.getItem(true);

            if (reward.getCommands().size() > 0) {
                itemStack = new ItemBuilder(itemStack)
                        .addLore(" ")
                        .addLore("&aCommands:").build();
                for (String command : reward.getCommands())
                    new ItemBuilder(itemStack).addLore("&c" + command);
            }

            itemStack = new ItemBuilder(itemStack)
                    .addLore(" ")
                    .addLore("&aDisplay Item: &c" + reward.isDisplayItemOnly())
                    .addLore(" ")
                    .addLore("&aChance: &c" + reward.getChance() + "%").build();

            inventory.addItem(itemStack);
        }

        editors.add(player.getUniqueId());

        player.openInventory(inventory);
    }

    public void showRewards(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.AQUA + "" + ChatColor.BOLD + "Crate Rewards");
        List<Integer> randomGear = new ArrayList<>();

        for (Reward reward : rewards) {
            inventory.setItem(reward.getSlot(), new ItemBuilder(reward.getItem(true)).addLore("").build());
            if (reward.getGearCode().startsWith("randomgear"))
                randomGear.add(reward.getSlot());
        }

        player.openInventory(inventory);

        if (randomGear.isEmpty())
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getOpenInventory() == null) {
                    cancel();
                    return;
                }

                for (int p : randomGear) {
                    ItemStack randomCarbyne = main.getGearManager().getRandomCarbyneGear(true).getItem(false);
                    //ItemMeta meta = randomCarbyne.getItemMeta();
                    //meta.setLore(inventory.getItem(p).getItemMeta().getLore());
                    //meta.setDisplayName(ChatColor.GOLD + "Randomly Selected Gear");
                    //randomCarbyne.setItemMeta(meta);

                    inventory.setItem(p, randomCarbyne);
                }
            }
        }.runTaskTimer(main, 0, 10);
    }

    public void generateRewards(Player player) {
        int openTime = main.getConfig().getInt("crates.crate-opening-time");
        int fillerId = main.getConfig().getInt("crates.filler-itemid");
        int fillerPeriod = main.getConfig().getInt("crates.filler-period");

        Inventory inventory = Bukkit.createInventory(player, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Crate Rewards");

        if (player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            player.setItemInHand(player.getItemInHand());
        } else
            player.setItemInHand(new ItemStack(Material.AIR));

        crateOpeners.put(player.getUniqueId(), inventory);
        crateOpenersAmount.put(player.getUniqueId(), rewardsAmount - 1);

        new BukkitRunnable() {
            int runTime = openTime * fillerPeriod;

            @Override
            public void run() {
                if (runTime > 0)
                    runTime--;
                else {
                    cancel();
                    return;
                }

                if (rewardsAmount == 1) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i == 13)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                } else if (rewardsAmount == 2) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i == 12 || i == 14)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                } else if (rewardsAmount == 3) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i >= 12 && i <= 14)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                } else if (rewardsAmount == 5) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i >= 11 && i <= 15)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                } else {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (i == 13)
                            continue;

                        inventory.setItem(i, new ItemBuilder(Material.getMaterial(fillerId)).name("").durability(new Random().nextInt(16)).build());
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 0L, fillerPeriod);

        ArrayList<RewardGenerator> rewardGenerators = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!crateOpeners.containsKey(player.getUniqueId())) {
                    for (RewardGenerator rewardGenerator : rewardGenerators)
                        if (!rewardGenerator.hasRan())
                            rewardGenerator.stopScheduler(player, true);

                    cancel();
                }
            }
        }.runTaskTimer(main, 0L, 1L);

        List<ItemStack> rewardItems = new ArrayList<>();
        for (Reward reward : rewards)
            rewardItems.add(reward.getItem(false));

        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        List<Reward> chosenRewards = getRewards(rewardsAmount, profile);

        if (rewardsAmount == 1)
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 0, openTime, rewardItems, chosenRewards.get(0)));
        else if (rewardsAmount == 2) {
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 12, 0, openTime, rewardItems, chosenRewards.get(0)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 14, 10, openTime, rewardItems, chosenRewards.get(1)));
        } else if (rewardsAmount == 3) {
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 12, 0, openTime, rewardItems, chosenRewards.get(0)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 10, openTime, rewardItems, chosenRewards.get(1)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 14, 20, openTime, rewardItems, chosenRewards.get(2)));
        } else if (rewardsAmount == 5) {
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 11, 0, openTime, rewardItems, chosenRewards.get(0)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 12, 10, openTime, rewardItems, chosenRewards.get(1)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 20, openTime, rewardItems, chosenRewards.get(2)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 14, 30, openTime, rewardItems, chosenRewards.get(3)));
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 15, 40, openTime, rewardItems, chosenRewards.get(4)));
        } else
            rewardGenerators.add(new RewardGenerator(this, player, inventory, 13, 0, openTime, rewardItems, chosenRewards.get(0)));

        if (!profile.getCrateProgression().containsKey(name))
            profile.getCrateProgression().put(name, 0.0);

        double random = (double) Maths.randomNumberBetween(progressIncreaseP, progressIncreaseM);
        profile.getCrateProgression().put(name, profile.getCrateProgression().get(name) + random);
        Bukkit.broadcastMessage("Before: " + profile.getCrateProgression().get(name));
        Bukkit.broadcastMessage("Random: " + random);
        Bukkit.broadcastMessage("After: " + profile.getCrateProgression().get(name));
        player.openInventory(inventory);
    }

    public void knockbackPlayer(Player player, Location relative) {
        double xpower = 0.25;
        double ypower = 0.3;
        double zpower = 0.25;
        double x = relative.getX() - player.getLocation().getX();
        double y = relative.getY() - player.getLocation().getY();
        double z = relative.getZ() - player.getLocation().getZ();
        Vector nv = new Vector(x, y, z);
        Vector nv2 = new Vector(nv.getX() * -xpower, ypower, nv.getZ() * -zpower);
        player.setVelocity(new Vector(0, 0, 0));
        player.setVelocity(nv2);
    }

    public Reward getReward(Profile profile) {
        double totalPercentage = 0;

        if (!profile.getCrateProgression().containsKey(name)) {
            profile.getCrateProgression().put(name, 0.0);
        }
        double progress = profile.getCrateProgression().get(name);

        for (Reward reward : getRewards())
            totalPercentage += reward.getChance();

        int index = -1;
        double random = Math.random() * totalPercentage;

        for (int i = 0; i < rewards.size(); i++) {
            Reward reward = getRewards().get(i);
            random -= ((reward.getChance()) + (reward.getProgress() * progress));

            if (random <= 0) {
                index = i;
                break;
            }
        }

        if (progress >= 100) {
            profile.getCrateProgression().put(name, 0.0);
        }

        return rewards.get(index);
    }

    public ArrayList<Reward> getRewards(int amount, Profile profile) {
        ArrayList<Reward> rewards = new ArrayList<>();

        if (!profile.getCrateProgression().containsKey(name)) {
            profile.getCrateProgression().put(name, .0);
        }
        double progress = profile.getCrateProgression().get(name);

        for (int a = 0; a < amount; a++) {
            double totalPercentage = 0;

            for (Reward reward : getRewards()) {
                Bukkit.broadcastMessage(reward.getDisplayName() + " : " + (reward.getProgress() * progress));
                totalPercentage += (reward.getChance() + (reward.getProgress() * progress));
            }

            int index = -1;
            double random = Math.random() * totalPercentage;

            for (int i = 0; i < getRewards().size(); i++) {
                Reward reward = getRewards().get(i);

                random -= ((reward.getChance()) + (reward.getProgress() * progress));

                if (random <= 0) {
                    index = i;
                    break;
                }
            }

            rewards.add(getRewards().get(index));
        }

        Bukkit.broadcastMessage("Progress : " + progress);
        if (progress >= 100) {
            profile.getCrateProgression().put(name, 0.0);
        }

        return rewards;
    }

    public Reward getReward(int id) {
        for (Reward reward : rewards)
            if (reward.getId() == id)
                return reward;

        return null;
    }

    public void runEffect(String crateName) {
        switch (crateName) {
            case "ObsidianCrate":
                runObsidianEffect();
                break;
            case "EmeraldCrate":
                runEmeraldEffect();
                break;
            case "DiamondCrate":
                runDiamondEffect();
                break;
            case "GoldCrate":
                runGoldEffect();
                break;
            case "IronCrate":
                runIronEffect();
                break;
        }
    }

    private void runObsidianEffect() {
        new BukkitRunnable() {
            private double theta = 0, radius = 0.55;
            private Location loc = getLocation().clone().add(0.5, 0.5, 0.5);

            @Override
            public void run() {
                ParticleEffect.OrdinaryColor purple = new ParticleEffect.OrdinaryColor(244, 66, 244);

                theta += 0.2;

                double x = Math.cos(theta) * radius;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;

                loc.add(x, y, z);
                ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                loc.subtract(x, 0, z);
                loc.subtract(x, 0, z);
                ParticleEffect.REDSTONE.display(purple, loc, 40, false);
                loc.add(x, -y, z);
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }

    private void runEmeraldEffect() {
        new BukkitRunnable() {
            private double theta = 0, radius = 0.6;
            private Location loc = getLocation().clone().add(0.5, 0.5, 0.5);

            @Override
            public void run() {
                theta += 0.2;
                double x = Math.cos(theta) * radius;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;

                loc.add(x, y, z);
                ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 1, loc, 40, false);
                loc.subtract(x, y, z);
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }

    private void runDiamondEffect() {
        new BukkitRunnable() {
            private double theta = 0, radius = 0.6;
            private Location loc = getLocation().clone().add(0.5, 0, 0.5);

            @Override
            public void run() {
                ParticleEffect.OrdinaryColor blue = new ParticleEffect.OrdinaryColor(66, 212, 244);

                theta += 0.13;

                double x = Math.sin(theta) * radius;
                double z = Math.cos(theta) * radius;

                loc.add(x, 0, z);
                loc.add(0, 0.25, 0);
                ParticleEffect.REDSTONE.display(blue, loc, 50, true);
                loc.add(0, 0.25, 0);
                ParticleEffect.REDSTONE.display(blue, loc, 50, true);
                loc.subtract(x, 0.5, z);
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }

    private void runGoldEffect() {
        new BukkitRunnable() {
            private double theta = 0, radius = 0.6;
            private Location loc = getLocation().clone().add(0.5, 0.5, 0.5);

            @Override
            public void run() {
                ParticleEffect.OrdinaryColor gold = new ParticleEffect.OrdinaryColor(244, 217, 66);

                theta += 0.13;

                double x = Math.sin(theta) * radius;
                double z = Math.cos(theta) * radius;

                loc.add(x, 0, z);
                ParticleEffect.REDSTONE.display(gold, loc, 50, true);
                loc.subtract(x, 0, z);
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }

    private void runIronEffect() {
        new BukkitRunnable() {
            private double theta = 0, radius = 0.6;
            private Location loc = getLocation().clone().add(0.5, 0.5, 0.5);

            @Override
            public void run() {
                ParticleEffect.OrdinaryColor silver = new ParticleEffect.OrdinaryColor(201, 197, 175);

                theta += 0.14;
                double y = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;

                loc.add(0, y, z);
                ParticleEffect.REDSTONE.display(silver, loc, 50, true);
                loc.subtract(0, y, z);
            }
        }.runTaskTimerAsynchronously(main, 0, 1);
    }
}
