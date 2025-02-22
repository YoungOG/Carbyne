package com.medievallords.carbyne.listeners;


import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.scoreboard.Board;
import com.medievallords.carbyne.utils.scoreboard.BoardCooldown;
import com.medievallords.carbyne.utils.scoreboard.BoardFormat;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class CooldownListeners implements Listener {

    public static double POTION_COOLDOWN = 15.0d;

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getType() != EntityType.SPLASH_POTION) {
            return;
        }

        if (event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Board board = Board.getByPlayer(((Player) event.getEntity().getShooter()).getUniqueId());
        if (board != null) {
            BoardCooldown potionCooldown = board.getCooldown("potion");
            if (potionCooldown == null) {
                new BoardCooldown(board, "potion", POTION_COOLDOWN);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPearl(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem()) {
            if (e.getItem().getType() == Material.ENDER_PEARL) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Board board = Board.getByPlayer(p.getUniqueId());

                    if (board != null) {
                        BoardCooldown enderpearlCooldown = board.getCooldown("enderpearl");

                        if (enderpearlCooldown != null) {
                            e.setCancelled(true);
                            p.updateInventory();
                            MessageManager.sendMessage(p, "&eYou cannot throw another Enderpearl for &6" + enderpearlCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds!");
                        } else
                            new BoardCooldown(board, "enderpearl", 60.0d);
                    }
                }
            } else if (e.getItem().getType() == Material.POTION) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Board board = Board.getByPlayer(p.getUniqueId());

                    if (board != null) {
                        BoardCooldown potionCooldown = board.getCooldown("potion");

                        if (potionCooldown != null) {
                            e.setCancelled(true);
                            p.updateInventory();
                            MessageManager.sendMessage(p, "&eYou cannot " + (e.getItem().getDurability() > 16385 ? "throw" : "drink") + " another Potion for &6" + potionCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (e.getItem() != null) {
            if (e.getItem().getType() == Material.GOLDEN_APPLE) {
                e.setCancelled(true);
                MessageManager.sendMessage(p, "&cGolden Apples are disabled.");
               /* Board board = Board.getByPlayer(p);

                if (board != null) {
                    if (e.getItem().getDurability() < 1) {
                        BoardCooldown godappleCooldown = board.getCooldown("goldenapple");

                        if (godappleCooldown != null) {
                            e.setCancelled(true);
                            p.updateInventory();
                            MessageManager.sendMessage(p, "&eYou cannot eat another Golden Apple for &6" + godappleCooldown.getFormattedString(BoardFormat.SECONDS) + " &eseconds!");
                        } else
                            new BoardCooldown(board, "goldenapple", 300.0D);
                    } else {
                        BoardCooldown godappleCooldown = board.getCooldown("godapple");

                        if (godappleCooldown != null) {
                            e.setCancelled(true);
                            p.updateInventory();
                            MessageManager.sendMessage(p, "&eYou cannot eat another God Apple for &6" + godappleCooldown.getFormattedString(BoardFormat.MINUTES) + " &eminutes!");
                        } else
                            new BoardCooldown(board, "godapple", 900.0D);
                    }
                }*/
            } else if (e.getItem().getType() == Material.POTION) {
                Board board = Board.getByPlayer(p.getUniqueId());

                if (board != null) {
                    BoardCooldown potionCooldown = board.getCooldown("potion");

                    if (potionCooldown == null)
                        new BoardCooldown(board, "potion", POTION_COOLDOWN);
                }
            }
        }
    }
}
