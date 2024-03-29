/*
 * Copyright (C) 2004-2017 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.dao.factory.impl;

import com.l2jserver.gameserver.clanbonus.ClanBonusesDao;
import com.l2jserver.gameserver.dao.*;
import com.l2jserver.gameserver.dao.factory.IDAOFactory;
import com.l2jserver.gameserver.dao.impl.mysql.*;
import com.l2jserver.gameserver.loginbonus.LoginBonusDao;

/**
 * MySQL DAO Factory implementation.
 * @author Zoey76
 */
enum MySQLDAOFactory implements IDAOFactory
{
	INSTANCE;
	
	private final FriendDAO friendDAO = new FriendDAOMySQLImpl();
	private final HennaDAO hennaDAO = new HennaDAOMySQLImpl();
	private final ItemDAO itemDAO = new ItemDAOMySQLImpl();
	private final ItemReuseDAO itemReuseDAO = new ItemReuseDAOMySQLImpl();
	private final PetDAO petDAO = new PetDAOMySQLImpl();
	private final PetSkillSaveDAO petSkillSaveDAO = new PetSkillSaveDAOMySQL();
	private final PlayerDAO playerDAO = new PlayerDAOMySQLImpl();
	private final PlayerSkillSaveDAO playerSkillSaveDAO = new PlayerSkillSaveDAOMySQLImpl();
	private final PremiumItemDAO premiumItemDAO = new PremiumItemDAOMySQLImpl();
	private final RecipeBookDAO recipeBookDAO = new RecipeBookDAOMySQLImpl();
	private final RecipeShopListDAO recipeShopListDAO = new RecipeShopListDAOMySQLImpl();
	private final RecommendationBonusDAO recommendationBonusDAO = new RecommendationBonusDAOMySQLImpl();
	private final ServitorSkillSaveDAO servitorSkillSaveDAO = new ServitorSkillSaveDAOMySQLImpl();
	private final ShortcutDAO shortcutDAO = new ShortcutDAOMySQLImpl();
	private final SkillDAO skillDAO = new SkillDAOMySQLImpl();
	private final SubclassDAO subclassDAO = new SubclassDAOMySQLImpl();
	private final TeleportBookmarkDAO teleportBookmarkDAO = new TeleportBookmarkDAOMySQLImpl();
	private final MaxOnlineDao maxOnlineDao = new MaxOnlineDao();
	private final LoginBonusDao loginBonusDao = new LoginBonusDao();
	private final CustomVariablesDao customVariablesDao = new CustomVariablesDao();
	private final ClanBonusesDao clanBonusesDao = new ClanBonusesDao();
	private final CommunityBuffListDao communityBuffListDao = new CommunityBuffListDao();
	
	@Override
	public FriendDAO getFriendDAO()
	{
		return friendDAO;
	}
	
	@Override
	public HennaDAO getHennaDAO()
	{
		return hennaDAO;
	}
	
	@Override
	public ItemDAO getItemDAO()
	{
		return itemDAO;
	}
	
	@Override
	public ItemReuseDAO getItemReuseDAO()
	{
		return itemReuseDAO;
	}
	
	@Override
	public PetDAO getPetDAO()
	{
		return petDAO;
	}
	
	@Override
	public PetSkillSaveDAO getPetSkillSaveDAO()
	{
		return petSkillSaveDAO;
	}
	
	@Override
	public PlayerDAO getPlayerDAO()
	{
		return playerDAO;
	}
	
	@Override
	public PlayerSkillSaveDAO getPlayerSkillSaveDAO()
	{
		return playerSkillSaveDAO;
	}
	
	@Override
	public PremiumItemDAO getPremiumItemDAO()
	{
		return premiumItemDAO;
	}
	
	@Override
	public RecipeBookDAO getRecipeBookDAO()
	{
		return recipeBookDAO;
	}
	
	@Override
	public RecipeShopListDAO getRecipeShopListDAO()
	{
		return recipeShopListDAO;
	}
	
	@Override
	public RecommendationBonusDAO getRecommendationBonusDAO()
	{
		return recommendationBonusDAO;
	}
	
	@Override
	public ServitorSkillSaveDAO getServitorSkillSaveDAO()
	{
		return servitorSkillSaveDAO;
	}
	
	@Override
	public ShortcutDAO getShortcutDAO()
	{
		return shortcutDAO;
	}
	
	@Override
	public SkillDAO getSkillDAO()
	{
		return skillDAO;
	}
	
	@Override
	public SubclassDAO getSubclassDAO()
	{
		return subclassDAO;
	}
	
	@Override
	public TeleportBookmarkDAO getTeleportBookmarkDAO()
	{
		return teleportBookmarkDAO;
	}

	@Override
	public MaxOnlineDao getMaxOnlineDao() {
		return maxOnlineDao;
	}

	@Override
	public LoginBonusDao getLoginBonusDao() {
		return loginBonusDao;
	}

	@Override
	public CustomVariablesDao getCustomVariablesDao() {
		return customVariablesDao;
	}

	@Override
	public ClanBonusesDao getClanBonusesDao() {
		return clanBonusesDao;
	}

	public CommunityBuffListDao getCommunityBuffListDao() {
		return communityBuffListDao;
	}

}
