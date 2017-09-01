package com.medievallords.carbyne.utils;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import com.google.common.collect.Collections2;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.conquerpoints.objects.ConquerPoint;
import com.medievallords.carbyne.conquerpoints.objects.ConquerPointState;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.gear.GearManager;
import com.medievallords.carbyne.listeners.CombatTagListeners;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.squads.SquadType;
import com.medievallords.carbyne.staff.StaffManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Calvin on 3/13/2017
 *
 * for the Carbyne project.
 */
public class CarbyneBoardAdapter implements BoardAdapter {

    private Carbyne main;

    public CarbyneBoardAdapter(Carbyne main) {
        this.main = main;
    }

    @Override
    public String getTitle(Player player) {
        return "&b&lMedieval Lords";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) {
        ProfileManager profileManager = main.getProfileManager();
        GearManager gearManager = main.getGearManager();
        SquadManager squadManager = main.getSquadManager();
        ArrayList<String> lines = new ArrayList<>();
        Iterator itr = set.iterator();

        if (main.getStaffManager().getStaffModePlayers().contains(player.getUniqueId())) {
            return staffScoreboard(player);
        }

        if (player.hasPermission("carbyne.staff.staffmode")) {
            lines.add("&7Vanished&c: " + main.getStaffManager().isVanished(player));
            lines.add("    ");
        }

        if (!CombatTagListeners.isInCombat(player.getUniqueId())) {
            if (Account.getAccount(player.getName()) != null) {
                lines.add("&aBalance&b: " + MessageManager.format(Account.getAccount(player.getName()).getBalance()));
            }

            if (main.getMissionsManager().getUuidMissions().containsKey(player.getUniqueId())) {
                ArrayList<Mission> activeMissions = new ArrayList<>();
                Mission[] missions = main.getMissionsManager().getUuidMissions().get(player.getUniqueId()).getCurrentMissions();
                for (Mission mission : missions) {
                    if (mission != null && !mission.isActive())
                        continue;

                    activeMissions.add(mission);
                }

                if (activeMissions.size() > 0) {
                    lines.add(" ");
                    lines.add("&aActive Missions&b: " + activeMissions.size());

//            for (Mission mission : activeMissions)
//                lines.add("&b- " + mission.getName() + " &fb[&f" + mission.getTimeLeft() + "&b]");
                }
            }

            if (profileManager.getProfile(player.getUniqueId()).getProfession() != null) {
                Profile profile = profileManager.getProfile(player.getUniqueId());
                lines.add(" ");
                lines.add("&aProfession&b: " + profile.getProfession().getName());
                lines.add("  &aLevel&b: " + profile.getProfessionLevel());
                lines.add("  &aProgress&b: " + profile.getProfessionProgress());
            }
        }

        /*if ((player.getItemInHand() != null && gearManager.getDurability(player.getItemInHand()) > -1.0) || (player.getInventory().getHelmet() != null && gearManager.getDurability(player.getInventory().getHelmet()) > -1.0) || (player.getInventory().getChestplate() != null && gearManager.getDurability(player.getInventory().getChestplate()) > -1.0) || (player.getInventory().getLeggings() != null && gearManager.getDurability(player.getInventory().getLeggings()) > -1.0) || (player.getInventory().getBoots() != null && gearManager.getDurability(player.getInventory().getBoots()) > -1.0)) {
            lines.add(" ");
            lines.add("&6Durability:");

            if (player.getInventory().getHelmet() != null && gearManager.getDurability(player.getInventory().getHelmet()) > -1) {
                lines.add(" &eHelmet&b: " + ((int) gearManager.getDurability(player.getInventory().getHelmet())));
            }

            if (player.getInventory().getChestplate() != null && gearManager.getDurability(player.getInventory().getChestplate()) > -1) {
                lines.add(" &eChestplate&b: " + ((int) gearManager.getDurability(player.getInventory().getChestplate())));
            }

            if (player.getInventory().getLeggings() != null && gearManager.getDurability(player.getInventory().getLeggings()) > -1) {
                lines.add(" &eLeggings&b: " + ((int) gearManager.getDurability(player.getInventory().getLeggings())));
            }

            if (player.getInventory().getBoots() != null && gearManager.getDurability(player.getInventory().getBoots()) > -1) {
                lines.add(" &eBoots&b: " + ((int) gearManager.getDurability(player.getInventory().getBoots())));
            }
        }

        if (player.getItemInHand() != null && gearManager.getDurability(player.getItemInHand()) > -1) {
            lines.add(" &eHand&b: " + ((int) gearManager.getDurability(player.getItemInHand())));
        }*/

        if (squadManager.getSquad(player.getUniqueId()) != null) {
            Squad squad = squadManager.getSquad(player.getUniqueId());

            if (squad.getMembers().size() > 0) {
                lines.add(" ");
                lines.add("&dSquad [&b" + (squad.getType() == SquadType.PUBLIC ? "&b" : "&c") + squad.getType().toString().toLowerCase().substring(0, 1).toUpperCase() + squad.getType().toString().toLowerCase().substring(1) + "&d]:");

                for (UUID member : squad.getAllPlayers()) {
                    if (!member.equals(player.getUniqueId())) {
                        if (squad.getLeader().equals(member)) {
                            lines.add(" &b&l" + Bukkit.getPlayer(member).getName() + " " + formatHealth(Bukkit.getPlayer(member).getHealth()));
                        } else {
                            lines.add(" &b" + Bukkit.getPlayer(member).getName() + " " + formatHealth(Bukkit.getPlayer(member).getHealth()));
                        }
                    }
                }
            }
        }

        Collection<ConquerPoint> conquerPoints = Collections2.filter(main.getConquerPointManager().getConquerPoints(),
                conquerPoint -> (conquerPoint != null && conquerPoint.getState() == ConquerPointState.CAPTURING));

        if (conquerPoints.size() > 0) {
            lines.add(" ");
            lines.add("&4Conquer Points:");

            for (ConquerPoint conquerPoint : conquerPoints)
                lines.add("&b- &5" + conquerPoint.getId().replace("_", " ").substring(0, Math.min(conquerPoint.getId().replace("_", "").length(), 5)) + "   &c[" + "&4" + MessageManager.convertSecondsToMinutes(conquerPoint.getCaptureTime()) + "&c]");
        }

        if (profileManager.getProfile(player.getUniqueId()) != null) {
            Profile profile = profileManager.getProfile(player.getUniqueId());

            if (profile.isPvpTimePaused() && profile.getRemainingPvPTime() > 1) {
                lines.add("         ");
                lines.add("&cPvPTimer&b: " + formatTime(profile.getRemainingPvPTime()));
            } else if (profile.getRemainingPvPTime() > 1) {
                lines.add("         ");
                lines.add("&cPvPTimer&b: " + formatTime(profile.getRemainingPvPTime()));
            }
        }

        if (board.getCooldown("target") == null) {
            if (squadManager.getSquad(player.getUniqueId()) != null) {
                Squad squad = squadManager.getSquad(player.getUniqueId());

                if (squad.getTargetUUID() != null) {
                    squad.setTargetUUID(null);
                }

                if (squad.getTargetSquad() != null) {
                    squad.setTargetSquad(null);
                }
            }
        }

        try {
            while (itr.hasNext()) {
                BoardCooldown cooldown = (BoardCooldown) itr.next();

                if (cooldown.getId().equals("logout")) {
                    lines.add("       ");
                    lines.add("&cLogout&b: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("target")) {
                    if (squadManager.getSquad(player.getUniqueId()) != null) {
                        Squad squad = squadManager.getSquad(player.getUniqueId());

                        if (squad.getTargetUUID() != null || squad.getTargetSquad() != null) {
                            lines.add("     ");
                            lines.add("&cTarget&b: " + (squad.getTargetSquad() != null ? Bukkit.getPlayer(squad.getTargetSquad().getLeader()).getName() + "'s Squad" : Bukkit.getPlayer(squad.getTargetUUID()).getName()));
                        }
                    }
                }

                if (cooldown.getId().equals("combattag")) {
                    if (CombatTagListeners.isInCombat(player.getUniqueId())) {
                        lines.add("  ");
                        lines.add("&cCombat Timer&b: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                    }
                }

                if (cooldown.getId().equals("special")) {
                    lines.add("   ");
                    lines.add("&cSpecial CD&b: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("enderpearl")) {
                    lines.add("   ");
                    lines.add("&cEnderpearl CD&b: " + cooldown.getFormattedString(BoardFormat.SECONDS));
                }

                if (cooldown.getId().equals("godapple")) {
                    lines.add("   ");
                    lines.add("&cGod Apple CD&b: " + cooldown.getFormattedString(BoardFormat.MINUTES));
                }
            }
        } catch (Exception e) {
            main.getLogger().log(Level.WARNING, e.getMessage());
        }

        if (lines.size() >= 1) {
            lines.add(0, "&7&m---------------------");
            lines.add("&7&m---------------------");
        }

        return lines;
    }

    private List<String> staffScoreboard(Player player) {
        StaffManager staffManager = main.getStaffManager();
        ArrayList<String> lines = new ArrayList<>();
        lines.add("&7Vanished: &c" + staffManager.isVanished(player));
        lines.add("    ");

        lines.add("&7Chat Muted: &a" + (staffManager.isChatMuted() ? "&a" + staffManager.isChatMuted() : "&c" + staffManager.isChatMuted()));
        lines.add("&7Chat Speed: &a" + staffManager.getSlowChatTime() + "s");
        lines.add("    ");

        lines.add("&7Flying: " + (player.isFlying() ? "&atrue" : "&cfalse"));

        if (lines.size() >= 1) {
            lines.add(0, "&7&m---------------------");
            lines.add("&7&m---------------------");
        }

        return lines;
    }

    String formatHealth(double health) {
        double hearts = (health / 2) / 5;
        DecimalFormat format = new DecimalFormat("#");

        if (hearts <= 10 && hearts >= 7.5) {
            return String.format(" &a%s \u2764", format.format(hearts));
        } else if (hearts <= 7.5 && hearts >= 5) {
            return String.format(" &e%s \u2764", format.format(hearts));
        } else if (hearts <= 5 && hearts >= 2.5) {
            return String.format(" &6%s \u2764", format.format(hearts));
        } else {
            return String.format(" &c%s \u2764", format.format(hearts));
        }
    }

    String formatBalance(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String formatted = formatter.format(amount);
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }

    String formatTime(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
