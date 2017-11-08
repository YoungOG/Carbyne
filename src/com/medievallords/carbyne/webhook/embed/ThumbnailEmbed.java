package com.medievallords.carbyne.webhook.embed;

import lombok.*;

/**
 * Created by Dalton on 9/7/2017.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailEmbed {

    String url;
    String proxy_url;
    int height;
    int width;

}
