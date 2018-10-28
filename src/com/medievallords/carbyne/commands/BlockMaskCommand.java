package com.medievallords.carbyne.commands;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.InstantFirework;
import com.medievallords.carbyne.utils.MessageManager;
import com.medievallords.carbyne.utils.command.BaseCommand;
import com.medievallords.carbyne.utils.command.Command;
import com.medievallords.carbyne.utils.command.CommandArgs;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class BlockMaskCommand extends BaseCommand implements Listener {

    public BlockMaskCommand() {
        Bukkit.getPluginManager().registerEvents(this, Carbyne.getInstance());
    }

    private HashMap<UUID, MaskRunnable> runnables = new HashMap<>();
    private Set<UUID> snowballs = new HashSet<>();

    @Command(name = "cgsnow", permission = "carbyne.administrator", inGameOnly = true)
    public void onCom(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (snowballs.contains(player.getUniqueId())) {
            snowballs.remove(player.getUniqueId());
            MessageManager.sendMessage(player, "&cR");
        } else {
            snowballs.add(player.getUniqueId());
            MessageManager.sendMessage(player, "&aA");
        }
    }

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() == null) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        if (snowballs.contains(player.getUniqueId())) {
            event.getEntity().setMetadata("explodingSnowball", new FixedMetadataValue(Carbyne.getInstance(), "true"));
        }
    }

    @EventHandler
    public void onProjectile(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("explodingSnowball")) {
            InstantFirework.spawn(event.getEntity().getLocation(), FireworkEffect.builder().withColor(Color.ORANGE).withColor(Color.RED).build());
        }
    }

    @Command(name = "tvel", permission = "carbyne.administrator", inGameOnly = true)
    public void onComma(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 7) {
            Player to = Bukkit.getPlayer(args[0]);
            if (to == null) {
                return;
            }

            double x, y, z;
            int t, p;
            try {
                x = Double.parseDouble(args[3]);
                y = Double.parseDouble(args[4]);
                z = Double.parseDouble(args[5]);
                t = Integer.parseInt(args[1]);
                p = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cError occurred");
                return;
            }

            new BukkitRunnable() {
                private int timer = 0;

                @Override
                public void run() {
                    timer++;
                    to.setVelocity(new Vector(x, y, z));

                    if (timer >= t) {
                        cancel();
                    }
                }
            }.runTaskTimer(Carbyne.getInstance(), 0, p);
        } else if (args.length == 6) {
            Player to = Bukkit.getPlayer(args[0]);
            if (to == null) {
                return;
            }

            int t, p;
            try {
                t = Integer.parseInt(args[1]);
                p = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.sendMessage(player, "&cError occurred");
                return;
            }

            String functionX = args[3];
            String functionY = args[4];
            String functionZ = args[5];

            Function function = new Function();

            new BukkitRunnable() {
                private int timer = 0;

                @Override
                public void run() {
                    timer++;
                    to.setVelocity(new Vector(function.f(player.getLocation().getX(), functionX), function.f(player.getLocation().getY(), functionY), function.f(player.getLocation().getZ(), functionZ)));

                    if (timer >= t) {
                        cancel();
                    }
                }
            }.runTaskTimer(Carbyne.getInstance(), 0, p);
        } else {
            MessageManager.sendMessage(player, "&cUsage: &7/testvelocity <pl> <t> <p> <x> <y> <z> <l>");
            MessageManager.sendMessage(player, "&cUsage: &7/testvelocity <pl> <t> <p> <fX> <fY> <fZ>");
        }
    }

    @Command(name = "settowns", permission = "carbyne.administrator", inGameOnly = true)
    public void onC(CommandArgs commandArgs) {
        for (Town town : TownyUniverse.getDataSource().getTowns()) {
            try {
                town.setBalance(100, "custom");
            } catch (EconomyException e) {
                e.printStackTrace();
            }
        }
    }

    @Command(name = "setchange", permission = "carbyne.administrator", inGameOnly = true)
    public void onComman(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Location location = player.getTargetBlock((Set<Material>) null, 5).getLocation();
        for (final Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 50, 50, 50)) {
            if (entity instanceof Player) {
                final Player other = (Player) entity;
                other.sendBlockChange(location, Material.STATIONARY_WATER, (byte) 0);
            }
        }
    }

    @Command(name = "carbyneblockmask", inGameOnly = true, permission = "carbyne.administrator")
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
            if (runnables.containsKey(player.getUniqueId())) {
                runnables.get(player.getUniqueId()).cancel();
                MessageManager.sendMessage(player, "&cEffect has stopped.");
            } else {
                MessageManager.sendMessage(player, "&cYou do not have any effects.");
            }
        } else {
            if (args.length == 3) {
                if (runnables.containsKey(player.getUniqueId())) {
                    runnables.get(player.getUniqueId()).cancel();
                }

                int radius = 0, data = 0;
                Material material = Material.STONE;

                try {
                    radius = Integer.parseInt(args[2]);
                    data = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    MessageManager.sendMessage(player, "&cYou may only input integers for data and radius.");
                    return;
                }

                try {
                    material = Material.getMaterial(args[0].toUpperCase());
                } catch (Exception e) {
                    MessageManager.sendMessage(player, "&cYou may only input a valid material.");
                    return;
                }

                final MaskRunnable maskRunnable = new MaskRunnable(player, material, data, radius);
                runnables.put(player.getUniqueId(), maskRunnable);
                maskRunnable.runTaskTimerAsynchronously(Carbyne.getInstance(), 0, 1);
                MessageManager.sendMessage(player, "&aYou have applied an effect.");
            } else {
                MessageManager.sendMessage(player, "&cUsage: &7/carbyneblockmask <material> <data> <radius>");
            }
        }
    }

    private final List<Location> getBlocksInRadius(final Location l, final int radius) {
        final List<Location> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    final Location newloc = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z);
                    if (newloc.getBlock().getType().isSolid() && newloc.distance(l) <= radius) {
                        blocks.add(newloc);
                    }
                }
            }
        }
        return blocks;
    }

    public class MaskRunnable extends BukkitRunnable {

        private final Player player;
        private final int data, radius;
        private final Material material;
        private Set<Location> locations = new HashSet<>();
        private Location prevLocation;

        public MaskRunnable(Player player, Material material, int data, int radius) {
            this.player = player;
            this.material = material;
            this.data = data;
            this.radius = radius;
            prevLocation = player.getLocation();
        }

        @Override
        public void run() {
            if (prevLocation.getBlockX() != player.getLocation().getBlockX() || prevLocation.getBlockY() != player.getLocation().getBlockY() || prevLocation.getBlockZ() != player.getLocation().getBlockZ()) {
                prevLocation = player.getLocation();
                locations.clear();
                locations.addAll(getBlocksInRadius(player.getLocation(), radius));
                for (Location location : locations) {
                    for (final Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 50, 50, 50)) {
                        if (entity instanceof Player) {
                            final Player other = (Player) entity;
                            other.sendBlockChange(location, material, (byte) data);
                            new BukkitRunnable() {
                                public void run() {
                                    if (!locations.contains(location)) {
                                        other.sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData());
                                    }
                                }
                            }.runTaskLaterAsynchronously(Carbyne.getInstance(), 100);
                        }
                    }
                }
            }
        }
    }

