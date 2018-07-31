package ai;

import ai.npc.AbstractNpcAI;
import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Npc;

public class IdleBossManager {

    private long lastActionTime = 0;
    private AbstractNpcAI npcAI;

    private long checkDelay = Config.CHECK_ACTIVITY_DELAY;
    private long inactivityThreshold;

    private static final long MINUTE_IN_MS = 60000;

    private String checkActivityTimerName = "CHECK_ACTIVITY";

    private Runnable onIdle;

    private L2Npc controllerNpc;

    public IdleBossManager(AbstractNpcAI npcAI, long inactivityThreshold, Runnable onIdle) {
        this.npcAI = npcAI;
        this.inactivityThreshold = inactivityThreshold * MINUTE_IN_MS;
        this.onIdle = onIdle;
    }

    public IdleBossManager(AbstractNpcAI npcAI, long checkDelay, long inactivityThreshold,
        String checkActivityTimerName, Runnable onIdle, L2Npc controllerNpc) {
        this.npcAI = npcAI;
        this.checkDelay = checkDelay;
        this.inactivityThreshold = inactivityThreshold * MINUTE_IN_MS;
        this.checkActivityTimerName = checkActivityTimerName;
        this.onIdle = onIdle;
        this.controllerNpc = controllerNpc;
    }

    public IdleBossManager updateLastActionTime() {
        lastActionTime = System.currentTimeMillis();
        return this;
    }

    public String getCheckActivityTimerName() {
        return checkActivityTimerName;
    }

    public long getLastActionTime() {
        return lastActionTime;
    }

    public IdleBossManager startCheckTimer() {
        npcAI.startQuestTimer(checkActivityTimerName, checkDelay, controllerNpc, null);
        return this;
    }

    public IdleBossManager cancelCheckTimer() {
        npcAI.cancelQuestTimer(checkActivityTimerName, controllerNpc, null);
        return this;
    }

    public IdleBossManager handleCheck() {
        if (System.currentTimeMillis() - getLastActionTime() > inactivityThreshold) {
            npcAI.cancelQuestTimer(checkActivityTimerName, controllerNpc, null);
            onIdle.run();
        } else {
            startCheckTimer();
        }
        return this;
    }

}
