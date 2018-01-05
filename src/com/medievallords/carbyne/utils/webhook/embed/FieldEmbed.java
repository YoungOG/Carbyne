package com.medievallords.carbyne.utils.webhook.embed;

import lombok.*;

/**
 * Created by Dalton on 9/7/2017.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldEmbed {

    String name;
    String value;
    boolean inline;

}
