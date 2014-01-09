package city.controllers;

import main.ClayConstants;
import models.CityModel;
import screens.AbstractScreen;
import city.processes.AbstractProcess;

public class CityInterfaceController extends AbstractProcess
{

	public CityInterfaceController(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		//_options = _model.getInterfaceOptions();
		_isInterfaceToggleButtonLifted = true;

		basicBuildingOptions();
	}

	@Override
	public void execute()
	{
//		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
//		{
//			if (_isInterfaceToggleButtonLifted)
//			{
//				_model.setInterfaceToggle(!_model.isInterfaceToggle());
//				_isInterfaceToggleButtonLifted = false;
//			}
//		}
//		else
//			_isInterfaceToggleButtonLifted = true;
//		if (Mouse.isButtonDown(0) && Mouse.getX() >= ClayConstants.DEFAULT_MAP_WIDTH)
//		{
//			for (AbstractButton button : _options)
//			{
//				if (button.isPressed())
//				{
//					if (button instanceof SelectBuildingButton)
//						_model.setSelectedBuilding(((SelectBuildingButton) button)
//								.getIdentifier());
//				}
//			}
//		}
	}

	private void basicBuildingOptions()
	{
//		int adjustedX = ClayConstants.DEFAULT_MAP_WIDTH;
//		AbstractButton clayBlockButton = new SelectBuildingButton(
//				43 + adjustedX, ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X,
//				TILE_Y, BuildingData.getBuildingByTag("clay-block"));
//		AbstractButton houseButton = new SelectBuildingButton(43 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("house"));
//		AbstractButton deepHomeButton = new SelectBuildingButton(43 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 105, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("deep-home"));
//		
//		
//		AbstractButton manaFarmButton = new SelectBuildingButton(
//				78 + adjustedX, ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X,
//				TILE_Y, BuildingData.getBuildingByTag("crucible"));
//		AbstractButton storageButton = new SelectBuildingButton(78 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("storage"));
//		AbstractButton sculptorStudioButton = new SelectBuildingButton(78 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 105, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("sculptors-studio"));
//		
//		AbstractButton rampButton = new SelectBuildingButton(113 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("ramp"));
//		AbstractButton obeliskButton = new SelectBuildingButton(
//				113 + adjustedX, ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X,
//				TILE_Y, BuildingData.getBuildingByTag("obelisk"));
//		AbstractButton studioButton = new SelectBuildingButton(148 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("chute"));
//		AbstractButton ladderButton = new SelectBuildingButton(148 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("ladder"));
//		AbstractButton ovenButton = new SelectBuildingButton(148 + adjustedX,
//				ClayConstants.DEFAULT_MAP_HEIGHT - 105, TILE_X, TILE_Y,
//				BuildingData.getBuildingByTag("oven"));
//		
//		_options.add(clayBlockButton);
//		_options.add(houseButton);
//		_options.add(deepHomeButton);
//		_options.add(manaFarmButton);
//		_options.add(storageButton);
//		_options.add(sculptorStudioButton);
//		_options.add(rampButton);
//		_options.add(obeliskButton);
//		_options.add(studioButton);
//		_options.add(ladderButton);
//		_options.add(ovenButton);
	}

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

	private boolean _isInterfaceToggleButtonLifted;

	private CityModel _model;

}
