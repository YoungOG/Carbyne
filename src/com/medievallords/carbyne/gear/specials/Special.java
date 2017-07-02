package com.medievallords.carbyne.gear.specials;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
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
        Squad squadCaster = Carbyne.getInstance().getSquadManager().getSquad(caster.getUniqueId());
        Squad squadHit = Carbyne.getInstance().getSquadManager().getSquad(hit.getUniqueId());

        if (squadHit == null || squadCaster == null) {
            return false;
        } else if (squadCaster.getUniqueId().equals(squadHit.getUniqueId())) {
            return true;
        }

        return false;
    }

    default boolean isInSafeZone(LivingEntity entity) {
        if (TownyUniverse.getTownBlock(entity.getLocation()) != null && !TownyUniverse.getTownBlock(entity.getLocation()).getPermissions().pvp) {
            return true;
        } else {
            return false;
        }
    }
}