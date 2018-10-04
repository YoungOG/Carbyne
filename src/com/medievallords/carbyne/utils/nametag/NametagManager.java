package com.medievallords.carbyne.utils.nametag;

import com.medievallords.carbyne.squads.Squad;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NametagManager {

    private static Map<String, NametagPlayer> players = new HashMap<>();

    public static void setup(Player p) {
        if (!players.containsKey(p.getName())) {
            NametagPlayer nametagPlayer = new NametagPlayer(p);
            players.put(p.getName(), nametagPlayer);
        }
    }

    public static NametagPlayer getPlayer(Player p) {
        return players.get(p.getName());
    }

    public static boolean contains(Player p) {
        return players.containsKey(p.getName());
    }

    public static void remove(Player p) {
        if (contains(p)) {
            players.remove(p.getName());
        }
    }

    public static void updateNametag(Player toRefresh, Player refreshFor) {
        NametagPlayer toRefreshTag = NametagManager.getPlayer(toRefresh);
        NametagPlayer refreshForTag = NametagManager.getPlayer(refreshFor);

        Nametag nametag;

        if (refreshForTag.getPlayerNametag(toRefreshTag) == null) {
            nametag = new Nametag(toRefresh.getName(), "", "");
            refreshForTag.setPlayerNametag(toRefreshTag, nametag);
        } else
            nametag = refreshForTag.getPlayerNametag(toRefreshTag);

        int health = (int) toRefresh.getHealth();
        String suffix = StringUtils.formatHealthBar(health);
        nametag.setSuffix(" " + suffix);

        //Duel
        /*Squad squad1 = squadManager.getSquad(refreshFor.getUniqueId());
        Squad squad2 = squadManager.getSquad(toRefresh.getUniqueId());
        if (squad1 != null && squad2 != null && squad1 == squad2) {
            nametag.setPrefix(ChatColor.AQUA + "");
        } else {
            nametag.setPrefix(ChatColor.GOLD + "");
        }*/
            /*

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
        */

        nametag.setPrefix(ChatColor.GOLD + "");
        //Squad
        Squad toRefreshSquad = StaticClasses.squadManager.getSquad(toRefresh.getUniqueId());
        Squad refreshForSquad = StaticClasses.squadManager.getSquad(refreshFor.getUniqueId());

        if (toRefreshSquad != null && refreshForSquad != null) {
            if (toRefreshSquad.getUniqueId().equals(refreshForSquad.getUniqueId())) {
                if (toRefreshSquad.getLeader().equals(toRefresh.getUniqueId()))
                    nametag.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD);
                else
                    nametag.setPrefix(ChatColor.AQUA + "");

                refreshForTag.update(toRefreshTag, nametag);
                return;
            } else if (refreshForSquad.getTargetSquad() != null)
                    if (refreshForSquad.getTargetSquad().equals(toRefreshSquad)) {
                        nametag.setPrefix(ChatColor.RED + "");
                        refreshForTag.update(toRefreshTag, nametag);
                        return;
                    }
        } else {
            if (refreshForSquad != null)
                if (refreshForSquad.getTargetUUID() != null)
                    if (refreshForSquad.getTargetUUID().equals(toRefresh.getUniqueId())) {
                        nametag.setPrefix(ChatColor.RED + "");
                        refreshForTag.update(toRefreshTag, nametag);
                        return;
                    }
        }

        //ZPermissionsService service = Carbyne.getInstance().getService();

        //if (service != null) {
        //String prefix = ChatColor.translateAlternateColorCodes('&', service.getPlayerPrefix(toRefresh.getUniqueId()));
        //nametag.setPrefix(prefix.length() > 16 ? prefix.substring(0, 16) : prefix);
        //nametag.setSuffix("");
        //}

        refreshForTag.update(toRefreshTag, nametag);
    }

    public static void updateNametag(Player player) {
        for (Player all : PlayerUtility.getOnlinePlayers())
            if (!all.getUniqueId().equals(player.getUniqueId()))
                updateNametag(player, all);
    }

    public static void clear() {
        players.clear();
    }
}