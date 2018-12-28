package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class AllDynamicDropStatsTest {

    @Test
    void testYamlMapping() throws Exception {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        InputStream configStream = new FileInputStream(new File("./dist/game/config/gameplay/DynamicDropRates.yml"));
        assertThat(configStream).isNotNull();

        AllDynamicDropData allDynamicDropData = yamlMapper.readValue(
                configStream,
                AllDynamicDropData.class
        );

        assertThat(allDynamicDropData.getMobs().getEquipment().getWeapon(new GradeInfo(Grade.A, GradeCategory.LOW))).isNotEmpty();
    }

}