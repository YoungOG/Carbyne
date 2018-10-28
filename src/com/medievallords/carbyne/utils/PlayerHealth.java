package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.zones.Zone;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
public class PlayerHealth {

    public static final HashMap<UUID, PlayerHealth> players = new HashMap<>();

    private double health, maxHealth, divider, lastDamage;
    private Zone zone;

    private int
            professionLevel = 1,
            stamina = 100,
            piledriveReady = 0;
    private double
            professionProgress = 0,
            requiredProfessionProgress = 100;
    private long
            dailyRewardDayTime = -1,
            dailyRewardChallengeTime = -1,
            piledriveCombo = 0,
            sprintCombo = 0;
    private boolean
            skillsToggled = true,
            piledriveBoolReady = false,
            sprintToggled = false,
            blocking = false;

    public PlayerHealth(double health, double maxHealth, boolean skillsToggled) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.skillsToggled = skillsToggled;
        this.divider = maxHealth / 20.0;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        this.divider = maxHealth / 20.0;
    }

    public void setLastDamage(double lastDamage) {
        this.lastDamage = lastDamage;
    }

    public void setHealth(double health, Player player) {
        if (player.isDead()) {
            return;
        }

        if (health <= 0) {
            this.health = 0;
            player.setHealth(0);
        } else if (health > maxHealth) {
            this.health = maxHealth;
            player.setHealth(20.0);
        } else {
            this.health = health;
            double realHealth = health / divider;
            player.setHealth(realHealth);
        }
    }

    public void runTickGeneral(Player player) {
        if (stamina < 15) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setAllowFlight(false);
                }
            }.runTask(Carbyne.getInstance());
        } else {
            if (player.getWorld().getName().equals("world") && skillsToggled) {
                Board board = Board.getByPlayer(player.getUniqueId());
                if (board != null) {
                    BoardCooldown skillCooldown = board.getCooldown("skill");

                    if (skillCooldown == null)
                        if (skillsToggled)
                            if (!player.getAllowFlight())
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.setAllowFlight(true);
                                    }
                                }.runTask(Carbyne.getInstance());
                }
            }
        }

        if (sprintToggled) {
            stamina -= 7;

            if (stamina < 7) {
                sprintToggled = false;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setWalkSpeed(0.2f);
                    }
                }.runTask(Carbyne.getInstance());

                MessageManager.sendMessage(player, "&cSuper Sprint has been disabled!");
            }
        } else if (blocking) {
            stamina -= 15;

            if (stamina < 15) {
                blocking = false;
                MessageManager.sendMessage(player, "&cSuper Block has been disabled!");
            }
        } else if (stamina < 100)
            stamina++;

        if (piledriveReady > 0)
            piledriveReady--;
        else {
            if (piledriveBoolReady) {
                MessageManager.sendMessage(player, "&cYou are no longer able to piledrive!");
                piledriveBoolReady = false;
            }
        }
    }

    public static PlayerHealth getPlayerHealth(UUID uuid) {
        return players.get(uuid);
    }
}
