package screens;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.ClayConstants;
import models.CityModel;
import models.PlayerModel;
import city.controllers.BuildingPlacementController;
import city.controllers.CityInterfaceController;
import city.effects.AbstractEffect;
import city.effects.ArchitectureEffect;
import city.effects.BackgroundEffect;
import city.effects.CityInterfaceEffect;
import city.effects.GolemEffect;
import city.generics.data.BuildingData;
import city.generics.data.GolemData;
import city.generics.entities.BuildingEntity;
import city.processes.BuildingTickProcess;
import city.processes.GolemBehaviorProcess;
import city.processes.GolemMaintenanceProcess;
import city.processes.ResourceManagerProcess;
import city.processes.StorageInventoryProcess;
import city.ui.menus.AbstractMenu;
import city.ui.menus.BuildingMenu;
import city.ui.menus.StorageInventoryMenu;
import city.util.ClayTerrainGenerator;

public class CityScreen extends AbstractScreen
{
	public CityScreen(PlayerModel playerModel_)
	{
		super(playerModel_);
		_playerModel = playerModel_;
	}

	@Override
	protected void init()
	{
		_cityModel = new CityModel(this);

		_menus = new ArrayList<AbstractMenu>();
		_menus.add(new BuildingMenu(this));
		_menus.add(new StorageInventoryMenu(this));

		_cityModel.setSelectedMenu(_menus.get(0));

		_effects.add(new BackgroundEffect(this));
		_effects.add(new ArchitectureEffect(this));
		_effects.add(new GolemEffect(this));
		_effects.add(new CityInterfaceEffect(this));
		// _effects.add(new CursorEffect(this));

		for (AbstractEffect effect : _effects)
		{
			effect.init();
		}

		initTerrain();
		initGolems();

		_controllers.add(new BuildingPlacementController(this));
		_controllers.add(new CityInterfaceController(this));
		_processes.add(new GolemBehaviorProcess(this));
		_processes.add(new GolemMaintenanceProcess(this));
		_processes.add(new BuildingTickProcess(this));
		_processes.add(new ResourceManagerProcess(this));
		_processes.add(new StorageInventoryProcess(this));
	}

	public void initTerrain()
	{
		ClayTerrainGenerator gen = new ClayTerrainGenerator(this);
		BuildingEntity[][] tileValues = _cityModel.getTileValues();
		gen.buildTerrain(
				_cityModel.getTileValues(),
				2,
				((ClayConstants.DEFAULT_MAP_HEIGHT / ClayConstants.TILE_Y) / 2) - 1,
				new Point(
						0,
						(ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X) - 1));
		for (int i = 0; i < ClayConstants.DEFAULT_MAP_WIDTH
				/ ClayConstants.TILE_X; i++)
		{
			if (tileValues[i][0] == null)
				tileValues[i][0] = new BuildingEntity(
						BuildingData.getBuildingByTag("dirt-block"), new Point(
								i * ClayConstants.TILE_X, 0), this,
						ClayConstants.DEFAULT_BUILDING_POSITION);
		}
	}

	public void initGolems()
	{
		Random random = new Random();
		int placementX = random.nextInt(ClayConstants.DEFAULT_MAP_WIDTH
				- ClayConstants.TILE_X);
		int placementY = ClayConstants.TILE_Y;
		int indexX = placementX / ClayConstants.TILE_X;
		for (int indexY = 1; indexY < ClayConstants.DEFAULT_MAP_HEIGHT
				/ ClayConstants.TILE_Y; indexY++)
		{
			if (_cityModel.getTileValue(indexX, indexY) == null)
				break;
			placementY += ClayConstants.TILE_Y;
		}
		_cityModel.addGolem(
				GolemData.getGolem(ClayConstants.GOLEM_CLAY),
				placementX,
				placementY);
	}

	@Override
	public CityModel getModel()
	{
		return _cityModel;
	}

	public List<AbstractMenu> getMenus()
	{
		return _menus;
	}

	protected CityModel _cityModel;

	protected List<AbstractMenu> _menus;
}
