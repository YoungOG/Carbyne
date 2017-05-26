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

//        Bukkit.broadcastMessage(" ");
//        Bukkit.broadcastMessage(ChatColor.BOLD + "refreshFor(" + refreshFor.getName() + "):");
//        Bukkit.broadcastMessage("  Registered Nametags(" + refreshForTag.getRegisteredNametags().size() + "):");
//        for (Nametag nametag : refreshForTag.getRegisteredNametags()) {
//            Bukkit.broadcastMessage(ChatColor.stripColor("    - Name: " + nametag.getName() + ", Prefix: " + nametag.getPrefix() + ", Suffix: " + nametag.getSuffix()));
//        }
//        Bukkit.broadcastMessage("  Player Nametags(" + refreshForTag.getPlayerNametags().keySet().size() + " | " + refreshForTag.getPlayerNametags().values().size() + "):");
//        for (NametagPlayer nametagPlayer : refreshForTag.getPlayerNametags().keySet()) {
//            Bukkit.broadcastMessage(ChatColor.stripColor("    - Name: " + nametagPlayer.getName() + ", Nametag(Name: " + refreshForTag.getPlayerNametags().get(nametagPlayer).getName() + ", Prefix: " + refreshForTag.getPlayerNametags().get(nametagPlayer).getPrefix() + ", Suffix: " + refreshForTag.getPlayerNametags().get(nametagPlayer).getSuffix() + ")"));
//        }
//
//        Bukkit.broadcastMessage(ChatColor.BOLD + "toRefresh(" + toRefresh.getName() + "):");
//        Bukkit.broadcastMessage("  Registered Nametags(" + toRefreshTag.getRegisteredNametags().size() + "):");
//        for (Nametag nametag : toRefreshTag.getRegisteredNametags()) {
//            Bukkit.broadcastMessage(ChatColor.stripColor("    - Name: " + nametag.getName() + ", Prefix: " + nametag.getPrefix() + ", Suffix: " + nametag.getSuffix()));
//        }
//        Bukkit.broadcastMessage("  Player Nametags(" + toRefreshTag.getPlayerNametags().keySet().size() + " | " + toRefreshTag.getPlayerNametags().values().size() + "):");
//        for (NametagPlayer nametagPlayer : toRefreshTag.getPlayerNametags().keySet()) {
//            Bukkit.broadcastMessage(ChatColor.stripColor("    - Name: " + nametagPlayer.getName() + ", Nametag(Name: " + toRefreshTag.getPlayerNametags().get(nametagPlayer).getName() + ", Prefix: " + toRefreshTag.getPlayerNametags().get(nametagPlayer).getPrefix() + ", Suffix: " + toRefreshTag.getPlayerNametags().get(nametagPlayer).getSuffix() + ")"));
//        }

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
                        nametag.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + "");
                    } else {
                        nametag.setPrefix(ChatColor.AQUA + "");
                    }
                } else {
                    if (squad1.getLeader().equals(toRefresh.getUniqueId())) {
                        nametag.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "");
                    } else {
                        nametag.setPrefix(ChatColor.RED + "");
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
                    nametag.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + "");
                } else {
                    nametag.setPrefix(ChatColor.AQUA + "");
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