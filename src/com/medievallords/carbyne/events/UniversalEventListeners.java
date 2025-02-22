package com.medievallords.carbyne.events;

import com.medievallords.carbyne.Carbyne;
import org.bukkit.event.Listener;

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

//    @EventHandler
//    public void onMove(PlayerMoveEvent e) {
//        if (e.getTo().getDirection().equals(e.getFrom().getDirection())) return;
//        final Player player = e.getPlayer();
//        new BukkitRunnable() {
//            public void run() {
//                for (Event event : eventManager.getActiveEvents()) {
//                    if (event.getWaitingTasks().containsKey(player)) {
//                        event.getWaitingTasks().remove(player);
//                        MessageManager.sendMessage(player, "&cTeleportation cancelled!");
//                        break;
//                    }
//                }
//            }
//        }.runTaskAsynchronously(Carbyne.getInstance());
//    }
//
//    @EventHandler
//    public void onDamage(EntityDamageByEntityEvent e)
//    {
//        Profile entity = main.getProfileManager().getProfile(e.getEntity().getUniqueId());
//        if(entity != null && entity.getActiveEvent() != null && entity.getActiveEvent().getProperties().contains(EventProperties.PVP_DISABLED))
//        {
//            e.setCancelled(true);
//            return;
//        }
//        else
//        {
//            if(e.getDamager() instanceof Player)
//            {
//                Profile damager = main.getProfileManager().getProfile(e.getEntity().getUniqueId());
//                if (damager != null && damager.getActiveEvent() != null && damager.getActiveEvent().getProperties().contains(EventProperties.PVP_DISABLED)) {
//                    e.setCancelled(true);
//                    return;
//                }
//            }
//            else if (e.getDamager() instanceof Projectile)
//            {
//                ProjectileSource shooter = ((Projectile) e.getDamager()).getShooter();
//                if(shooter instanceof Player)
//                {
//                    Profile pShooter = main.getProfileManager().getProfile(((Player) shooter).getUniqueId());
//                    if(pShooter != null && pShooter.getActiveEvent() != null && pShooter.getActiveEvent().getProperties().contains(EventProperties.PVP_DISABLED))
//                    {
//                        e.setCancelled(true);
//                        return;
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onSpellCast(SpellCastEvent e)
//    {
//        Profile pCaster = main.getProfileManager().getProfile(e.getCaster().getUniqueId());
//        if(pCaster != null && pCaster.getActiveEvent() != null && pCaster.getActiveEvent().getProperties().contains(EventProperties.SPELLS_DISABLED))
//            e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onTeleport(PlayerTeleportEvent e)
//    {
//        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
//        {
//            Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
//            if (profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().isActive() && profile.getActiveEvent().getProperties().contains(EventProperties.PLUGIN_TELEPORT_DISABLED))
//                e.setCancelled(true);
//        }
//        else if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
//        {
//            Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
//            if(profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().getProperties().contains(EventProperties.ENDERPEARL_TELEPORT_DISABLED))
//                e.setCancelled(true);
//        }
//    }
//
//    @EventHandler
//    public void onPlayerDeath(PlayerDeathEvent e) {
//        Profile profile = main.getProfileManager().getProfile(e.getEntity().getUniqueId());
//        if (profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().properties.contains(EventProperties.REMOVE_PLAYER_ON_DEATH)) {
//            Event event = profile.getActiveEvent();
//            event.removePlayerFromEvent(e.getEntity());
//        }
//    }
//
//    @EventHandler
//    public void onQuit(PlayerQuitEvent e) {
//        Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
//        if (profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().getProperties().contains(EventProperties.REMOVE_PLAYER_ON_QUIT)) {
//            profile.getActiveEvent().removePlayerFromEvent(e.getPlayer());
//        }
//    }
//
//    @EventHandler
//    public void onDrink(PlayerItemConsumeEvent e) {
//        Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
//        if (profile != null && profile.getActiveEvent() != null && profile.getActiveEvent().properties.contains(EventProperties.PREVENT_POTION_DRINKING))
//            e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onHunger(FoodLevelChangeEvent e) {
//        Profile profile = main.getProfileManager().getProfile(e.getEntity().getUniqueId());
//        if (profile.getActiveEvent() != null && profile.getActiveEvent().properties.contains(EventProperties.HUNGER_DISABLED))
//            e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
//        if (e.getPlayer().isOp()) return;
//        Profile profile = main.getProfileManager().getProfile(e.getPlayer().getUniqueId());
//        if (profile.getActiveEvent() != null && profile.getActiveEvent().isCommandWhitelistActive()) {
//            Event event = profile.getActiveEvent();
//            String[] cmd = e.getMessage().split(" ");
//            if (!event.getWhitelistedCommands().contains(cmd[0])) {
//                e.setCancelled(true);
//            }
//        }
//    }

}
