package com.medievallords.carbyne.utils;

/**
 * Created by Dalton on 6/22/2017.
 */
public class Maths {

    /**
     * Min inclusive max exclusive
     *
     * @param max exclusive
     * @param min inclusive
     * @return the number
     */
    public static Integer randomNumberBetween(Integer max, Integer min) {
        return (int) (Math.random() * (max - min) + min);
    }
}
