package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.PlayerHealth;
import com.medievallords.carbyne.utils.StaticClasses;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.medievallords.carbyne.utils.scoreboard.Board;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Calvin on 3/24/2017
 * for the Carbyne project.
 */
public class ToggleCommand extends BaseCommand {

    @Command(name = "toggle", inGameOnly = true)
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());

        if (args.length < 1 || args.length > 2) {
            MessageManager.sendMessage(player, "&cUsage: /toggle <effects/hud/tab/skills/chat/announcements> [on/off]");
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("effects")) {
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cCould not load your profiles. Please contact an administrator.");
                    return;
                }

                if (!profile.hasEffectsToggled()) {
                    profile.setShowEffects(true);
                    MessageManager.sendMessage(player, "&aYour particle effects have been enabled.");
                } else {
                    profile.setShowEffects(false);
                    MessageManager.sendMessage(player, "&aYour particle effects have been disabled.");
                }
            } else if (args[0].equalsIgnoreCase("hud") || args[0].equalsIgnoreCase("scoreboard")) {
                if (Board.getByPlayer(player.getUniqueId()) != null) {
                    Board.getBoards().remove(Board.getByPlayer(player.getUniqueId()));
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    MessageManager.sendMessage(player, "&aYour scoreboard HUD have been disabled.");
                } else {
                    new Board(player, Carbyne.getInstance().getCarbyneBoardAdapter());
                    MessageManager.sendMessage(player, "&aYour scoreboard HUD have been enabled.");
                }
            } else if (args[0].equalsIgnoreCase("skills")) {
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player.getUniqueId());
                if (playerHealth.isSkillsToggled()) {
                    profile.setSkillsToggled(false);
                    playerHealth.setSkillsToggled(false);
                    player.setAllowFlight(false);
                    MessageManager.sendMessage(player, "&cSkills have been disabled.");
                } else {
                    profile.setSkillsToggled(true);
                    playerHealth.setSkillsToggled(true);
                    MessageManager.sendMessage(player, "&aSkills have been enabled.");
                }
            } else if (args[0].equalsIgnoreCase("chat")) {
                if (profile.isChatEnabled()) {
                    profile.setChatEnabled(false);
                    MessageManager.sendMessage(player, "&cYou have disabled chat messages.");
                } else {
                    profile.setChatEnabled(true);
                    MessageManager.sendMessage(player, "&aYou have enabled chat messages.");
                }
            } else if (args[0].equalsIgnoreCase("announcements")) {
                if (profile.isAnnouncementsEnabled()) {
                    profile.setAnnouncementsEnabled(false);
                    MessageManager.sendMessage(player, "&cYou have disabled announcement messages.");
                } else {
                    profile.setAnnouncementsEnabled(true);
                    MessageManager.sendMessage(player, "&aYou have enabled announcement messages.");
                }
            } else if (args[0].equalsIgnoreCase("tab")) {
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cCould not load your profiles. Please contact an administrator.");
                    return;
                }

                if (!profile.isShowTab()) {
                    profile.setShowTab(true);
                    MessageManager.sendMessage(player, "&aCustom tablist has been enabled. Relog to apply changes.");
                } else {
                    profile.setShowTab(false);
                    MessageManager.sendMessage(player, "&aCustom tablist has been disabled. Relog to apply changes.");
                }
            } else if (args[0].equalsIgnoreCase("vanisheffect")) {
                if (!profile.isVanishEffect()) {
                    profile.setVanishEffect(true);
                    MessageManager.sendMessage(player, "&aYou are now using the vanish teleportation effect.");
                } else {
                    profile.setVanishEffect(false);
                    MessageManager.sendMessage(player, "&aYou are no longer using the vanish teleportation effect.");
                }
            } else {
                MessageManager.sendMessage(player, "&cUsage: /toggle <effects/hud/tab/skills/chat/announcements> [on/off]");
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("effects")) {
                if (profile == null) {
                    MessageManager.sendMessage(player, "&cCould not load your profiles. Please contact an administrator.");
                    return;
                }

                if (args[1].equalsIgnoreCase("on")) {
                    profile.setShowEffects(true);

                    MessageManager.sendMessage(player, "&aYour particle effects have been enabled.");
                } else if (args[1].equalsIgnoreCase("off")) {
                    profile.setShowEffects(false);

                    MessageManager.sendMessage(player, "&aYour particle effects have been disabled.");
                } else {
                    MessageManager.sendMessage(player, "&cUsage: /toggle <effects> [on/off]");
                }
            } else if (args[0].equalsIgnoreCase("hud") || args[0].equalsIgnoreCase("scoreboard")) {
                if (args[1].equalsIgnoreCase("on")) {
                    if (Board.getByPlayer(player.getUniqueId()) == null) {
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                        MessageManager.sendMessage(player, "&aYour scoreboard HUD have been enabled.");
                    }
                } else if (args[1].equalsIgnoreCase("off")) {
                    if (Board.getByPlayer(player.getUniqueId()) != null) {
                        Board.getBoards().remove(Board.getByPlayer(player.getUniqueId()));
                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                        MessageManager.sendMessage(player, "&aYour scoreboard HUD have been disabled.");
                    }
                } else {
                    MessageManager.sendMessage(player, "&cUsage: /toggle <effects/hud> [on/off]");
                }
            } else {
                MessageManager.sendMessage(player, "&cUsage: /toggle <effects/hud/tab/skills/chat/announcements> [on/off]");
            }
        }
    }
}
