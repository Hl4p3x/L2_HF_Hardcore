package com.l2jserver.gameserver.clanbonus;

public class ClanBonus {

    private int clanId;
    private String bonusType;
    private long bonusTime;

    public ClanBonus(int clanId, String bonusType, long bonusTime) {
        this.clanId = clanId;
        this.bonusType = bonusType;
        this.bonusTime = bonusTime;
    }

    public int getClanId() {
        return clanId;
    }

    public String getBonusType() {
        return bonusType;
    }

    public long getBonusTime() {
        return bonusTime;
    }

    @Override
    public String toString() {
        return "ClanBonus{" +
                "clanId=" + clanId +
                ", bonusType='" + bonusType + '\'' +
                ", bonusTime=" + bonusTime +
                '}';
    }

}
