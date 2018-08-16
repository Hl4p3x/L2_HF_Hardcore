package handlers.communityboard.custom.bufflists;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Arrays;
import java.util.List;

public class Doomcryer implements BuffList {

    public final List<SkillHolder> buffs = Arrays.asList(
            new SkillHolder(1007, 3), // Chant of Battle
            new SkillHolder(1009, 3), // Chant of Shielding
            new SkillHolder(1006, 3), // Chant of Fire
            new SkillHolder(1002, 3), // Flame Chant
            new SkillHolder(1535, 1), // Chant of Movement
            new SkillHolder(1518, 1), // Chant of Critical Attack
            new SkillHolder(1519, 1), // Chant of Blood Awakening
            new SkillHolder(1517, 1), // Chant of Combat
            new SkillHolder(1310, 4), // Chant of Vampire
            new SkillHolder(1309, 3), // Chant of Eagle
            new SkillHolder(1308, 3), // Chant of Predator
            new SkillHolder(1284, 3), // Chant of Revenge
            new SkillHolder(1253, 3), // Chant of Rage
            new SkillHolder(1252, 3), // Chant of Evasion
            new SkillHolder(1251, 2), // Chant of Fury
            new SkillHolder(1391, 3), // Earth Chant
            new SkillHolder(1390, 3), // War Chant
            new SkillHolder(1562, 2), // Chant of Berserker
            new SkillHolder(1549, 1), // Chant of Elements
            new SkillHolder(1413, 1), // Magnus' Chant
            new SkillHolder(1461, 1), // Chant of Protection
            new SkillHolder(1363, 1), // Chant of Victory
            new SkillHolder(1362, 1) // Chant of Spirit
    );

    @Override
    public List<SkillHolder> getBuffs() {
        return buffs;
    }
}
