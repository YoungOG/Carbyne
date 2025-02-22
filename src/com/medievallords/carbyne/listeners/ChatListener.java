package com.medievallords.carbyne.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.*;
import com.medievallords.carbyne.utils.webhook.DiscordMessage;
import com.medievallords.carbyne.utils.webhook.StaffHook;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.util.StringMgmt;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Calvin on 1/9/2017
 * for the Carbyne-Gear project.
 */
public class ChatListener implements Listener {

    //private ArrayList<UUID> notMoved = new ArrayList<>();
    private HashMap<UUID, String> lastMessage = new HashMap<>();
    private StaffHook staffHook;

    public ChatListener() {
        staffHook = new StaffHook("https://discordapp.com/api/webhooks/484263323244691456/vEjKOUvn7EWoqhAxhEIVm3pDXh2IqS-jUrPBGY814cYicxOocDm9Ps_ClJbrk7IMWKg5");
    }

//    @EventHandler
//    public void onJoin(PlayerJoinEvent event) {
//        if (!event.getPlayer().hasPermission("carbyne.staff"))
//            notMoved.add(event.getPlayer().getUniqueId());
//    }
//
//    @EventHandler
//    public void onMove(PlayerMoveEvent event) {
//        Location from = event.getFrom();
//        Location to = event.getTo();
//
//        if ((from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()))
//            notMoved.remove(event.getPlayer().getUniqueId());
//    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

//        if (!player.hasPermission("carbyne.staff")) {
//            RegionManager regionManager = Carbyne.getInstance().getWorldGuardPlugin().getRegionManager(player.getWorld());
//            ApplicableRegionSet regionSet = regionManager.getApplicableRegions(player.getLocation());
//
//            for (ProtectedRegion region : regionSet.getRegions())
//                if (region.getId().equalsIgnoreCase("SpawnNoTalk")) {
//                    MessageManager.sendMessage(player, "&cYou cannot talk in this region.");
//                    event.setCancelled(true);
//                    return;
//                }
//        }

        event.setMessage(event.getMessage().replace("<love>", "\u2764"));
        event.setMessage(event.getMessage().replace("<happy>", "\u263a"));
        event.setMessage(event.getMessage().replace("<sad>", "\u2639"));
        event.setMessage(event.getMessage().replace("<dead>", "\u2620"));
        event.setMessage(event.getMessage().replace("<peace>", "\u270c"));
        event.setMessage(event.getMessage().replace("<phone>", "\u2706"));
        event.setMessage(event.getMessage().replace("<star>", "\u2605"));
        event.setMessage(event.getMessage().replace("<checkmark>", "\u2713"));
        event.setMessage(event.getMessage().replace("<yingyang>", "\u262F"));
        event.setMessage(event.getMessage().replace("<toxic>", "\u2622"));
        event.setMessage(event.getMessage().replace("<swords>", "\u2694"));

        if (event.getMessage().length() >= 5)
            if (!player.hasPermission("carbyne.staff"))
                if (MessageManager.is60PercentUpper(event.getMessage())) {
                    MessageManager.sendMessage(player, "&cYour message contained over 60% upper-case characters.");
                    event.setMessage(event.getMessage().toLowerCase());
                }

        if (StaticClasses.staffManager.getStaffChatPlayers().contains(player.getUniqueId())) {
            MessageManager.sendStaffMessage(player, event.getMessage());
            event.setCancelled(true);
            return;
        }

        if (StaticClasses.staffManager.isChatMuted() && !event.getPlayer().hasPermission("carbyne.staff")) {
            event.setCancelled(true);
            MessageManager.sendMessage(event.getPlayer(), "&cThe chat is currently muted.");
            return;
        }

        if (StaticClasses.staffManager.getSlowChatTime() > 0 && !event.getPlayer().hasPermission("carbyne.staff"))
            if (!Cooldowns.tryCooldown(event.getPlayer().getUniqueId(), "slowChatCD", StaticClasses.staffManager.getSlowChatTime() * 1000)) {
                MessageManager.sendMessage(event.getPlayer(), "&cYou may speak again in " + (Cooldowns.getCooldown(event.getPlayer().getUniqueId(), "slowChatCD") / 1000) + " seconds");
                event.setCancelled(true);
                return;
            }

