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
import ru.tehkode.permissions.bukkit.PermissionsEx;

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
        if(contains(p)){
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
                    } else {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD ");
                    }
                } else {
                    if (squad1.getLeader().equals(toRefresh.getUniqueId())) {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD" + ChatColor.AQUA + "" + ChatColor.BOLD + " ");
                    } else {
                        nametag.setPrefix(ChatColor.AQUA + "SQUAD ");
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
                } else {
                    nametag.setPrefix(ChatColor.AQUA + "SQUAD ");
                }

                refreshForTag.update(toRefreshTag, nametag);
                return;
            }
        }

        String prefix = ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(toRefresh).getPrefix());

        nametag.setPrefix(prefix.length() > 16 ? prefix.substring(0, 16) : prefix);
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
}