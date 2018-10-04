package com.medievallords.carbyne.quests;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.quests.config.QuestLineConfig;
import com.medievallords.carbyne.quests.rewards.QuestReward;
import com.medievallords.carbyne.quests.rewards.QuestRewardItem;
import com.medievallords.carbyne.quests.types.*;
import com.medievallords.carbyne.utils.BookUtil;
import com.medievallords.carbyne.utils.HiddenStringUtils;
import com.medievallords.carbyne.utils.ItemBuilder;
import com.medievallords.carbyne.utils.StaticClasses;
import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

@Getter
public class QuestHandler {

    // private HashMap<UUID, BossBar> questBars = new HashMap<>();

    private final List<Task> tasks = new ArrayList<>();
    private final List<Quest> quests = new ArrayList<>();
    private final List<Quest> forcedQuests = new ArrayList<>();

    public QuestHandler() {
        load();
    }

    public void load() {
        if (!quests.isEmpty()) {
            for (Profile profile : StaticClasses.profileManager.getLoadedProfiles()) {
                profile.getQuests().clear();
                for (Quest quest : getQuests(profile.getUniqueId())) {
                    StringBuilder builder = new StringBuilder(quest.getName() + ":" + quest.getPlayers().get(profile.getUniqueId()) + "!");
                    for (Task task : quest.getTasks()) {
                        builder.append(task.getName() + ":" + task.getPlayers().get(profile.getUniqueId()) + ",");
                    }

                    profile.getQuests().add(builder.toString());
                }
            }
        }

        ConfigurationSection cs = Carbyne.getInstance().getQuestsFileConfiguration().getConfigurationSection("Tasks");
        if (cs == null) {
            cs = Carbyne.getInstance().getQuestsFileConfiguration().createSection("Tasks");
            try {
                Carbyne.getInstance().getQuestsFileConfiguration().save(Carbyne.getInstance().getQuestsFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tasks.clear();
        quests.clear();
        forcedQuests.clear();

        for (String key : cs.getKeys(false)) {
            String type = cs.getString(key + ".Type");
            List<String> line = cs.getStringList(key + ".Options");
            List<String> commands = new ArrayList<>();
            if (cs.contains(key + ".Commands")) {
                commands = cs.getStringList(key + ".Commands");
            }

            switch (type.toLowerCase()) {
                case "gatherresource":
                    tasks.add(new GatherResourceTask(key, new QuestLineConfig(line), commands));
                    break;
                case "killentity":
                    tasks.add(new KillEntityTask(key, new QuestLineConfig(line), commands));
                    break;
                case "craft":
                    tasks.add(new CraftTask(key, new QuestLineConfig(line), commands));
                    break;
                case "lootchest":
                    tasks.add(new LootChestTask(key, new QuestLineConfig(line), commands));
                    break;
                case "discoverzone":
                    tasks.add(new DiscoverZoneTask(key, new QuestLineConfig(line), commands));
                    break;
                case "enterdungeon":
                    tasks.add(new EnterDungeonTask(key,  new QuestLineConfig(line), commands));
                    break;
                case "interactnpc":
                    tasks.add(new InteractWithNPCTask(key, new QuestLineConfig(line), commands));
                    break;
                case "jointown":
                    tasks.add(new JoinTownTask(key, new QuestLineConfig(line), commands));
                    break;
                case "depositmoney":
                    tasks.add(new DepositMoneyTask(key, new QuestLineConfig(line), commands));
                    break;
                case "learnspell":
                    tasks.add(new LearnSpellTask(key, new QuestLineConfig(line), commands));
                    break;
                case "castspell":
                    tasks.add(new CastSpellTask(key, new QuestLineConfig(line), commands));
                    break;
                case "enterportal":
                    tasks.add(new EnterPortalTask(key, new QuestLineConfig(line), commands));
                    break;
                case "enchant":
                    tasks.add(new EnchantTask(key, new QuestLineConfig(line), commands));
                    break;
                case "repair":
                    tasks.add(new RepairCarbyneTask(key, new QuestLineConfig(line), commands));
                    break;
                case "voting":
                    tasks.add(new VotingStreakTask(key, new QuestLineConfig(line), commands));
                    break;
                case "smelt":
                    tasks.add(new SmeltTask(key, new QuestLineConfig(line), commands));
                    break;
                case "createsquad":
                    tasks.add(new CreateSquadTask(key, new QuestLineConfig(line), commands));
                    break;
                case "createnation":
                    tasks.add(new CreateNationTask(key, new QuestLineConfig(line), commands));
                    break;
            }
        }

        cs = Carbyne.getInstance().getQuestsFileConfiguration().getConfigurationSection("Quests");

        if (cs == null) {
            cs = Carbyne.getInstance().getQuestsFileConfiguration().createSection("Quests");
            try {
                Carbyne.getInstance().getQuestsFileConfiguration().save(Carbyne.getInstance().getQuestsFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String key : cs.getKeys(false)) {
            List<String> line = new ArrayList<>();
            List<String> informationLore = new ArrayList<>();
            if (cs.contains(key + ".Options")) {
                line = cs.getStringList(key + ".Options");
            }
            if (cs.contains(key + ".Info")) {
                informationLore = cs.getStringList(key + ".Info");
            }

            List<Task> tasksToAdd = new ArrayList<>();
            List<String> taskNames = cs.getStringList(key + ".Tasks");
            if (taskNames != null) {
                for (String s : taskNames) {
                    Task task = getTask(s);

                    if (task != null)
                        tasksToAdd.add(task);
                }
            }

            List<String> commands = new ArrayList<>();
            if (cs.contains(key + ".Commands")) {
                commands = cs.getStringList(key + ".Commands");
            }

            List<QuestRewardItem> questRewardItems = new ArrayList<>();
            ConfigurationSection itemSection = cs.getConfigurationSection(key + ".Rewards");
            if (itemSection != null) {
                for (String itemName : itemSection.getKeys(false)) {
                    int itemId = 1;
                    int itemData = 0;
                    int amount = 1;
                    String displayName = "";
                    String rewardDisplayName = "";
                    List<String> lore = new ArrayList<>();
                    List<String> enchantments = new ArrayList<>();

                    if (itemSection.contains(itemName + ".ItemID")) {
                        itemId = itemSection.getInt(itemName + ".ItemID");
                    }

                    if (itemSection.contains(itemName + ".ItemData")) {
                        itemData = itemSection.getInt(itemName + ".ItemData");
                    }

                    if (itemSection.contains(itemName + ".Amount")) {
                        amount = itemSection.getInt(itemName + ".Amount");
                    }
                    if (itemSection.contains(itemName + ".DisplayName")) {
                        displayName = itemSection.getString(itemName + ".DisplayName");
                    }
                    if (itemSection.contains(itemName + ".RewardDisplayName")) {
                        rewardDisplayName = itemSection.getString(itemName + ".RewardDisplayName");
                    }

                    if (itemSection.contains(itemName + ".Lore")) {
                        lore = itemSection.getStringList(itemName + ".Lore");
                    }

                    if (itemSection.contains(itemName + ".Enchantments")) {
                        enchantments = itemSection.getStringList(itemName + ".Enchantments");
                    }


                    QuestRewardItem questRewardItem = new QuestRewardItem(Integer.parseInt(itemName), Material.getMaterial(itemId), displayName, lore, amount, itemData, rewardDisplayName);
                    for (String s : enchantments) {
                        String[] args = s.split(",");
                        questRewardItem.getEnchantments().put(Enchantment.getByName(args[0]), Integer.valueOf(args[1]));
                    }

                    questRewardItems.add(questRewardItem);
                }
            }

            QuestReward questReward = new QuestReward(commands, questRewardItems);

            Quest quest = new Quest(key, informationLore, new QuestLineConfig(line), tasksToAdd);
            quest.setReward(questReward);
            if (quest.isForcedQuest()) {
                forcedQuests.add(quest);
            } else {
                quests.add(quest);
            }
        }

        List<String> questOrder = Carbyne.getInstance().getQuestsFileConfiguration().getStringList("ForcedQuestOrder");
        if (questOrder != null) {
            List<Quest> newForcedQuests = new ArrayList<>();
            for (String s : questOrder) {
                Quest quest = getQuest(s);
                if (quest != null) {
                    newForcedQuests.add(quest);
                }
            }

            this.forcedQuests.clear();
            this.forcedQuests.addAll(newForcedQuests);
        }

        for (Profile profile : StaticClasses.profileManager.getLoadedProfiles()) {
            handleProfile(profile);
        }
    }

    private void handleProfile(Profile profile) {
        for (String questString : profile.getQuests()) {
            //quest:1000!task1:10,task2:90,
            String[] questSplit = questString.split("!");
            if (questSplit.length != 2) {
                continue;
            }

            String[] timeSplit = questSplit[0].split(":");
            if (timeSplit.length != 2) {
                continue;
            }

            Quest quest = getQuest(timeSplit[0]);
            if (quest == null) {
                continue;
            }

            long time;
            try {
                time = Long.parseLong(timeSplit[1]);
            } catch (NumberFormatException e) {
                continue;
            }

            String[] taskSplit = questSplit[1].split(",");
            for (String taskString : taskSplit) {
                String[] progressSplit = taskString.split(":");
                System.out.println("Split: " + taskString);
                if (progressSplit.length != 2) {
                    continue;
                }

                System.out.println("More than 2");

                String taskName = progressSplit[0];

                System.out.println("Name: " + taskName);

                int progress;
                try {
                    progress = Integer.parseInt(progressSplit[1]);
                } catch (NumberFormatException e) {
                    continue;
                }

                for (Task task : quest.getTasks()) {
                    if (task.getName().equalsIgnoreCase(taskName)) {
                        System.out.println(taskName + ":" + progress);
                        task.setPlayer(profile.getUniqueId(), progress);
                    }
                }
            }

            System.out.println("Quest: " + quest.getName() + " Time: " + time);
            quest.addPlayer(profile.getUniqueId(), time);
        }
    }

    public void reload() {
        try {
            Carbyne.getInstance().setQuestsFileConfiguration(YamlConfiguration.loadConfiguration(Carbyne.getInstance().getQuestsFile()));
            Carbyne.getInstance().getQuestsFileConfiguration().save(Carbyne.getInstance().getQuestsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    public Quest getQuest(final String name) {
        for (Quest quest : quests)
            if (quest.getName().equalsIgnoreCase(name))
                return quest;

        for (Quest quest : this.forcedQuests)
            if (quest.getName().equalsIgnoreCase(name))
                return quest;

        return null;
    }

    public Quest getQuest(ItemStack is) {
        if (is == null || is.getType() == Material.AIR)
            return null;

        if (!is.hasItemMeta())
            return null;

        if (is.getItemMeta() == null)
            return null;

        if (!is.getItemMeta().hasLore())
            return null;

        if (is.getItemMeta().getLore() == null)
            return null;

        List<String> lore = is.getItemMeta().getLore();

        if (lore == null || lore.isEmpty())
            return null;

        String hiddenLore = HiddenStringUtils.extractHiddenString(lore.get(0));
        return getQuest(hiddenLore);
    }

    public List<Quest> getQuests(final UUID uuid) {
        List<Quest> quests = new ArrayList<>();
        for (Quest quest : this.quests)
            if (quest.getPlayers().containsKey(uuid))
                quests.add(quest);

        for (Quest quest : this.forcedQuests)
            if (quest.getPlayers().containsKey(uuid))
                quests.add(quest);

        return quests;
    }

    public void openQuestInfo(Player player) {
        ItemStack book = new ItemBuilder(Material.WRITTEN_BOOK).build();
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<Quest> quests = getQuests(player.getUniqueId());
        if (!quests.isEmpty()) {
            for (Quest quest : quests) {
                String infoString = "";
                for (String info : quest.getInformationLore()) {
                    infoString = infoString + ChatColor.translateAlternateColorCodes('&', info) + " ";
                }

                String text = quest.getDifficulty().getColor() + "§l" + quest.getDisplayName() + "     "+ "\n";
                text += "§0(" + quest.getDifficulty().getColor() + WordUtils.capitalizeFully(quest.getDifficulty().name()) + "§0)\n";
//                        text += "\n";
//                        //text += "§6Quest§0:\n" +
                text += infoString;
                text += "\n";
                text += "§6Progress§0:\n";

                for (Task task : tasks) {
                    if (!task.isCompleted(player.getUniqueId())) {
                        text += ChatColor.translateAlternateColorCodes('&', "  " + task.getProgress(player) + "\n");
                    }
                }

//                text += "§6Time§0: " + quest.getRemainingTimeString(player) + "\n";
//                text += "§6Rewards§0:";
                bookMeta.addPage(text);
                //
                // Double Up (EASY)
                //
                //  Quest:
                //  - A spell has been casted upon the spawn castle
                //   disabling travellers from trading. You must
                //   stop this madness! The witches of the wood are
                //   behind this! Kill a few for me would ya?

                //  Progress:
                //    Witches: 3/10
                //  Time: 00:20:12
                //  Rewards:
                //  - Diamond Axe 1
                //  - Glistering Melon 2
            }
        } else {
            bookMeta.addPage("§cYou do not have any active quests. Go to the quest giver at spawn to take one.");
        }

        book.setItemMeta(bookMeta);

        BookUtil.openBook(book, player);
    }

    public Inventory getQuestInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§d§lQuests");

        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name("&a").build();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, pane);
        }

        List<Quest> currentQuests = getQuests(player.getUniqueId());
        for (Quest quest : currentQuests) {
            if (quest.isForcedQuest()) {
                ItemStack item = quest.getDisplayItem(player, true);
                inventory.setItem(4, item);
                return inventory;
            }
        }

        Profile profile = StaticClasses.profileManager.getProfile(player.getUniqueId());
        if (!profile.getForcedQuests().isEmpty()) {
            Quest forcedQuest = findForcedQuest(profile);
            if (forcedQuest != null) {
                //INVENTORY WITH ONE SLOT FOR THE QUEST
                ItemStack item = forcedQuest.getDisplayItem(player, false);
                inventory.setItem(4, item);
                return inventory;
            }
        }

        Set<Integer> toAdd = new HashSet<>();
        for (Difficulty difficulty : Difficulty.values()) {
            toAdd.add(difficulty.getData());
        }

        for (Quest quest : currentQuests) {
            ItemStack item = quest.getDisplayItem(player, true);
            switch (quest.getDifficulty()) {
                case EASY:
                    toAdd.remove(Difficulty.EASY.getData());
                    inventory.setItem(1, item);
                    break;
                case MEDIUM:
                    toAdd.remove(Difficulty.MEDIUM.getData());
                    inventory.setItem(3, item);
                    break;
                case HARD:
                    toAdd.remove(Difficulty.HARD.getData());
                    inventory.setItem(5, item);
                    break;
                case BRUTAL:
                    toAdd.remove(Difficulty.BRUTAL.getData());
                    inventory.setItem(7, item);
                    break;
            }
        }

        for (Difficulty difficulty : Difficulty.values()) {
            if (!toAdd.contains(difficulty.getData())) {
                continue;
            }

            Quest newQuest = findFreeQuest(profile, difficulty);
            if (newQuest != null) {
                ItemStack item = newQuest.getDisplayItem(player, false);
                switch (difficulty) {
                    case EASY:
                        inventory.setItem(1, item);
                        break;
                    case MEDIUM:
                        inventory.setItem(3, item);
                        break;
                    case HARD:
                        inventory.setItem(5, item);
                        break;
                    case BRUTAL:
                        inventory.setItem(7, item);
                        break;
                }
            }
        }

        return inventory;
    }

    public void handleComplete(Quest quest, int slot, Player player, Inventory inventory) {
        Quest newQuest = findFreeQuest(StaticClasses.profileManager.getProfile(player.getUniqueId()), quest.getDifficulty());
        if (newQuest != null) {
            ItemStack item = newQuest.getDisplayItem(player, false);
            inventory.setItem(slot, item);
        }
    }

    public void handleDelete(Quest quest, Player player) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "§c§lSkip Quest");

        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).name("&a").build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane);
        }

        ItemStack accept = new ItemBuilder(Material.EMERALD_BLOCK).name("&a&lYes.")
                .addLore(HiddenStringUtils.encodeString(quest.getName())).addLore("&dDo you really want").addLore("&dto skip this quest?").build();

        ItemStack deny = new ItemBuilder(Material.REDSTONE_BLOCK).name("&c&lNo.")
                .addLore(HiddenStringUtils.encodeString(quest.getName())).addLore("&dDo you really want").addLore("&dto skip this quest?").build();

        inventory.setItem(1, deny);
        inventory.setItem(3, accept);

        player.openInventory(inventory);
    }