        if (player.hasPermission("carbyne.staff.pin")) {
            Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

            if (!profile.hasPin()) {
                event.setCancelled(true);
                MessageManager.sendMessage(player, "&7You cannot chat until you entered your PIN.");
            }

            if (StaticClasses.staffManager.getFrozenStaff().contains(player.getUniqueId())) {
                event.setCancelled(true);

                if (!isFourDigitCode(event.getMessage())) {
                    MessageManager.sendMessage(player, "&4That PIN is incorrect. Please try again.");
                    return;
                }

                if (!event.getMessage().equalsIgnoreCase(profile.getPin())) {
                    MessageManager.sendMessage(player, "&4That PIN is incorrect. Please try again.");
                    return;
                }

                StaticClasses.staffManager.getFrozenStaff().remove(player.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setWalkSpeed(0.2f);
                        player.removePotionEffect(PotionEffectType.JUMP);
                    }
                }.runTask(Carbyne.getInstance());
                MessageManager.sendMessage(player, "&7You have been successfully authenticated.");
                return;
            }
        }

        if (lastMessage.containsKey(player.getUniqueId()) && event.getMessage().equalsIgnoreCase(lastMessage.get(player.getUniqueId()))) {
            MessageManager.sendMessage(player, "&cDo not repeat yourself.");
            event.setCancelled(true);
            return;
        }

