package com.medievallords.carbyne.parties.commands;

import com.medievallords.carbyne.parties.Party;
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
public class PartyFriendlyFireCommand extends BaseCommand implements Listener {

    public PartyFriendlyFireCommand() {
        Bukkit.getPluginManager().registerEvents(this, getCarbyne());
    }

    @Command(name = "party.friendlyfire", inGameOnly = true, aliases = {"ff"})
    public void execute(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();
        Party party = getPartyManager().getParty(player.getUniqueId());

        if (args.length != 0) {
            MessageManager.sendMessage(player, "&cUsage: /party");
            return;
        }

        if (party == null) {
            MessageManager.sendMessage(player, "&cYou are not in a party.");
            return;
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            MessageManager.sendMessage(player, "&cOnly the leader can toggle friendly fire.");
            return;
        }

        party.setFriendlyFireToggled(!party.isFriendlyFireToggled());
        MessageManager.sendMessage(player, "&aYou have toggled friendly fire to &b" + party.isFriendlyFireToggled() + "&a.");
        party.sendMembersMessage("&aFriendly fire has been set to &b" + party.isFriendlyFireToggled() + "&a.");
    }

    @EventHandler
    public void onFriendlyFire(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = null;
            Party party = getPartyManager().getParty(damaged.getUniqueId());

            if (party == null) {
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
                if (getPartyManager().getParty(damager.getUniqueId()) != null && getPartyManager().getParty(damager.getUniqueId()).getUniqueId().equals(party.getUniqueId())) {
                    if (!party.isFriendlyFireToggled()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
