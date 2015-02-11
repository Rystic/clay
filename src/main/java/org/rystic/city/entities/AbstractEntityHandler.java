package org.rystic.city.entities;

import org.rystic.city.util.MapUpdateEvent;

public abstract class AbstractEntityHandler
{
	public abstract void mapUpdated(MapUpdateEvent update_);

	public abstract void tickUpdated();
	
	public boolean listensForTick()
	{
		return false;
	}
}
