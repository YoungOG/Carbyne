package com.medievallords.carbyne.utils.webhook;

import com.google.gson.Gson;

/**
 * Created by Dalton on 9/7/2017.
 */
public class StaffHook {

    public static final Gson gson = new Gson();
    private final String url;

    public StaffHook(String url) {
        this.url = url;
    }

    public void sendMessage(DiscordMessage dm) {
        Runnable runnable = new Runnable() {
            public void run() {
                String strResponse = HttpRequest.post(url)
                        .acceptJson()
                        .contentType("application/json")
                        .header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11")
                        .send(gson.toJson(dm))
                        .body();
                if (strResponse.isEmpty()) {
                    Response response = gson.fromJson(strResponse, Response.class);
                    try {
                        if (response == null) return;
                        if (response.getMessage().equals("You are being rate limited.")) {
                            try {
                                Thread.sleep(response.getRetryAfter());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new WebhookException(e.getMessage());
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.run();
    }

}
