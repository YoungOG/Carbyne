package com.medievallords.carbyne.professions.types;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.Profession;
import com.medievallords.carbyne.profiles.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
public class FarmingProfession extends Profession {


    public FarmingProfession(String name, double chance, int minNuggets, int maxNuggets, String goldMessage) {
        super(name, chance, minNuggets, maxNuggets, goldMessage);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onFarmBlock(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!isCrop(block)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Profile playerData = Carbyne.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (playerData == null || playerData.getProfession() == null || playerData.getProfession() != this) {
            return;
        }

        giveReward(player);
    }


    public boolean isCrop(Block block) {
        int durability = block.getData();

        if (block.getType() == Material.CARROT || block.getType() == Material.POTATO) {
            return durability >= 4;
        } else if (block.getType() == Material.CROPS) {
            return durability >= 4;
        } else if (block.getType() == Material.NETHER_WARTS) {
            return durability >= 2;
        } else {
            return false;
        }
    }
}
