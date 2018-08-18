package handlers.communityboard.custom.bufflists.sets;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import handlers.communityboard.custom.bufflists.BuffFilter;
import handlers.communityboard.custom.bufflists.BuffList;

import java.util.*;

public class AllBuffs implements BuffList {

    @Override
    public List<SkillHolder> getBuffs() {
        List<SkillHolder> allBuffs = new ArrayList<>();
        allBuffs.addAll(new Dominator().getBuffs());
        allBuffs.addAll(new Doomcryer().getBuffs());
        allBuffs.addAll(new ElvenSaint().getBuffs());
        allBuffs.addAll(new Hierophant().getBuffs());
        allBuffs.addAll(new ShillenSaint().getBuffs());
        allBuffs.addAll(new SpectralDancer().getBuffs());
        allBuffs.addAll(new SwordMuse().getBuffs());
        allBuffs.addAll(new WarsmithAndSummoners().getBuffs());
        return allBuffs;
    }

    @Override
    public Optional<SkillHolder> findBySkillId(int skillId) {
        return BuffFilter.findBySkillId(getBuffs(), skillId);
    }

    public static Map<String, BuffList> getBuffMap() {
        Map<String, BuffList> buffs = new HashMap<>();
        buffs.put("overlord", new Dominator());
        buffs.put("warcryer", new Doomcryer());
        buffs.put("elven_elder", new ElvenSaint());
        buffs.put("shillen_elder", new ShillenSaint());
        buffs.put("warsmith_summoners", new WarsmithAndSummoners());
        buffs.put("songs", new SwordMuse());
        buffs.put("dances", new SpectralDancer());
        buffs.put("prophet", new Hierophant());
        buffs.put("dominator", new Dominator());
        return buffs;
    }

}
