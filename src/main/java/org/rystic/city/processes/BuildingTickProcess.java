package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.List;

import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.screens.AbstractScreen;

public class BuildingTickProcess extends AbstractProcess
{
	public BuildingTickProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_buildings = new ArrayList<BuildingEntity>();
	}

	@Override
	public void execute()
	{
		for (BuildingEntity entity : _buildings)
		{
			if (entity.isTickFinished())
			{
				if (entity.shouldTickReset())
				{
					entity.tickReset();
				}
			}
			else
				entity.tick();
		}
	}

	public void register(BuildingEntity entity_)
	{
		_buildings.add(entity_);
	}

	public void unregister(BuildingEntity entity_)
	{
		_buildings.remove(entity_);
	}

	private List<BuildingEntity> _buildings;

}
