package com.medievallords.carbyne.missions.listeners;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.ProfileCreatedEvent;
import com.medievallords.carbyne.missions.MissionsManager;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.PlayerMissionData;
import com.medievallords.carbyne.missions.object.interfaces.*;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dalton on 8/9/2017.
 */
public class MissionListeners implements Listener {

    private MissionsManager missionsManager = Carbyne.getInstance().getMissionsManager();

    @EventHandler
    public void onNewPlayerJoin(ProfileCreatedEvent e) {
        missionsManager.assignNoobMissions(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            public void run() {
                missionsManager.assignRandomMissionsIfAbsent(e.getPlayer().getUniqueId());
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null) {
            String invName = e.getClickedInventory().getName();
            if (invName != null) {
                String[] parse = invName.split(" ");
                if (parse.length == 2 && missionsManager.isValid(parse[1])) {
                    e.setCancelled(true);
                    if (e.getWhoClicked() instanceof Player) {
                        Player player = (Player) e.getWhoClicked();
                        PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                        Mission clickedMission = playerMissionData.getMissionFromSlot(e.getRawSlot());
                        if (clickedMission == null) return;
                        clickedMission.clickMission(player, e.getInventory(), e.getRawSlot());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        final Player dead = e.getEntity();
        new BukkitRunnable() {
            public void run() {
                PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(killer.getUniqueId());
                Mission[] missions = playerMissionData.getCurrentMissions();
                for (Mission mission : missions) {
                    if (!mission.isActive()) continue;
                    if (mission instanceof KillPlayerMission) {
                        if (!(((KillPlayerMission) mission).getKilledPlayers().contains(dead))) {
                            mission.incrementObjectiveCount(killer.getUniqueId(), 1);
                            ((KillPlayerMission) mission).getKilledPlayers().add(dead);
                            return;
                        }
                    }
                    Mission daily = playerMissionData.getDailyChallenge();
                    if (!daily.isActive()) return;
                    if (mission instanceof KillPlayerMission) {
                        if (!(((KillPlayerMission) daily).getKilledPlayers().contains(dead))) {
                            daily.incrementObjectiveCount(killer.getUniqueId(), 1);
                            ((KillPlayerMission) daily).getKilledPlayers().add(dead);
                            return;
                        }
                    }
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        final MaterialData typeBroken = new MaterialData(e.getBlock().getType(), e.getBlock().getData());
        final Player player = e.getPlayer();
        new BukkitRunnable() {
            public void run() {
                PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId()); // Should never be null
                Mission[] missions = playerMissionData.getCurrentMissions();
                for (Mission mission : missions) {
                    if (!mission.isActive()) continue;
                    if (mission instanceof BlockBreakingMission) {
                        if (((BlockBreakingMission) mission).getGoalMaterials().length == 0 || Arrays.asList(((BlockBreakingMission) mission).getGoalMaterials()).contains(typeBroken)) {
                            mission.incrementObjectiveCount(player.getUniqueId(), 1);
                            return;
                        }
                    }
                }
                Mission daily = playerMissionData.getDailyChallenge();
                if (!daily.isActive()) return;
                if (daily instanceof BlockBreakingMission) {
                    if (((BlockBreakingMission) daily).getGoalMaterials().length == 0 || Arrays.asList(((BlockBreakingMission) daily).getGoalMaterials()).contains(typeBroken)) {
                        daily.incrementObjectiveCount(player.getUniqueId(), 1);
                    }
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void onFishPull(PlayerFishEvent e) {
        if (e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            if (e.getCaught() instanceof Item) {
                final Player player = e.getPlayer();
                final Item item = (Item) e.getCaught();
                new BukkitRunnable() {
                    public void run() {
                        PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                        final ItemStack itemStack = item.getItemStack();
                        Mission[] missions = playerMissionData.getCurrentMissions();
                        for (Mission mission : missions) {
                            if (!mission.isActive())
                                continue;
                            if (mission instanceof FishingMission) {
                                if (((FishingMission) mission).getRequiredFish().length == 0 || Arrays.asList(((FishingMission) mission).getRequiredFish()).contains(itemStack.getData())) {
                                    mission.incrementObjectiveCount(player.getUniqueId(), 1);
                                    return;
                                }
                            }
                        }
                        Mission daily = playerMissionData.getDailyChallenge();
                        if (!daily.isActive()) return;
                        if (daily instanceof FishingMission) {
                            if (((FishingMission) daily).getRequiredFish().length == 0 || Arrays.asList(((FishingMission) daily).getRequiredFish()).contains(itemStack.getData())) {
                                daily.incrementObjectiveCount(player.getUniqueId(), 1);
                            }
                        }
                    }
                }.runTaskAsynchronously(Carbyne.getInstance());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        final MaterialData placed = new MaterialData(e.getBlockPlaced().getType(), e.getBlockPlaced().getData());
        final Player player = e.getPlayer();
        new BukkitRunnable() {
            public void run() {
                PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                Mission[] missions = playerMissionData.getCurrentMissions();
                for (Mission mission : missions) {
                    if (!mission.isActive())
                        continue;
                    if (mission instanceof BlockPlacingMission) {
                        if (((BlockPlacingMission) mission).getGoalBlocks().length == 0 || Arrays.asList(((BlockPlacingMission) mission).getGoalBlocks()).contains(placed)) {
                            mission.incrementObjectiveCount(player.getUniqueId(), 1);
                            return;
                        }
                    }
                }
                Mission daily = playerMissionData.getDailyChallenge();
                if (!daily.isActive()) return;
                if (daily instanceof BlockPlacingMission) {
                    if (((BlockPlacingMission) daily).getGoalBlocks().length == 0 || Arrays.asList(((BlockPlacingMission) daily).getGoalBlocks()).contains(placed)) {
                        daily.incrementObjectiveCount(player.getUniqueId(), 1);
                    }
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void residentJoinTown(TownAddResidentEvent e) {
        final Resident resident = e.getResident();
        new BukkitRunnable() {
            public void run() {
                Player player = Bukkit.getPlayer(resident.getName());
                if (player == null) return;
                PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                Mission[] missions = playerMissionData.getCurrentMissions();
                for (Mission mission : missions) {
                    if (!mission.isActive())
                        continue;

                    if (mission instanceof JoinTownMission)
                        mission.incrementObjectiveCount(player.getUniqueId(), 1);
                }
                Mission daily = playerMissionData.getDailyChallenge();
                if (!daily.isActive()) return;
                if (daily instanceof JoinTownMission) {
                    daily.incrementObjectiveCount(player.getUniqueId(), 1);
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void onNewTown(NewTownEvent e) {
        final Resident resident = e.getTown().getMayor();

        new BukkitRunnable() {
            public void run() {
                Player player = Bukkit.getPlayer(resident.getName());
                if (player == null) return;
                PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                Mission[] missions = playerMissionData.getCurrentMissions();
                for (Mission mission : missions) {
                    if (!mission.isActive())
                        continue;

                    if (mission instanceof JoinTownMission)
                        mission.incrementObjectiveCount(player.getUniqueId(), 1);
                }
                Mission daily = playerMissionData.getDailyChallenge();
                if (!daily.isActive()) return;
                if (daily instanceof JoinTownMission) {
                    daily.incrementObjectiveCount(player.getUniqueId(), 1);
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void playerItemPickupEvent(PlayerPickupItemEvent e) {
        final ItemStack is = e.getItem().getItemStack();
        final Player player = e.getPlayer();
        new BukkitRunnable() {
            public void run() {
                PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(player.getUniqueId());
                Mission[] missions = playerMissionData.getCurrentMissions();
                for (int i = 0; i < missions.length; i++) {
                    Mission mission = missions[i];
                    if (!mission.isActive()) continue;
                    if (mission instanceof PlayerItemPickupMission) {
                        List<String> solutionNames = (((PlayerItemPickupMission) mission).getItemsForPickup());
                        for (String name : solutionNames) {
                            if (MissionsManager.lootItems.containsKey(name.toLowerCase())) {
                                if (ItemBuilder.areItemsEqual(MissionsManager.lootItems.get(name.toLowerCase()), is)) {
                                    mission.incrementObjectiveCount(player.getUniqueId(), 1);
                                    new BukkitRunnable() {
                                        public void run() {
                                            is.setType(Material.AIR);
                                        }
                                    }.runTask(Carbyne.getInstance());
                                    return;
                                }
                            }
                        }
                    }
                }
                Mission daily = playerMissionData.getDailyChallenge();
                if (daily instanceof PlayerItemPickupMission) {
                    List<String> solutionNames = ((PlayerItemPickupMission) daily).getItemsForPickup();
                    for (String name : solutionNames) {
                        if (MissionsManager.lootItems.containsKey(name.toLowerCase())) {
                            if (ItemBuilder.areItemsEqual(MissionsManager.lootItems.get(name.toLowerCase()), is)) {
                                daily.incrementObjectiveCount(player.getUniqueId(), 1);
                                new BukkitRunnable() {
                                    public void run() {
                                        is.setType(Material.AIR);
                                    }
                                }.runTask(Carbyne.getInstance());
                                return;
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(Carbyne.getInstance());
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        final String mobName = e.getMobType().getInternalName();
        if (e.getKiller() instanceof Player) {
            final Player killer = (Player) e.getKiller();
            new BukkitRunnable() {
                public void run() {
                    PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(killer.getUniqueId());
                    Mission[] missions = playerMissionData.getCurrentMissions();
                    for (int i = 0; i < missions.length; i++) {
                        Mission mission = missions[i];
                        if (mission instanceof BossHuntMission) {
                            if (((BossHuntMission) mission).getBossNames().contains(mobName)) {
                                mission.incrementObjectiveCount(killer.getUniqueId(), 1);
                                return;
                            }
                        }
                    }
                    Mission daily = playerMissionData.getDailyChallenge();
                    if (!daily.isActive()) return;
                    if (daily instanceof BossHuntMission) {
                        if (((BossHuntMission) daily).getBossNames().contains(mobName)) {
                            daily.incrementObjectiveCount(killer.getUniqueId(), 1);
                        }
                    }
                }
            }.runTaskAsynchronously(Carbyne.getInstance());
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            final Player killer = e.getEntity().getKiller();
            final EntityType type = e.getEntity().getType();
            new BukkitRunnable() {
                public void run() {
                    PlayerMissionData playerMissionData = missionsManager.getUuidMissions().get(killer.getUniqueId());
                    Mission[] missions = playerMissionData.getCurrentMissions();
                    for (int i = 0; i < missions.length; i++) {
                        Mission mission = missions[i];
                        if (mission instanceof KillEntityMission) {
                            if (((KillEntityMission) mission).getEntityTypes().contains(type)) {
                                mission.incrementObjectiveCount(killer.getUniqueId(), 1);
                                return;
                            }
                        }
                    }
                    Mission daily = playerMissionData.getDailyChallenge();
                    if (!daily.isActive()) return;
                    if (daily instanceof KillEntityMission) {
                        if (((KillEntityMission) daily).getEntityTypes().contains(type)) {
                            daily.incrementObjectiveCount(killer.getUniqueId(), 1);
                            return;
                        }
                    }
                }
            }.runTaskAsynchronously(Carbyne.getInstance());
        }
    }

}