//    public class MaskBlock {
//
//        @Getter
//        private int id, data;
//
//        public MaskBlock(int id, int data) {
//            this.id = id;
//            this.data = data;
//        }
//    }

    public class Function {

        public double f(Double x, String s) {
            return eval(s.replace("x", x.toString()));
        }

        public double eval(final String str) {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < str.length()) ? str.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                // Grammar:
                // expression = term | expression `+` term | expression `-` term
                // term = factor | term `*` factor | term `/` factor
                // factor = `+` factor | `-` factor | `(` expression `)`
                //        | number | functionName factor | factor `^` factor

                double parseExpression() {
                    double x = parseTerm();
                    for (; ; ) {
                        if (eat('+')) x += parseTerm(); // addition
                        else if (eat('-')) x -= parseTerm(); // subtraction
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (; ; ) {
                        if (eat('*')) x *= parseFactor(); // multiplication
                        else if (eat('/')) x /= parseFactor(); // division
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor(); // unary plus
                    if (eat('-')) return -parseFactor(); // unary minus

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') { // functions
                        while (ch >= 'a' && ch <= 'z') nextChar();
                        String func = str.substring(startPos, this.pos);
                        x = parseFactor();
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("sin")) x = Math.sin(x);
                        else if (func.equals("cos")) x = Math.cos(x);
                        else if (func.equals("tan")) x = Math.tan(x);
                        else throw new RuntimeException("Unknown function: " + func);
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                    return x;
                }
            }.parse();
        }


    }
}
