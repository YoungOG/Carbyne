package com.medievallords.carbyne.staff.tickets;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Dalton on 5/30/2017.
 *
 * This class is the ticket manager but it also stores data into a mongo database collection to
 * see which staff are answering tickets. The stats are stored in physical memory while the server
 * is running, but the amount of data is kilobytes small and won't use much memory.
 */
@Getter
public class TicketManager
{

    private Carbyne main = Carbyne.getInstance();
    private MongoCollection<Document> statCollection;
    private List<Ticket> activeRequests;
    private Map<UUID, Statistics> staffStats;
    private List<Player> onlineStaff;

    public TicketManager()
    {
        load();
    }

    public void reload()
    {
        saveStats();
        load();
    }

    public void load()
    {
        statCollection = main.getMongoDatabase().getCollection("staff-statistics");
        activeRequests = new ArrayList<Ticket>();
        staffStats = new HashMap<UUID, Statistics>();
        onlineStaff = new ArrayList<Player>();

        TicketTask ticketTask = new TicketTask(this);
        ticketTask.runTaskTimerAsynchronously(main, 0L, 24000L);

        loadStats();
    }

    /**
     * PRECONDITION: The player's uuid is not a key in active requests and player has proper permissions.
     * Creates a ticket and adds it to the active tickets.
     * @param player The player who sent the ticket
     * @param playerMessage The players message
     */
    public void createTicket(Player player, String playerMessage)
    {
        activeRequests.add(new Ticket(player, playerMessage));
        MessageManager.sendMessage(player, "&3Successfully submitted ticket!");

        for(Player p : Bukkit.getOnlinePlayers())
            if(p.hasPermission("carbyne.commands.ticket"))
                MessageManager.sendMessage(p, "&3" + player.getName() + " has submitted a ticket!");
    }

    /**
     * PRECONDITION: Player has proper permissions.
     * Shows all of the active tickets to the sender.
     * @param viewer The viewer.
     */
    public void showTickets(Player viewer)
    {
        MessageManager.sendMessage(viewer, "ACTIVE TICKETS");
        activeRequests.forEach((v) -> MessageManager.sendMessage(viewer, "&2#&e" + v.getId() + "&2 | &e" + v.getSentBy().getName() + "&3 | &4" + v.getStatus().name().toUpperCase() + " &3|"));
    }

    /**
     * PRECONDITION: Player has the proper permissions.
     * Show the information of a ticket to a player.
     * @param viewer Viewer of the ticket.
     * @param id Id of ticket to check.
     */
    public void showTicket(Player viewer, int id)
    {
        Ticket ticket = null;
        for(Ticket t : activeRequests)
            if(t.getId() == id)
            {
                ticket = t;
                break;
            }

        if(ticket == null)
        {
            MessageManager.sendMessage(viewer, "&cA ticket with this id does not exist!");
            return;
        }

        String claimedBy;
        claimedBy = (ticket.getClaimedBy() == null) ? "Nobody" : ticket.getClaimedBy().getName();

        MessageManager.sendMessage(viewer, new String[] {
                "&3Ticket #&e" + ticket.getId(),
                "&3Sent by: &e" + ticket.getSentBy().getName(),
                "&3Status: &4" + ticket.getStatus(),
                "&3Claimed by: &e" + claimedBy,
                "&3Date: &e" + ticket.getDate(),
                "&3Message: &e" + ticket.getPlayerMessage()
        });
    }

    /**
     * PRECONDITION: Player has proper permissions.
     * Switches the status of a ticket to claimed and updates the tickets information to reflect the change. Update claimed stats.
     * @param id Ticket id.
     * @param claimer The player claiming the ticket.
     * @return Failure or success.
     */
    public boolean claimTicket(int id, Player claimer)
    {
        for(Iterator<Ticket> itr = activeRequests.iterator(); itr.hasNext();)
        {
            Ticket ticket = itr.next();
            if(ticket.getId() == id)
                if(!ticket.getStatus().equals(TicketStatus.CLAIMED))
                {
                    ticket.setClaimedBy(claimer);
                    ticket.setStatus(TicketStatus.CLAIMED);
                    Statistics stats = staffStats.get(claimer.getUniqueId());
                    stats.setClaimed(stats.getClaimed() + 1);
                    MessageManager.sendMessage(claimer, "&3Successfully claimed the ticket with the id &e" + id + "&3!");
                    return true;
                }
                else MessageManager.sendMessage(claimer, "&cThis ticket is already claimed!");
        }
        return false;
    }

    /**
     * PRECONDITION: Player has proper permissions.
     * Closes and deletes a ticket. Updates stats.
     * @param id Id of the ticket to close.
     * @param closer Player attempting to close the ticket.
     * @return Failure or success.
     */
    public boolean closeTicket(int id, Player closer)
    {
        for(Iterator<Ticket> itr = activeRequests.iterator(); itr.hasNext();)
        {
            Ticket ticket = itr.next();
            if(ticket.getId() == id)
                if(ticket.getStatus().equals(TicketStatus.CLAIMED))
                {
                    activeRequests.remove(ticket);
                    Statistics stats = staffStats.get(closer.getUniqueId());
                    stats.setClosed(stats.getClosed() + 1);
                    MessageManager.sendMessage(closer, "&3Successfully closed the ticket with the id of &e" + id + "&3!");
                    return true;
                }
        }
        return false;
    }

    /**
     * Loads stats from the memory
     */
    private void loadStats()
    {
        for(Document pKeyDoc : statCollection.find())
        {
            UUID uuid = UUID.fromString(pKeyDoc.getString("uuid"));
            Document statDoc = pKeyDoc.get("stats", Document.class);
            int claimed = statDoc.getInteger("claimed");
            int closed = statDoc.getInteger("closed");
            staffStats.put(uuid, new Statistics(claimed, closed));
        }
    }

    /**
     * Save the stats that are currently in memory.
     */
    public void saveStats()
    {
        for(UUID uuid : staffStats.keySet())
        {
            Statistics stats = staffStats.get(uuid);
            Document pKeyDoc = new Document();
            pKeyDoc.put("uuid", uuid.toString());

            Document statDoc = new Document();
            statDoc.put("claimed", stats.getClaimed());
            statDoc.put("closed", stats.getClosed());

            pKeyDoc.put("stats", statDoc);

            statCollection.replaceOne(new Document("uuid", uuid.toString()), pKeyDoc, new UpdateOptions().upsert(true));
        }
    }

    /**
     * Returns the amount of tickets opened.
     * @return 0 if no tickets are opened, greater than 0 otherwise.
     */
    public int ticketsOpened()
    {
        int openTickets = 0;
        for(Ticket ticket : activeRequests)
            if(ticket.getStatus().equals(TicketStatus.OPEN))
                openTickets++;
        return openTickets;
    }

    /**
     *
     * @param player Checking this player.
     * @return true if a ticket is sent by the player, false if no ticket is claimed by the player
     */
    public boolean doesPlayerHaveActiveTicket(Player player)
    {
        for(Ticket ticket : activeRequests)
            if(ticket.getSentBy().equals(player)) return true;
        return false;
    }

}
