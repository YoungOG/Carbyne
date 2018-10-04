package com.medievallords.carbyne.gear.specials;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.StaticClasses;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface Special {

    int getRequiredCharge();

    String getSpecialName();

    void callSpecial(Player caster);

    default void broadcastMessage(String radiusMessage, Location centerPoint, int radius) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getWorld() == centerPoint.getWorld()) {
                if (onlinePlayer.getLocation().distance(centerPoint) < radius) {
                    MessageManager.sendMessage(onlinePlayer, radiusMessage);
                }
            }
        }
    }

    default boolean isOnSameTeam(Player caster, Player hit) {
        Squad squadCaster = StaticClasses.squadManager.getSquad(caster.getUniqueId());
        Squad squadHit = StaticClasses.squadManager.getSquad(hit.getUniqueId());

        if (squadHit == null || squadCaster == null) {
            return false;
        } else return squadCaster.getUniqueId().equals(squadHit.getUniqueId());

    }

    default boolean isInSafeZone(LivingEntity entity) {
        return TownyUniverse.getTownBlock(entity.getLocation()) != null && !TownyUniverse.getTownBlock(entity.getLocation()).getPermissions().pvp;
    }

    default boolean isInSafeZone(Location location) {
        return TownyUniverse.getTownBlock(location) != null && !TownyUniverse.getTownBlock(location).getPermissions().pvp;
    }
}