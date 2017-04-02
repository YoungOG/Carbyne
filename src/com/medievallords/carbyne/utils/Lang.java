package com.medievallords.carbyne.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Calvin on 11/16/2016
 * for the Utils project.
 */
public enum Lang {

    CRATE_HELP("CRATE_HELP",
            "&7&m-------&r&7 [ &aCrates &7] &m-------",
            "&a/crate &7- Shows available commands",
            "&a/crate create &b(name) &7- Create a new crate",
            "&a/crate edit &b(name) &7- Edit the loot of a crate",
            "&a/crate setblock &b(name) &7- Edits the location of a crate",
            "&a/crate remove &b(name) &7- Remove a crate",
            "&a/crate rename &b(current) (new) &7- Rename a crate",
            "&a/crate key give &b(player/all) (name) (amount) &7- Gives the player(s) key(s)",
            "&a/crate list &b(crates/keys) &7- Lists all available crates or keys",
            "&a/crate reload &7- Reload the configuration",
            "&a/crate toggle &7- Enable or disable the plugin"),

    USAGE_CRATE_KEY("COMMANDS.USAGE.CRATE_KEY", "&/crate key give <name/a> <key> <amount>"),
    USAGE_CRATE_LIST("COMMANDS.USAGE.CRATE_LIST", "&c/crate list <crates/keys>"),
    USAGE_GIVEAWAY("COMMANDS.USAGE.GIVEAWAY", "&c/giveaway <number/end>"),
    USAGE_CLEAR_LAG("COMMANDS.USAGE.CLEARLAG", "&c/clearlag <clear/garbage>"),

    TOO_FEW_ARGS_GIVEAWAY_COMMAND("COMMANDS.TOO_FEW_ARGS.GIVEAWAY", "&c/giveaway <number/end>"),
    TOO_FEW_ARGS_CLEAR_LAG_COMMAND("COMMANDS.TOO_FEW_ARGS.CLEAR_LAG", "&c/clearlag <clear/garbage>"),
    TOO_FEW_ARGS_CRATE_CREATE("COMMANDS.TOO_FEW_ARGS.CRATE_CREATE", "&c/crate create <name>"),
    TOO_FEW_ARGS_CRATE_EDIT("COMMANDS.TOO_FEW_ARGS.CRATE_EDIT", "&c/crate edit <name>"),
    TOO_FEW_ARGS_CRATE_KEY("COMMANDS.TOO_FEW_ARGS.CRATE_KEY", "&c/crate key give <name/-a> <key> <amount>"),
    TOO_FEW_ARGS_CRATE_LIST("COMMANDS.TOO_FEW_ARGS.CRATE_LIST", "&c/crate list <crates/keys>"),
    TOO_FEW_ARGS_CRATE_RELOAD("COMMANDS.TOO_FEW_ARGS.CRATE_RELOAD", "&c/crate reload"),
    TOO_FEW_ARGS_CRATE_REMOVE("COMMANDS.TOO_FEW_ARGS.CRATE_REMOVE", "&c/crate remove <name>"),
    TOO_FEW_ARGS_CRATE_RENAME("COMMANDS.TOO_FEW_ARGS.CRATE_RENAME", "&c/crate rename <oldName> <newName>"),
    TOO_FEW_ARGS_CRATE_SET_LOCATION("COMMANDS.TOO_FEW_ARGS.CRATE_SET_LOCATION", "&c/crate location <name>"),

    TOO_MANY_ARGS_GIVEAWAY_COMMAND("COMMANDS.TOO_MANY_ARGS.GIVEAWAY", "/giveaway <number/end>"),
    TOO_MANY_ARGS_CLEAR_LAG_COMMAND("COMMANDS.TOO_MANY_ARGS.CLEAR_LAG", "&c/clearlag <clear/garbage>"),
    TOO_MANY_ARGS_CRATE_CREATE("COMMANDS.TOO_MANY_ARGS.CRATE_CREATE", "&c/crate create <name>"),
    TOO_MANY_ARGS_CRATE_EDIT("COMMANDS.TOO_MANY_ARGS.CRATE_EDIT", "&c/crate edit <name>"),
    TOO_MANY_ARGS_CRATE_KEY("COMMANDS.TOO_MANY_ARGS.CRATE_KEY", "&c/crate key give <name/-a> <key> <amount>"),
    TOO_MANY_ARGS_CRATE_LIST("COMMANDS.TOO_MANY_ARGS.CRATE_LIST", "&c/crate list <crates/keys>"),
    TOO_MANY_ARGS_CRATE_RELOAD("COMMANDS.TOO_MANY_ARGS.CRATE_RELOAD", "&c/crate reload"),
    TOO_MANY_ARGS_CRATE_REMOVE("COMMANDS.TOO_MANY_ARGS.CRATE_REMOVE", "&c/crate remove <name>"),
    TOO_MANY_ARGS_CRATE_RENAME("COMMANDS.TOO_MANY_ARGS.CRATE_RENAME", "&c/crate rename <oldName> <newName>"),
    TOO_MANY_ARGS_CRATE_SET_LOCATION("COMMANDS.TOO_MANY_ARGS.CRATE_SET_LOCATION", "&c/crate location <name>"),

