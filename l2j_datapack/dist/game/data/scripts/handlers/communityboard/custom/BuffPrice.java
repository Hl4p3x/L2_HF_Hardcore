package handlers.communityboard.custom;

import com.l2jserver.gameserver.model.skills.Skill;

public class BuffPrice {

    private final Skill skill;
    private final long price;

    public BuffPrice(Skill skill, long price) {
        this.skill = skill;
        this.price = price;
    }

    public Skill getSkill() {
        return skill;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "BuffPrice{" +
                "skill=" + skill +
                ", price=" + price +
                '}';
    }

}
