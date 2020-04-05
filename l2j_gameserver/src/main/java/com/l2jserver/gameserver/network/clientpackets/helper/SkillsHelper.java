package com.l2jserver.gameserver.network.clientpackets.helper;

import com.l2jserver.Config;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2FishermanInstance;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.AcquireSkillType;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.CommonSkill;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.AcquireSkillDone;
import com.l2jserver.gameserver.network.serverpackets.ExStorageMaxCount;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

public class SkillsHelper {


    /**
     * Perform a simple check for current player and skill.<br> Takes the needed SP if the skill require it and all
     * requirements are meet.<br> Consume required items if the skill require it and all requirements are meet.<br>
     *
     * @param player  the skill learning player.
     * @param trainer the skills teaching Npc.
     * @param s       the skill to be learn.
     * @return {@code true} if all requirements are meet, {@code false} otherwise.
     */
    public static boolean checkPlayerSkill(SkillLearnData skillLearnData, L2PcInstance player, L2Npc trainer,
                                           L2SkillLearn s) {
        if (s != null) {
            if (s.getSkillId() == skillLearnData.getId() && (s.getSkillLevel() == skillLearnData.getLevel())) {
                // Hack check.
                if (s.getGetLevel() > player.getLevel()) {
                    player.sendPacket(SystemMessageId.YOU_DONT_MEET_SKILL_LEVEL_REQUIREMENTS);
                    Util.handleIllegalPlayerAction(player,
                            "Player " + player.getName() + ", level " + player.getLevel() + " is requesting skill Id: "
                                    + skillLearnData.getId() + " level " + skillLearnData.getLevel()
                                    + " without having minimum required level, " + s.getGetLevel()
                                    + "!", IllegalActionPunishmentType.NONE);
                    return false;
                }

                // First it checks that the skill require SP and the player has enough SP to learn it.
                final int levelUpSp = s.getCalculatedLevelUpSp(player.getClassId(), player.getLearningClass());
                if ((levelUpSp > 0) && (levelUpSp > player.getSp())) {
                    player.sendPacket(SystemMessageId.NOT_ENOUGH_SP_TO_LEARN_SKILL);
                    showSkillList(skillLearnData.getSkillType(), trainer, player);
                    return false;
                }

                if (!Config.DIVINE_SP_BOOK_NEEDED && (skillLearnData.getId() == CommonSkill.DIVINE_INSPIRATION
                        .getId())) {
                    return true;
                }

                // Check for required skills.
                if (!s.getPreReqSkills().isEmpty()) {
                    for (SkillHolder skill : s.getPreReqSkills()) {
                        if (player.getSkillLevel(skill.getSkillId()) != skill.getSkillLvl()) {
                            if (skill.getSkillId() == CommonSkill.ONYX_BEAST_TRANSFORMATION.getId()) {
                                player.sendPacket(SystemMessageId.YOU_MUST_LEARN_ONYX_BEAST_SKILL);
                            } else {
                                player.sendPacket(SystemMessageId.ITEM_OR_PREREQUISITES_MISSING_TO_LEARN_SKILL);
                            }
                            return false;
                        }
                    }
                }

                // Check for required items.
                if (!s.getRequiredItems().isEmpty()) {
                    // Then checks that the player has all the items
                    long reqItemCount = 0;
                    for (ItemHolder item : s.getRequiredItems()) {
                        reqItemCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
                        if (reqItemCount < item.getCount()) {
                            // Player doesn't have required item.
                            player.sendPacket(SystemMessageId.ITEM_OR_PREREQUISITES_MISSING_TO_LEARN_SKILL);
                            showSkillList(skillLearnData.getSkillType(), trainer, player);
                            return false;
                        }
                    }
                    // If the player has all required items, they are consumed.
                    for (ItemHolder itemIdCount : s.getRequiredItems()) {
                        if (!player
                                .destroyItemByItemId("SkillLearn", itemIdCount.getId(), itemIdCount.getCount(), trainer,
                                        true)) {
                            Util.handleIllegalPlayerAction(player,
                                    "Somehow player " + player.getName() + ", level " + player.getLevel()
                                            + " lose required item Id: " + itemIdCount.getId()
                                            + " to learn skill while learning skill Id: " + skillLearnData.getId() + " level "
                                            + skillLearnData.getLevel()
                                            + "!", IllegalActionPunishmentType.NONE);
                        }
                    }
                }
                // If the player has SP and all required items then consume SP.
                if (levelUpSp > 0) {
                    player.setSp(player.getSp() - levelUpSp);
                    final StatusUpdate su = new StatusUpdate(player);
                    su.addAttribute(StatusUpdate.SP, player.getSp());
                    player.sendPacket(su);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Add the skill to the player and makes proper updates.
     *
     * @param player  the player acquiring a skill.
     * @param trainer the Npc teaching a skill.
     * @param skill   the skill to be learn.
     */
    public static void giveSkill(SkillLearnData skillLearnData, L2PcInstance player, L2Npc trainer, Skill skill) {
        // Send message.
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.LEARNED_SKILL_S1);
        sm.addSkillName(skill);
        player.sendPacket(sm);

        player.sendPacket(new AcquireSkillDone());

        player.addSkill(skill, true);
        player.sendSkillList();

        player.updateShortCuts(skillLearnData.getId(), skillLearnData.getLevel());
        showSkillList(skillLearnData.getSkillType(), trainer, player);

        // If skill is expand type then sends packet:
        if ((skillLearnData.getId() >= 1368) && (skillLearnData.getId() <= 1372)) {
            player.sendPacket(new ExStorageMaxCount(player));
        }

        // Notify scripts of the skill learn.
        EventDispatcher
                .getInstance()
                .notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, skillLearnData.getSkillType()), trainer);
    }

    /**
     * Wrapper for returning the skill list to the player after it's done with current skill.
     *
     * @param trainer the Npc which the {@code player} is interacting
     * @param player  the active character
     */
    public static void showSkillList(AcquireSkillType skillType, L2Npc trainer, L2PcInstance player) {
        if ((skillType == AcquireSkillType.TRANSFORM) || (skillType
                == AcquireSkillType.SUBCLASS) || (skillType
                == AcquireSkillType.TRANSFER) || (skillType == AcquireSkillType.MASTERY)) {// Managed in Datapack.
            return;
        }

        if (trainer instanceof L2FishermanInstance) {
            L2FishermanInstance.showFishSkillList(player);
        } else {
            L2NpcInstance.showSkillList(player, trainer, player.getLearningClass());
        }
    }

}
