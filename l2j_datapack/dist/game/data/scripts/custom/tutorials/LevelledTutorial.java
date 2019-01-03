package custom.tutorials;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

import java.util.Optional;

public class LevelledTutorial {

    private final int id;
    private final int level;
    private final String htmlFile;
    private final Quest quest;

    public LevelledTutorial(Quest quest, int id, int level, String htmlFile) {
        this.quest = quest;
        this.id = id;
        this.level = level;
        this.htmlFile = htmlFile;
    }

    public int level() {
        return level;
    }

    public void start(L2PcInstance player) {
        quest.setIsCustom(true);
        QuestState questState = quest.getQuestState(player, true);
        questState.showHtmlFile(htmlFile);
        questState.set("shown_" + id, "true");
        player.increaseVitalityLevel();
    }

    public boolean hasNotBeenShown(L2PcInstance player) {
        QuestState questState = quest.getQuestState(player, true);
        return "false".equalsIgnoreCase(Optional.ofNullable(questState.get("shown_" + id)).orElse("false"));
    }

}
