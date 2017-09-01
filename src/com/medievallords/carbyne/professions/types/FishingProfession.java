package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class FishingProfession extends Profession {


    public FishingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            Profile playerData = Carbyne.getInstance().getProfileManager().getProfile(event.getPlayer().getUniqueId());
            if (playerData == null || playerData.getProfession() == null || playerData.getProfession() != this) {
                return;
            }

            giveReward(event.getPlayer());
        }
    }
}
