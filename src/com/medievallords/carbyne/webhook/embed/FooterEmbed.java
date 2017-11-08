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
public class FooterEmbed {

    String text;
    String icon_url;
    String proxy_icon_url;

}
