package com.medievallords.carbyne.missions;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.lootchests.LootChestManager;
import com.medievallords.carbyne.missions.enums.Difficulty;
import com.medievallords.carbyne.missions.object.Mission;
import com.medievallords.carbyne.missions.object.MissionData;
import com.medievallords.carbyne.missions.object.PlayerMissionData;
import com.medievallords.carbyne.missions.object.implementations.noobmissions.AquireWealthMission;
import com.medievallords.carbyne.missions.object.implementations.noobmissions.JoinATownMission;
import com.medievallords.carbyne.missions.object.interfaces.BossHuntMission;
import com.medievallords.carbyne.utils.DateUtil;
import com.medievallords.carbyne.utils.InventoryUpdater;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.Maths;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Dalton on 8/8/2017.
 */
public class MissionsManager {

    private static String inventoryTitle = ChatColor.translateAlternateColorCodes('&', "&b&lMissions ");
    public static String timeUntilNewMissions;
    public static Map<String, ItemStack> lootItems = new HashMap<>();
    public static Map<String, Double> chance = new HashMap<>();

    @Getter
    private final List<MissionData> missionData;

    @Getter
    private final Map<UUID, PlayerMissionData> uuidMissions;
    @Getter
    private long actualTimeUntilNewMissions = -1;

    private LootChestManager lootChestManager = Carbyne.getInstance().getLootChestManager();