    SUCCESS_CRATE_CREATE("COMMANDS.SUCCESS.CRATE_CREATE", "&aSuccessfully created a new crate named &b{CRATE_NAME}&a."),
    SUCCESS_CRATE_EDIT("COMMANDS.SUCCESS.CRATE_EDIT", "&aYou are not editing &b{CRATE_NAME}&a."),
    SUCCESS_CRATE_KEY("COMMANDS.SUCCESS.CRATE_KEY", "&aYou have given &b{AMOUNT} &e{KEY_NAME} &ato &5{NAME}&a."),
    SUCCESS_CRATE_LIST_HEADER("COMMANDS.SUCCESS.CRATE_LIST.HEADER", "&aAvailable {TYPE}:"),
    SUCCESS_CRATE_LIST_NAME_COLOR("COMMANDS.SUCCESS.CRATE_LIST.NAME_COLOR", "AQUA"),
    SUCCESS_CRATE_LIST_COMMA_COLOR("COMMANDS.SUCCESS.CRATE_LIST.COMMA_COLOR", "GRAY"),
    SUCCESS_CRATE_RELOAD("COMMANDS.SUCCESS.CRATE_RELOAD", "&aConfiguration successfully reloaded."),
    SUCCESS_CRATE_REMOVE("COMMANDS.SUCCESS.CRATE_REMOVE", "&aYou have remove the crate &b{CRATE_NAME}&a."),
    SUCCESS_CRATE_RENAME("COMMANDS.SUCCESS.CRATE_RENAME", "&aYou have renamed the crate &b{CURRENT_NAME} &ato &b{NEW_NAME}&a."),
    SUCCESS_CRATE_SET_LOCATION("COMMANDS.SUCCESS.CRATE_SET_LOCATION", "&aYou have set the location of &b{CRATE_NAME} &ato (World: &b{WORLD}&a, X: &b{X}&a, Y: &b{Y}&a, Z: &b{Z}&a)."),
    SUCCESS_GIVEAWAY_START("COMMANDS.SUCCESS.GIVEAWAY.START", "&aYou have started a giveaway"),
    SUCCESS_GIVEAWAY_END("COMMANDS.SUCCESS.GIVEAWAY.END", "&aYou have ended a giveaway"),
    SUCCESS_CLEAR_LAG_CLEAR("COMMANDS.SUCCESS.CLEAR_LAG.CLEAR", "&aCleared &b{AMOUNT} &aout of &b{TOTAL} &afrom the world &b{WORLD}&a."),
    SUCCESS_CLEAR_LAG_MEMORY("COMMANDS.SUCCESS.CLEAR_LAG.MEMORY", "&aCurrent Memory: &b{FREE_MEM}&a/&b{MAX_MEM} &a| &b{USED_MEM}MB &a(&b{USED_MEM_PERCENT}%&a)"),
    SUCCESS_CLEAR_LAG_GC("COMMANDS.SUCCESS.CLEAR_LAG.GC", "&aGargbage Collector finished! Took: &b{TIME_MS}ms &a(&b{TIME_SEC}s&a)"),

    ERROR_PLAYER_NOT_FOUND("COMMANDS.ERROR.PLAYER_NOT_FOUND", "&cThat player could not be found."),
    ERROR_CRATE_CREATE("COMMANDS.ERROR.CRATE_CREATE", "&aThere is already a crate named that."),
    CRATE_NOT_FOUND("COMMANDS.ERROR.CRATE_NOT_FOUND", "&aThat crate could not be found."),
    CRATE_KEY_VALID_AMOUNT("COMMANDS.ERROR.CRATE_KEY_VALID_AMOUNT", "&cPlease enter a valid amount."),
    CRATE_KEYS_NO_KEYS("COMMANDS.ERROR.CRATE_KEY_NO_KEYS", "&cThere are no valid keys."),
    CRATE_KEYS_NOT_FOUND("COMMANDS.ERROR.CRATE_KEYS_NOT_FOUND", "&cA key by that name could not be found."),
    CRATE_LIST_NO_CRATES("COMMANDS.ERROR.CRATE_LIST_NO_CRATES", "&cThere are no available crates to display."),
    CRATE_LIST_NO_KEYS("COMMANDS.ERROR.CRATE_LIST_NO_KEYS", "&cThere are no available keys to display."),
    CRATE_RENAME_NAME_EXISTS("COMMANDS.ERROR.CRATE_RENAME_NAME_EXISTS", "&cA crate is already using that name."),
    CLAIM_ALREADY_USED("COMMANDS.ERROR.CLAIM_ALREADY_USED", "&aYou have already claimed your ranks rewards."),
    GIVEAWAY_ALREADY_RUNNING("COMMANDS.ERROR.GIVEAWAY_ALREADY_RUNNING", "&cA giveaway is already running.");

    private final String path;
    private final String[] messages;
    private static FileConfiguration LANG;

    Lang(String path, String... messages) {
        this.path = path;
        this.messages = messages;
    }

    public static void setFile(FileConfiguration langFile) {
        LANG = langFile;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(path, messages[0]));
    }

    public String getFirstMessage() {
        return ChatColor.translateAlternateColorCodes('&', messages[0]);
    }

    public String[] getAllMessages() { return messages; }

    public String getPath() {
        return path;
    }
}
