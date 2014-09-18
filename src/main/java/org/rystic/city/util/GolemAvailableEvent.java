package org.rystic.city.util;

import org.rystic.screens.AbstractScreen;

public class GolemAvailableEvent
{
	public GolemAvailableEvent(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}
	
	public AbstractScreen getHomeScreen()
	{
		return _homeScreen;
	}
	
	private AbstractScreen _homeScreen;
}