    public Task getTask(final String name) {
        for (Task task : tasks)
            if (task.getName().equalsIgnoreCase(name))
                return task;

        return null;
    }

    public List<Task> getTasks(final UUID uuid) {
        List<Task> tasks = new ArrayList<>();
        for (Task task : this.tasks)
            if (task.getPlayers().containsKey(uuid))
                tasks.add(task);

        return tasks;
    }

    public Quest findFreeQuest(Profile profile, Difficulty difficulty) {
        //Profile profile = main.getProfileManager().getProfile(uuid);
        for (Quest quest : quests) {
            if (!profile.getDormantQuests().contains(quest.getName()) && quest.getDifficulty() == difficulty) {
                return quest;
            }
        }

        removeAllTaken(profile, difficulty);
        return null;
    }

    public void removeAllTaken(Profile profile, Difficulty difficulty) {
        for (Quest quest : quests) {
            if (profile.getDormantQuests().contains(quest.getName()) && quest.getDifficulty() == difficulty) {
                profile.getDormantQuests().remove(quest.getName());
            }
        }
    }

    public List<String> getForcedQuestsByName() {
        List<String> list = new ArrayList<>();
        for (Quest quest : forcedQuests) {
            list.add(quest.getName());
        }

        return list;
    }

    public Quest findForcedQuest(Profile profile) {
        if (profile.getForcedQuests().isEmpty()) {
            return null;
        }

        Quest quest = getQuest(profile.getForcedQuests().get(0));
        if (quest == null) {
            profile.getForcedQuests().remove(0);
            return findForcedQuest(profile);
        } else {
            return quest;
        }
    }
}
