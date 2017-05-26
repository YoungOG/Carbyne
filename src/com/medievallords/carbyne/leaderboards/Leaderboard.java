package com.medievallords.carbyne.leaderboards;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.economy.account.Account;
import com.medievallords.carbyne.profiles.Profile;
import com.medievallords.carbyne.utils.LocationSerialization;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.Namer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Calvin on 1/23/2017
 * for the Carbyne-Gear project.
 */

@Getter
@Setter
public class Leaderboard {

    private Carbyne main = Carbyne.getInstance();

    private String boardId;
    private LeaderboardType leaderboardType;
    private Location primarySignLocation;
    private ArrayList<Location> signLocations = new ArrayList<>();
    private ArrayList<Location> headLocations = new ArrayList<>();
    private BukkitTask bukkitTask;

    public Leaderboard(String boardId) {
        this.boardId = boardId;

        run();
    }

    public void run() {
        new LeaderboardTypeGenerator();

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateSigns(leaderboardType);
            }
        }.runTaskTimerAsynchronously(main, 5*20L, 5*20L);
    }

    public void stop() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    public void updateSigns(LeaderboardType leaderboardType) {
        Block primarySignBlock = primarySignLocation.getBlock();

        if (primarySignBlock == null) {
            return;
        }

        BlockState primarySignBlockState = primarySignBlock.getState();

        if (primarySignBlockState instanceof Sign) {
            Sign sign = (Sign) primarySignBlockState;

            sign.setLine(0, ChatColor.translateAlternateColorCodes('&', "&7&m-----------"));
            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&aCategory&7:"));
            sign.setLine(2, ChatColor.translateAlternateColorCodes('&', "&b" + Namer.capitalizeFirstLetter(leaderboardType.toString().toLowerCase())));
            sign.setLine(3, ChatColor.translateAlternateColorCodes('&', "&7&m-----------"));
            sign.update(true);
        }

        switch (leaderboardType) {
            case BALANCE:
                List<Account> balanceAccounts = new ArrayList<>();
                List<String> balanceAccountNames = new ArrayList<>();

                balanceAccounts.addAll(Account.getAccounts());

                balanceAccounts.sort(Comparator.comparingDouble(Account::getBalance).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Account account = balanceAccounts.get(i);

                        balanceAccountNames.add(account.getAccountHolder());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, account.getAccountHolder());
                            sign.setLine(3, "" + MessageManager.format(account.getBalance()));
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        balanceAccountNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(balanceAccountNames);
                break;
            case KILLS:
                List<Profile> killProfiles = new ArrayList<>();
                List<String> killProfileNames = new ArrayList<>();

                killProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                killProfiles.sort(Comparator.comparingInt(Profile::getKills).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = killProfiles.get(i);

                        killProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getKills());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        killProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(killProfileNames);
                break;
            case CARBYNE_KILLS:
                List<Profile> carbyneKillProfiles = new ArrayList<>();
                List<String> carbyneProfileNames = new ArrayList<>();

                carbyneKillProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                carbyneKillProfiles.sort(Comparator.comparingInt(Profile::getCarbyneKills).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = carbyneKillProfiles.get(i);

                        carbyneProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getCarbyneKills());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        carbyneProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(carbyneProfileNames);
                break;
            case DEATHS:
                List<Profile> deathProfiles = new ArrayList<>();
                List<String> deathProfileNames = new ArrayList<>();

                deathProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                deathProfiles.sort(Comparator.comparingInt(Profile::getDeaths).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = deathProfiles.get(i);

                        deathProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getDeaths());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        deathProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(deathProfileNames);
                break;
            case CARBYNE_DEATHS:
                List<Profile> carbyneDeathProfiles = new ArrayList<>();
                List<String> carbyneDeathProfileNames = new ArrayList<>();

                carbyneDeathProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                carbyneDeathProfiles.sort(Comparator.comparingInt(Profile::getCarbyneDeaths).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = carbyneDeathProfiles.get(i);

                        carbyneDeathProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getCarbyneDeaths());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        carbyneDeathProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(carbyneDeathProfileNames);
                break;
            case KDRATIO:
                List<Profile> KDRatioProfiles = new ArrayList<>();
                List<String> KDRatioProfileNames = new ArrayList<>();

                KDRatioProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                KDRatioProfiles.sort(Comparator.comparingDouble(Profile::getKDR).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = KDRatioProfiles.get(i);

                        KDRatioProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getKDR());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        KDRatioProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(KDRatioProfileNames);
                break;
            case CARBYNE_KDRATIO:
                List<Profile> carbyneKDRatioProfiles = new ArrayList<>();
                List<String> carbyneKDRatioProfileNames = new ArrayList<>();

                carbyneKDRatioProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                carbyneKDRatioProfiles.sort(Comparator.comparingDouble(Profile::getCarbyneKDR).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = carbyneKDRatioProfiles.get(i);

                        carbyneKDRatioProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getCarbyneKDR());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        carbyneKDRatioProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(carbyneKDRatioProfileNames);
                break;
            case KILLSTREAK:
                List<Profile> killstreakProfiles = new ArrayList<>();
                List<String> killstreakProfileNames = new ArrayList<>();

                killstreakProfiles.addAll(main.getProfileManager().getLoadedProfiles());

                killstreakProfiles.sort(Comparator.comparingDouble(Profile::getKillStreak).reversed());

                for (int i = (signLocations.size() - 1); i >= 0; i--) {
                    if (signLocations.get(i) == null) {
                        continue;
                    }

                    Block block = signLocations.get(i).getBlock();
                    BlockState blockState = block.getState();

                    try {
                        Profile profile = killstreakProfiles.get(i);

                        killstreakProfileNames.add(profile.getUsername());

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, profile.getUsername());
                            sign.setLine(3, "" + profile.getKillStreak());
                            sign.update();
                            sign.update(true);
                        }
                    } catch (Exception e) {
                        killstreakProfileNames.add("MHF_Question");

                        if (blockState instanceof Sign) {
                            Sign sign = (Sign) blockState;

                            sign.setLine(0, "" + (i + 1));
                            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&m------"));
                            sign.setLine(2, "");
                            sign.setLine(3, "");
                            sign.update();
                            sign.update(true);
                        }
                    }
                }

                updateHeads(killstreakProfileNames);
                break;
            default:
                clearSigns();
                clearHeads();
                break;
        }
    }

    public void clearSigns() {
        for (Location location : signLocations) {
            Block block = location.getBlock();

            if (block == null) {
                return;
            }

            BlockState blockState = block.getState();

            if (blockState instanceof Sign) {
                Sign sign = (Sign) blockState;

                sign.setLine(0, "");
                sign.setLine(1, "");
                sign.setLine(2, "");
                sign.setLine(3, "");
                sign.update();
                sign.update(true);
            }
        }
    }

    public void updateHeads(List<String> names) {
        Collections.reverse(names);

        for (int i = 0; i < names.size(); i++) {
            Location location = headLocations.get(i);

            if (location != null) {
                Block block = location.getBlock();

                if (block != null) {
                    BlockState blockState = block.getState();

                    if (blockState instanceof Skull) {
                        Skull skull = (Skull) blockState;

                        if (names.get(i) != null) {
                            if (names.get(i).startsWith("town-")) {
                                skull.setOwner((names.get(i) != null ? (!names.get(i).isEmpty() ? names.get(i) : "AcE_whatever") : "AcE_whatever"));
                            } else if (names.get(i).startsWith("nation-")) {
                                skull.setOwner((names.get(i) != null ? (!names.get(i).isEmpty() ? names.get(i) : "pologobbyboy") : "pologobbyboy"));
                            } else {
                                skull.setOwner((names.get(i) != null ? (!names.get(i).isEmpty() ? names.get(i) : "MHF_Question") : "MHF_Question"));
                            }

                            skull.update();
                            skull.update(true);
                        }
                    }
                }
            }
        }
    }

    public void clearHeads() {
        for (Location location : headLocations) {
            if (location.getBlock() != null) {
                BlockState blockState = location.getBlock().getState();

                if (blockState instanceof Skull) {
                    Skull skull = (Skull) blockState;
                    skull.setOwner("MHF_Question");
                    skull.update();
                    skull.update(true);
                }
            }
        }
    }

    public void save() {
        ConfigurationSection section = main.getLeaderboardFileConfiguration().getConfigurationSection("Leaderboards");

        if (!section.isSet(boardId)) {
            section.createSection(boardId);
        }

        if (!section.isSet(boardId + ".PrimarySignLocation")) {
            section.createSection(boardId + ".PrimarySignLocation");
        }

        if (!section.isSet(boardId + ".signLocations")) {
            section.createSection(boardId + ".signLocations");
            section.set(boardId + ".signLocations", new ArrayList<String>());
        }

        if (!section.isSet(boardId + ".headLocations")) {
            section.createSection(boardId + ".headLocations");
            section.set(boardId + ".headLocations", new ArrayList<String>());
        }

        section.set(boardId + ".PrimarySignLocation", (primarySignLocation != null ? LocationSerialization.serializeLocation(primarySignLocation) : ""));

        if (signLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : signLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(boardId + ".signLocations", locationStrings);
        }

        if (headLocations.size() > 0) {
            ArrayList<String> locationStrings = new ArrayList<>();

            for (Location location : headLocations) {
                locationStrings.add(LocationSerialization.serializeLocation(location));
            }

            section.set(boardId + ".headLocations", locationStrings);
        }

        try {
            main.getLeaderboardFileConfiguration().save(main.getLeaderboardFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.WARNING, "Failed to save leaderboard " + boardId + "!");
        }
    }

    public class LeaderboardTypeGenerator {

        private Carbyne main = Carbyne.getInstance();

        private int index;

        public LeaderboardTypeGenerator() {
            this.index = 0;

            startScheduler();
        }

        public void startScheduler() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    next();
                    leaderboardType = get();
                }
            }.runTaskTimerAsynchronously(main, 0L, 5*20);
        }

        private void next() {
            index++;
            index %= Arrays.asList(LeaderboardType.values()).size();
        }

        public LeaderboardType get() {
            return Arrays.asList(LeaderboardType.values()).get(index);
        }

        public LeaderboardType prev() {
            return Arrays.asList(LeaderboardType.values()).get((Arrays.asList(LeaderboardType.values()).size() + index - 1) % Arrays.asList(LeaderboardType.values()).size());
        }
    }
}
