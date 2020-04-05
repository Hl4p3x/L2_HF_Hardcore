package com.l2jserver.gameserver.model.stats;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Formulas test.
 *
 * @author Zoey76
 */
public class FormulasTest {

    private static final int HP_REGENERATE_PERIOD_CHARACTER = 3000;

    private static final int HP_REGENERATE_PERIOD_DOOR = 300000;

    @Mock
    private L2Character character;

    @Mock
    private Skill skill;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRegeneratePeriod() {
        Mockito.when(character.isDoor()).thenReturn(true);

        assertEquals(Formulas.getRegeneratePeriod(character), HP_REGENERATE_PERIOD_DOOR);

        Mockito.when(character.isDoor()).thenReturn(false);

        assertEquals(Formulas.getRegeneratePeriod(character), HP_REGENERATE_PERIOD_CHARACTER);
    }

    @Test
    public void testCalcAtkSpd() {
        testCalcAtkSpd(0, true, 1, false, false, 0, 0.0, false, false, 0.0);
        testCalcAtkSpd(0, true, 0, false, false, 0, 0.0, false, false, Double.NaN);
        testCalcAtkSpd(0, false, 1, false, false, 0, 0.0, false, false, Double.NaN);
        testCalcAtkSpd(0, false, 0, false, true, 500, 0.0, false, false, 0.0);
        testCalcAtkSpd(600, false, 0, false, true, 500, 0.0, false, false, 500.0);
        testCalcAtkSpd(3000, false, 0, false, true, 600, 0.0, false, false, 1665.0);
        testCalcAtkSpd(0, false, 0, false, false, 0, 500.0, false, false, 0.0);
        testCalcAtkSpd(600, false, 0, false, false, 0, 500.0, false, false, 500.0);
        testCalcAtkSpd(3000, false, 0, false, false, 0, 600.0, false, false, 1665.0);
        testCalcAtkSpd(1400, false, 0, false, true, 0, 0.0, true, false, 2.147483647E9);
        testCalcAtkSpd(1400, false, 0, false, true, 0, 0.0, false, true, 2.147483647E9);
        testCalcAtkSpd(1400, false, 0, true, true, 0, 0.0, true, false, 1000.0);
        testCalcAtkSpd(1400, false, 0, true, true, 0, 0.0, false, true, 1000.0);
    }

    public void testCalcAtkSpd(int hitTime, boolean isChanneling, int channelingSkillId, boolean isStatic,
                               boolean isMagic, int mAtkSpeed, double pAtkSpeed, boolean isChargedSpiritshots,
                               boolean isChargedBlessedSpiritShots, double expected) {
        Mockito.when(skill.getHitTime()).thenReturn(hitTime);
        Mockito.when(skill.isChanneling()).thenReturn(isChanneling);
        Mockito.when(skill.getChannelingSkillId()).thenReturn(channelingSkillId);
        Mockito.when(skill.isStatic()).thenReturn(isStatic);
        Mockito.when(skill.isMagic()).thenReturn(isMagic);
        Mockito.when(character.getMAtkSpd()).thenReturn(mAtkSpeed);
        Mockito.when(character.getPAtkSpd()).thenReturn(pAtkSpeed);
        Mockito.when(character.isChargedShot(ShotType.SPIRITSHOTS)).thenReturn(isChargedSpiritshots);
        Mockito.when(character.isChargedShot(ShotType.BLESSED_SPIRITSHOTS)).thenReturn(isChargedBlessedSpiritShots);

        assertEquals(Formulas.calcCastTime(character, skill), expected);
    }

}
