/*
 * Copyright (C) 2004-2016 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.bypasshandlers;

import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

public class SupportMagic implements IBypassHandler {

	private static final String[] COMMANDS = {
		"supportmagicservitor",
		"supportmagic"
	};

	private static final SkillHolder[] FIGHTER_BUFFS = {
		new SkillHolder(4327, 1), // Haste
		new SkillHolder(4322, 1), // Wind Walk
		new SkillHolder(4323, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(4324, 1), // Bless the Body
		new SkillHolder(4325, 1), // Vampiric Rage
		new SkillHolder(4326, 1), // Regeneration
	};
	private static final SkillHolder[] MAGE_BUFFS = {
		new SkillHolder(4322, 1), // Wind Walk
		new SkillHolder(4323, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(4328, 1), // Bless the Soul
		new SkillHolder(4329, 1), // Acumen
		new SkillHolder(4330, 1), // Concentration
		new SkillHolder(4331, 1), // Empower
	};
	private static final SkillHolder[] SUMMON_BUFFS = {
		new SkillHolder(4327, 1), // Haste
		new SkillHolder(4322, 1), // Wind Walk
		new SkillHolder(4323, 1), // Shield
		new SkillHolder(5637, 1), // Magic Barrier
		new SkillHolder(4324, 1), // Bless the Body
		new SkillHolder(4325, 1), // Vampiric Rage
		new SkillHolder(4326, 1), // Regeneration
		new SkillHolder(4328, 1), // Bless the Soul
		new SkillHolder(4329, 1), // Acumen
		new SkillHolder(4330, 1), // Concentration
		new SkillHolder(4331, 1), // Empower
	};

	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc() || activeChar.isCursedWeaponEquipped()) {
			return false;
		}

		if (command.equalsIgnoreCase(COMMANDS[0])) {
			makeSupportMagic(activeChar, (L2Npc) target, true);
		} else if (command.equalsIgnoreCase(COMMANDS[1])) {
			makeSupportMagic(activeChar, (L2Npc) target, false);
		}
		return true;
	}

	private static void makeSupportMagic(L2PcInstance player, L2Npc npc, boolean isSummon) {
		if (isSummon && !player.hasServitor()) {
			npc.showChatWindow(player, "data/html/default/SupportMagicNoSummon.htm");
			return;
		}

		if (player.getClassId().level() > 1) {
			player.sendMessage(
				"Only adventurers who have not completed their 2nd class transfer may receive these buffs.");
			return;
		}

		if (isSummon) {
			npc.setTarget(player.getSummon());
			for (SkillHolder skill : SUMMON_BUFFS) {
				npc.doCast(skill.getSkill());
			}
		} else {
			npc.setTarget(player);
			if (player.isInCategory(CategoryType.NOVICE_MAGE)) {
				for (SkillHolder skill : MAGE_BUFFS) {
					npc.doCast(skill.getSkill());
				}
			} else {
				for (SkillHolder skill : FIGHTER_BUFFS) {
					npc.doCast(skill.getSkill());
				}
			}
		}
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}

}