/*
 * Copyright (C) 2004-2016 L2J Server
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
package com.l2jserver.loginserver.model.data;

import com.l2jserver.localization.Language;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

/**
 * @author HorridoJoho
 */
public final class AccountInfo
{
	private final String _login;
	private final String _passHash;
	private final int _accessLevel;
	private final int _lastServer;
	private final Language accountLanguage;

	public AccountInfo(final String login, final String passHash, final int accessLevel, final int lastServer, Language accountLanguage)
	{
		Objects.requireNonNull(login, "login parameter is null");
		Objects.requireNonNull(passHash, "passHash parameter is null");
		
		if (login.isEmpty())
		{
			throw new IllegalArgumentException("login string is empty");
		}
		if (passHash.isEmpty())
		{
			throw new IllegalArgumentException("passHash string is empty");
		}
		
		_login = login.toLowerCase();
		_passHash = passHash;
		_accessLevel = accessLevel;
		_lastServer = lastServer;
		this.accountLanguage = accountLanguage;
	}

	public boolean checkPassword(String password) {
		return BCrypt.checkpw(password, _passHash);
	}

	public String getLogin()
	{
		return _login;
	}
	
	public int getAccessLevel()
	{
		return _accessLevel;
	}
	
	public int getLastServer()
	{
		return _lastServer;
	}

	public Language getAccountLanguage() {
		return accountLanguage;
	}

}
