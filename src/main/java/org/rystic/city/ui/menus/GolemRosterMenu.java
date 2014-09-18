package org.rystic.city.ui.menus;

import org.rystic.city.ui.menus.areas.GolemRosterArea;
import org.rystic.screens.AbstractScreen;

public class GolemRosterMenu extends AbstractMenu
{
	public GolemRosterMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_,  "Golem Roster Menu");
		_areas.add(new GolemRosterArea(homeScreen_));
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{

	}

	@Override
	public void handleMouseWheel(boolean upwardScroll_)
	{
		// TODO Auto-generated method stub
		
	}
}
