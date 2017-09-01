package com.medievallords.carbyne.staff;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
public class StaffManager {

    private Carbyne main = Carbyne.getInstance();
    @Getter
    private HashSet<UUID> vanish = new HashSet<>(), frozen = new HashSet<>(), frozenStaff = new HashSet<>();
    @Getter
    private List<UUID> staffModePlayers = new ArrayList<>();
    @Getter
    private Map<UUID, SpecialPlayerInventory> inventories = new HashMap<>();
    @Getter
    @Setter
    private boolean chatMuted = false;
    @Getter
    @Setter
    private int slowChatTime = 0;
    @Getter
    @Setter
    private int serverSlots = 175;

    private Map<String, Boolean> falsePerms = new HashMap<>(), truePerms = new HashMap<>();
    @Getter
    private final ItemStack randomTeleportTool, toggleVanishTool, freezeTool, inspectInventoryTool, thruTool, air, ticketTool, wand;

    @Getter
    private File staffWhitelistCommandsFile;
    @Getter
    private FileConfiguration staffWhitelistCommandConfiguration;

    @Getter
    private List<String> staffmodeCommandWhitelist;

    public StaffManager() {

        staffWhitelistCommandsFile = new File(main.getDataFolder(), "staffmodewhitelist.yml");
        staffWhitelistCommandConfiguration = YamlConfiguration.loadConfiguration(staffWhitelistCommandsFile);

        staffmodeCommandWhitelist = staffWhitelistCommandConfiguration.getStringList("Commands");

        falsePerms.put(new String("mv.bypass.gamemode.*"), false);
        falsePerms.put(new String("CreativeControl.*"), false);
        truePerms.put(new String("mv.bypass.gamemode.*"), true);
        truePerms.put(new String("CreativeControl.*"), true);

        randomTeleportTool = new ItemBuilder(Material.WATCH).name("&5Random Teleport").addLore("&fRight click to teleport to a random player").addLore("&fLeft click to teleport to a random player underground").build();
        toggleVanishTool = new ItemBuilder(Material.INK_SACK).durability(10).name("&5Vanish").addLore("&fToggle vanish").build();
        freezeTool = new ItemBuilder(Material.ICE).name("&1Freeze").addLore("&fFreeze a player").build();
        inspectInventoryTool = new ItemBuilder(Material.BOOK).name("&2Inspect Inventory").addLore("&fView the contents of a player\'s inventory").build();
        thruTool = new ItemBuilder(Material.COMPASS).name("&4Thru Tool").addLore("&fWarp through walls and doors").build();
        air = new ItemBuilder(Material.AIR).build();
        ticketTool = new ItemBuilder(Material.PAPER).name("&6Tickets").build();
        wand = new ItemBuilder(Material.WOOD_AXE).name("&6World Edit").build();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : frozen) {
                    MessageManager.sendMessage(id, "&f\u2588\u2588\u2588\u2588&c\u2588&f\u2588\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588\u2588&c\u2588&6\u2588&c\u2588&f\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588");
                    MessageManager.sendMessage(id, "&f\u2588&c\u2588&6\u2588\u2588&0\u2588&6\u2588\u2588&c\u2588&f\u2588");
                    MessageManager.sendMessage(id, "&f\u2588&c\u2588&6\u2588\u2588\u2588\u2588\u2588&c\u2588&f\u2588");
                    MessageManager.sendMessage(id, "&c\u2588&6\u2588\u2588\u2588&0\u2588&6\u2588\u2588\u2588&c\u2588");
                    MessageManager.sendMessage(id, "&c\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                    MessageManager.sendMessage(id, "&4&l[&c&l!&4&l] &6You have been frozen! Do not log out or you will be banned!");
                    MessageManager.sendMessage(id, "&4&l[&c&l!&4&l] &6Join the Discord using: /discord");
                }
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 3 * 25L);
    }

    /**
     * PRECONDITION: Player has permission carbyne.staff.staffmode
     *
     * @param player Player to toggle staff mode for
     */
    public void toggleStaffMode(Player player) {
        UUID pUUID = player.getUniqueId();
        if (staffModePlayers.contains(pUUID)) {
            toggleGamemode(player, false);
            staffModePlayers.remove(pUUID);
            player.getInventory().clear();
            showPlayer(player);
            //Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), "pex user " + player.getName() + " remove mv.bypass.gamemode.*");
            PermissionUtils.setPermissions(player.addAttachment(main), falsePerms, true);
            MessageManager.sendMessage(player, "&cYou have disabled staff mode and are visible!");
        } else {
            if (PlayerUtility.isInventoryEmpty(player)) {
                toggleGamemode(player, true);
                staffModePlayers.add(pUUID);
                player.getInventory().setContents(new ItemStack[]{thruTool, inspectInventoryTool, freezeTool, air, ticketTool, wand, air, toggleVanishTool, randomTeleportTool});
                vanishPlayer(player);
                //Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), "pex user " + player.getName() + " add mv.bypass.gamemode.*");
                PermissionUtils.setPermissions(player.addAttachment(main), truePerms, true);
                MessageManager.sendMessage(player, "&cYou have enabled staff mode and have vanished!");
                if (main.getTrailManager().getAdvancedEffects().containsKey(player.getUniqueId())) {
                    main.getTrailManager().getAdvancedEffects().remove(player.getUniqueId());
                }

                if (main.getTrailManager().getActivePlayerEffects().containsKey(player.getUniqueId())) {
                    main.getTrailManager().getActivePlayerEffects().remove(player.getUniqueId());
                }

            } else MessageManager.sendMessage(player, "&cYou need an empty inventory to enter staff mode!");
        }
    }

    /**
     * Method can be used to find players x-raying. Note that 30 is the gold spawn height.
     *
     * @param player Player to teleport
     */
    public void teleportToPlayerUnderY30(Player player) {
        List<Player> players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers())

            if (p.getLocation().getY() <= 30 && !player.equals(p))
                players.add(p);

        if (players.size() == 0)
            return;

        player.teleport(players.get(Maths.randomNumberBetween(players.size(), 0)));
    }

    /**
     * Randomly inspect players on the server. Eliminates staff bias.
     *
     * @param player Player to teleport
     */
    public void teleportToRandomPlayer(Player player) {
        List<Player> players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers())
            if (!player.equals(p) && (!player.hasPermission("carbyne.staff") || !player.isOp()))
                players.add(p);

        if (players.size() == 0) {
            MessageManager.sendMessage(player, "&cThere are no available players to teleport to.");
            return;
        }

        player.teleport(players.get(Maths.randomNumberBetween(players.size(), 0)));
    }

    /**
     * PRECONDITION: Player is in staff mode and has permissions to vanish
     *
     * @param player Player to toggle vanish
     */
    public void toggleVanish(Player player) {
        if (vanish.contains(player.getUniqueId())) {
            showPlayer(player);
            MessageManager.sendMessage(player, "&cYou have been un-vanished!");
        } else {
            vanishPlayer(player);
            MessageManager.sendMessage(player, "&cYou are now vanished!");
        }
    }

    /**
     * Toggle method for freeze
     *
     * @param player Player to freeze
     */
    public void toggleFreeze(Player player) {
        if (frozen.contains(player.getUniqueId()))
            unfreezePlayer(player);
        else
            freezePlayer(player);
    }

    public void toggleFreeze(Player player, Player freezer) {
        if (frozen.contains(player.getUniqueId())) {
            unfreezePlayer(player);
            MessageManager.sendMessage(freezer, "&9You have unfrozen " + player.getName() + "!");
        } else {
            freezePlayer(player);
            MessageManager.sendMessage(freezer, "&9You have frozen " + player.getName() + "!");
        }
    }

    public void showPlayerInventory(Player playerInv, Player viewer) {
        SpecialPlayerInventory inv = inventories.get(playerInv.getUniqueId());

        if (inv == null) {
            inv = new SpecialPlayerInventory(playerInv, playerInv.isOnline());
        }

        viewer.openInventory(inv.getBukkitInventory());
    }

    private void vanishPlayer(Player player) {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (!all.getUniqueId().equals(player.getUniqueId())) {
                if (!all.hasPermission("carbyne.staff.canseevanished")) {
                    all.hidePlayer(player);
                }
            }
        }

        vanish.add(player.getUniqueId());
    }

    public void showPlayer(Player player) {
        for (Player all : PlayerUtility.getOnlinePlayers()) {
            if (!all.getUniqueId().equals(player.getUniqueId())) {
                //if (!all.canSee(player)) {
                all.showPlayer(player);
                //}
            }
        }
        vanish.remove(player.getUniqueId());
    }

    public void freezePlayer(Player player) {
        frozen.add(player.getUniqueId());
        player.setWalkSpeed(0.0F);
        MessageManager.sendMessage(player, "&cYou have been frozen.");
    }

    public void unfreezePlayer(Player player) {
        frozen.remove(player.getUniqueId());
        player.setWalkSpeed(0.2F);
        MessageManager.sendMessage(player, "&aYou are no longer frozen.");
    }

    public void shutdown() {
        for (UUID id : frozen) {
            unfreezePlayer(Bukkit.getPlayer(id));
        }
        for (UUID id : staffModePlayers) {
            Bukkit.getPlayer(id).getInventory().clear();
            //Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), "pex user " + Bukkit.getPlayer(id).getName() + " remove mv.bypass.gamemode.*");
            PermissionUtils.setPermissions(Bukkit.getPlayer(id).addAttachment(main), falsePerms, true);
        }
    }

    private void toggleGamemode(Player player, boolean flag) {
        if (flag) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    public boolean isVanished(Player player) {
        return vanish.contains(player.getUniqueId());
    }

    public static void messageStaff(String... message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("carbyne.staff")) {
                MessageManager.sendMessage(player, message);
            }
        }
    }

}
