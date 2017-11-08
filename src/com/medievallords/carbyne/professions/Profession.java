package com.medievallords.carbyne.professions;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.github.paperspigot.Title;

import java.util.Random;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
@Getter
@Setter
public abstract class Profession implements Listener {

    private static final int MAX_LEVEL = 12;

    private String name;
    private double chance, maxChance;
    private int minNuggets, maxNuggets;
    //    private HashMapCache
    private String goldMessage;

    public Profession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        this.name = name;
        this.chance = chance;
        this.minNuggets = minNuggets;
        this.maxNuggets = maxNuggets;
        this.goldMessage = goldMessage;

        Bukkit.getServer().getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

    public void giveReward(Player player) {
        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (profile == null)
            return;

        double dropChance = (profile.getProfessionLevel() * 0.01) + chance;

        if (dropChance > maxChance)
            dropChance = maxChance;

        if (Math.random() < dropChance) {
            int randomAmount = new Random().nextInt(maxNuggets - minNuggets + 1) + minNuggets;
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemBuilder(Material.GOLD_NUGGET).amount(randomAmount).build());
            MessageManager.sendMessage(player, goldMessage);
            profile.setProfessionProgress(profile.getProfessionProgress() + 5);

            if (profile.getProfessionProgress() >= profile.getRequiredProfessionProgress()) {
                profile.setProfessionProgress(0);
                if (profile.getProfessionLevel() < 12) {
                    profile.setProfessionLevel(profile.getProfessionLevel() + 1);
                    player.sendTitle(new Title.Builder().title(ChatColor.GREEN + "Level Up!").subtitle(ChatColor.translateAlternateColorCodes('&', "&eYou are now level &7" + profile.getProfessionLevel() + " &ein " + profile.getProfession().getName())).stay(35).build());
                    player.getWorld().playSound(player.getEyeLocation(), Sound.LEVEL_UP, 10, (float) Math.random() * 5);
                }


                profile.setRequiredProfessionProgress(profile.getProfessionLevel() * 100);
            }
        }
    }
}
