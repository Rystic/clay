package city.processes;

import java.util.List;

import models.CityModel;

import screens.AbstractScreen;
import city.entities.GolemEntity;

public class GolemMaintenanceProcess extends AbstractProcess
{
	public GolemMaintenanceProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_golemList = _model.getGolems();
		_newGolemList = _model.getNewGolems();
	}
	
	@Override
	public void execute()
	{
		int mana = _model.getMana();
		if (mana > 0)
		{
			double manaDividend = mana / _golemList.size();
			_model.setMana(0);
			for (GolemEntity golem : _golemList)
			{
				//golem.increaseMana(manaDividend);
			}
		}
		if (_newGolemList.size() > 0)
		{
			_golemList.addAll(_newGolemList);
			_newGolemList.clear();
		}
	}
	
	private CityModel _model;
	
	private List<GolemEntity> _golemList;
	private List<GolemEntity> _newGolemList;
}
