package com.medievallords.carbyne.experimental;

import com.medievallords.carbyne.experimental.neat.Connection;
import com.medievallords.carbyne.experimental.neat.Counter;
import com.medievallords.carbyne.experimental.neat.GeneticAlgorithm;
import com.medievallords.carbyne.experimental.neat.Genome;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BotHandler implements Listener {

    private GeneticAlgorithm geneticAlgorithm;

    private CraftWorld world;
    private Location location;
    private Player player;

    private int generation = 1;

    private Map<NeuralBot, PlayerBot> bots = new HashMap<>();

    // 10 input, 8 output
    public BotHandler(CraftWorld world, Location location, Player player) {
        this.world = world;
        this.location = location;
        this.player = player;

        geneticAlgorithm = new GeneticAlgorithm() {
            @Override
            public float evaluateFitness(Genome genome) {
                return genome.getFitness();
            }
        };

        List<Genome> genomes = new ArrayList<>();
        for (int i = 0; i < GeneticAlgorithm.populationSize; i++) {
            Genome startingGenome = new Genome(new com.medievallords.carbyne.experimental.neat.Counter(), new Counter());
            List<Integer> inputNeurons = new ArrayList<>();
            for (int in = 0; in < 10; in++) {
                inputNeurons.add(geneticAlgorithm.newNeuron(startingGenome));
            }

            List<Integer> outputNeurons = new ArrayList<>();
            for (int out = 0; out < 8; out++) {
                outputNeurons.add(geneticAlgorithm.newNeuron(startingGenome));
            }

            for (int in : inputNeurons) {
                for (int out : outputNeurons) {
                    Connection connection = geneticAlgorithm.newConnection(in, out, (float) Math.random(), startingGenome);
                    startingGenome.addConnection(connection);
                }
            }

            for (int in : inputNeurons) {
                startingGenome.addNeuron(in, Genome.Type.INPUT);
            }

            for (int out : outputNeurons) {
                startingGenome.addNeuron(out, Genome.Type.OUTPUT);
            }

            genomes.add(startingGenome);

            NeuralBot bot = new NeuralBot(world, startingGenome, this);
            PlayerBot playerBot = new PlayerBot(world, bot, this);
            bot.setTarget(playerBot);

            spawnEntity(bot, player);
            spawnEntity(playerBot, player);

            bot.getBukkitEntity().teleport(location);
            playerBot.getBukkitEntity().teleport(location);

            bot.getBukkitEntity().setCustomName("NEURAL");
            bot.getBukkitEntity().setCustomNameVisible(true);

            bot.start();
            playerBot.start();

            bots.put(bot, playerBot);
        }

        geneticAlgorithm.setGenomes(genomes);
    }

    public void died(NeuralBot neuralBot) {
        neuralBot.setFitness();
        PlayerBot playerBot = neuralBot.getTarget();
        if (playerBot != null) {
            playerBot.die();
        }
        bots.remove(neuralBot);

        if (bots.isEmpty()) {
            reset();
        }
    }

    public void died(PlayerBot bot) {
        NeuralBot neuralBot = bot.getTarget();
        neuralBot.setFitness();
        bots.remove(neuralBot);
        neuralBot.die();

        if (bots.isEmpty()) {
            reset();
        }
    }

    private void reset() {
        GeneticAlgorithm.GeneticObject go = geneticAlgorithm.evolve();
        for (int i = 0; i < GeneticAlgorithm.populationSize; i++) {
            NeuralBot bot = new NeuralBot(world, go.genomes.get(i), this);
            PlayerBot playerBot = new PlayerBot(world, bot, this);
            bot.setTarget(playerBot);

            spawnEntity(bot, player);
            spawnEntity(playerBot, player);

            bot.getBukkitEntity().teleport(location);
            playerBot.getBukkitEntity().teleport(location);

            bot.getBukkitEntity().setCustomName("NEURAL");
            bot.getBukkitEntity().setCustomNameVisible(true);

            bot.start();
            playerBot.start();

            bots.put(bot, playerBot);
        }

        generation++;
        Bukkit.broadcastMessage("Generation: " + generation);
        Bukkit.broadcastMessage("Max fitness: " + geneticAlgorithm.maxFitness);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.VILLAGER) {
            return;
        }

        PlayerBot bot = getBot(event.getEntity().getUniqueId());
        if (bot != null) {
            bot.onDamage();
            return;
        }

        NeuralBot neuralBot = getNBot(event.getEntity().getUniqueId());
        if (neuralBot != null) {
            neuralBot.onDamage();
        }
    }

    public PlayerBot getBot(UUID uuid) {
        for (PlayerBot bot : bots.values()) {
            if (bot.getUniqueID().equals(uuid)) {
                return bot;
            }
        }

        return null;
    }

    public NeuralBot getNBot(UUID uuid) {
        for (NeuralBot bot : bots.keySet()) {
            if (bot.getUniqueID().equals(uuid)) {
                return bot;
            }
        }

        return null;
    }

    public NeuralBot getNeuralBot(PlayerBot bot) {
        for (NeuralBot neuralBot : bots.keySet()) {
            if (bots.get(neuralBot).equals(bot)) {
                return neuralBot;
            }
        }

        return null;
    }

    public NeuralBot spawnEntity(NeuralBot bot, Player player) {
        /*PlayerBot bot = new PlayerBot(MinecraftServer.getServer(),
                    MinecraftServer.getServer().getWorldServer(0),
                    new GameProfile(player.getUniqueId(), player.getName()),
                    new PlayerInteractManager(((CraftWorld)player.getWorld()).getHandle()),
                    player, CraftItemStack.asNMSCopy(kit.getItems()[0]),
                    difficulty,
                    this);*/

        registerEntity("NeuralBot", 49, PlayerBot.class, PlayerBot.class);

        bot.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0, 0);
        ((CraftWorld) player.getWorld()).addEntity(bot, CreatureSpawnEvent.SpawnReason.CUSTOM);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(bot.getBukkitEntity().getEntityId());
        EntityPlayer entityPlayer = new EntityPlayer(MinecraftServer.getServer(), bot.getWorld().getWorld().getHandle(),
                new GameProfile(player.getUniqueId(), "NeuralBot"),
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        try {
            Field idField = spawn.getClass().getDeclaredField("a");
            idField.setAccessible(true);
            // Access the entity-id field and change its value
            idField.set(spawn, bot.getBukkitEntity().getEntityId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //bot.entityPlayer = entityPlayer;


        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);
        return bot;
    }

    public PlayerBot spawnEntity(PlayerBot bot, Player player) {
        /*PlayerBot bot = new PlayerBot(MinecraftServer.getServer(),
                    MinecraftServer.getServer().getWorldServer(0),
                    new GameProfile(player.getUniqueId(), player.getName()),
                    new PlayerInteractManager(((CraftWorld)player.getWorld()).getHandle()),
                    player, CraftItemStack.asNMSCopy(kit.getItems()[0]),
                    difficulty,
                    this);*/

        registerEntity("PlayerBot", 49, PlayerBot.class, PlayerBot.class);

        bot.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0, 0);
        ((CraftWorld) player.getWorld()).addEntity(bot, CreatureSpawnEvent.SpawnReason.CUSTOM);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(bot.getBukkitEntity().getEntityId());
        EntityPlayer entityPlayer = new EntityPlayer(MinecraftServer.getServer(), bot.getWorld().getWorld().getHandle(),
                new GameProfile(player.getUniqueId(), "PlayerBot"),
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        try {
            Field idField = spawn.getClass().getDeclaredField("a");
            idField.setAccessible(true);
            // Access the entity-id field and change its value
            idField.set(spawn, bot.getBukkitEntity().getEntityId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //bot.entityPlayer = entityPlayer;

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);
        return bot;
    }

    public static void registerEntity(String name, int id, Class<?> nmsClass, Class<?> customClass) {
        try {
            List<Map<?, ?>> dataMaps = new ArrayList<Map<?, ?>>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMaps.get(2).containsKey(id)) {
                dataMaps.get(0).remove(name);
                dataMaps.get(2).remove(id);
            }

            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);

            for (Field f : BiomeBase.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(BiomeBase.class.getSimpleName())) {
                    if (f.get(null) != null) {
                        for (Field list : BiomeBase.class.getDeclaredFields()) {
                            if (list.getType().getSimpleName().equals(List.class.getSimpleName())) {
                                list.setAccessible(true);
                                @SuppressWarnings("unchecked")
                                List<BiomeBase.BiomeMeta> metaList = (List<BiomeBase.BiomeMeta>) list.get(f.get(null));

                                for (BiomeBase.BiomeMeta meta : metaList) {
                                    Field clazz = BiomeBase.BiomeMeta.class.getDeclaredFields()[0];
                                    if (clazz.get(meta).equals(nmsClass)) clazz.set(meta, customClass);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