//        if (notMoved.contains(event.getPlayer().getUniqueId())) {
//            event.setCancelled(true);
//            //WHAT IS THE PURPOSE OF THIS AGAIN?
//            MessageManager.sendMessage(player, "&cYou must move before chatting.");
//            return;
//        }

        JSONMessage newMessage = JSONMessage.create("");

        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        if (profile != null) {
            switch (profile.getProfileChatChannel()) {
                case LOCAL:
                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f[&bLocal&f] "));
                    break;
                case TOWN:
                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f[&aTown&f] "));
                    break;
                case NATION:
                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f[&dNation&f] "));
                    break;
            }
        }

        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

            if (resident.hasTown()) {
                Town town = resident.getTown();

                newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f["));

                if (resident.hasNation()) {
                    Nation nation = resident.getTown().getNation();

                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&6" + nation.getName()));

                    if (getNationMessagePart(nation) != null) {
                        newMessage.tooltip(getNationMessagePart(nation));
                        newMessage.runCommand("/nation " + nation.getName());
                    }

                    newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f:"));
                }

                newMessage.then(ChatColor.translateAlternateColorCodes('&', "&3" + town.getName()));

                if (getTownMessagePart(town) != null) {
                    newMessage.tooltip(getTownMessagePart(town));
                    newMessage.runCommand("/town " + town.getName());
                }

                newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f]"));
            }
        } catch (NotRegisteredException e1) {
            e1.printStackTrace();
        }

        newMessage.then("").then(ChatColor.translateAlternateColorCodes('&', player.getDisplayName()));

        try {
            Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

            if (getPlayerMessagePart(resident, player) != null) {
                newMessage.tooltip(getPlayerMessagePart(resident, player));
            }
        } catch (NotRegisteredException e1) {
            e1.printStackTrace();
        }

        newMessage.suggestCommand("/msg " + player.getName() + " ");

        String message = event.getMessage();
        newMessage.then(ChatColor.translateAlternateColorCodes('&', "&f: "));

        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW, LinkType.EMAIL))
                .build();

        String[] msg = message.split(" ");
        //String chatFix = "";
        for (String s : msg) {
            if (s.equalsIgnoreCase("%item%") && player.hasPermission("carbyne.donator")) {
                ItemStack item = player.getInventory().getItemInMainHand();
                String type = item.getType().name().substring(0, 1).toUpperCase();
                String sl = type + item.getType().name().substring(1).toLowerCase().replace("_", " ");

                StringBuilder toolTip;

                toolTip = new StringBuilder((item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : sl) + "\n");
                for (Enchantment enchantment : player.getInventory().getItemInMainHand().getEnchantments().keySet())
                    toolTip.append("&7").append(MessageManager.getEnchantmentFriendlyName(enchantment)).append(" &7").append(MessageManager.getPotionAmplifierInRomanNumerals(item.getEnchantments().get(enchantment))).append("\n");

                if (item.getItemMeta().hasLore())
                    for (String l : item.getItemMeta().getLore())
                        toolTip.append(l).append("\n");

                toolTip.append("\n" + "&7").append(sl);

                newMessage.then("").then(item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : sl).tooltip(ChatColor.translateAlternateColorCodes('&', toolTip.toString())).then(" ");
            } else {
                LinkSpan link = null;

                if (linkExtractor.extractLinks(s).iterator().hasNext()) {
                    link = linkExtractor.extractLinks(s).iterator().next();
                }

                if (link != null) {
                    /*if (chatFix.length() > 0) {
                        newMessage.then(player.hasPermission("carbyne.chatcolors") ? ChatColor.translateAlternateColorCodes('^', chatFix) : chatFix);
                        chatFix = "";
                    }*/

                    newMessage.then("[Link] ").color(ChatColor.AQUA)
                            .openURL(s.substring(link.getBeginIndex(), link.getEndIndex()))
                            .tooltip(getUrlInfoMessagePart(s));


                } else {
                    newMessage.then((player.hasPermission("carbyne.chatcolors") ? ChatColor.translateAlternateColorCodes('&', s) : s) + " ");
                }
            }
        }

        if (profile != null) {
            switch (profile.getProfileChatChannel()) {
                case LOCAL:
                    for (Player all : PlayerUtility.getPlayersInRadius(player.getLocation(), 35)) {
                        if (all.getUniqueId().equals(player.getUniqueId())) {
                            newMessage.send(all);
                        } else {
                            Profile other = StaticClasses.profileManager.getProfile(all.getUniqueId());
                            if (!other.getIgnoredPlayers().contains(player.getUniqueId()) && other.isChatEnabled()) {
                                newMessage.send(all);
                            }
                        }
                    }

                    break;
                case TOWN:
                    Resident townResident;
                    try {
                        townResident = TownyUniverse.getDataSource().getResident(player.getName());
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    if (townResident == null)
                        return;

                    Town residentTown;

                    try {
                        residentTown = townResident.getTown();
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    List<Player> townResidents = new ArrayList<>();
                    for (Resident townRes : residentTown.getResidents()) {
                        Player p = Bukkit.getPlayer(townRes.getName());

                        if (p == null || (!p.isOnline()))
                            break;

                        townResidents.add(p);
                    }

                    for (Player all : townResidents) {
                        if (all.getUniqueId().equals(player.getUniqueId())) {
                            newMessage.send(all);
                        } else {
                            Profile other = StaticClasses.profileManager.getProfile(all.getUniqueId());
                            if (other.isChatEnabled()) {
                                newMessage.send(all);
                            }
                        }
                    }

                    break;
                case NATION:
                    Resident nationResident;
                    try {
                        nationResident = TownyUniverse.getDataSource().getResident(player.getName());
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    if (nationResident == null)
                        return;

                    Town nationTown;
                    try {
                        nationTown = nationResident.getTown();
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    Nation nation;
                    try {
                        nation = nationTown.getNation();
                    } catch (NotRegisteredException e) {
                        return;
                    }

                    List<Player> nationResidents = new ArrayList<>();
                    for (Resident nationRes : nation.getResidents()) {
                        Player p = Bukkit.getPlayer(nationRes.getName());

                        if (p == null || !p.isOnline())
                            break;

                        nationResidents.add(p);
                    }

                    for (Player p : nationResidents) {
                        if (p.getUniqueId().equals(player.getUniqueId())) {
                            newMessage.send(p);
                        } else {
                            Profile other = StaticClasses.profileManager.getProfile(p.getUniqueId());
                            if (other.isChatEnabled()) {
                                newMessage.send(p);
                            }
                        }
                    }

                    break;
                case GLOBAL:
                    for (Player all : PlayerUtility.getOnlinePlayers()) {
                        if (all.getUniqueId().equals(player.getUniqueId())) {
                            newMessage.send(all);
                        } else {
                            Profile other = StaticClasses.profileManager.getProfile(all.getUniqueId());
                            if (!other.getIgnoredPlayers().contains(player.getUniqueId()) && other.isChatEnabled()) {
                                newMessage.send(all);
                            }
                        }
                    }

                    break;
            }
        }

        String username = "Chat Logger";
        String image = "https://i.stack.imgur.com/oZidd.jpg";
        staffHook.sendMessage(new DiscordMessage(username, new SimpleDateFormat("hh:mm a").format(new Date()) + " - " + player.getName() + ": " + event.getMessage(), image));

        System.out.println("[Chat Message] " + player.getName() + ": " + event.getMessage());
        event.setCancelled(true);
    }

    public JSONMessage getTownMessagePart(Town town) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            JSONMessage message = JSONMessage.create("");

            String title = ChatTools.formatTitle(TownyFormatter.getFormattedName(town));
            title += ((!town.isAdminDisabledPVP() && (town.isPVP() || town.getWorld().isForcePVP())) ? " &4(PvP)" : "");
            title += (town.isOpen() ? " &b7(Open)" : "");

            lines.add(title);
            lines.add("&2Board: &a" + town.getTownBoard());
            lines.add("&2Town Size: &a" + town.getTownBlocks().size() + " / " + TownySettings.getMaxTownBlocks(town)
                    + (TownySettings.isSellingBonusBlocks() ? (" &b[Bought: " + town.getPurchasedBlocks() + "/" + TownySettings.getMaxPurchedBlocks() + "]") : "")
                    + ((town.getBonusBlocks() > 0) ? (" &b[Bonus: " + town.getBonusBlocks() + "]") : "")
                    + ((TownySettings.getNationBonusBlocks(town) > 0) ? (" &b[NationBonus: " + TownySettings.getNationBonusBlocks(town) + "]") : "")
                    + (town.isPublic() ? (" &7[Home: " + (town.hasHomeBlock() ? town.getHomeBlock().getCoord().toString() : "None") + "]") : ""));
            lines.add("&2Outposts: &a" + town.getMaxOutpostSpawn());
            lines.add("&2Permissions: &a" + town.getPermissions().getColourString().replace("f", "r"));
            lines.add("&2Explosions: " + (!town.isBANG() && !town.getWorld().isForceExpl() ? "&aOFF" : "&4ON")
                    + " &2Firespread: " + (!town.isFire() && !town.getWorld().isForceFire() ? "&aOFF" : "&4ON")
                    + " &2Mob Spawns: " + (!town.hasMobs() && !town.getWorld().isForceTownMobs() ? "&aOFF" : "&4ON"));

            String bankString;

            if (TownyEconomyHandler.isActive()) {
                bankString = "&2Bank: &a" + town.getHoldingFormattedBalance();

                if (town.hasUpkeep()) {
                    bankString += " &8| &2Daily upkeep: &4" + TownySettings.getTownUpkeepCost(town);
                }

                bankString += " &8| &2Tax: &4" + town.getTaxes() + (town.isTaxPercentage() ? "%" : "");

                lines.add(bankString);
            }

            lines.add("&2Mayor: &a" + TownyFormatter.getFormattedName(town.getMayor()));

            ArrayList<String> rankList = new ArrayList<>();
            ArrayList<Resident> residentsWithRanks = new ArrayList<>();

            for (String townRank : TownyPerms.getTownRanks()) {
                for (Resident resident : town.getResidents()) {
                    if (resident.getTownRanks() != null && resident.getTownRanks().contains(townRank)) {
                        residentsWithRanks.add(resident);
                    }
                }

                rankList.addAll(TownyFormatter.getFormattedResidents(townRank, residentsWithRanks));
                residentsWithRanks.clear();
            }

            lines.addAll(rankList);

            if (town.hasNation()) {
                lines.add("&2Nation: &a" + TownyFormatter.getFormattedName(town.getNation()));
            }

            String[] residents = TownyFormatter.getFormattedNames(town.getResidents().toArray(new Resident[0]));
            if (residents.length > 34) {
                String[] entire = residents;
                residents = new String[36];
                System.arraycopy(entire, 0, residents, 0, 35);
                residents[35] = "and more...";
            }

            lines.addAll(ChatTools.listArr(residents, "&2Residents &a[" + town.getNumResidents() + "]" + "&2" + ":" + "&f" + " "));

            for (int i = 0; i < lines.size(); i++) {
                String string = lines.get(i);

                if (i != lines.size() - 1) {
                    message.then(ChatColor.translateAlternateColorCodes('&', string) + "\n");
                } else {
                    message.then(ChatColor.translateAlternateColorCodes('&', string));
                }
            }

            return message;
        } catch (TownyException ignored) {
            ignored.printStackTrace();
        }

        return null;
    }

    public JSONMessage getNationMessagePart(Nation nation) {
        ArrayList<String> lines = new ArrayList<>();
        JSONMessage message = JSONMessage.create("");

        lines.add(ChatTools.formatTitle(TownyFormatter.getFormattedName(nation)));

        String line = "";
        if (TownyEconomyHandler.isActive()) {
            line = "&2Bank: &a" + nation.getHoldingFormattedBalance();

            if (TownySettings.getNationBonusBlocks(nation) > 0.0) {
                line += " &8| &2Daily upkeep: &4" + TownySettings.getNationUpkeepCost(nation);
            }
        }

        if (nation.isNeutral()) {
            if (line.length() > 0) {
                line += " &8| ";
            }

            line += "&7Peaceful";
        }

        if (line.length() > 0) {
            lines.add(line);
        }

        if (nation.getNumTowns() > 0 && nation.hasCapital() && nation.getCapital().hasMayor()) {
            lines.add("&2King: &a" + TownyFormatter.getFormattedName(nation.getCapital().getMayor()) + " &2NationTax: &4" + nation.getTaxes());
        }

        if (nation.getAssistants().size() > 0) {
            lines.addAll(ChatTools.listArr(TownyFormatter.getFormattedNames(nation.getAssistants()), "&2Assistants:&f "));
        }

        lines.addAll(ChatTools.listArr(TownyFormatter.getFormattedNames(nation.getTowns().toArray(new Town[nation.getTowns().size()])), "&2Towns &a[" + nation.getNumTowns() + "]&2:&f "));
        lines.addAll(ChatTools.listArr(TownyFormatter.getFormattedNames(nation.getAllies().toArray(new Nation[nation.getAllies().size()])), "&2Allies &a[" + nation.getAllies().size() + "]&2:&f "));
        lines.addAll(ChatTools.listArr(TownyFormatter.getFormattedNames(nation.getEnemies().toArray(new Nation[nation.getEnemies().size()])), "&2Enemies &a[" + nation.getEnemies().size() + "]&2:&f "));

        for (int i = 0; i < lines.size(); i++) {
            String string = lines.get(i);

            if (i != lines.size() - 1) {
                message.then(ChatColor.translateAlternateColorCodes('&', string) + "\n");
            } else {
                message.then(ChatColor.translateAlternateColorCodes('&', string));
            }
        }

        return message;
    }

    public JSONMessage getPlayerMessagePart(Resident resident, Player player) {
        ArrayList<String> lines = new ArrayList<>();
        JSONMessage message = JSONMessage.create("");

        lines.add(ChatTools.formatTitle(TownyFormatter.getFormattedName(resident)));
        lines.add("&2Registered: &a" + TownyFormatter.registeredFormat.format(resident.getRegistered()) + " &8| &2Last Online: &a" + TownyFormatter.lastOnlineFormat.format(resident.getLastOnline()));

        if (Carbyne.getInstance().getPermissions().getPrimaryGroup(player) != null) {
            lines.add("&2Group: &a" + Carbyne.getInstance().getPermissions().getPrimaryGroup(player).toLowerCase().substring(0, 1).toUpperCase() + Carbyne.getInstance().getPermissions().getPrimaryGroup(player).toLowerCase().substring(1));
        }

        lines.add("&2Owner of: &a" + resident.getTownBlocks().size() + " plots");
        lines.add("&2    Perm: " + resident.getPermissions().getColourString());
        lines.add("&2PvP: " + (resident.getPermissions().pvp ? "&4ON" : "&aOFF")
                + " &2Explosions: " + (resident.getPermissions().explosion ? "&4ON" : "&aOFF")
                + " &2Firespread: " + (resident.getPermissions().fire ? "&4ON" : "&aOFF")
                + " &2Mob Spawns: " + (resident.getPermissions().mobs ? "&4ON" : "&aOFF"));

        try {
            if (TownyEconomyHandler.isActive()) {
                lines.add("&2Bank: &a" + resident.getHoldingFormattedBalance());
            }
        } catch (NullPointerException ignored) {
        }

        String line = "&2Town: &a";
        if (!resident.hasNation()) {
            line += "None";
        } else {
            try {
                line += TownyFormatter.getFormattedName(resident.getTown());
            } catch (TownyException e) {
                line += "Error: " + e.getMessage();
            }
        }

        lines.add(line);

        if (resident.hasTown() && !resident.getTownRanks().isEmpty()) {
            lines.add("&2Town Ranks: &a" + StringMgmt.join(resident.getTownRanks(), ","));
        }

        if (resident.hasNation() && !resident.getNationRanks().isEmpty()) {
            lines.add("&2Nation Ranks: &a" + StringMgmt.join(resident.getNationRanks(), ","));
        }

        lines.addAll(TownyFormatter.getFormattedResidents("Friends", resident.getFriends()));

        for (int i = 0; i < lines.size(); i++) {
            String string = lines.get(i);

            if (i != lines.size() - 1) {
                message.then(ChatColor.translateAlternateColorCodes('&', string) + "\n");
            } else {
                message.then(ChatColor.translateAlternateColorCodes('&', string));
            }
        }

        return message;
    }

    public JSONMessage getUrlInfoMessagePart(String url) {
        JSONMessage message = JSONMessage.create("");

        String title = "&7No Title Provided";
        Long startMillis = System.currentTimeMillis();

        try {
            title = "&d" + getHeader(url);
        } catch (SocketTimeoutException ex1) {
            title = "&cTimed Out";
        } catch (UnknownHostException ex2) {
            title = "&cUnknown Host";
        } catch (Exception ex3) {
            title = "&d" + url;
        } finally {
            title = title + " &7(&c" + (System.currentTimeMillis() - startMillis) + "MS&7)";
        }

        message.then(ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeHtml(title)));

        return message;
    }

    public String getHeader(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setReadTimeout(500);
        con.setConnectTimeout(500);
        con.addRequestProperty("User-Agent", "Mozilla/4.76");
        con.setDoOutput(true);
        StringBuilder html = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")));
        char[] buffer = new char[1024];

        while (reader.read(buffer, 0, buffer.length) != -1) {
            html.append(buffer);
        }

        Matcher m = Pattern.compile("<title>(.+)</title>").matcher(html.toString());

        if (m.find()) {
            return m.group(1);
        }

        return "No Page Title";
    }

    public int getLastPossibleFullWordIndex(String string, int split) {
        int i = 0;
        int li = string.length();
        boolean n = false;
        char[] charArray;

        for (int length = (charArray = string.toCharArray()).length, j = 0; j < length; ++j) {
            char c = charArray[j];

            if (c == ' ')
                n = true;
            else {
                if (n) {
                    li = i;
                }

                n = false;
            }

            if (++i > split)
                return li;
        }

        return li;
    }

    public boolean isFourDigitCode(String string) {
        String regex = "[0-9]+";

        return string.length() == 4 && string.matches(regex);
    }
}
