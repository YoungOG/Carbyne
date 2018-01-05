package com.medievallords.carbyne.tutorial;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.ProfileCreatedEvent;
import com.medievallords.carbyne.tutorial.tasks.BoatExplodeTask;
import com.medievallords.carbyne.tutorial.tasks.CollapseTask;
import com.medievallords.carbyne.tutorial.tasks.RockFallTask;
import com.medievallords.carbyne.utils.EntityHider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager implements Listener {

    private List<Player> doingTutorial = new ArrayList<>();
    private EntityHider entityHider = Carbyne.getInstance().getEntityHider();

    public static List<RockFallTask> rockFallTasks = new ArrayList<>();
    public static List<CollapseTask> collapseTasks = new ArrayList<>();

    public void hideEntity(Entity entity, Player ignored) {
        for (int i = 0; i < doingTutorial.size(); i++) {
            Player player = doingTutorial.get(i);
            if (!player.getUniqueId().equals(ignored.getUniqueId())) {
                entityHider.hideEntity(player, entity);
            }
        }
    }

    @EventHandler
    public void onProfileCreation(ProfileCreatedEvent event) {
        startTutorial(event.getPlayer());
    }

    public void startTutorial(Player player) {
        doingTutorial.add(player);
        BoatExplodeTask task = new BoatExplodeTask(player);
        task.runTaskLaterAsynchronously(Carbyne.getInstance(), 49);
    }

    private Location checkFor = new Location(Bukkit.getWorld("world"), -562, 63, -1566);

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        for (int i = 0; i < rockFallTasks.size(); i++) {
            RockFallTask task = rockFallTasks.get(i);
            if (task.getEntities().contains(event.getEntity())) {
                event.setCancelled(true);
                task.spawnRock(event.getBlock().getLocation());
            }
        }
        for (int i = 0; i < collapseTasks.size(); i++) {
            CollapseTask task = collapseTasks.get(i);
            if (task.getEntities().contains(event.getEntity())) {
                event.setCancelled(true);
                task.spawnRock(event.getBlock().getLocation());
            }
        }
    }
}
