package city.util;

import screens.AbstractScreen;

public class MapUpdateEvent
{
	public MapUpdateEvent(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}
	
	public AbstractScreen getHomeScreen()
	{
		return _homeScreen;
	}
	
	private AbstractScreen _homeScreen;
}
