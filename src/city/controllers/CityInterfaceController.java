package city.controllers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import screens.AbstractScreen;
import screens.CityScreen;
import city.processes.AbstractProcess;
import city.ui.menus.AbstractMenu;

public class CityInterfaceController extends AbstractProcess
{

	public CityInterfaceController(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		basicBuildingOptions();
		_cityScreen = (CityScreen) _homeScreen;
		_pressedKeys = new ArrayList<Integer>();
	}

	@Override
	public void execute()
	{
		List<AbstractMenu> menus = _cityScreen.getMenus();
		for (AbstractMenu menu : menus)
		{
			for (Integer key : menu.getHotKeys())
			{
				if (!_pressedKeys.contains(key) && Keyboard.isKeyDown(key))
				{
					_pressedKeys.add(key);
					menu.handleKeyEvent(key);
				}
			}
			int wheel = Mouse.getDWheel();
			if (wheel != 0)
				menu.handleMouseWheel(wheel > 0);
		}
		if (_pressedKeys.size() > 0)
		{
			List<Integer> liftedKeys = new ArrayList<Integer>();
			for (Integer key : _pressedKeys)
			{
				if (!Keyboard.isKeyDown(key))
					liftedKeys.add(key);
			}
			_pressedKeys.removeAll(liftedKeys);
		}
		// if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		// {
		// if (_isInterfaceToggleButtonLifted)
		// {
		// _model.setInterfaceToggle(!_model.isInterfaceToggle());
		// _isInterfaceToggleButtonLifted = false;
		// }
		// }
		// else
		// _isInterfaceToggleButtonLifted = true;
		// if (Mouse.isButtonDown(0) && Mouse.getX() >=
		// ClayConstants.DEFAULT_MAP_WIDTH)
		// {
		// for (AbstractButton button : _options)
		// {
		// if (button.isPressed())
		// {
		// if (button instanceof SelectBuildingButton)
		// _model.setSelectedBuilding(((SelectBuildingButton) button)
		// .getIdentifier());
		// }
		// }
		// }
	}

	private void basicBuildingOptions()
	{
		// int adjustedX = ClayConstants.DEFAULT_MAP_WIDTH;
		// AbstractButton clayBlockButton = new SelectBuildingButton(
		// 43 + adjustedX, ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X,
		// TILE_Y, BuildingData.getBuildingByTag("clay-block"));
		// AbstractButton houseButton = new SelectBuildingButton(43 + adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("house"));
		// AbstractButton deepHomeButton = new SelectBuildingButton(43 +
		// adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 105, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("deep-home"));
		//
		//
		// AbstractButton manaFarmButton = new SelectBuildingButton(
		// 78 + adjustedX, ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X,
		// TILE_Y, BuildingData.getBuildingByTag("crucible"));
		// AbstractButton storageButton = new SelectBuildingButton(78 +
		// adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("storage"));
		// AbstractButton sculptorStudioButton = new SelectBuildingButton(78 +
		// adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 105, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("sculptors-studio"));
		//
		// AbstractButton rampButton = new SelectBuildingButton(113 + adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("ramp"));
		// AbstractButton obeliskButton = new SelectBuildingButton(
		// 113 + adjustedX, ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X,
		// TILE_Y, BuildingData.getBuildingByTag("obelisk"));
		// AbstractButton studioButton = new SelectBuildingButton(148 +
		// adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 35, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("chute"));
		// AbstractButton ladderButton = new SelectBuildingButton(148 +
		// adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 70, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("ladder"));
		// AbstractButton ovenButton = new SelectBuildingButton(148 + adjustedX,
		// ClayConstants.DEFAULT_MAP_HEIGHT - 105, TILE_X, TILE_Y,
		// BuildingData.getBuildingByTag("oven"));
		//
		// _options.add(clayBlockButton);
		// _options.add(houseButton);
		// _options.add(deepHomeButton);
		// _options.add(manaFarmButton);
		// _options.add(storageButton);
		// _options.add(sculptorStudioButton);
		// _options.add(rampButton);
		// _options.add(obeliskButton);
		// _options.add(studioButton);
		// _options.add(ladderButton);
		// _options.add(ovenButton);
	}

	private List<Integer> _pressedKeys;

	private CityScreen _cityScreen;
}
