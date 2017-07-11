package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.nisovin.magicspells.events.SpellCastEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Dalton on 7/8/2017.
 */
public class UniversalEventListeners implements Listener
{

    private Carbyne main = Carbyne.getInstance();
    private EventManager eventManager;

    public UniversalEventListeners(EventManager eventManager)
    {
        this.eventManager = eventManager;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e)
    {
        Profile entity = main.getProfileManager().getProfile(e.getEntity().getUniqueId());
        if(entity != null && entity.getActiveEvent() != null && entity.getActiveEvent().getProperties().contains(EventProperties.PVP_DISABLED))
        {
            e.setCancelled(true);
            return;
        }
        else
        {
            if(e.getDamager() instanceof Player)
            {
                Profile damager = main.getProfileManager().getProfile(e.getEntity().getUniqueId());
                if (damager != null && damager.getActiveEvent() != null && damager.getActiveEvent().getProperties().contains(EventProperties.PVP_DISABLED)) {
                    e.setCancelled(true);
                    return;
                }
            }
            else if (e.getDamager() instanceof Projectile)
            {
                ProjectileSource shooter = ((Projectile) e.getDamager()).getShooter();
                if(shooter instanceof Player)
                {
                    Profile pShooter = main.getProfileManager().getProfile(((Player) shooter).getUniqueId());
                    if(pShooter != null && pShooter.getActiveEvent() != null && pShooter.getActiveEvent().getProperties().contains(EventProperties.PVP_DISABLED))
                    {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpellCast(SpellCastEvent e)
    {
        Profile pCaster = main.getProfileManager().getProfile(e.getCaster().getUniqueId());
        if(pCaster != null && pCaster.getActiveEvent() != null && pCaster.getActiveEvent().getProperties().contains(EventProperties.SPELLS_DISABLED))
            e.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e)
    {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
        {
            Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
            if(profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().getProperties().contains(EventProperties.PLUGIN_TELEPORT_DISABLED))
                e.setCancelled(true);
        }
        else if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
        {
            Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
            if(profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().getProperties().contains(EventProperties.ENDERPEARL_TELEPORT_DISABLED))
                e.setCancelled(true);
        }
    }

}
