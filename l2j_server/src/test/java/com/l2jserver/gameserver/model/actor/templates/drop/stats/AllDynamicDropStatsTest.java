package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountPair;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.ChanceCountStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.NewEquipmentDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.equipment.WeaponArmorJewelStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourceDropData;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.resources.ResourcesStats;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.scrolls.*;
import com.l2jserver.gameserver.model.items.craft.ResourceGrade;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AllDynamicDropStatsTest {

    @Test
    void testYamlMapping() throws Exception {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        InputStream configStream = new FileInputStream(new File("./dist/game/config/DynamicDropRates.yml"));
        assertThat(configStream).isNotNull();

        AllDynamicDropStats allDynamicDropStats = yamlMapper.readValue(
                configStream,
                AllDynamicDropStats.class
        );

        assertThat(allDynamicDropStats.getMobs().getEquipment().getArmor().getChances()).isNotEmpty();

        NewAllDynamicDropData newAllDynamicDropData = new NewAllDynamicDropData(
                convertDropData(allDynamicDropStats.getMobs()),
                convertDropData(allDynamicDropStats.getRaid())
        );

        yamlMapper.writeValue(new File("./dist/game/config/DynamicDropRates_new.yml"), newAllDynamicDropData);
    }

    private NewDynamicDropData convertDropData(DynamicDropStats dynamicDropStats) {
        return new NewDynamicDropData(
                convertEquipmentStats(dynamicDropStats.getEquipment()),
                convertEquipmentStats(dynamicDropStats.getParts()),
                convertEquipmentStats(dynamicDropStats.getRecipes()),
                convertResources(dynamicDropStats.getResources()),
                convertScrollDropData(dynamicDropStats.getScrolls())
        );
    }

    private NewAllScrollsDropData convertScrollDropData(AllScrollsStats allScrollsStats) {
        return new NewAllScrollsDropData(
                convertScrollData(allScrollsStats.getWeapon()),
                convertScrollData(allScrollsStats.getArmor()),
                allScrollsStats.getMisc()
        );
    }

    private Map<ScrollGrade, ChanceCountPair> convertScrollChanceCountStats(ScrollChanceCountStats scrollChanceCountStats) {
        Set<ScrollGrade> allGradeInfos = new LinkedHashSet<>();
        allGradeInfos.addAll(scrollChanceCountStats.getChance().keySet());
        allGradeInfos.addAll(scrollChanceCountStats.getCount().keySet());

        Map<ScrollGrade, ChanceCountPair> result = new LinkedHashMap<>();
        for (ScrollGrade gradeInfo : allGradeInfos) {
            result.put(gradeInfo, new ChanceCountPair(scrollChanceCountStats.getChance().get(gradeInfo), scrollChanceCountStats.getCount().get(gradeInfo)));
        }

        return result;
    }

    private ScrollDropData convertScrollData(ScrollsStats scrollsStats) {
        return new ScrollDropData(
                convertScrollChanceCountStats(scrollsStats.getNormal()),
                convertScrollChanceCountStats(scrollsStats.getBlessed())
        );
    }

    private ResourceDropData convertResources(ResourcesStats resources) {
        return new ResourceDropData(
                resources.getOvergradeMultiplier(),
                convertResourceStatsToPairs(resources)
        );
    }

    private NewEquipmentDropData convertEquipmentStats(WeaponArmorJewelStats weaponArmorJewelStats) {
        return new NewEquipmentDropData(
                convertStatsToPairs(weaponArmorJewelStats.getWeapon()),
                convertStatsToPairs(weaponArmorJewelStats.getArmor()),
                convertStatsToPairs(weaponArmorJewelStats.getJewels())
        );
    }

    private Map<ResourceGrade, ChanceCountPair> convertResourceStatsToPairs(ResourcesStats chanceCountStats) {
        Set<ResourceGrade> allGradeInfos = new LinkedHashSet<>();
        allGradeInfos.addAll(chanceCountStats.getChance().keySet());
        allGradeInfos.addAll(chanceCountStats.getCount().keySet());

        Map<ResourceGrade, ChanceCountPair> result = new LinkedHashMap<>();
        for (ResourceGrade gradeInfo : allGradeInfos) {
            result.put(gradeInfo, new ChanceCountPair(chanceCountStats.getChance().get(gradeInfo), chanceCountStats.getCount().get(gradeInfo)));
        }

        return result;
    }

    private Map<GradeInfo, ChanceCountPair> convertStatsToPairs(ChanceCountStats chanceCountStats) {
        Set<GradeInfo> allGradeInfos = new LinkedHashSet<>();
        allGradeInfos.addAll(chanceCountStats.getChances().keySet());
        allGradeInfos.addAll(chanceCountStats.getCounts().keySet());

        Map<GradeInfo, ChanceCountPair> result = new LinkedHashMap<>();
        for (GradeInfo gradeInfo : allGradeInfos) {
            result.put(gradeInfo, new ChanceCountPair(chanceCountStats.getChances().get(gradeInfo), chanceCountStats.getCounts().get(gradeInfo)));
        }

        return result;
    }

}