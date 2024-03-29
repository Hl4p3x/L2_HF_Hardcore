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
package instances;

import instances.CrystalCaverns.CrystalCaverns;
import instances.DarkCloudMansion.DarkCloudMansion;
import instances.DisciplesNecropolisPast.DisciplesNecropolisPast;
import instances.ElcadiasTent.ElcadiasTent;
import instances.HideoutOfTheDawn.HideoutOfTheDawn;
import instances.IceQueensCastle.IceQueensCastle;
import instances.JiniaGuildHideout1.JiniaGuildHideout1;
import instances.JiniaGuildHideout2.JiniaGuildHideout2;
import instances.JiniaGuildHideout3.JiniaGuildHideout3;
import instances.JiniaGuildHideout4.JiniaGuildHideout4;
import instances.LibraryOfSages.LibraryOfSages;
import instances.MithrilMine.MithrilMine;
import instances.MonasteryOfSilence1.MonasteryOfSilence1;
import instances.NornilsGarden.NornilsGarden;
import instances.NornilsGardenQuest.NornilsGardenQuest;
import instances.SanctumOftheLordsOfDawn.SanctumOftheLordsOfDawn;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instance class-loader.
 * @author FallenAngel
 */
public final class InstanceLoader
{
	private static final Logger _log = Logger.getLogger(InstanceLoader.class.getName());

	private static final Class<?>[] SCRIPTS =
	{
		CrystalCaverns.class,
		DarkCloudMansion.class,
		DisciplesNecropolisPast.class,
		ElcadiasTent.class,
		HideoutOfTheDawn.class,
		IceQueensCastle.class,
		JiniaGuildHideout1.class,
		JiniaGuildHideout2.class,
		JiniaGuildHideout3.class,
		JiniaGuildHideout4.class,
		LibraryOfSages.class,
		MithrilMine.class,
		MonasteryOfSilence1.class,
		NornilsGarden.class,
		NornilsGardenQuest.class,
		SanctumOftheLordsOfDawn.class,
	};

	public static void main(String[] args)
	{
		_log.info(InstanceLoader.class.getSimpleName() + ": Loading Instances scripts.");
		for (Class<?> script : SCRIPTS)
		{
			try
			{
                script.getConstructor().newInstance();
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, InstanceLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":", e);
			}
		}
	}
}
