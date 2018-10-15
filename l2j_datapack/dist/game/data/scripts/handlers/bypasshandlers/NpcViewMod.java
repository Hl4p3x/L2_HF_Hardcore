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

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.*;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.HtmlUtil;
import com.l2jserver.gameserver.util.Util;
import handlers.bypasshandlers.npcviewmod.DropListButtonsView;
import handlers.bypasshandlers.npcviewmod.RegularDropView;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author NosBit
 */
public class NpcViewMod implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"NpcViewMod"
	};

	private static final RegularDropView REGULAR_DROP_VIEW = new RegularDropView();

	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (!st.hasMoreTokens())
		{
			_log.warning("Bypass[NpcViewMod] used without enough parameters.");
			return false;
		}
		
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "view":
			{
				final L2Object target;
				if (st.hasMoreElements())
				{
					try
					{
						target = L2World.getInstance().findObject(Integer.parseInt(st.nextToken()));
					}
					catch (NumberFormatException e)
					{
						return false;
					}
				}
				else
				{
					target = activeChar.getTarget();
				}
				
				final L2Npc npc = target instanceof L2Npc ? (L2Npc) target : null;
				if (npc == null)
				{
					return false;
				}
				
				NpcViewMod.sendNpcView(activeChar, npc);
				break;
			}
			case "droplist":
			{
				if (st.countTokens() < 2)
				{
					_log.warning("Bypass[NpcViewMod] used without enough parameters.");
					return false;
				}
				
				final String dropListScopeString = st.nextToken();
				try
				{
					final DropListScope dropListScope = Enum.valueOf(DropListScope.class, dropListScopeString);
					final L2Object target = L2World.getInstance().findObject(Integer.parseInt(st.nextToken()));
					final L2Npc npc = target instanceof L2Npc ? (L2Npc) target : null;
					if (npc == null)
					{
						return false;
					}
					final int page = st.hasMoreElements() ? Integer.parseInt(st.nextToken()) : 0;
					sendNpcDropList(activeChar, npc, dropListScope, page);
				}
				catch (NumberFormatException e)
				{
					return false;
				}
				catch (IllegalArgumentException e)
				{
					_log.warning("Bypass[NpcViewMod] unknown drop list scope: " + dropListScopeString);
					return false;
				}
				break;
			}
			case "aggrolist": {
				if (st.countTokens() < 1) {
					_log.warning("Bypass[NpcViewMod] used without enough parameters.");
					return false;
				}
				try {
					final L2Object target = L2World.getInstance().findObject(Integer.parseInt(st.nextToken()));
					final L2Attackable npc = target instanceof L2Attackable ? (L2Attackable) target : null;
					if (npc == null) {
						return false;
					}
					sendAggroList(activeChar, npc, "Aggression", (AggroInfo::getHate));
				} catch (NumberFormatException e) {
					return false;
				}
				break;
			}
            case "dmglist": {
                if (st.countTokens() < 1) {
                    _log.warning("Bypass[NpcViewMod] used without enough parameters.");
                    return false;
                }
                try {
                    final L2Object target = L2World.getInstance().findObject(Integer.parseInt(st.nextToken()));
                    final L2Attackable npc = target instanceof L2Attackable ? (L2Attackable) target : null;
                    if (npc == null) {
                        return false;
                    }
                    sendAggroList(activeChar, npc, "Damage", (aggroInfo -> (long) aggroInfo.getDamage()));
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            }
		}
		
		return true;
	}

    private void sendAggroList(L2PcInstance activeChar, L2Attackable npc, String statName, Function<AggroInfo, Long> extractInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
        stringBuilder.append("<title>").append(statName).append("</title>");
		stringBuilder.append("</head>");
		stringBuilder.append("<body>");
		stringBuilder.append("<center>");

		stringBuilder.append("<table width=300 cellpadding=2 cellspacing=2 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td width=\"200\"><font color=\"LEVEL\"><b>Name</b></font></td>");
		stringBuilder.append("<td width=\"100\"><font color=\"LEVEL\"><b>").append(statName).append("</b></font></td>");
		stringBuilder.append("</tr>");


		List<AggroInfo> aggroInfos = npc.getAggroList().values().stream().sorted(Comparator.comparing(extractInfo).reversed()).collect(Collectors.toList());
		long totalValue = aggroInfos.stream().mapToLong(extractInfo::apply).sum();

		aggroInfos.stream().limit(24).forEach(aggro -> {
			stringBuilder.append("<tr>");
			String attackerName = aggro.getAttacker().getName();
			if (attackerName == null) {
                if (aggro.getAttacker() instanceof L2Summon) {
                    L2Summon attackerPet = (L2Summon) aggro.getAttacker();
                    attackerName = attackerPet.getTemplate().getName();
				} else {
					attackerName = "Unknown";
				}
			}

			if (aggro.getAttacker() instanceof L2Summon) {
                L2Summon attackerPet = (L2Summon) aggro.getAttacker();
                attackerName += " (" + attackerPet.getOwner().getName() + ")";
            }

			stringBuilder.append("<td>").append(attackerName).append("</td>");

			long aggroPercentage = 0L;
			if (totalValue != 0) {
				aggroPercentage = extractInfo.apply(aggro) * 100 / totalValue;
			}

			stringBuilder.append("<td>").append(aggroPercentage).append("% (").append(extractInfo.apply(aggro)).append(")</td>");
			stringBuilder.append("</tr>");
		});

		stringBuilder.append("</table>");

        stringBuilder.append("<table>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td>");
        stringBuilder.append("<button value=\"Show Aggression\" width=110 height=25 action=\"bypass NpcViewMod aggroList ")
				.append(npc.getObjectId())
				.append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\">");
        stringBuilder.append("</td>");
        stringBuilder.append("<td>");
        stringBuilder.append("<button value=\"Show Damage\" width=110 height=25 action=\"bypass NpcViewMod dmgList ")
                .append(npc.getObjectId())
                .append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\">");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");

		stringBuilder.append("</center>");
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");

		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setHtml(stringBuilder.toString());
		activeChar.sendPacket(html);
	}

	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
	public static void sendNpcView(L2PcInstance activeChar, L2Npc npc)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), "data/html/mods/NpcView/Info.htm");
		html.replace("%name%", npc.getName());
		html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long) npc.getCurrentHp(), npc.getMaxHp(), false));
		html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long) npc.getCurrentMp(), npc.getMaxMp(), false));
		
		final L2Spawn npcSpawn = npc.getSpawn();
		if ((npcSpawn == null) || (npcSpawn.getRespawnMinDelay() == 0))
		{
			html.replace("%respawn%", "None");
		}
		else
		{
			TimeUnit timeUnit = TimeUnit.MILLISECONDS;
			long min = Long.MAX_VALUE;
			for (TimeUnit tu : TimeUnit.values())
			{
				final long minTimeFromMillis = tu.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
				final long maxTimeFromMillis = tu.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
				if ((TimeUnit.MILLISECONDS.convert(minTimeFromMillis, tu) == npcSpawn.getRespawnMinDelay()) && (TimeUnit.MILLISECONDS.convert(maxTimeFromMillis, tu) == npcSpawn.getRespawnMaxDelay()))
				{
					if (min > minTimeFromMillis)
					{
						min = minTimeFromMillis;
						timeUnit = tu;
					}
				}
			}
			final long minRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
			final long maxRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
			final String timeUnitName = timeUnit.name().charAt(0) + timeUnit.name().toLowerCase().substring(1);
			if (npcSpawn.hasRespawnRandom())
			{
				html.replace("%respawn%", minRespawnDelay + "-" + maxRespawnDelay + " " + timeUnitName);
			}
			else
			{
				html.replace("%respawn%", minRespawnDelay + " " + timeUnitName);
			}
		}
		
		html.replace("%atktype%", Util.capitalizeFirst(npc.getAttackType().name().toLowerCase()));
		html.replace("%atkrange%", npc.getStat().getPhysicalAttackRange());
		
		html.replace("%patk%", (int) npc.getPAtk(activeChar));
		html.replace("%pdef%", (int) npc.getPDef(activeChar));
		
		html.replace("%matk%", (int) npc.getMAtk(activeChar, null));
		html.replace("%mdef%", (int) npc.getMDef(activeChar, null));
		
		html.replace("%atkspd%", npc.getPAtkSpd());
		html.replace("%castspd%", npc.getMAtkSpd());
		
		html.replace("%critrate%", npc.getStat().getCriticalHit(activeChar, null));
		html.replace("%evasion%", npc.getEvasionRate(activeChar));
		
		html.replace("%accuracy%", npc.getStat().getAccuracy());
		html.replace("%speed%", (int) npc.getStat().getMoveSpeed());
		
		html.replace("%attributeatktype%", Elementals.getElementName(npc.getStat().getAttackElement()));
		html.replace("%attributeatkvalue%", npc.getStat().getAttackElementValue(npc.getStat().getAttackElement()));
		html.replace("%attributefire%", npc.getStat().getDefenseElementValue(Elementals.FIRE));
		html.replace("%attributewater%", npc.getStat().getDefenseElementValue(Elementals.WATER));
		html.replace("%attributewind%", npc.getStat().getDefenseElementValue(Elementals.WIND));
		html.replace("%attributeearth%", npc.getStat().getDefenseElementValue(Elementals.EARTH));
		html.replace("%attributedark%", npc.getStat().getDefenseElementValue(Elementals.DARK));
		html.replace("%attributeholy%", npc.getStat().getDefenseElementValue(Elementals.HOLY));

		html.replace("%dropListButtons%", DropListButtonsView.render(npc));
        html.replace("%aggroListButtons%", getAggroAndDmgListButton(npc));
		
		activeChar.sendPacket(html);
	}

    public static String getAggroAndDmgListButton(L2Npc npc) {
		final StringBuilder sb = new StringBuilder();
		if (npc instanceof L2Attackable) {
			L2Attackable attackable = (L2Attackable) npc;
			final Map<L2Character, AggroInfo> aggroList = attackable.getAggroList();
			if ((aggroList != null) && !aggroList.isEmpty()) {
				sb.append("<table width=295 cellpadding=0 cellspacing=0><tr>");
				sb.append("<td align=center><button value=\"Show Aggression\" width=110 height=25 action=\"bypass NpcViewMod aggroList ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
                sb.append("<td align=center><button value=\"Show Damage\" width=110 height=25 action=\"bypass NpcViewMod dmgList ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
				sb.append("</tr></table>");
			}
		}

		return sb.toString();
	}

	public static void sendNpcDropList(L2PcInstance activeChar, L2Npc npc, DropListScope dropListScope, int page) {
		Optional<String> htmlOptional = REGULAR_DROP_VIEW.renderHtml(activeChar, npc, dropListScope, page);
		htmlOptional.ifPresent(html -> Util.sendCBHtml(activeChar, html));
	}

}
