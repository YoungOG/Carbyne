package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class TamingProfession extends Profession {

    public TamingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getOwner();
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Profile playerData = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (playerData == null || playerData.getProfession() == null || playerData.getProfession() != this) {
            return;
        }

        giveReward(player);
    }
}
