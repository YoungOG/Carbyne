package com.medievallords.carbyne.squads.commands;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Williams on 2017-03-13.
 * for the Carbyne project.
 */
public class SquadFriendlyFireCommand extends BaseCommand implements Listener {

    public SquadFriendlyFireCommand() {
        Bukkit.getPluginManager().registerEvents(this, getCarbyne());
    }

    @Command(name = "squad.friendlyfire", inGameOnly = true, aliases = {"s" +
            "quad.ff"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Squad squad = getSquadManager().getSquad(player.getUniqueId());

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /squad");
            return;
        }

        if (squad == null) {
            MessageManager.sendMessage(player, "&cYou are not in a squad.");
            return;
        }

        if (!squad.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cOnly the leader can toggle friendly fire.");
            return;
        }

        squad.setFriendlyFireToggled(!squad.isFriendlyFireToggled());
        MessageManager.sendMessage(player, "&aYou have toggled friendly fire to &b" + squad.isFriendlyFireToggled() + "&a.");
        squad.sendMembersMessage("&aFriendly fire has been set to &b" + squad.isFriendlyFireToggled() + "&a.");
    }

    @EventHandler
    public void onFriendlyFire(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = null;
            Squad squad = getSquadManager().getSquad(damaged.getUniqueId());

            if (squad == null) {
                return;
            }

            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }

            if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();

                if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                }
            }

            if (damager != null) {
                if (getSquadManager().getSquad(damager.getUniqueId()) != null && getSquadManager().getSquad(damager.getUniqueId()).getUniqueId().equals(squad.getUniqueId())) {
                    if (!squad.isFriendlyFireToggled()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
