package com.medievallords.carbyne.events.hohengroth;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by William on 7/11/2017.
 */
@Getter
@Setter
public class HohengrothCoffer {

    private double cofferPercent;

    public void deposit(int amount) {
        cofferPercent += 0.001;
    }
}
