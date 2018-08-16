package handlers.communityboard.custom;

import java.util.List;

public class BuffList {

    private final String name;
    private final List<BuffPrice> buffs;

    public BuffList(String name, List<BuffPrice> buffs) {
        this.name = name;
        this.buffs = buffs;
    }

    public String getName() {
        return name;
    }

    public List<BuffPrice> getBuffs() {
        return buffs;
    }

    @Override
    public String toString() {
        return "BuffList{" +
                "name='" + name + '\'' +
                ", buffs=" + buffs +
                '}';
    }

}
