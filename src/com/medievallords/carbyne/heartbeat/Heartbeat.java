package com.medievallords.carbyne.heartbeat;

import org.bukkit.Bukkit;

/**
 * Created by Calvin on 2/1/2017
 * for the Carbyne-Gear project.
 */
public class Heartbeat extends Thread {

    private static int currentBeats = 0;
    private int myBeat;
    private HeartbeatTask task;
    private long beat;
    private int heartBeat = 0;

    public Heartbeat(HeartbeatTask task, long beat) {
        this.beat = beat;
        this.task = task;
        currentBeats++;
        myBeat = currentBeats;
    }

    @Override
    public void run() {
        while (task.heartbeat()) {
            heartBeat++;

            try {
                Thread.sleep(beat);
            } catch (InterruptedException e) {
                Bukkit.broadcastMessage("HEARTBEAT CATCH:" + heartBeat + e.getMessage());
            }
        }

        stopThread();
    }

    public void stopThread() {
        Thread.currentThread().interrupt();
    }

    public static int getHearbeatCount(){
        return currentBeats;
    }

    @Override
    public String toString(){
        return  "[Heartbeat:" + myBeat + ":" + this.task.toString() + " ]";
    }
}
