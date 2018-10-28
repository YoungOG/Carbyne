package com.medievallords.carbyne.dungeons.dungeons.instance;

import com.boydti.fawe.util.TaskManager;
import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.customevents.DungeonEnterEvent;
import com.medievallords.carbyne.dungeons.dungeons.Dungeon;
import com.medievallords.carbyne.dungeons.mechanics.*;
import com.medievallords.carbyne.dungeons.mechanics.targeters.Target;
import com.medievallords.carbyne.dungeons.player.DPlayer;
import com.medievallords.carbyne.dungeons.triggers.DistanceTrigger;
import com.medievallords.carbyne.dungeons.triggers.InteractTrigger;
import com.medievallords.carbyne.dungeons.triggers.MobTrigger;
import com.medievallords.carbyne.dungeons.triggers.Trigger;
import com.medievallords.carbyne.utils.*;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.AttributeInstance;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.PacketPlayOutCamera;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class DungeonInstance {

    private int id;
    private World world;
    private Dungeon dungeon;
    private DungeonStage stage;

    private HashMap<UUID, DPlayer> fallenSoldiers = new HashMap<>();
    private HashMap<Location, MythicMob> spawners;
    private HashMap<String, Integer> maxSpawns = new HashMap<>();

    public List<DPlayer> players = new ArrayList<>();
    private List<UUID> joiningPlayers = new ArrayList<>();
    private List<UUID> possibleJoins = new ArrayList<>();
    private List<Location> lootChests = new ArrayList<>();
    private List<Trigger> triggers = new ArrayList<>();

    public DungeonInstance(int id, World world, Dungeon dungeon, List<Player> players, List<Trigger> triggersToClone, HashMap<Location, MythicMob> spawners, List<Location> lootChests) {
        this.id = id;
        this.spawners = spawners;
        this.lootChests = lootChests;
        this.world = world;
        this.dungeon = dungeon;

        for (Player player : players) {
            DPlayer dPlayer = new DPlayer(player, this);
            this.players.add(dPlayer);
        }

        for (Trigger trigger : triggersToClone) {

            Location l = trigger.getLocation();
            Location copyLoc = new Location(world, l.getX(), l.getY(), l.getZ());
            Trigger cloned = null;
            String triggerName = "dungeonInstanceTrigger_" + trigger.getName() + "_" + id;
            if (trigger instanceof InteractTrigger) {
                cloned = new InteractTrigger(triggerName, copyLoc, ((InteractTrigger) trigger).getState());
            } else if (trigger instanceof DistanceTrigger) {
                cloned = new DistanceTrigger(triggerName, copyLoc, ((DistanceTrigger) trigger).getState(), ((DistanceTrigger) trigger).getDistance(), 100);
            } else if (trigger instanceof MobTrigger) {
                cloned = new MobTrigger(triggerName, copyLoc, ((MobTrigger) trigger).getMobType(), ((MobTrigger) trigger).getState());
            }

            List<Mechanic> mechanics = new ArrayList<>();
            Mechanic newMechanic = null;
            for (Mechanic mechanic : trigger.getMechanics()) {
                if (mechanic instanceof MechanicRedstone) {
                    newMechanic = new MechanicRedstone("redstone", Target.getTarget(mechanic.getTarget().getType()), ((MechanicRedstone) mechanic).getLocation());
                } else if (mechanic instanceof MechanicTeleport) {
                    newMechanic = new MechanicTeleport("teleport", Target.getTarget(mechanic.getTarget().getType()), ((MechanicTeleport) mechanic).getLocation());
                } else if (mechanic instanceof MechanicMessage) {
                    newMechanic = new MechanicMessage("message", Target.getTarget(mechanic.getTarget().getType()), ((MechanicMessage) mechanic).getMessage());
                } else if (mechanic instanceof MechanicSpawnEntity) {
                    newMechanic = new MechanicSpawnEntity("spawnentity", Target.getTarget(mechanic.getTarget().getType()), ((MechanicSpawnEntity) mechanic).getEntities());
                } else {
                    continue;
                }

                mechanics.add(newMechanic);
            }

            cloned.getMechanics().addAll(mechanics);

            this.triggers.add(cloned);
            //Trigger.triggers.add(cloned);
        }
    }

    public void onJoinInstance(Player player) {
        sendAll("&7" + player.getName() + "&a has joined the dungeon!");
        joiningPlayers.remove(player.getUniqueId());
        possibleJoins.remove(player.getUniqueId());
        DPlayer dPlayer = new DPlayer(player, this);
        this.players.add(dPlayer);
        Location lobby = new Location(world, dungeon.getLobbyLocation().getX(), dungeon.getLobbyLocation().getY(), dungeon.getLobbyLocation().getZ());
        player.teleport(lobby);
    }

    public void prepare() {
        stage = DungeonStage.PREPARING;
        Location lobby = new Location(world, dungeon.getLobbyLocation().getX(), dungeon.getLobbyLocation().getY(), dungeon.getLobbyLocation().getZ());

        PluginManager pm = Bukkit.getPluginManager();
        for (DPlayer player : players) {
            player.getBukkitPlayer().teleport(lobby);
            DungeonEnterEvent event = new DungeonEnterEvent(player.getBukkitPlayer(), this);
            pm.callEvent(event);
        }

        stage = DungeonStage.LOBBY;
    }

    public void initiate() {
        sendAll("&aInitiating...");
        new BukkitRunnable() {
            private int t = 5;

            public void run() {
                if (!possibleJoins.isEmpty()) {
                    cancel();
                    sendAll("&aMore players are joining the dungeon. Initiation stopped.");
                    return;
                }
                int ready = 0;

                for (DPlayer dPlayer : players) {
                    if (dPlayer.isReady()) {
                        ready++;
                    }
                }

                if (t <= 0) {
                    start();
                    cancel();
                    return;
                }

                if (ready >= players.size()) {
                    sendAll("&a" + t + "...");
                    sendAllSound(Sound.BLOCK_NOTE_PLING, 1F, 1F);
                    t--;
                } else {
                    cancel();
                    sendAll("&cInitiation stopped.");
                }
            }
        }.runTaskTimer(Carbyne.getInstance(), 0, 20);
    }

    private void start() {
        for (UUID uuid : joiningPlayers) {
            MessageManager.sendMessage(uuid, "&cThe dungeon you requested to join has started. You may not join it now.");
        }

        joiningPlayers.clear();
        stage = DungeonStage.FIGHTING;
        Location spawn = new Location(world, dungeon.getSpawnLocation().getX(), dungeon.getSpawnLocation().getY(), dungeon.getSpawnLocation().getZ());

        for (DPlayer player : players) {
            Player bukkitPlayer = player.getBukkitPlayer();
            bukkitPlayer.teleport(spawn);
            MessageManager.sendMessage(bukkitPlayer, "&a&lYou have entered the dungeon!");
        }

        spawn.getWorld().strikeLightningEffect(spawn);
        spawn.getWorld().strikeLightningEffect(spawn.clone().add(1, 0, 3));
        spawn.getWorld().strikeLightningEffect(spawn.clone().add(3, 0, 1));
    }

    public void onQuit(DPlayer dPlayer) {
        if (dPlayer.isInCombat()) {
            dPlayer.getBukkitPlayer().setHealth(0);
        }
    }

    public void onJoin(DPlayer dPlayer) {
        Location spawn = new Location(world, dungeon.getSpawnLocation().getX(), dungeon.getSpawnLocation().getY(), dungeon.getSpawnLocation().getZ());
        dPlayer.getBukkitPlayer().teleport(spawn);
    }

    public void onDeath(DPlayer dPlayer) {
        switch (dungeon.getSpawnMode()) {
            case FALLEN:
                spawnFallenSoldier(dPlayer);
                break;
            case SPECTATOR:
                onLeave(dPlayer);
                MessageManager.sendMessage(dPlayer.getBukkitPlayer(), "&cYou are out of the dungeon.");
                break;
        }
    }

    public void onRespawn(DPlayer dPlayer) {
        switch (dungeon.getSpawnMode()) {
            case RESPAWN:
                MessageManager.sendMessage(dPlayer.getBukkitPlayer(), "&cSending you to the dungeon in 3 seconds.");
                TaskManager.IMP.later(new Runnable() {
                    @Override
                    public void run() {
                        Location spawn = new Location(world, dungeon.getSpawnLocation().getX(), dungeon.getSpawnLocation().getY(), dungeon.getSpawnLocation().getZ());
                        dPlayer.getBukkitPlayer().teleport(spawn);
                    }
                }, 60);
                break;
        }
    }

    public void onLeave(DPlayer dPlayer) {
        Cooldowns.setCooldown(dPlayer.getBukkitPlayer().getUniqueId(), "dungeon:name:" + dungeon.getName(), dungeon.getCooldown());
        players.remove(dPlayer);
        if (dungeon.getCompleteLocation() != null)
            dPlayer.getBukkitPlayer().teleport(dungeon.getCompleteLocation());
        else
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + dPlayer.getBukkitPlayer().getName());

        if (players.isEmpty()) {
            stage = DungeonStage.FINISHED;
            //Trigger.triggers.removeAll(triggers);
            StaticClasses.dungeonHandler.getInstances().remove(this);

            for (int i = 0; i < world.getPlayers().size(); i++) {
                Player player = world.getPlayers().get(i);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
            }

            Bukkit.unloadWorld(world, false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    WorldLoader.deleteWorld(world.getWorldFolder());
                }
            }.runTaskAsynchronously(Carbyne.getInstance());
            Bukkit.getWorlds().remove(world);
        }
    }

    public void readyUp(DPlayer player) {
        if (!possibleJoins.isEmpty()) {
            MessageManager.sendMessage(player.getBukkitPlayer(), "&aYou need to wait until all players have joined the dungeon.");
            return;
        }

        if (player.isReady()) {
            player.setReady(false);
            MessageManager.sendMessage(player.getBukkitPlayer(), "&cYou have cancelled the initiation.");
            player.getBukkitPlayer().playSound(player.getBukkitPlayer().getLocation(), Sound.BLOCK_NOTE_BASS, 100, 2);
            return;
        }

        player.setReady(true);
        sendAllSound(Sound.BLOCK_NOTE_PLING, 1, 2, player.getBukkitPlayer().getLocation());
        int ready = 0;
        for (DPlayer dPlayer : players) {
            if (dPlayer.isReady()) {
                ready++;
            }
        }

        if (ready >= players.size()) {
            initiate();
        } else {
            int size = players.size();
            sendAll("&aYou need &d&l" + size + "&a players to start the dungeon. &c" + ready + " &7&l/&c " + size);
        }
    }

    private void spawnFallenSoldier(DPlayer dPlayer) {
        dPlayer.getBukkitPlayer().setHealth(20.0);
        PlayerHealth.getPlayerHealth(dPlayer.getBukkitPlayer().getUniqueId()).setHealth(500, dPlayer.getBukkitPlayer());
        dPlayer.getBukkitPlayer().setGameMode(GameMode.CREATIVE);
        Zombie zombie = dPlayer.getBukkitPlayer().getWorld().spawn(dPlayer.getBukkitPlayer().getLocation(), Zombie.class);
        zombie.setBaby(false);
        AttributeInstance attributes = (((CraftLivingEntity) zombie).getHandle()).getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
        if (attributes != null) {
            attributes.setValue(StaticClasses.gearManager.calculateDamage(dPlayer.getBukkitPlayer()) * 2.0);
        }

        PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftZombie) zombie).getHandle());

        ((CraftPlayer) dPlayer.getBukkitPlayer()).getHandle().playerConnection.sendPacket(camera);

        fallenSoldiers.put(zombie.getUniqueId(), dPlayer);

    }

    public void onFallenSoldierKill(UUID mobUuid, DPlayer dPlayer) {
        PacketPlayOutCamera camera = new PacketPlayOutCamera(((CraftPlayer) dPlayer.getBukkitPlayer()).getHandle());

        ((CraftPlayer) dPlayer.getBukkitPlayer()).getHandle().playerConnection.sendPacket(camera);

        fallenSoldiers.remove(mobUuid);
    }

    public void sendAll(String message) {
        for (DPlayer dPlayer : players) {
            MessageManager.sendMessage(dPlayer.getBukkitPlayer(), message);
        }
    }

    private void sendAllSound(Sound sound, float volume, float pitch, Location location) {
        for (DPlayer dPlayer : players) {
            dPlayer.getBukkitPlayer().playSound(location, sound, volume, pitch);
        }
    }

    private void sendAllSound(Sound sound, float volume, float pitch) {
        for (DPlayer dPlayer : players) {
            dPlayer.getBukkitPlayer().playSound(dPlayer.getBukkitPlayer().getLocation(), sound, volume, pitch);
        }
    }

    public DPlayer getPlayer(Player player) {
        for (DPlayer dPlayer : players) {
            if (dPlayer.getBukkitPlayer().getUniqueId().equals(player.getUniqueId())) {
                return dPlayer;
            }
        }

        return null;
    }

    public List<Trigger> getTriggers(Class clazz) {
        List<Trigger> triggersToReturn = new ArrayList<>();
        for (Trigger trigger : triggers) {
            if (trigger.getClass().getName().equalsIgnoreCase(clazz.getName())) {
                triggersToReturn.add(trigger);
            }
        }

        return triggersToReturn;
    }
}
