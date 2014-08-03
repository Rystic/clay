package models;

import java.util.ArrayList;
import java.util.List;

import city.generics.GenericBuilding;
import city.generics.data.BuildingData;

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
