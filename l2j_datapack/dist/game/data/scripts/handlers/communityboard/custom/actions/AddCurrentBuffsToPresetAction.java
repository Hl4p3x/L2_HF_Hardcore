package handlers.communityboard.custom.actions;

import com.l2jserver.Config;
import com.l2jserver.common.util.StringUtil;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CommunityBuffList;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.localization.Strings;
import handlers.communityboard.custom.ActionArgs;
import handlers.communityboard.custom.BoardAction;
import handlers.communityboard.custom.ProcessResult;
import handlers.communityboard.custom.bufflists.sets.AllBuffs;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddCurrentBuffsToPresetAction implements BoardAction {

    private static final Logger LOG = LoggerFactory.getLogger(AddPresetBuffAction.class);

    @Override
    public ProcessResult process(L2PcInstance player, ActionArgs args) {
        if (args.isEmpty() || args.getArgs().size() != 1) {
            LOG.warn("Invalid add current buffs to preset request from {} with args {}", player, args);
            ProcessResult.failure(Strings.of(player).get("invalid_request"));
        }

        String presetName = args.getArgs().get(0);
        if (StringUtil.isBlank(presetName)) {
            ProcessResult.failure(Strings.of(player).get("preset_id_cannot_be_empty"));
        }

        Optional<CommunityBuffList> buffList = DAOFactory.getInstance().getCommunityBuffListDao().findSingleCommunityBuffSet(player.getObjectId(), presetName);
        if (buffList.isEmpty()) {
            LOG.warn("Player {} is trying to add buffs to list that does not exist or does not belong to him {}", player, presetName);
            return ProcessResult.success();
        }

        List<BuffInfo> currentPlayerBuffs = new ArrayList<>(player.getEffectList().getBuffs());
        List<BuffInfo> currentPlayerDances = new ArrayList<>(player.getEffectList().getDances());

        Set<SkillHolder> currentPlayerBuffSkills = currentPlayerBuffs.stream().map(buff -> new SkillHolder(buff.getSkill().getId(), buff.getSkill().getLevel())).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<SkillHolder> currentPlayerDanceSkills = currentPlayerDances.stream().map(buff -> new SkillHolder(buff.getSkill().getId(), buff.getSkill().getLevel())).collect(Collectors.toCollection(LinkedHashSet::new));

        Set<SkillHolder> allCurrentPlayerBuffs = new LinkedHashSet<>();
        allCurrentPlayerBuffs.addAll(currentPlayerBuffSkills);
        allCurrentPlayerBuffs.addAll(currentPlayerDanceSkills);

        Set<SkillHolder> availableBuffs = new LinkedHashSet<>(new AllBuffs().getBuffs());
        allCurrentPlayerBuffs.retainAll(availableBuffs);

        Set<SkillHolder> buffsAlreadyInList = new LinkedHashSet<>(buffList.get().getSkills());
        allCurrentPlayerBuffs.removeAll(buffsAlreadyInList);

        for (SkillHolder buff : allCurrentPlayerBuffs) {
            if (buffList.get().getSkills().size() < Config.MAX_CUSTOM_PRESET_BUFFS) {
                DAOFactory.getInstance().getCommunityBuffListDao().addToCommunityBuffList(buffList.get().getId(), buff);
            } else {
                break;
            }
        }

        CommunityBoardHandler.getInstance().handleParseCommand("_bbs_buff update_preset " + buffList.get().getName(), player);
        return ProcessResult.success();
    }

}
