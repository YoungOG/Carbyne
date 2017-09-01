package com.medievallords.carbyne.professions;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.professions.types.*;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Williams on 2017-08-09
 * for the Carbyne project.
 */
@Getter
public class ProfessionManager {

    private List<Profession> professions = new ArrayList<>();

    public ProfessionManager() {
        load();
    }

    public void load() {
        ConfigurationSection cs = Carbyne.getInstance().getConfig().getConfigurationSection("Professions");

        professions.add(new SmeltingProfession("Smelting", cs.getDouble("Smelting.Chance"), 6, 9, "&aYou have earned &6credits &afrom smelting!"));
        professions.add(new CraftingProfession("Crafting", cs.getDouble("Crafting.Chance"), 6, 9, "&aYou have earned &6credits &afrom crafting!"));
        professions.add(new TamingProfession("Taming", cs.getDouble("Taming.Chance"), 20, 40, "&aYou have earned &6credits &afrom taming!"));
        professions.add(new RepairingProfession("Repairing", cs.getDouble("Repairing.Chance"), 10, 17, "&aYou have earned &6credits &afrom repairing!"));
        professions.add(new FarmingProfession("Farming", cs.getDouble("Farming.Chance"), 5, 8, "&aYou have earned &6credits &afrom farming!"));
        professions.add(new FishingProfession("Fishing", cs.getDouble("Fishing.Chance"), 8, 15, "&aYou have earned &6credits &afrom fishing!"));
        professions.add(new AlchemyProfession("Alchemy", cs.getDouble("Alchemy.Chance"), 8, 15, "&aYou have earned &6credits &afrom alchemy!"));
        professions.add(new EnchantingProfession("Enchanting", cs.getDouble("Enchanting.Chance"), 10, 35, "&aYou have earned &6credits &afrom enchanting!"));
        professions.add(new ProspectingProfession("Prospecting", cs.getDouble("Prospecting.Chance"), 10, 35, "&aYou have earned &6credits &afrom mining!"));
        professions.add(new HuntingProfession("Hunting", cs.getDouble("Hunting.Chance"), 10, 35, "&aYou have earned &6credits &afrom hunting!"));

        reload();
    }

    public Profession getProfession(String name) {
        for (Profession profession : professions) {
            if (profession.getName().equalsIgnoreCase(name)) {
                return profession;
            }
        }
        return null;
    }

    public void reload() {
        Carbyne.getInstance().reloadConfig();
        Carbyne.getInstance().saveConfig();

        for (Profession profession : professions) {
            ConfigurationSection cs = Carbyne.getInstance().getConfig().getConfigurationSection("Professions." + profession.getName());
            profession.setChance(cs.getDouble("Chance"));
            profession.setMaxNuggets(cs.getInt("MaxNuggets"));
            profession.setMinNuggets(cs.getInt("MinNuggets"));
            profession.setMaxChance(cs.getDouble("MaxChance"));
        }
    }


}
