package org.rystic.screens;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rystic.city.controllers.BuildingPlacementController;
import org.rystic.city.controllers.CityInterfaceController;
import org.rystic.city.effects.AbstractEffect;
import org.rystic.city.effects.ArchitectureEffect;
import org.rystic.city.effects.BackgroundEffect;
import org.rystic.city.effects.CityInterfaceEffect;
import org.rystic.city.effects.GolemEffect;
import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.data.GolemData;
import org.rystic.city.processes.BuildingTickProcess;
import org.rystic.city.processes.GolemMaintenanceProcess;
import org.rystic.city.processes.HeatTickProcess;
import org.rystic.city.processes.ResourceManagerProcess;
import org.rystic.city.processes.StorageInventoryProcess;
import org.rystic.city.processes.GolemBehaviorProcess;
import org.rystic.city.processes.TrafficProcess;
import org.rystic.city.ui.menus.AbstractMenu;
import org.rystic.city.ui.menus.BuildingMenu;
import org.rystic.city.ui.menus.GolemRosterMenu;
import org.rystic.city.ui.menus.ResourceManagementMenu;
import org.rystic.city.util.ClayTerrainGenerator;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.models.PlayerModel;

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
		_menus.add(new ResourceManagementMenu(this));
		_menus.add(new GolemRosterMenu(this));

		_cityModel.setSelectedMenu(_menus.get(0));

		_effects.add(new BackgroundEffect(this));
		_effects.add(new ArchitectureEffect(this));
		_effects.add(new GolemEffect(this));
//		if (new Random().nextInt(100) < 10)
//			_effects.add(new RainEffect(this));
		_effects.add(new CityInterfaceEffect(this));

		for (AbstractEffect effect : _effects)
		{
			effect.init();
		}

		initTerrain();

		_controllers.add(new BuildingPlacementController(this));
		_controllers.add(new CityInterfaceController(this));
		
		_processes.add(new GolemBehaviorProcess(this));
		_processes.add(new GolemMaintenanceProcess(this));
		_processes.add(new BuildingTickProcess(this));
		_processes.add(new ResourceManagerProcess(this));
		_processes.add(new StorageInventoryProcess(this));
		_processes.add(new HeatTickProcess(this));
		_processes.add(new TrafficProcess(this));

		initGolems();
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
				placementY,
				null);
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
