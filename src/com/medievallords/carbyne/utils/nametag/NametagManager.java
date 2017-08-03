package com.medievallords.carbyne.utils.nametag;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.duels.duel.Duel;
import com.medievallords.carbyne.duels.duel.DuelManager;
import com.medievallords.carbyne.duels.duel.types.RegularDuel;
import com.medievallords.carbyne.duels.duel.types.SquadDuel;
import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.squads.SquadManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class NametagManager {

    private static Map<String, NametagPlayer> players = new HashMap<>();

    public static void setup(Player p){
        if(!players.containsKey(p.getName())){
            NametagPlayer nametagPlayer = new NametagPlayer(p);
            players.put(p.getName(),nametagPlayer);
        }
    }

    public static NametagPlayer getPlayer(Player p){
        return players.get(p.getName());
    }

    public static boolean contains(Player p){
        return players.containsKey(p.getName());
    }

    public static void remove(Player p){
        if (contains(p)) {
            players.remove(p.getName());
        }
    }

    public static void updateNametag(Player toRefresh, Player refreshFor) {
        DuelManager duelManager = Carbyne.getInstance().getDuelManager();
        SquadManager squadManager = Carbyne.getInstance().getSquadManager();

        NametagPlayer toRefreshTag = NametagManager.getPlayer(toRefresh);
        NametagPlayer refreshForTag = NametagManager.getPlayer(refreshFor);

        Nametag nametag;

        if (refreshForTag.getPlayerNametag(toRefreshTag) == null) {
            nametag = new Nametag(toRefresh.getName(), "", "");
            refreshForTag.setPlayerNametag(toRefreshTag, nametag);
        } else {
            nametag = refreshForTag.getPlayerNametag(toRefreshTag);
        }

        //Duel
        Duel toRefreshDuel = duelManager.getDuelFromUUID(toRefresh.getUniqueId());
        Duel refreshForDuel = duelManager.getDuelFromUUID(refreshFor.getUniqueId());
        if (toRefreshDuel != null && refreshForDuel != null && toRefreshDuel == refreshForDuel)  {
            if (toRefreshDuel instanceof RegularDuel) {
                nametag.setPrefix(ChatColor.YELLOW + "");
                nametag.setSuffix("");
                refreshForTag.update(toRefreshTag, nametag);
                return;
            }

            if (toRefreshDuel instanceof SquadDuel) {
                SquadDuel squadDuel = (SquadDuel) toRefreshDuel;
                Squad squad1 = squadDuel.getSquadOne();
                Squad squad2 = squadDuel.getSquadTwo();

                if (squad1.getUniqueId().equals(squad2.getUniqueId())) {
                    if (squad1.getLeader().equals(toRefresh.getUniqueId())) {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD" + ChatColor.AQUA + "" + ChatColor.BOLD + " ");
                        nametag.setSuffix(ChatColor.translateAlternateColorCodes('&', formatHealth(toRefresh.getHealth())));
                    } else {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD ");
                        nametag.setSuffix(ChatColor.translateAlternateColorCodes('&', formatHealth(toRefresh.getHealth())));
                    }
                } else {
                    if (squad1.getLeader().equals(refreshFor.getUniqueId())) {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD" + ChatColor.AQUA + "" + ChatColor.BOLD + " ");
                        nametag.setSuffix(ChatColor.translateAlternateColorCodes('&', formatHealth(toRefresh.getHealth())));
                    } else {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD ");
                        nametag.setSuffix(ChatColor.translateAlternateColorCodes('&', formatHealth(toRefresh.getHealth())));
                    }
                }

                refreshForTag.update(toRefreshTag, nametag);
                return;
            }
        }

        //Squad
        Squad toRefreshSquad = squadManager.getSquad(toRefresh.getUniqueId());
        Squad refreshForSquad = squadManager.getSquad(refreshFor.getUniqueId());

        if (toRefreshSquad != null && refreshForSquad != null) {
            if (toRefreshSquad.getUniqueId().equals(refreshForSquad.getUniqueId())) {
                if (toRefreshSquad.getLeader().equals(toRefresh.getUniqueId())) {
                    nametag.setPrefix(ChatColor.AQUA + "SQUAD" + ChatColor.AQUA + "" + ChatColor.BOLD + " ");
                    nametag.setSuffix(ChatColor.translateAlternateColorCodes('&', formatHealth(toRefresh.getHealth())));
                } else {
                    nametag.setPrefix(ChatColor.AQUA + "SQUAD ");
                    nametag.setSuffix(ChatColor.translateAlternateColorCodes('&', formatHealth(toRefresh.getHealth())));
                }

                refreshForTag.update(toRefreshTag, nametag);
                return;
            } else {
                if (refreshForSquad.getTargetSquad() != null) {
                    if (refreshForSquad.getTargetSquad().equals(toRefreshSquad)) {
                        nametag.setPrefix(ChatColor.RED + "ENEMY" + ChatColor.RED + "" + ChatColor.BOLD + " ");
                        nametag.setSuffix("");

                        refreshForTag.update(toRefreshTag, nametag);
                        return;
                    }
                }
            }
        } else {
            if (refreshForSquad != null) {
                if (refreshForSquad.getTargetUUID() != null) {
                    if (refreshForSquad.getTargetUUID().equals(toRefresh.getUniqueId())) {
                        nametag.setPrefix(ChatColor.RED + "ENEMY" + ChatColor.RED + "" + ChatColor.BOLD + " ");
                        nametag.setSuffix("");

                        refreshForTag.update(toRefreshTag, nametag);
                        return;
                    }
                }
            }
        }

        ZPermissionsService service = Carbyne.getInstance().getService();

        if (service != null) {
            String prefix = ChatColor.translateAlternateColorCodes('&', service.getPlayerPrefix(toRefresh.getUniqueId()));
            nametag.setPrefix(prefix.length() > 16 ? prefix.substring(0, 16) : prefix);
            nametag.setSuffix("");
        }

        refreshForTag.update(toRefreshTag, nametag);
    }

    public static void updateNametag(Player player) {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (!all.getUniqueId().equals(player.getUniqueId())) {
                updateNametag(player, all);
            }
        }
    }

    public static void clear() {
        players.clear();
    }

    static String formatHealth(double health) {
        double hearts = health / 2;
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
}