package org.rystic.city.entities.building.handlers;

import org.rystic.city.entities.AbstractEntityHandler;
import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.util.MapUpdateEvent;

public class TickHandler extends AbstractEntityHandler
{
	public TickHandler(BuildingEntity building_)
	{
		_building = building_;
	}

	@Override
	public void mapUpdated(MapUpdateEvent update_)
	{

	}

	@Override
	public void tickUpdated()
	{
		_tickTime--;
	}

	private BuildingEntity _building;
	
	private int _tickTime;

}
