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
package handlers.admincommandhandlers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.CrestTable;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.data.xml.impl.*;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.WalkingManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.drop.calculators.DynamicDropCalculator;
import com.l2jserver.gameserver.model.actor.templates.drop.stats.DynamicDropTable;
import com.l2jserver.gameserver.scripting.L2ScriptEngineManager;
import com.l2jserver.gameserver.util.Util;

import javax.script.ScriptException;
import java.io.File;
import java.util.StringTokenizer;

/**
 * @author NosBit
 */
public class AdminReload implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_reload"
	};
	
	private static final String RELOAD_USAGE = "Usage: //reload <config|access|npc|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|effect|handler|enchant>";
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		if (actualCommand.equalsIgnoreCase("admin_reload"))
		{
			if (!st.hasMoreTokens())
			{
				AdminHtml.showAdminHtml(activeChar, "reload.htm");
				activeChar.sendMessage(RELOAD_USAGE);
				return true;
			}
			
			final String type = st.nextToken();
			switch (type.toLowerCase())
			{
				case "dynamic_drop_rates": {
					DynamicDropTable.getInstance().load();
					DynamicDropCalculator.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Dynamic Drop Rates." );
					break;
				}
				case "dynamic_drop": {
					DynamicDropCalculator.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Dynamic Drop Data.");
                    break;
                }
				case "config":
				{
					Config.load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Configs.");
					break;
				}
				case "access":
				{
					AdminData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Access.");
					break;
				}
				case "npc":
				{
					NpcData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Npcs.");
					break;
				}
				case "quest":
				{
					if (st.hasMoreElements())
					{
						String value = st.nextToken();
						if (!Util.isDigit(value))
						{
							QuestManager.getInstance().reload(value);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest Name:" + value + ".");
						}
						else
						{
							final int questId = Integer.parseInt(value);
							QuestManager.getInstance().reload(questId);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quest ID:" + questId + ".");
						}
					}
					else
					{
						QuestManager.getInstance().reloadAllScripts();
						activeChar.sendMessage("All scripts have been reloaded.");
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Quests.");
					}
					break;
				}
				case "walker":
				{
					WalkingManager.getInstance().load();
					activeChar.sendMessage("All walkers have been reloaded");
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Walkers.");
					break;
				}
				case "htm":
				case "html":
				{
					if (st.hasMoreElements())
					{
						final String path = st.nextToken();
						final File file = new File(Config.DATAPACK_ROOT, "data/html/" + path);
						if (file.exists())
						{
							HtmCache.getInstance().reload(file);
							AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htm File:" + file.getName() + ".");
						}
						else
						{
							activeChar.sendMessage("File or Directory does not exist.");
						}
					}
					else
					{
						HtmCache.getInstance().reload();
						activeChar.sendMessage("Cache[HTML]: " + HtmCache.getInstance().getMemoryUsage() + " megabytes on " + HtmCache.getInstance().getLoadedFiles() + " files loaded");
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Htms.");
					}
					break;
				}
				case "multisell":
				{
					MultisellData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Multisells.");
					break;
				}
				case "buylist":
				{
					BuyListData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Buylists.");
					break;
				}
				case "teleport":
				{
					TeleportLocationTable.getInstance().reloadAll();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Teleports.");
					break;
				}
				case "skill":
				{
					SkillData.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Skills.");
					break;
				}
				case "item":
				{
					ItemTable.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Items.");
					break;
				}
				case "door":
				{
					DoorData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Doors.");
					break;
				}
				case "zone":
				{
					ZoneManager.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Zones.");
					break;
				}
				case "cw":
				{
					CursedWeaponsManager.getInstance().reload();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Cursed Weapons.");
					break;
				}
				case "crest":
				{
					CrestTable.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Crests.");
					break;
				}
				case "effect":
				{
					final File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "handlers/EffectMasterHandler.java");
					try
					{
						L2ScriptEngineManager.getInstance().executeScript(file);
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Effects.");
					}
					catch (ScriptException e)
					{
						L2ScriptEngineManager.getInstance().reportScriptFileError(file, e);
						activeChar.sendMessage("There was an error while loading handlers.");
					}
					break;
				}
				case "handler":
				{
					final File file = new File(L2ScriptEngineManager.SCRIPT_FOLDER, "handlers/MasterHandler.java");
					try
					{
						L2ScriptEngineManager.getInstance().executeScript(file);
						AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded Handlers.");
					}
					catch (ScriptException e)
					{
						L2ScriptEngineManager.getInstance().reportScriptFileError(file, e);
						activeChar.sendMessage("There was an error while loading handlers.");
					}
					break;
				}
				case "enchant":
				{
					EnchantItemGroupsData.getInstance().load();
					EnchantItemData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded item enchanting data.");
					break;
				}
				case "transform":
				{
					TransformData.getInstance().load();
					AdminData.getInstance().broadcastMessageToGMs(activeChar.getName() + ": Reloaded transform data.");
					break;
				}
				default:
				{
					activeChar.sendMessage(RELOAD_USAGE);
					return true;
				}
			}
			activeChar.sendMessage("WARNING: There are several known issues regarding this feature. Reloading server data during runtime is STRONGLY NOT RECOMMENDED for live servers, just for developing environments.");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
