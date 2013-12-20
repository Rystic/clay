package city.util;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import main.ClayConstants;
import screens.AbstractScreen;

public class MapUpdateEvent
{
	public MapUpdateEvent(AbstractScreen homeScreen_)
	{
		_homeScreen = homeScreen_;
	}

	@SuppressWarnings("unchecked")
	public MapUpdateEvent(AbstractScreen homeScreen_,
			Map<Integer, Object> params_)
	{
		_homeScreen = homeScreen_;
		if (params_.containsKey(ClayConstants.EVENT_MAP_UPDATE))
			_points = (List<Point>) params_.get(ClayConstants.EVENT_MAP_UPDATE);
		if (params_.containsKey(ClayConstants.EVENT_ITEM_UPDATE))
			_itemUpdate = (boolean) params_
					.get(ClayConstants.EVENT_ITEM_UPDATE);
	}

	public AbstractScreen getHomeScreen()
	{
		return _homeScreen;
	}

	public List<Point> getPoints()
	{
		return _points;
	}

	public boolean isItemUpdate()
	{
		return _itemUpdate;
	}

	private AbstractScreen _homeScreen;

	private List<Point> _points;

	private boolean _itemUpdate;

}
