package com.medievallords.carbyne.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.command.defaults.TimingsCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.spigotmc.CustomTimingsHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

@RequiredArgsConstructor
public class TimingsFixListener implements Listener {

    private final Plugin plugin;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/timings paste") && event.getPlayer().hasPermission("timingsfix")) {
            handle(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().startsWith("timings paste")) {
            handle(event.getSender());
        }
    }

    private void handle(CommandSender sender) {
        if (!plugin.getServer().getPluginManager().useTimings()) {
            sender.sendMessage("Please enable timings by setting \"settings.plugin-profiling\" to true in bukkit.yml");
            return;
        }

        if (!plugin.getServer().getPluginManager().useTimings()) {
            sender.sendMessage("Please enable timings by typing /timings on");
            return;
        }

        long sampleTime = System.nanoTime() - TimingsCommand.timingStart;
        int index = 0;
        File timingFolder = new File("timings");
        timingFolder.mkdirs();
        File timings = new File(timingFolder, "timings.txt");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while (timings.exists()) timings = new File(timingFolder, "timings" + (++index) + ".txt");
        PrintStream fileTimings;
        fileTimings = new PrintStream(bout);

        CustomTimingsHandler.printTimings(fileTimings);
        fileTimings.println("Sample time " + sampleTime + " (" + sampleTime / 1E9 + "s)");

        fileTimings.println("<spigotConfig>");
        fileTimings.println(Bukkit.spigot().getConfig().saveToString());
        fileTimings.println("</spigotConfig>");

        new PasteThread(sender, bout).start();
    }

    private static class PasteThread extends Thread {

        private final CommandSender sender;
        private final ByteArrayOutputStream bout;

        PasteThread(CommandSender sender, ByteArrayOutputStream bout) {
            super("Timings paste thread");
            this.sender = sender;
            this.bout = bout;
        }

        @Override
        public synchronized void start() {
            if (sender instanceof RemoteConsoleCommandSender) {
                run();
            } else {
                super.start();
            }
        }

        @Override
        public void run() {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("https://timings.spigotmc.org/paste").openConnection();
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setInstanceFollowRedirects(false);

                OutputStream out = con.getOutputStream();
                out.write(this.bout.toByteArray());
                out.close();

                JsonObject location = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
                con.getInputStream().close();

                String pasteID = location.get("key").getAsString();
                sender.sendMessage(ChatColor.GREEN + "Timings results can be viewed at https://www.spigotmc.org/go/timings?url=" + pasteID);
            } catch (IOException ex) {
                sender.sendMessage(ChatColor.RED + "Error pasting timings, check your console for more information");
                Bukkit.getServer().getLogger().log(Level.WARNING, "Could not paste timings", ex);
            }
        }
    }
}