package org.rystic.city.processes;

import org.rystic.city.generics.entities.BuildingEntity;
import org.rystic.city.generics.entities.GolemEntity;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class TrafficProcess extends AbstractProcess
{

	public TrafficProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();
	}

	@Override
	public void execute()
	{
		if (_doUpdate)
		{
			_doUpdate = false;
			BuildingEntity[][] buildings = _model.getTileValues();
			for (int i = 0; i < buildings.length; i++)
			{
				for (int j = 0; j < buildings[0].length; j++)
				{
					if (buildings[i][j] != null)
						buildings[i][j].clearTraffic();
				}
			}
			for (GolemEntity golem : _model.getGolems())
			{
				if (!golem.isVisible()) continue;
				BuildingEntity golemTile = buildings[golem.getGridX()][golem
						.getGridY()];
				if (golemTile != null)
					golemTile.addTraffic(golem.getTrafficWeight());
			}
			for (GolemEntity golem : _model.getGolems())
			{
				golem.updateMoveSpeed();
			}
		}
	}

	public void doUpdate()
	{
		_doUpdate = true;
	}

	private boolean _doUpdate;

	private CityModel _model;

}
