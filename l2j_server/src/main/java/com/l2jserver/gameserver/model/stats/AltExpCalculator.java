package com.l2jserver.gameserver.model.stats;

import com.l2jserver.Config;

public class AltExpCalculator {

    public static long[] calculateExpAndSp(long expReward, int spReward, int dmgDone, long totalDamage) {
        return new long[]{
                calculateProgressStat(expReward, dmgDone, totalDamage, Config.ALT_GAME_EXPONENT_XP),
                calculateProgressStat(spReward, dmgDone, totalDamage, Config.ALT_GAME_EXPONENT_SP)
        };
    }

    public static long calculateProgressStat(long reward, int dmgDone, long totalDamage, float exponent) {
        double progressStat = ((double) reward * dmgDone) / totalDamage;
        if (exponent != 0) {
            progressStat *= Math.pow(2., exponent);
        }
        return (int) progressStat;
    }

}

