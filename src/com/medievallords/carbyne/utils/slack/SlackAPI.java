package com.medievallords.carbyne.utils.slack;

import com.google.gson.JsonObject;
import com.medievallords.carbyne.Carbyne;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * An API for sending and handling Slack messages.
 */
public class SlackAPI {
    // Default emoji.
    public static final String DEFAULT_ICON = ":robot_face:";

    // Singular instance.
    private static SlackAPI _instance;

    // Don't allow instantiation elsewhere.
    private SlackAPI() {
    }

    /**
     * Sends a message asynchronously to a Slack channel.
     *
     * @param team        The team which contains the target channel.
     * @param channel     The target channel for the message.
     * @param message     The message to be displayed.
     * @param customTitle Whether or not to use a custom title for the message.
     *                    If <code>false</code> the default team title is used.
     */
    public void sendMessage(SlackTeam team, String channel, SlackMessage message, boolean customTitle) {
        Bukkit.getScheduler().runTaskAsynchronously(Carbyne.getInstance(), () -> {
            // Set message title.
            if (!customTitle) {
                message.setUsername(team.getTitle());
                message.setIcon(DEFAULT_ICON);
            }

            // Set message channel.
            JsonObject msg = message.toJson();
            msg.addProperty("channel", channel);

            // Run the call.
            runWebCall(team, msg);
        });
    }

    /**
     * Runs a web call to a specified Slack incoming-hook.
     *
     * @param team The team to run the call on.
     * @param call The call to be run.
     */
    private String runWebCall(SlackTeam team, JsonObject call) {
        HttpURLConnection connection = null;
        try {
            // Create connection.
            URL url = new URL(team.getURL());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Setup payload.
            String payload = "payload=" + URLEncoder.encode(call.toString(), "UTF-8");

            // Send request.
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(payload);
            dos.flush();
            dos.close();

            // Receive response.
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            String response = "";
            while ((line = rd.readLine()) != null) {
                response += line + "\n";
            }

            rd.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                // Terminate connection.
                connection.disconnect();
            }
        }

        return "500 Error";
    }

    /**
     * Gets the singular instance of the Slack API.
     *
     * @return The {@link SlackAPI} instance.
     */
    public static SlackAPI getInstance() {
        if (_instance == null) {
            _instance = new SlackAPI();
        }

        return _instance;
    }
}