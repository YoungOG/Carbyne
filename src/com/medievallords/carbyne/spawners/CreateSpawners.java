package com.medievallords.carbyne.spawners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Williams on 2017-03-17.
 * for the Carbyne project.
 */

@Getter
@Setter
public class CreateSpawners {

    private static HashMap<Player, Location> pos1 = new HashMap<>();
    private static HashMap<Player, Location> pos2 = new HashMap<>();

    public static Map<Player, Location> getPos1() {
        return pos1;
    }

    public static Map<Player, Location> getPos2() {
        return pos2;
    }

    public static void createSpawners(Player player, String name, Location location1, Location location2, String mobName, int amount, Material material, String group){
        MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(mobName);
        if(mob == null){
            player.sendMessage("Could not find mob: " + mobName);
            return;
        }
        List<MythicSpawner> spawners = MythicMobs.inst().getSpawnerManager().listSpawners;
        for(MythicSpawner spawner : spawners){
            if(spawner.getName().contains(name)){
                player.sendMessage("Already a spawner: " + name);
                return;
            }
        }
        int amount2 = amount;
        List<Block> blocks = getRegionBlocks(location1.getWorld(), location1, location2, material);
        for(Block block : blocks) {
            if(amount <= 0){
                break;
            }
            MythicSpawner mythicSpawner = MythicMobs.inst().getSpawnerManager().createSpawner(name + amount, block.getLocation(), mobName);
            block.getLocation().getBlock().setType(Material.AIR);
            mythicSpawner.setGroup(group);
            amount--;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "Spawners created. &a" + amount2 + " &rof mob &5" + mobName + " &rin group &5" + group));
    }

    public static List<Block> getRegionBlocks(World world, Location loc1, Location loc2, Material material) {
        List<Block> blocks = new ArrayList<Block>();
        blocks.clear();

        for(double x = loc1.getX(); x <= loc2.getX(); x++) {
            for(double y = loc1.getY(); y <= loc2.getY(); y++) {
                for(double z = loc1.getZ(); z <= loc2.getZ(); z++) {
                    Location loc = new Location(world, x, y, z);
                    if(loc.getBlock().getType() == material) {
                        blocks.add(loc.getBlock());
                    }
                }
            }
        }
        return blocks;
    }
}
