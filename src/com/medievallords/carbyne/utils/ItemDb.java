package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDb {

    private Carbyne kmain = Carbyne.getInstance();
    private Map<String, Integer> items = new HashMap<>();
    private Map<ItemData, List<String>> names = new HashMap<>();
    private Map<ItemData, String> primaryName = new HashMap<>();
    private Map<String, Short> durabilities = new HashMap<>();
    private final Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");

    public ItemDb() {
        onReload();
    }

    public void onReload() {
        try {
            File file = new File(kmain.getDataFolder(), "item.csv");

            if (!file.exists()) {
                Bukkit.getPluginManager().disablePlugin(kmain);
            }

            Reader in = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);

            durabilities.clear();
            items.clear();
            names.clear();
            primaryName.clear();

            for (CSVRecord record : records) {
                String line1 = record.get(0);
                String line2 = record.get(1);

                String line3 = record.get(2);

                line1 = line1.trim().toLowerCase();
                if (line1.length() > 0 && line1.charAt(0) == '#') {
                    continue;
                }

                line2 = line2.trim().toLowerCase();
                if (line2.length() > 0 && line2.charAt(0) == '#') {
                    continue;
                }

                line3 = line3.trim().toLowerCase();
                if (line3.length() > 0 && line3.charAt(0) == '#') {
                    continue;
                }

                String[] parts = {line1, line2, line3};

                if (parts.length < 3) {
                    continue;
                }

                int numeric = Integer.parseInt(parts[1]);
                short data = parts.length > 2 && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0;
                String itemName = parts[0].toLowerCase();

                durabilities.put(itemName, data);
                items.put(itemName, numeric);

                ItemData itemData = new ItemData(numeric, data);

                if (names.containsKey(itemData)) {
                    List<String> nameList = names.get(itemData);
                    nameList.add(itemName);
                    nameList.sort(new LengthCompare());
                } else {
                    List<String> nameList = new ArrayList<>();
                    nameList.add(itemName);
                    names.put(itemData, nameList);
                    primaryName.put(itemData, itemName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ItemStack get(String id, int quantity) throws Exception {
        final ItemStack retval = get(id.toLowerCase());
        retval.setAmount(quantity);
        return retval;
    }

    public ItemStack get(String id) throws Exception {
        int itemid = 0;
        String itemname;
        short metaData = 0;
        Matcher parts = splitPattern.matcher(id);
        if (parts.matches()) {
            itemname = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {
            itemname = id;
        }

        if (NumberUtil.isInt(itemname)) {
            itemid = Integer.parseInt(itemname);
        } else if (NumberUtil.isInt(id)) {
            itemid = Integer.parseInt(id);
        } else {
            itemname = itemname.toLowerCase();
        }

        if (itemid < 1) {
            if (items.containsKey(itemname)) {
                itemid = items.get(itemname);

                if (durabilities.containsKey(itemname) && metaData == 0) {
                    metaData = durabilities.get(itemname);
                }
            } else if (Material.getMaterial(itemname.toUpperCase()) != null) {
                Material bMaterial = Material.getMaterial(itemname.toUpperCase());
                itemid = bMaterial.getId();
            } else {
                try {
                    Material bMaterial = Bukkit.getUnsafe().getMaterialFromInternalName(itemname.toLowerCase());
                    itemid = bMaterial.getId();
                } catch (Throwable throwable) {
                    throw new Exception(throwable);
                }
            }
        }

        if (itemid < 1) {
            throw new Exception();
        }

        final Material mat = Material.getMaterial(itemid);
        if (mat == null) {
            throw new Exception();
        }
        final ItemStack retval = new ItemStack(mat);
        retval.setAmount(mat.getMaxStackSize());
        retval.setDurability(metaData);
        return retval;
    }

    public List<ItemStack> getMatching(Player p, String[] args) throws Exception {
        List<ItemStack> is = new ArrayList<>();

        if (args.length < 1) {
            is.add(p.getItemInHand());
        } else if (args[0].equalsIgnoreCase("hand")) {
            is.add(p.getItemInHand());
        } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) {
            for (ItemStack stack : p.getInventory().getContents()) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack);
            }
        } else if (args[0].equalsIgnoreCase("blocks")) {
            for (ItemStack stack : p.getInventory().getContents()) {
                if (stack == null || stack.getTypeId() > 255 || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack);
            }
        } else {
            is.add(get(args[0]));
        }

        if (is.isEmpty() || is.get(0).getType() == Material.AIR) {
            throw new Exception();
        }

        return is;
    }

    public String names(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        List<String> nameList = names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            nameList = names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }

        return StringUtil.joinList(", ", nameList);
    }

    public String name(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        String name = primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            name = primaryName.get(itemData);
            if (name == null) {
                return null;
            }
        }
        return name;
    }


    static class ItemData {
        final private int itemNo;
        final private short itemData;

        ItemData(final int itemNo, final short itemData) {
            this.itemNo = itemNo;
            this.itemData = itemData;
        }

        public int getItemNo() {
            return itemNo;
        }

        public short getItemData() {
            return itemData;
        }

        @Override
        public int hashCode() {
            return (31 * itemNo) ^ itemData;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }
            ItemData pairo = (ItemData) o;
            return this.itemNo == pairo.getItemNo()
                    && this.itemData == pairo.getItemData();
        }
    }

    class LengthCompare implements Comparator<String> {
        public LengthCompare() {
            super();
        }

        @Override
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }
}