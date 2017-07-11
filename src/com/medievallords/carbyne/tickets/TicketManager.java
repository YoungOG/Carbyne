package com.medievallords.carbyne.tickets;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.profiles.ProfileManager;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerUtility;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by WE on 2017-07-08.
 */
@Getter
@Setter
public class TicketManager {

    private ProfileManager profileManager = Carbyne.getInstance().getProfileManager();

    private MongoCollection<Document> ticketCollection = Carbyne.getInstance().getMongoDatabase().getCollection("tickets");

    private ArrayList<Ticket> tickets = new ArrayList<>();

    private HashMap<UUID, Ticket> respondingTickets = new HashMap<>();

    private HashMap<UUID, String> newTickets = new HashMap<>();

    public static String guiName = ChatColor.translateAlternateColorCodes('&', "&d&lTickets");
    public static String listName = ChatColor.translateAlternateColorCodes('&', "&e&lTicket list");

    private HashMap<Player, Ticket> claimedTickets = new HashMap<>();

    public static final int MAX_TICKETS = 3;

    public TicketManager () {
        load();

        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Carbyne.getInstance(), new Runnable() {
            @Override
            public void run() {

                int openTickets = getAmountOfOpenTickets();
                if (openTickets > 0) {

                    for (Player player : PlayerUtility.getOnlinePlayers()) {
                        if (player.hasPermission("carbyne.staff.tickets")) {
                            MessageManager.sendMessage(player, "&2There are currently &6" + openTickets + "&2 open tickets");
                        }
                    }
                }

                for (Player player : PlayerUtility.getOnlinePlayers()) {
                    if (getTicketAmount(player.getUniqueId(), false) > 0) {
                        MessageManager.sendMessage(player, "&2You have gotten &6" + getPlayerResponses(player.getUniqueId()));
                    }
                }
            }
        },0, 24000);
    }

    public void load () {
        for (Document document : ticketCollection.find()) {
            UUID player = UUID.fromString(document.getString("player"));
            TicketStatus status = TicketStatus.valueOf(document.getString("status"));
            String date = document.getString("date");
            String question = document.getString("question");
            String response = document.getString("response");
            UUID staff = UUID.fromString(document.getString("staff"));

            Ticket ticket = new Ticket(player, question, response, status, date, staff);
            tickets.add(ticket);
        }
    }

    public void save () {
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            Document doc = new Document();
            doc.append("player", ticket.getPlayer().toString());

            doc.append("date", ticket.getPlayer());
            doc.append("status", ticket.getStatus().toString());
            doc.append("staff", ticket.getStaff().toString());
            doc.append("question", ticket.getQuestion());
            doc.append("response", ticket.getResponse());

            ticketCollection.replaceOne(Filters.eq("uniqueId", ticket.getPlayer().toString()), doc);
        }
    }

    public void openPlayerTickets(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        Inventory inv = Bukkit.createInventory(null, 54, player.getName() + "'s Tickets");

        for (int i = tickets.size(); i > 0; i--) {
            Ticket ticket = tickets.get(i - 1);
            if (ticket.getPlayer().equals(player.getUniqueId())) {
                List<String> lore = new ArrayList<>();
                lore.add("&a");

                int lenght = 0;
                int index = 0;
                for (String s : ticket.getQuestion().split(" ")) {
                    lenght++;

                    if (lenght >= 12) {
                        lenght = 0;
                        index++;
                        lore.add("&a");
                    }

                    lore.set(index, lore.get(index) + s + " ");

                }

                inv.addItem(new ItemBuilder(getMaterial(ticket)).name("&aTicket #" + (i - 1)).setLore(lore).addLore("").addLore("&6" + ticket.getDate()).build());
            }
        }

        player.openInventory(inv);
    }

    public void openTicketGUI(UUID uuid, boolean staff) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (staff) {
            Inventory inv = Bukkit.createInventory(null, 9, guiName);

            inv.setContents(new ItemStack[] {
                    null, null, null,
                    new ItemBuilder(Material.NAME_TAG).name("&6Tickets").build(), null,
                    new ItemBuilder(Material.BREAD).build()
            });

            player.openInventory(inv);
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 9, guiName);

        inv.setContents(new ItemStack[] {
                null, null, null,
                new ItemBuilder(Material.GOLD_INGOT).name("&6New Ticket").build(), null,
                new ItemBuilder(Material.IRON_INGOT).build(), null,
                new ItemBuilder(Material.PAPER).name("&6My Tickets").build()
        });

        player.openInventory(inv);
    }

    public void openTicketList (UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);

        Inventory inv = Bukkit.createInventory(null, 54, listName);
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null) {
                List<String> lore = new ArrayList<>();
                if (Bukkit.getServer().getPlayer(ticket.getPlayer()) != null || !Bukkit.getServer().getPlayer(ticket.getPlayer()).isOnline()) {
                    lore.add("&e" + Bukkit.getServer().getPlayer(ticket.getPlayer()).getName());
                } else {
                    if (Bukkit.getServer().getOfflinePlayer(ticket.getPlayer()) != null) {
                        lore.add("&e" + Bukkit.getServer().getOfflinePlayer(ticket.getPlayer()).getName());
                    }
                }
                lore.add("&a");

                int lenght = 0;
                int index = 1;
                for (String s : ticket.getQuestion().split(" ")) {
                    lenght++;

                    if (lenght >= 12) {
                        lenght = 0;
                        index++;
                        lore.add("&a");
                    }

                    lore.set(index, lore.get(index) + s + " ");

                }

                inv.addItem(new ItemBuilder(getMaterial(ticket)).name("&aTicket #" + (i)).setLore(lore).addLore("").addLore("&6" + ticket.getDate()).build());
            }
        }

        player.openInventory(inv);
    }

    public void closeTicket(UUID player, String name) {
        Ticket ticket = getTicket(name);
        if (ticket == null) {
            return;
        }

        if ((ticket.getStatus() ==  TicketStatus.CLAIMED || ticket.getStatus() == TicketStatus.CLOSED) && !ticket.getStaff().equals(player)) {
            MessageManager.sendMessage(player, "&cYou cannot claim this ticket");
            return;
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setStaff(player);

        Profile profile = profileManager.getProfile(player);
        if (profile != null) {
            profile.setClosedTickets(profile.getClosedTickets() + 1);
        }
    }

    public void claimTicket (UUID player, String name) {
        Ticket ticket = getTicket(name);
        if (ticket == null) {
            return;
        }

        if ((ticket.getStatus() ==  TicketStatus.CLAIMED || ticket.getStatus() == TicketStatus.CLOSED) && !ticket.getStaff().equals(player)) {
            MessageManager.sendMessage(player, "&cYou cannot claim this ticket");
            return;
        }

        respondingTickets.put(player, ticket);
        ticket.setStatus(TicketStatus.CLAIMED);
        ticket.setStaff(player);

        Profile profile = profileManager.getProfile(player);
        if (profile != null) {
            profile.setClaimedTickets(profile.getClaimedTickets() + 1);
        }
    }

    public void createTicket (UUID player, String msg) {
        if (getTicketAmount(player, true) >= 3) {
            MessageManager.sendMessage(player, "&cYou can have a maximum of 3 active tickets at a time");
            return;
        }

        Ticket ticket = new Ticket(player, msg);
        tickets.add(ticket);
        MessageManager.sendMessage(player, "&aTicket submitted");
        newTickets.remove(player);
    }

    public void openTicket(Player player, String name, boolean staff) {
        Ticket ticket = getTicket(name);
        if (ticket == null) {
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9, name);

        if (!staff) {
            List<String> response = new ArrayList<>();
            response.add("&e" + Bukkit.getServer().getPlayer(ticket.getStaff()).getName());
            response.add("&a");

            int lenght = 0;
            int index = 1;
            for (String s : ticket.getResponse().split(" ")) {
                lenght++;

                if (lenght >= 12) {
                    lenght = 0;
                    index++;
                    response.add("&a");
                }

                response.set(index, response.get(index) + s + " ");

            }

            inv.setContents(new ItemStack[]{
                    null, null, null,
                    new ItemBuilder(Material.REDSTONE).name("&aRemove").build(), null,
                    !ticket.getResponse().equals("") ? new ItemBuilder(Material.IRON_INGOT).name("&6Response").setLore(response).build() : new ItemBuilder(Material.IRON_INGOT).name("&cNo Response").build(),
                    new ItemBuilder(getMaterial(ticket)).name(getColor(ticket) + ticket.getStatus().name()).build()
            });

        } else {

            inv.setContents(new ItemStack[]{
                    null, null, null,
                    new ItemBuilder(Material.BED).name("&aRespond").build(), null,
                    ticket.getStatus() != TicketStatus.CLOSED ? new ItemBuilder(Material.BAKED_POTATO).name("&cClose").build() : null,
                    new ItemBuilder(getMaterial(ticket)).name(getColor(ticket) + ticket.getStatus().name()).build()
            });
        }

        player.openInventory(inv);
    }


    public Ticket getTicket (String name) {
        String chat = ChatColor.stripColor(name);
        String[] split = chat.replace(" ", "").split("#");
        if (split.length < 2) {
            return null;
        }

        int index = Integer.parseInt(split[1]);
        return tickets.get(index);
    }

    public Material getMaterial(Ticket ticket) {
        switch (ticket.getStatus()) {
            case OPEN: return Material.EMERALD_BLOCK;
            case CLOSED: return Material.REDSTONE_BLOCK;
            case CLAIMED: return Material.GOLD_BLOCK;
        }

        return null;
    }

    public int getTicketAmount (UUID uuid, boolean open) {
        int amount = 0;
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null && ticket.getPlayer().equals(uuid)) {
                if (open && ticket.getStatus() == TicketStatus.OPEN)
                amount++;
            }
        }

        return amount;
    }

    public int getAmountOfOpenTickets () {
        int amount = 0;
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null && ticket.getStatus() == TicketStatus.OPEN) {
                amount++;
            }
        }

        return amount;
    }

    public int getPlayerResponses (UUID player) {
        int amount = 0;
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null && ticket.getPlayer().equals(player) && ticket.getResponse() != null && ticket.getResponse().length() >= 1) {
                amount++;
            }
        }

        return amount;
    }

    public List<Ticket> getOpenTickets () {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null && ticket.getStatus() == TicketStatus.OPEN) {
                tickets.add(ticket);
            }
        }

        return tickets;
    }

    public List<Ticket> getClosedTickets () {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null && ticket.getStatus() == TicketStatus.CLOSED) {
                tickets.add(ticket);
            }
        }

        return tickets;
    }

    public List<Ticket> getClaimedTickets () {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket != null && ticket.getStatus() == TicketStatus.CLAIMED) {
                tickets.add(ticket);
            }
        }

        return tickets;
    }

    public ChatColor getColor (Ticket ticket) {
        switch (ticket.getStatus()) {
            case OPEN: return ChatColor.GREEN;
            case CLOSED: return ChatColor.RED;
            case CLAIMED: return ChatColor.YELLOW;
        }

        return ChatColor.AQUA;
    }

}
