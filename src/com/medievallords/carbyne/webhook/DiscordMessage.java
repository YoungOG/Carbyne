package com.medievallords.carbyne.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dalton on 9/7/2017.
 */
@Getter
@Setter
@Builder
public class DiscordMessage {

    String username;
    String content;
    @SerializedName("avatar_url")
    String avatarURL;
    @SerializedName("tts")
    boolean textToSpeech;

    public DiscordMessage(String username, String content, String avatarURL) {
        this(username, content, avatarURL, false);
    }

    public DiscordMessage(String username, String content, String avatarURL, boolean tts) {
        this.username = username;
        this.content = content;
        this.avatarURL = avatarURL;
        this.textToSpeech = tts;
    }

    public void setUsername(String username) {
        if (username != null) this.username.substring(0, Math.min(31, username.length()));
        else this.username = null;
    }

    public static class DiscordMessageBuilder {
        List<DiscordEmbed> embeds = new ArrayList<>();

        public DiscordMessageBuilder embed(DiscordEmbed embed) {
            embeds.add(embed);
            return this;
        }
    }

}
