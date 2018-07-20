package com.l2jserver.gameserver.loginbonus;


public class LoginBonusRecord {

    private int ownerId;
    private LoginBonusType bonusType;
    private long lastBonusTimeInMs;

    public LoginBonusRecord(int ownerId, LoginBonusType bonusType, long lastBonusTimeInMs) {
        this.ownerId = ownerId;
        this.bonusType = bonusType;
        this.lastBonusTimeInMs = lastBonusTimeInMs;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public LoginBonusType getBonusType() {
        return bonusType;
    }

    public long getLastBonusTimeInMs() {
        return lastBonusTimeInMs;
    }

    @Override
    public String toString() {
        return "LoginBonusRecord{" +
                "ownerId=" + ownerId +
                ", bonusType='" + bonusType + '\'' +
                ", lastBonusTimeInMs=" + lastBonusTimeInMs +
                '}';
    }

}
