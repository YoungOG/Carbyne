package com.medievallords.carbyne.webhook;

import com.medievallords.carbyne.webhook.embed.*;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DiscordEmbed {

    String title, type, description, url, timestamp;
    int color;
    FooterEmbed footer;
    ImageEmbed image;
    ThumbnailEmbed thumbnail;
    VideoEmbed video;
    ProviderEmbed provider;
    AuthorEmbed author;
    List<FieldEmbed> fields = new ArrayList<>();

    public DiscordEmbed(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public DiscordEmbed(String title, String message, String url) {
        this.title = title;
        this.description = message;
        this.url = url;
    }

    public static DiscordMessage toDiscordMessage(DiscordEmbed embed, String username, String avatarUrl) {
        DiscordMessage dm = DiscordMessage.builder()
                .username(username)
                .avatarURL(avatarUrl)
                .content("")
                .embed(embed)
                .build();
        return dm;
    }

    public DiscordMessage toDiscordMessage(String username, String avatarUrl) {
        return DiscordEmbed.toDiscordMessage(this, username, avatarUrl);
    }

    public static class DiscordEmbedBuilder {
        List<FieldEmbed> fields = new ArrayList<>();

        public DiscordEmbedBuilder field(FieldEmbed embed) {
            fields.add(embed);
            return this;
        }
    }

}
