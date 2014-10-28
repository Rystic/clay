package org.rystic.city.processes;

import java.util.List;

import org.rystic.city.generics.entities.GolemEntity;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class GolemMaintenanceProcess extends AbstractProcess
{
	public GolemMaintenanceProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_golemList = _model.getGolems();
		_newGolemList = _model.getNewGolems();
		_deadGolemList = _model.getDeadGolems();
	}

	@Override
	public void execute()
	{
		int mana = _model.getMana();
		if (mana > 0)
		{
			for (GolemEntity golem : _golemList)
			{
				golem.adjustMana(mana);
			}
		}
		_model.setMana(0);
		if (_newGolemList.size() > 0)
		{
			_golemList.addAll(_newGolemList);
			_newGolemList.clear();
			TrafficProcess traficProcess = ((TrafficProcess) _homeScreen
					.getProcess(TrafficProcess.class));
			if (traficProcess != null)
				traficProcess.doUpdate();
		}
		if (_deadGolemList.size() > 0)
		{
			_golemList.removeAll(_deadGolemList);
			_deadGolemList.clear();
			TrafficProcess traficProcess = ((TrafficProcess) _homeScreen
					.getProcess(TrafficProcess.class));
			if (traficProcess != null)
				traficProcess.doUpdate();
		}
	}

	private CityModel _model;

	private List<GolemEntity> _golemList;
	private List<GolemEntity> _newGolemList;
	private List<GolemEntity> _deadGolemList;
}
