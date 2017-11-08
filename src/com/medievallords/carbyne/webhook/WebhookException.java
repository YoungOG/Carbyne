package com.medievallords.carbyne.webhook;

/**
 * Created by Dalton on 9/7/2017.
 */
public class WebhookException extends RuntimeException {

    public WebhookException(String reason) {
        super(reason);
    }

}
