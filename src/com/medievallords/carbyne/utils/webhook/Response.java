package com.medievallords.carbyne.utils.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Created by Dalton on 9/7/2017.
 */
@Getter
public class Response {

    boolean global;
    String message;
    @SerializedName("retry_after")
    int retryAfter;

}
