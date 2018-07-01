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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Calvin on 6/11/2017
 * for the Carbyne project.
 */
@Getter
public class StaffManager {

    private final ItemStack randomTeleportTool, toggleVanishTool, freezeTool, inspectInventoryTool, thruTool, air, wand;
    private Carbyne main = Carbyne.getInstance();
    private HashSet<UUID> vanish = new HashSet<>(), frozen = new HashSet<>(), frozenStaff = new HashSet<>();
    private List<UUID> staffModePlayers = new ArrayList<>(), staffChatPlayers = new ArrayList<>();
    private Set<UUID> staff = new HashSet<>();
    @Setter
    private boolean chatMuted = false;
    @Setter
    private int slowChatTime = 0;
    @Setter
    private int serverSlots = 175;
    private Map<String, Boolean> falsePerms = new HashMap<>(), truePerms = new HashMap<>();

    private File staffWhitelistCommandsFile;
    private FileConfiguration staffWhitelistCommandConfiguration;

    private List<String> staffmodeCommandWhitelist;

    public StaffManager() {
        staffWhitelistCommandsFile = new File(main.getDataFolder(), "staffmodewhitelist.yml");
        staffWhitelistCommandConfiguration = YamlConfiguration.loadConfiguration(staffWhitelistCommandsFile);

        staffmodeCommandWhitelist = staffWhitelistCommandConfiguration.getStringList("Commands");

        falsePerms.put("mv.bypass.gamemode.*", false);
        falsePerms.put("CreativeControl.*", false);
        truePerms.put("mv.bypass.gamemode.*", true);
        truePerms.put("CreativeControl.*", true);

        randomTeleportTool = new ItemBuilder(Material.WATCH).name("&5Random Teleport").addLore("&7Right click to teleport to a random player").addLore("&7Left click to teleport to a random player underground").build();
        toggleVanishTool = new ItemBuilder(Material.INK_SACK).durability(10).name("&5Vanish").addLore("&7Toggle vanish").build();
        freezeTool = new ItemBuilder(Material.ICE).name("&1Freeze").addLore("&7Freeze a player").build();
        inspectInventoryTool = new ItemBuilder(Material.BOOK).name("&2Inspect Inventory").addLore("&7View the contents of a player\'s inventory").build();
        thruTool = new ItemBuilder(Material.COMPASS).name("&4Thru Tool").addLore("&7Warp through walls and doors").build();
        wand = new ItemBuilder(Material.WOOD_AXE).name("&6World Edit").build();
        air = new ItemBuilder(Material.AIR).build();

        new BukkitRunnable() {
            @Override
            public void run() {
                int amount = PlayerUtility.getOnlinePlayers().size();
                logToFile("[Players] Online: " + amount + " ------ Time: " + new Date().toString());
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 20 * 60 * 30);

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
                    MessageManager.sendMessage(id, "&4&l[&c&l!&4&l] &6You have &c5 minutes &6to join our Discord.");
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
        UUID id = player.getUniqueId();

        if (staffModePlayers.contains(id)) {
            toggleGamemode(player, false);
            staffModePlayers.remove(id);
            player.getInventory().clear();
            showPlayer(player);

            for (String permissionNode : falsePerms.keySet())
                Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), "permissions player " + player.getName() + " set " + permissionNode + " false");

            MessageManager.sendMessage(player, "&cYou have disabled staff mode and are now visible!");
        } else
            if (PlayerUtility.isInventoryEmpty(player.getInventory())) {
                toggleGamemode(player, true);
                staffModePlayers.add(id);
                player.getInventory().setContents(new ItemStack[]{thruTool, inspectInventoryTool, freezeTool, air, wand, air, air, toggleVanishTool, randomTeleportTool});
                vanishPlayer(player);

                for (String permissionNode : truePerms.keySet())
                    Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), "permissions player " + player.getName() + " set " + permissionNode + " true");

                MessageManager.sendMessage(player, "&cYou have enabled staff mode and have vanished!");
                main.getTrailManager().getAdvancedEffects().remove(player.getUniqueId());
                main.getTrailManager().getActivePlayerEffects().remove(player.getUniqueId());

            } else
                MessageManager.sendMessage(player, "&cYou need an empty inventory to enter staff mode!");
    }

    /**
     * Method can be used to find players x-raying. Note that 30 is the gold spawn height.
     *
     * @param player Player to teleport
     */
    public void teleportToPlayerUnderY30(Player player) {
        List<Player> players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getWorld().getName().equalsIgnoreCase("player_world") && p.getLocation().getY() <= 30 && !player.equals(p) && (!p.hasPermission("carbyne.staff") || !p.isOp()))
                players.add(p);

        if (players.size() == 0) {
            MessageManager.sendMessage(player, "&cThere are no available players to teleport to.");
            return;
        }

        Player target = players.get(Maths.randomNumberBetween(players.size(), 0));
        player.teleport(target);
        MessageManager.sendMessage(player, "&aYou have teleported to &5" + target.getName() + "&a.");
    }

    /**
     * Randomly inspect players on the server. Eliminates staff bias.
     *
     * @param player Player to teleport
     */
    public void teleportToRandomPlayer(Player player) {
        List<Player> players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers())
            if (!p.getUniqueId().equals(player.getUniqueId()) && (!p.hasPermission("carbyne.staff") || !p.isOp()))
                players.add(p);

        if (players.size() == 0) {
            MessageManager.sendMessage(player, "&cThere are no available players to teleport to.");
            return;
        }

        Player target = players.get(Maths.randomNumberBetween(players.size(), 0));
        player.teleport(target);
        MessageManager.sendMessage(player, "&aYou have teleported to &5" + target.getName() + "&a.");
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

    public void vanishPlayer(Player player) {
        for (Player all : PlayerUtility.getOnlinePlayers())
            if (!all.getUniqueId().equals(player.getUniqueId()))
                if (!all.hasPermission("carbyne.staff.canseevanished"))
                    all.hidePlayer(player);

        staff.remove(player.getUniqueId());
        vanish.add(player.getUniqueId());
    }

    public void showPlayer(Player player) {
        for (Player all : PlayerUtility.getOnlinePlayers())
            if (!all.getUniqueId().equals(player.getUniqueId()))
                all.showPlayer(player);

        staff.add(player.getUniqueId());
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
        for (UUID id : frozen)
            unfreezePlayer(Bukkit.getPlayer(id));

        for (UUID id : staffModePlayers) {
            Bukkit.getPlayer(id).getInventory().clear();
            //Carbyne.getInstance().getServer().dispatchCommand(Carbyne.getInstance().getServer().getConsoleSender(), "pex user " + Bukkit.getPlayer(id).getName() + " remove mv.bypass.gamemode.*");
            PermissionUtils.setPermissions(Bukkit.getPlayer(id).addAttachment(main), falsePerms, true);
        }
    }

    private void toggleGamemode(Player player, boolean flag) {
        if (flag)
            player.setGameMode(GameMode.CREATIVE);
        else {
            player.setGameMode(GameMode.SURVIVAL);
            player.setMaxHealth(100.0);
            player.setHealth(player.getMaxHealth());
        }
    }

    public boolean isVanished(Player player) {
        return vanish.contains(player.getUniqueId());
    }

    public void logToFile(String message) {
        try {
            File saveTo = new File(Carbyne.getInstance().getDataFolder(), "playersLog.txt");

            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);

            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
