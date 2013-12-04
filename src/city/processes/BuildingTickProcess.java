package city.processes;

import java.util.ArrayList;
import java.util.List;

import screens.AbstractScreen;
import city.entities.BuildingEntity;

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

	private List<BuildingEntity> _buildings;

}
