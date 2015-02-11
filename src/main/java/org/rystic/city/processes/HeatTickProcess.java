package org.rystic.city.processes;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.screens.AbstractScreen;

public class HeatTickProcess extends AbstractProcess
{
	public HeatTickProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_buildings = new HashSet<BuildingEntity>();
		_noHeatBuildings = new HashSet<BuildingEntity>();
		_heatDecreaseChance = new Random();
	}

	@Override
	public void execute()
	{
		_noHeatBuildings.clear();
		for (BuildingEntity building : _buildings)
		{
			if (_heatDecreaseChance.nextInt(100) <= building.getCoolingRate())
			{
				building.heatTick();
				if (!building.isHeatedExcludeHeatDamage())
					_noHeatBuildings.add(building);
			}
		}
		_buildings.removeAll(_noHeatBuildings);
	}

	public void register(BuildingEntity entity_)
	{
		_buildings.add(entity_);
	}

	public void unregister(BuildingEntity entity_)
	{
		_buildings.remove(entity_);
	}

	private Set<BuildingEntity> _buildings;
	private Set<BuildingEntity> _noHeatBuildings;
	private Random _heatDecreaseChance;
}
