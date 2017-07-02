package com.medievallords.carbyne.profiles;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Profile {

    private UUID uniqueId;
    private String username, pin, previousInventoryContentString;
    private int kills, carbyneKills, deaths, carbyneDeaths, killStreak;
    private long pvpTime, timeLeft;
    private boolean pvpTimePaused, showEffects, safelyLogged, moving;

    public Profile(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean hasEffectsToggled() {
        return showEffects;
    }

    public double getKDR() {
        double kills = getKills();
        double deaths = getDeaths();
        double ratio;

        if (kills == 0.0D && deaths == 0.0D) {
            ratio = 0.0D;
        } else {
            if (kills > 0.0D && deaths == 0.0D) {
                ratio = kills;
            } else {
                if (deaths > 0.0D && kills == 0.0D) {
                    ratio = -deaths;
                } else {
                    ratio = kills / deaths;
                }
            }
        }

        return Math.round(ratio * 100.0D) / 100.0D;
    }

    public double getCarbyneKDR() {
        double kills = getCarbyneKills();
        double deaths = getCarbyneDeaths();
        double ratio;

        if (kills == 0.0D && deaths == 0.0D) {
            ratio = 0.0D;
        } else {
            if (kills > 0.0D && deaths == 0.0D) {
                ratio = kills;
            } else {
                if (deaths > 0.0D && kills == 0.0D) {
                    ratio = -deaths;
                } else {
                    ratio = kills / deaths;
                }
            }
        }

        return Math.round(ratio * 100.0D) / 100.0D;
    }

    public long getRemainingPvPTime() {
        return pvpTime - System.currentTimeMillis();
    }

    public boolean hasPin() {
        return pin != null && !pin.isEmpty();
    }

    public void setPvpTimePaused(boolean paused) {
        if (this.pvpTimePaused != paused) {
            if (paused)
                timeLeft = System.currentTimeMillis() - (System.currentTimeMillis() - getRemainingPvPTime());

            this.pvpTimePaused = paused;
        }
    }
}
