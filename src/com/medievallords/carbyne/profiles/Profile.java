package com.medievallords.carbyne.profiles;

import com.medievallords.carbyne.events.Event;
import com.medievallords.carbyne.professions.Profession;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Profile {

    private UUID uniqueId;
    private String username, pin, previousInventoryContentString;
    private int kills, carbyneKills, deaths, carbyneDeaths, killStreak, claimedTickets, closedTickets;
    private long pvpTime, timeLeft;
    private boolean pvpTimePaused, showEffects, safelyLogged, moving;
    private Event activeEvent;
    private List<UUID> ignoredPlayers = new ArrayList<>();
    private boolean localChatToggled = false, townChatToggled = false, nationChatToggled = false;
    private Profession profession;
    private int professionLevel = 1;
    private double professionProgress = 0, requiredProfessionProgress = 100;
    private long professionResetCooldown = 0;

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

    public long getRemainingTimeLeft() {
        return System.currentTimeMillis() + timeLeft;
    }

    public boolean hasPin() {
        return pin != null && !pin.isEmpty();
    }

    public void setPvpTimePaused(boolean paused) {
        if (this.pvpTimePaused != paused) {
            if (paused)
                //System.currentTimeMillis() - (System.currentTimeMillis() -
                timeLeft = getRemainingPvPTime();

            this.pvpTimePaused = paused;
        }
    }
}
