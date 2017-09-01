package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Created by WE on 2017-08-15.
 */
public class EnchantingProfession extends Profession {

    public EnchantingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();

        Profile profile = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());

        if (profile == null || profile.getProfession() == null || profile.getProfession() != this)
            return;

        giveReward(player);
    }
}
