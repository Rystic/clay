package org.rystic.city.util;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;

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

		if (params_.containsKey(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE))
			_storageAvailable = (boolean) params_
					.get(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE);
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

	public boolean isStorageAvailable()
	{
		return _storageAvailable;
	}

	private AbstractScreen _homeScreen;

	private List<Point> _points;

	private boolean _itemUpdate;

	private boolean _storageAvailable;
}
