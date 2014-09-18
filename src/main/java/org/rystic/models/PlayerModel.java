package org.rystic.models;

import java.util.ArrayList;
import java.util.List;

import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;

public class PlayerModel
{
	public PlayerModel()
	{
		_knownBuildings = new ArrayList<GenericBuilding>();
		loadKnownBuildings();
	}

	private void loadKnownBuildings()
	{
		int id = 0;
		while (true)
		{
			GenericBuilding building = BuildingData.getBuildingById(id);
			if (building == null)
				break;
			if (building.isUnlockedFromStart())
				_knownBuildings.add(building);
			id++;
		}
	}
	
	public List<GenericBuilding> getKnownBuildings()
	{
		return _knownBuildings;
	}
	
	private List<GenericBuilding> _knownBuildings;
}
