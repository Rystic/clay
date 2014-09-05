package city.processes;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import screens.AbstractScreen;
import city.generics.entities.BuildingEntity;

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
		for (BuildingEntity entity : _buildings)
		{
			if (_heatDecreaseChance.nextInt(100) <= 30)
			{
				entity.heatTick();
				if (!entity.isHeatedExcludeHeatDamage())
					_noHeatBuildings.add(entity);
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
