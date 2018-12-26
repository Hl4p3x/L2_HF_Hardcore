package custom.tutorials;

import com.l2jserver.gameserver.model.quest.Quest;

import java.util.Map;

public class LevelledServerGuide extends Quest {

    public static final int SERVER_GUIDE_QUEST_ID = 200001;

    private static final Map<Integer, Tutorial> TUTORIALS_BY_LEVEL = Map.ofEntries(
            Map.entry(1, new BuffTutorial())
          /*  Map.entry(3, new DropTutorial()),
            Map.entry(5, new StartLocationsTutorial()),
            Map.entry(7, new TownNavigationTutorial()),
            Map.entry(10, new FirstProfessionTutorial()),
            Map.entry(12, new HuntingGroundsTutorial()),
            Map.entry(20, new CraftTutorial()),
            Map.entry(25, new CrystallizationTutorial()),
            Map.entry(28, new SecondProfessionTutorial()),
            Map.entry(32, new RaidbossesTutorial()),
            Map.entry(36, new PlayerDropTutorial()),
            Map.entry(38, new SevenSignsTutorial()),
            Map.entry(42, new SoulCrystalsTutorial()),
            Map.entry(48, new AugmentationTutorial()),
            Map.entry(52, new ThirdProfessionTutorial()),
            Map.entry(74, new AttributeTutorial()),
            Map.entry(76, new SubclassTutorial()),
            Map.entry(78, new NoblessTutorial()),
            Map.entry(80, new SiegeTutorial()),
            Map.entry(81, new OlympiadTutorial()),
            Map.entry(84, new EpicTutorial())*/
    );

    public LevelledServerGuide() {
        super(SERVER_GUIDE_QUEST_ID, LevelledServerGuide.class.getSimpleName(), "custom/tutorials");
    }

    public static void main(String[] args) {
        new LevelledServerGuide();
    }

}
