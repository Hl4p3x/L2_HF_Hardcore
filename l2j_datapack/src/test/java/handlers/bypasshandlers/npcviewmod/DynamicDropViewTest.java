package handlers.bypasshandlers.npcviewmod;

import com.l2jserver.gameserver.model.actor.templates.drop.stats.basic.DropStats;
import handlers.bypasshandlers.npcviewmod.render.ItemGroupView;
import handlers.bypasshandlers.npcviewmod.render.ItemView;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicDropViewTest {

    @org.junit.jupiter.api.Test
    void renderItems() {
        DynamicDropView dynamicDropView = new DynamicDropView();

        List<ItemGroupView> testItems = Arrays.asList(
                new ItemGroupView("Weapons - Test", Arrays.asList(
                        new ItemView(1, "Sabre", "sabre.icon", DropStats.empty()),
                        new ItemView(2, "Pike", "pike.icon", DropStats.empty())
                )),
                new ItemGroupView("Armor - Test", Arrays.asList(
                        new ItemView(3, "Blue Wolf", "bw.icon", DropStats.empty()),
                        new ItemView(4, "Avadon", "ava.icon", DropStats.empty())
                )),
                new ItemGroupView("Jewels - Test", Arrays.asList(
                        new ItemView(5, "Black Ore", "bo.icon", DropStats.empty()),
                        new ItemView(6, "Major Ring", "mj.icon", DropStats.empty())
                ))
        );

        String result = dynamicDropView.renderItems(1, 3, testItems);
        assertThat(result).contains("Pike");
        assertThat(result).contains("Blue Wolf");
        assertThat(result).contains("Avadon");
        assertThat(result).doesNotContain("Sabre");
        assertThat(result).doesNotContain("Black Ore");
        assertThat(result).doesNotContain("Major Ring");
    }
}