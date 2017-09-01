package com.medievallords.carbyne.war.object;

import com.medievallords.carbyne.Carbyne;
import com.medievallords.carbyne.utils.MessageManager;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Dalton on 8/8/2017.
 */
public class WarPlot {

    //private WarManager warManager = Carbyne.getInstance().getWarManager();

    private TownBlock warBlock;
    private Town attackingTown, defendingTown;
    private Nation attackingNation, defendingNation;
    private BukkitRunnable task;

    private int defenderPoints = 0, attackerPoints = 0;

    public WarPlot(TownBlock warBlock, Town attackingTown, Town defendingTown, Nation attackingNation, Nation defendingNation) {
        this.warBlock = warBlock;
        this.attackingNation = attackingNation;
        this.defendingNation = defendingNation;
        this.attackingNation = attackingNation;
        this.defendingNation = defendingNation;

        start();
    }

    public void start() {
        String msg = new String("&bThe nation of &c%attacknation% &bhas began an assault on the town of &e%defendtown%&b in the nation of &2%defendnation%&b!")
                .replace("%attacknation%", attackingNation.getName()).replace("%defendtown%", defendingTown.getName()).replace("%defendnation%", defendingNation.getName());
        MessageManager.broadcastMessage(msg);

        warBlock.setType(TownBlockType.WILDS);
        warBlock.setLocked(true);

        task = new BukkitRunnable() {
            public void run() {
                /*if(attackerPoints >= warManager.getPOINTS_TO_WIN_ATTACKERS())
                    win(1);
                else if(defenderPoints >= warManager.getPOINTS_TO_WIN_DEFENDERS())
                    win(2);*/
            }
        };
        task.runTaskTimerAsynchronously(Carbyne.getInstance(), 0L, 20L);
    }

    public void tick() {

    }

    public void stop() {
        //warManager.getNationData().get(attackingNation).isAttacking = false;
        task.cancel();
    }

    public void win(final int who) {
        switch (who) {
            case 1: { //Attackers win
                warBlock.setTown(attackingTown);
                String msg = new String("&bThe town of &e%defendtown%&b under the nation of &2%defendnation%&b has lost a battle against the nation of &c%attacknation%&b!")
                        .replace("%defendtown%", defendingTown.getName()).replace("%defendnation%", defendingNation.getName()).replace("%attacknation%", attackingNation.getName());
                MessageManager.broadcastMessage(msg);
                break;
            }
            case 2: { //Defenders win
                String msg = new String("&bThe town of &e%defendtown%&b under the nation of &2%defendnation%&b has defeated the invaders from the nation of &c%attacknation%&b!")
                        .replace("%defendtown%", defendingTown.getName()).replace("%defendnation%", defendingNation.getName()).replace("%attacknation%", attackingNation.getName());
                MessageManager.broadcastMessage(msg);
                break;
            }
            default: { //Defenders win by default
                String msg = new String("&bThe town of %defendtown% &bwas under seige for a long time, so long that the attackers from %attacknation%&b decided to leave and try again later...")
                        .replace("%defendtown%", defendingTown.getName()).replace("%attacknation%", attackingNation.getName());
                MessageManager.broadcastMessage(msg);
                break;
            }
        }
        warBlock.setType(TownBlockType.RESIDENTIAL);
        warBlock.setLocked(false);
    }

}
