package com.medievallords.carbyne.experimental;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.mechanics.TestDragon;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import net.minecraft.server.v1_8_R3.DifficultyDamageScaler;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TempCommandNeat extends BaseCommand {

    private List<TestDragon> dragons = new ArrayList<>();

    public TempCommandNeat() {
        super();
        //registerEntity("TestDragon", 49,EntityEnderDragon.class, TestDragon.class);
        addToMaps(TestDragon.class, "TestDragon", 54);
    }

    @Command(name = "neat", inGameOnly = true, permission = "admin")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Bukkit.getPluginManager().registerEvents(new BotHandler((CraftWorld) player.getWorld(), player.getLocation(), player), Carbyne.getInstance());
    }

    @Command(name = "killallD", permission = "admin")
    public void onCo(CommandArgs commandArgs) {
        int i = dragons.size();
        for (TestDragon d : dragons) {
            d.die();
        }

        dragons.clear();
        Bukkit.broadcastMessage("A: " + i);
    }

    @Command(name = "spawnD", inGameOnly = true, permission = "admin")
    public void onC(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        TestDragon testDragon = new TestDragon(((CraftWorld) player.getWorld()).getHandle());
        spawnEntity(testDragon, player);
    }

    public void spawnEntity(TestDragon testDragon, Player player) {
        /*PlayerBot bot = new PlayerBot(MinecraftServer.getServer(),
                    MinecraftServer.getServer().getWorldServer(0),
                    new GameProfile(player.getUniqueId(), player.getName()),
                    new PlayerInteractManager(((CraftWorld)player.getWorld()).getHandle()),
                    player, CraftItemStack.asNMSCopy(kit.getItems()[0]),
                    difficulty,
                    this);*/

        DifficultyDamageScaler s = new DifficultyDamageScaler(EnumDifficulty.HARD, 1, 1, 1);
        testDragon.prepare(s, null);

        testDragon.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 0, 0);
        ((CraftWorld) player.getWorld()).addEntity(testDragon, CreatureSpawnEvent.SpawnReason.CUSTOM);
        dragons.add(testDragon);
    }

    private static void addToMaps(Class clazz, String name, int id) {
        //getPrivateField is the method from above.
        //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
        ((Map) getPrivateField("c", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(name, clazz);
        ((Map) getPrivateField("d", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, name);
        //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
        ((Map) getPrivateField("f", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
        //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);

            o = field.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }
}