    public MissionsManager() {
        missionData = new ArrayList<>();
        uuidMissions = new HashMap<>();
        loadMissions();
        try {
            actualTimeUntilNewMissions = DateUtil.parseDateDiff("1d", true);
        } catch (Exception e) {
        }
        timeUntilNewMissions = getTimeLeft();

        new BukkitRunnable() {
            public void run() {
                isItANewDay();
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);
    }

    private void loadMissions() {
        FileConfiguration fc = Carbyne.getInstance().getMissionFileConfiguration();

        for (String itemName : fc.getConfigurationSection("Items").getKeys(false)) {
            String materialName;
            String displayName;
            List<String> lore;
            List<String> enchants;
            int data;
            int amount;
            double chanceToSpawn;

            Material material;

            boolean raw = false;
            if ((materialName = fc.getString("Items." + itemName + ".Material")) == null) continue;
            if ((material = Material.valueOf(materialName)) == null) continue;

            if (material == Material.NETHER_STAR) {
                if ((amount = fc.getInt("Items." + itemName + ".Amount")) == -1) amount = 1;
                if ((chanceToSpawn = fc.getDouble("Items." + itemName + ".ChanceToSpawn")) == -1) chanceToSpawn = 0.01;
                ItemStack is = Carbyne.getInstance().getGearManager().getTokenItem();
                is.setAmount(amount);
                lootItems.put(itemName.toLowerCase(), is);
                chance.put(itemName.toLowerCase(), chanceToSpawn);
                continue;
            }

            if ((displayName = fc.getString("Items." + itemName + ".DisplayName")) == null) raw = true;
            lore = fc.getStringList("Items." + itemName + ".Lore");
            if ((enchants = fc.getStringList("Items." + itemName + ".Enchantments")) == null)
                enchants = new ArrayList<String>();
            if ((data = fc.getInt("Items." + itemName + ".Data")) == -1) data = 0;
            if ((amount = fc.getInt("Items." + itemName + ".Amount")) == -1) amount = 1;
            if ((chanceToSpawn = fc.getDouble("Items." + itemName + ".ChanceToSpawn")) == -1) chanceToSpawn = 0.01;
            HashMap<Enchantment, Integer> enchantments = new HashMap<>();
            for (String enchantString : enchants) {
                String[] split = enchantString.split(",");
                Enchantment e = Enchantment.getByName(split[0].toUpperCase());
                Integer i = new Integer(split[1]);
                enchantments.put(e, i);
            }

            if (raw) {
                ItemStack is = new ItemStack(material);
                is.setAmount(amount);
                lootItems.put(itemName.toLowerCase(), is);
                chance.put(itemName.toLowerCase(), chanceToSpawn);
            } else {
                lootItems.put(itemName.toLowerCase(), new ItemBuilder(material).name(displayName).setLore(lore).durability(data).amount(amount).addEnchantments(enchantments).build());
                chance.put(itemName.toLowerCase(), chanceToSpawn);
            }
        }

        for (String numeral : fc.getConfigurationSection("Missions").getKeys(false)) {
            String missionType;
            String missionName, timeLimit;
            String[] missionDescription;
            int objectiveGoal;
            double reward;

            if ((missionType = fc.getString("Missions." + numeral + ".MissionType")) == null) continue;
            if ((missionName = fc.getString("Missions." + numeral + ".Name")) == null) continue;
            if ((timeLimit = fc.getString("Missions." + numeral + ".TimeLimit")) == null) continue;
            if ((missionDescription = fc.getStringList("Missions." + numeral + ".Description").toArray(new String[0])).length == 0)
                continue;
            if ((objectiveGoal = fc.getInt("Missions." + numeral + ".ObjectiveGoal")) == -1) continue;
            if ((reward = fc.getDouble("Missions." + numeral + ".CashReward")) == -1) continue;

            List<String> lootData;
            lootData = fc.getStringList("Missions." + numeral + ".LootTable");
            for (int i = 0; i < lootData.size(); i++)
                lootData.set(i, lootData.get(i).toLowerCase());

            Class clazz = MissionData.getMissionType(missionType);

            if (MissionData.materialDataClass.contains(clazz)) {
                try {
                    List<String> objectiveRawData = fc.getStringList("Missions." + numeral + ".ObjectiveData");
                    MaterialData[] materialData = new MaterialData[objectiveRawData.size()];
                    for (int i = 0; i < objectiveRawData.size(); i++) {
                        String[] parse = objectiveRawData.get(i).split(":");
                        materialData[i] = new MaterialData(Material.valueOf(parse[0].toUpperCase()), new Integer(parse[1]).byteValue());
                    }
                    missionData.add(new MissionData(clazz, missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, materialData));
                } catch (Exception e) {
                    Carbyne.getInstance().getLogger().log(Level.WARNING, "Failed to load ObjectiveData for Mission " + missionName + "!");
                }
            } else if (MissionData.itemStackClass.contains(clazz)) {
                List<String> solutionData = fc.getStringList("Missions." + numeral + ".SolutionLootTable");

                missionData.add(new MissionData(clazz, missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, solutionData));
            } else if (MissionData.bossClasses.contains(clazz)) {
                List<String> bossNames = fc.getStringList("Missions." + numeral + ".BossNames");

                missionData.add(new MissionData(clazz, missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, bossNames));
            } else if (MissionData.entityDataClass.contains(clazz)) {
                List<String> types = fc.getStringList("Missions." + numeral + ".EntityTypes");

                missionData.add(new MissionData(clazz, missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData, types));
            } else {
                missionData.add(new MissionData(clazz, missionName, missionDescription, timeLimit, objectiveGoal, reward, lootData));
            }

        }
    }

    private void isItANewDay() {
        if (actualTimeUntilNewMissions <= System.currentTimeMillis()) {
            assignRandomMissionsToAll();
            try {
                actualTimeUntilNewMissions = DateUtil.parseDateDiff("1d", true);
            } catch (Exception e) {
            }
        } else
            timeUntilNewMissions = getTimeLeft();

    }

    private void assignRandomMissionsToAll() {
        for (UUID key : uuidMissions.keySet())
            uuidMissions.replace(key, new PlayerMissionData(generateMissionArray()));
    }

    public void assignRandomMissionsIfAbsent(final UUID uuid) {
        if (!uuidMissions.containsKey(uuid)) {
            uuidMissions.put(uuid, new PlayerMissionData(generateMissionArray()));
        }
    }

    public void assignRandomMissions(final UUID uuid) {
        uuidMissions.put(uuid, new PlayerMissionData(generateMissionArray()));
    }

    public void assignNoobMissions(final UUID uuid) {
        Mission[] empty = new Mission[3];
        empty[0] = new JoinATownMission();
        empty[1] = new AquireWealthMission();
        empty[2] = new JoinATownMission();
        uuidMissions.put(uuid, new PlayerMissionData(empty));
    }

    private Mission[] generateMissionArray() {
        Mission[] missions = new Mission[3];
        for (int i = 0; i < 3; i++) {
            Mission mission = chooseRandomMission();
            if (containsMission(missions, mission)) {
                i--;
                continue;
            }
            missions[i] = mission;
        }
        return missions;
    }

    private boolean containsMission(Mission[] missions, Mission mission) {
        for (int i = 0; i < missions.length; i++)
            if (missions[i] != null && missions[i].getClass() != null && missions[i].getClass().equals(mission.getClass()))
                return true;
        return false;
    }

    public Mission chooseRandomMission() {
        try {
            return missionData.get(Maths.randomNumberBetween(missionData.size(), 0)).instantiate();
        } catch (Exception e) {
            Carbyne.getInstance().getLogger().log(Level.SEVERE, "Failed to instaniate a mission: " + e.getMessage());
        }
        return chooseRandomMission();
    }

    public Mission chooseRandomMission(Difficulty difficulty) {
        try {
            final MissionData data = missionData.get(Maths.randomNumberBetween(missionData.size(), 0));
            Mission mission = data.instantiate();
            if (mission instanceof BossHuntMission)
                mission.changeDifficulty(data, Difficulty.BOSS);
            else
                mission.changeDifficulty(data, difficulty);
            return mission;
        } catch (Exception e) {
            Carbyne.getInstance().getLogger().log(Level.SEVERE, "Failed to instaniate a mission: " + e.getMessage());
        }
        return chooseRandomMission(difficulty);
    }

    public void showPlayerMissionInventory(final Player player) {
        PlayerMissionData playerMissionData = uuidMissions.get(player.getUniqueId());
        if (playerMissionData == null || playerMissionData.getCurrentMissions() == null || playerMissionData.getCurrentMissions()[0] == null)
            uuidMissions.put(player.getUniqueId(), new PlayerMissionData(generateMissionArray()));
        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, inventoryTitle + timeUntilNewMissions);

        for (int i = 0; i < inv.getSize(); i++) {
            switch (i) {
                case 10:
                    inv.setItem(i, playerMissionData.getDailyChallenge().getMissionTicket());
                    break;
                case 12:
                    inv.setItem(i, playerMissionData.getCurrentMissions()[0].getMissionTicket());
                    break;
                case 13:
                    inv.setItem(i, playerMissionData.getCurrentMissions()[1].getMissionTicket());
                    break;
                case 14:
                    inv.setItem(i, playerMissionData.getCurrentMissions()[2].getMissionTicket());
                    break;
                default:
                    inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).build());
                    break;
            }
        }

        player.openInventory(inv);
        new BukkitRunnable() {
            public void run() {
                if (inv == null || inv.getViewers().size() == 0)
                    cancel();
                else
                    for (HumanEntity humanEntity : inv.getViewers())
                        if (humanEntity instanceof Player)
                            InventoryUpdater.updateChestInventoryTitle((Player) humanEntity, inventoryTitle + timeUntilNewMissions);
                new BukkitRunnable() {
                    public void run() {
                        for (int i = 12; i < 15; i++) {
                            if (inv == null) break;
                            switch (i) {
                                case 10:
                                    inv.setItem(i, playerMissionData.getDailyChallenge().getMissionTicket());
                                    break;
                                case 12:
                                    inv.setItem(i, playerMissionData.getCurrentMissions()[0].getMissionTicket());
                                    break;
                                case 13:
                                    inv.setItem(i, playerMissionData.getCurrentMissions()[1].getMissionTicket());
                                    break;
                                case 14:
                                    inv.setItem(i, playerMissionData.getCurrentMissions()[2].getMissionTicket());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }.runTask(Carbyne.getInstance());
            }
        }.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);
    }

    private String getTimeLeft() {
        long diff = actualTimeUntilNewMissions - System.currentTimeMillis();
        String hoursLeft = String.valueOf((int) (diff / (60 * 60 * 1000)));
        String minutesLeft = String.valueOf((int) ((diff % (60 * 60 * 1000)) / (60 * 1000)));
        String secondsLeft = String.valueOf((int) ((diff % (60 * 1000)) / (1000)));
        if (hoursLeft.length() == 1) hoursLeft = 0 + hoursLeft;
        if (secondsLeft.length() == 1) secondsLeft = 0 + secondsLeft;
        if (minutesLeft.length() == 1) minutesLeft = 0 + minutesLeft;
        return new String(hoursLeft + ":" + minutesLeft + ":" + secondsLeft);
    }

    public boolean isValid(String dateStr) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.US);
        try {
            df.parse(dateStr);
            return true;
        } catch (ParseException exc) {
        }
        return false;

    }

}
