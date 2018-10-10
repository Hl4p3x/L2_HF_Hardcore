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

import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.categorized.GradedItemsDropDataTable;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.model.buylist.Product;
import com.l2jserver.gameserver.model.items.graded.Grade;
import com.l2jserver.gameserver.model.items.graded.GradeCategory;
import com.l2jserver.gameserver.model.items.graded.GradeInfo;
import com.l2jserver.gameserver.model.items.graded.GradedItem;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.BuyList;
import com.l2jserver.gameserver.network.serverpackets.ExBuySellList;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>gmshop = shows menu</li>
 * <li>buy id = shows shop with respective id</li>
 * </ul>
 */
public class AdminShop implements IAdminCommandHandler
{
	private static final Logger _log = Logger.getLogger(AdminShop.class.getName());
	
	private static final String[] ADMIN_COMMANDS =
	{
			"admin_buy",
			"admin_gmshop",
			"admin_categorized"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_buy")) {
			try {
				handleBuyRequest(activeChar, command.substring(10));
			} catch (IndexOutOfBoundsException e) {
				activeChar.sendMessage("Please specify buylist.");
			}
		} else if (command.startsWith("admin_categorized")) {
			String[] commandSplit = command.split(" ");

            Map<GradeInfo, List<GradedItem>> items = GradedItemsDropDataTable.getInstance().getGradedItemsMap();

			if (commandSplit.length == 1) {
                Set<GradeInfo> categories = items.keySet();

				StringBuilder html = new StringBuilder();
                html.append("<center><table>");
				categories.forEach(category -> {
                    String categoryKey = category.getGrade() + " " + category.getCategory();

					html.append("<tr>");
					html.append("<td>");
                    html.append("<button action=\"bypass -h admin_categorized ")
                            .append(categoryKey)
                            .append("\" value=\"")
                            .append(categoryKey)
                            .append("\" width=85 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					html.append("</td>");
					html.append("</tr>");
				});
                html.append("</table></center>");

				activeChar.sendPacket(new NpcHtmlMessage(html.toString()));
            } else if (commandSplit.length == 3) {
                Grade grade = Grade.valueOf(commandSplit[1]);
                GradeCategory gradeCategory = GradeCategory.valueOf(commandSplit[2]);
                Collection<GradedItem> categoryItems = items.get(new GradeInfo(grade, gradeCategory));

                L2BuyList buyList = new L2BuyList(0);
				categoryItems.forEach(item -> {
                    buyList.addProduct(new Product(0, ItemTable.getInstance().getTemplate(item.getItemId()), item.getItemPrice(), 0, -1));
				});

				activeChar.sendPacket(new BuyList(buyList, activeChar.getAdena(), 0));
				activeChar.sendPacket(new ExBuySellList(activeChar, false));
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);

			}
		} else if (command.equals("admin_gmshop")) {
			AdminHtml.showAdminHtml(activeChar, "gmshops.htm");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleBuyRequest(L2PcInstance activeChar, String command)
	{
		int val = -1;
		try
		{
			val = Integer.parseInt(command);
		}
		catch (Exception e)
		{
			_log.warning("admin buylist failed:" + command);
		}
		
		L2BuyList buyList = BuyListData.getInstance().getBuyList(val);
		
		if (buyList != null)
		{
			activeChar.sendPacket(new BuyList(buyList, activeChar.getAdena(), 0));
			activeChar.sendPacket(new ExBuySellList(activeChar, false));
		}
		else
		{
			_log.warning("no buylist with id:" + val);
		}
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
