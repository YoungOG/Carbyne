package com.medievallords.carbyne.events.implementations.listeners;

import com.medievallords.carbyne.events.implementations.CliffClimb;
import com.medievallords.carbyne.utils.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Dalton on 7/5/2017.
 */
public class CliffClimbListeners implements Listener
{

    private CliffClimb cliffClimb;

    public CliffClimbListeners(CliffClimb cliffClimb)
    {
        this.cliffClimb = cliffClimb;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        if(!e.getFrom().getDirection().equals(e.getTo().getDirection()) && cliffClimb.getWaitingTasks().containsKey(e.getPlayer()))
        {
            cliffClimb.getWaitingTasks().get(e.getPlayer()).cancel();
            cliffClimb.getWaitingTasks().remove(e.getPlayer());
            MessageManager.sendMessage(e.getPlayer(), "&cTeleportation cancelled!");
        }
    }

    @EventHandler
    public void onInteract (PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getLocation().equals(cliffClimb.getWinningLocation())) {
                if (cliffClimb.getParticipants().contains(event.getPlayer()) && cliffClimb.getWinner() == null && cliffClimb.isAfterCountdown()) {
                    MessageManager.sendMessage(event.getPlayer(), "&2You have defeated Cliff Climb!");
                    cliffClimb.setWinner(event.getPlayer());
                }
            }
        }
    }

    /*@EventHandler
    public void onTeleport(PlayerTeleportEvent e)
    {
        if(cliffClimb.getParticipants().contains(e.getPlayer()) && e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
            e.setCancelled(true);
        }
    }*/

    /*@EventHandler
    public void onSpellCast(SpellCastEvent e) {
        if(cliffClimb.getParticipants().contains(e.getCaster())) {
            e.setCancelled(true);
        }
    }*/

    /*@EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e)
    {
        if(e.getEntity() instanceof Player && cliffClimb.getParticipants().contains(e.getEntity())) {
            e.setCancelled(true);
            return;
        }
        if(e.getDamager() instanceof Player && cliffClimb.getParticipants().contains(e.getDamager())) {
            e.setCancelled(true);
            return;
        }
        if(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player && cliffClimb.getParticipants().contains(((Player)((Projectile) e.getDamager()).getShooter()))) {
            e.setCancelled(true);
            return;
        }
    }*/

}
