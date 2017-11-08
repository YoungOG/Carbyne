package com.medievallords.carbyne.webhook.embed;

import lombok.*;

/**
 * Created by Dalton on 9/7/2017.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEmbed {

    String url;
    String proxy_url;
    int height;
    int width;

}
