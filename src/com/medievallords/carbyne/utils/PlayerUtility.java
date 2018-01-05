package com.medievallords.carbyne.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerUtility {

    private static final int PROTOCOL_VERSION = 47;

    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static Resident getResident(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public static boolean hasClickedTop(InventoryClickEvent event) {
        return event.getRawSlot() == event.getSlot();
    }

    public static void checkForIllegalItems(Player player, Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasEnchants()) {
                        boolean hasIllegalItem = false;

                        for (Enchantment enchantment : item.getEnchantments().keySet()) {
                            if (item.getEnchantments().get(enchantment) > 10) {
                                hasIllegalItem = true;
                            }
                        }

                        if (hasIllegalItem) {
                            inventory.remove(item);

                            JSONMessage message = JSONMessage.create(ChatColor.translateAlternateColorCodes('&', "&f[&cWARNNING&f]: &cAn illegal item has been confiscated from &b" + player.getName() + "&c.\n"))
                                    .tooltip(ChatColor.translateAlternateColorCodes('&', "&cClick to teleport to &b" + player.getName() + "&c."))
                                    .runCommand("/tp " + player.getName())
                                    .then(ChatColor.translateAlternateColorCodes('&', "  &cItem Type: &b" + item.getType().name()) + "\n")
                                    .then(ChatColor.translateAlternateColorCodes('&', "  &cAmount: &b" + item.getAmount()) + "\n")
                                    .then(ChatColor.translateAlternateColorCodes('&', "  &cEnchantments: &b&nShow Enchantments"));

                            StringBuilder stringBuilder = new StringBuilder();

                            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                                stringBuilder.append("&c").append(enchantment.getName()).append(" &7(&b").append(enchantment.getId()).append("&7): &b").append(item.getEnchantmentLevel(enchantment)).append("\n");
                            }

                            message.tooltip(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));

                            for (Player all : PlayerUtility.getOnlinePlayers()) {
                                if (all.hasPermission("carbyne.illegalweapon")) {
                                    message.send(all);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void updateChestInventoryTitle(Player p, String title) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest", new ChatMessage(title), p.getOpenInventory().getTopInventory().getSize());
        ep.playerConnection.sendPacket(packet);
        ep.updateInventory(ep.activeContainer);
    }

    public static ArrayList<Player> getPlayersInRadius(Location radiusCenter, int radius) {
        ArrayList<Player> playerList = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getWorld().equals(radiusCenter.getWorld())) {
                if (onlinePlayer.getLocation().distance(radiusCenter) < radius) {
                    playerList.add(onlinePlayer);
                }
            }
        }
        return playerList;
    }

    /**
     * @param header The header of the tab list.
     */
    public static void broadcastHeader(String header) {
        broadcastHeaderAndFooter(header, null);
    }

    /**
     * @param footer The footer of the tab list.
     */
    public static void broadcastFooter(String footer) {
        broadcastHeaderAndFooter(null, footer);
    }

    /**
     * @param header The header of the tab list.
     * @param footer The footer of the tab list.
     */
    public static void broadcastHeaderAndFooter(String header, String footer) {
        for (Player player : Bukkit.getOnlinePlayers()) setHeaderAndFooter(player, header, footer);
    }

    /**
     * @param p      The Player.
     * @param header The header.
     */
    public static void setHeader(Player p, String header) {
        setHeaderAndFooter(p, header, null);
    }

    /**
     * @param p      The Player
     * @param footer The footer.
     */
    public static void setFooter(Player p, String footer) {
        setHeaderAndFooter(p, null, footer);
    }

    /**
     * @param player    The Player.
     * @param rawHeader The header in raw text.
     * @param rawFooter The footer in raw text.
     */
    public static void setHeaderAndFooter(Player player, String rawHeader, String rawFooter) {
        PacketContainer pc = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        pc.getChatComponents().write(0, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', rawHeader))).write(1, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', rawFooter)));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, pc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void removeItems(Inventory inventory, Material type, int data, int amount) {
        if (amount <= 0) {
            return;
        }

        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null) {
                continue;
            }

            if (type == is.getType() && is.getDurability() == data) {
                int newAmount = is.getAmount() - amount;

                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;

                    if (amount == 0)
                        break;
                }
            }
        }
    }

    public static void removeItems(Inventory inventory, ItemStack itemStack, int amount) {
        if (amount <= 0)
            return;

        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);

            if (is == null || is.getType() == Material.AIR)
                continue;

            if (itemStack.isSimilar(is)) {
                int newAmount = is.getAmount() - amount;

                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;

                    if (amount == 0)
                        break;
                }
            }
        }
    }

    public static int getEmptyInventorySlots(Inventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.getContents().length; i++) {
            if (inventory.getContents()[i] == null || inventory.getContents()[i].getType() == Material.AIR)
                count++;
        }
        return count;
    }

    public static boolean isInventoryEmpty(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents())
            if (itemStack != null && itemStack.getType() != Material.AIR)
                return false;

        return true;
    }

    public static ItemFrame getFrame(Location loc) {
        for (Entity e : loc.getChunk().getEntities())
            if (e instanceof ItemFrame)
                if (e.getLocation().getBlock().getLocation().distance(loc) == 0)
                    return (ItemFrame) e;
        return null;
    }
}