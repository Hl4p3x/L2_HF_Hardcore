package com.l2jserver.gameserver.model.actor.templates.drop.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
    }

}