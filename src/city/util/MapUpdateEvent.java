package city.util;

import java.awt.Point;
import java.util.List;

import screens.AbstractScreen;

public class MapUpdateEvent
{
	public MapUpdateEvent(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}
	
	public MapUpdateEvent(AbstractScreen homeScreen_, List<Point> points_)
	{
		_homeScreen = homeScreen_;
		_points = points_;
	}

	public AbstractScreen getHomeScreen()
	{
		return _homeScreen;
	}
	
	public List<Point> getPoints()
	{
		return _points;
	}

	private AbstractScreen _homeScreen;
	
	private List<Point> _points;

}
