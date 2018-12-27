package custom.tutorials;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.quest.Quest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class LevelledServerGuide extends Quest {

    public static final int SERVER_GUIDE_QUEST_ID = 200001;

    private final List<LevelledTutorial> TUTORIALS_BY_LEVEL = Arrays.asList(
            new LevelledTutorial(this, 1, 1, "buff_tutorial.html"),
            new LevelledTutorial(this, 2, 2, "fighting_tutorial.html"),
            new LevelledTutorial(this, 3, 3, "drop_tutorial.html"),
            new LevelledTutorial(this, 4, 5, "start_locations_tutorial.html"),
            new LevelledTutorial(this, 5, 7, "town_navigation_tutorial.html"),
            new LevelledTutorial(this, 6, 10, "first_profession_tutorial.html"),
            new LevelledTutorial(this, 7, 12, "hunting_grounds_tutorial.html"),
            new LevelledTutorial(this, 25, 14, "level_scaling_tutorial.html"),
            new LevelledTutorial(this, 8, 20, "craft_tutorial.html"),
            new LevelledTutorial(this, 9, 24, "crystallization_tutorial.html"),
            new LevelledTutorial(this, 10, 28, "second_profession_tutorial.html"),
            new LevelledTutorial(this, 11, 32, "raid_boss_tutorial.html"),
            new LevelledTutorial(this, 12, 36, "player_drop_tutorial.html"),
            new LevelledTutorial(this, 13, 38, "seven_signs_tutorial.html"),
            new LevelledTutorial(this, 14, 42, "soul_crystal_tutorial.html"),
            new LevelledTutorial(this, 15, 48, "augmentation_tutorial.html"),
            new LevelledTutorial(this, 16, 52, "mammon_tutorial.html"),
            new LevelledTutorial(this, 17, 58, "third_profession_tutorial.html"),
            new LevelledTutorial(this, 18, 74, "attribute_tutorial.html"),
            new LevelledTutorial(this, 19, 76, "subclass_tutorial.html"),
            new LevelledTutorial(this, 20, 77, "dualcraft_tutorial.html"),
            new LevelledTutorial(this, 21, 78, "nobless_tutorial.html"),
            new LevelledTutorial(this, 22, 80, "siege_tutorial.html"),
            new LevelledTutorial(this, 23, 81, "olympiad_tutorial.html"),
            new LevelledTutorial(this, 24, 82, "epic_tutorial.html")
    );

    public LevelledServerGuide() {
        super(SERVER_GUIDE_QUEST_ID, LevelledServerGuide.class.getSimpleName(), "custom/tutorials");

        Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LEVEL_CHANGED, (Consumer<OnPlayerLevelChanged>) this::onPlayerLevelChanged, this));
    }

    private void onPlayerLevelChanged(OnPlayerLevelChanged onPlayerLevelChanged) {
        if (onPlayerLevelChanged.hasLevelIncreased()) {
            TUTORIALS_BY_LEVEL.stream()
                    .filter(levelledTutorial -> onPlayerLevelChanged.getNewLevel() >= levelledTutorial.level())
                    .forEach(levelledTutorial -> {
                        L2PcInstance player = onPlayerLevelChanged.getActiveChar();
                        if (levelledTutorial.hasNotBeenShown(player)) {
                            levelledTutorial.start(player);
                        }
                    });
        }
    }

    public static void main(String[] args) {
        new LevelledServerGuide();
    }

}
