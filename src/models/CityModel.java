package models;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ClayConstants;
import screens.AbstractScreen;
import city.generics.GenericBuilding;
import city.generics.GenericGolem;
import city.generics.data.BuildingData;
import city.generics.entities.BuildingEntity;
import city.generics.entities.GolemEntity;
import city.ui.menus.AbstractMenu;

public class CityModel extends AbstractModel
{
	public CityModel(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_tileValues = new BuildingEntity[ClayConstants.DEFAULT_MAP_WIDTH
				/ ClayConstants.TILE_X][ClayConstants.DEFAULT_MAP_HEIGHT
				/ ClayConstants.TILE_Y];
		_xTileCount = _tileValues.length - 1;
		_yTileCount = _tileValues[0].length - 1;
		_golemList = new ArrayList<GolemEntity>();
		_newGolemList = new ArrayList<GolemEntity>();
		_mana = 0;
		_buildingMap = new HashMap<Integer, List<BuildingEntity>>();
		_selectedBuilding = BuildingData.getBuildingByTag("clay-block")
				.getBuildingIdentifier();
	}

	public List<GolemEntity> getGolems()
	{
		return _golemList;
	}

	public List<GolemEntity> getNewGolems()
	{
		return _newGolemList;
	}

	public int getGolemCount()
	{
		return _golemList.size();
	}

	public void addGolem(GenericGolem golem_, double x_, double y_)
	{
		_newGolemList.add(new GolemEntity(golem_, (int) x_, (int) y_,
				_homeScreen));
	}

	public Map<Integer, List<BuildingEntity>> getBuildingMap()
	{
		return _buildingMap;
	}

	public boolean hasBuilding(GenericBuilding building_)
	{
		return getBuildingMap().get(building_.getBuildingIdentifier()) != null
				&& !getBuildingMap().get(building_.getBuildingIdentifier())
						.isEmpty();
	}

	public int buildingCount(GenericBuilding building_)
	{
		return getBuildingMap().get(building_.getBuildingIdentifier()) == null ? 0 : getBuildingMap()
				.get(building_.getBuildingIdentifier()).size();
	}

	public BuildingEntity getTileValue(int x_, int y_)
	{
		if (x_ > _xTileCount || y_ > _yTileCount || x_ < 0 || y_ < 0)
			return null;
		return _tileValues[x_][y_];
	}

	public void clearTile(int x_, int y_)
	{
		if (x_ > _xTileCount || y_ > _yTileCount || x_ < 0 || y_ < 0)
			return;
		_tileValues[x_][y_] = new BuildingEntity(
				BuildingData.getBuildingByTag("clay-block"), new Point(x_
						* ClayConstants.TILE_X, y_ * ClayConstants.TILE_Y),
				_homeScreen, "base");
	}

	public BuildingEntity[][] getTileValues()
	{
		return _tileValues;
	}

	public void addMana(int mana_)
	{
		_mana += mana_;
	}

	public void setMana(int mana_)
	{
		_mana = mana_;
	}

	public int getMana()
	{
		return _mana;
	}

	public void setSelectedMenu(AbstractMenu _selectedMenu)
	{
		this._selectedMenu = _selectedMenu;
	}
	
	public AbstractMenu getSelectedMenu()
	{
		return _selectedMenu;
	}

	public void setSelectedBuilding(int selectedBuilding_)
	{
		_selectedBuilding = selectedBuilding_;
	}

	public int getSelectedBuilding()
	{
		return _selectedBuilding;
	}

	public int getAdjustedX()
	{
		return _adjustedX;
	}

	public boolean isInterfaceToggle()
	{
		return _interfaceToggle;
	}

	public void setInterfaceToggle(boolean interfaceToggle_)
	{
		_interfaceToggle = interfaceToggle_;
	}

	// Architecture
	private BuildingEntity[][] _tileValues;

	// Golems
	private List<GolemEntity> _golemList;
	private List<GolemEntity> _newGolemList;

	private int _mana;

	// Interface
	private AbstractMenu _selectedMenu;
	private int _selectedBuilding;
	private int _adjustedX = ClayConstants.DEFAULT_MAP_WIDTH
			- ClayConstants.DEFAULT_INTERFACE_WIDTH;
	private boolean _interfaceToggle;

	private int _xTileCount;
	private int _yTileCount;

	private Map<Integer, List<BuildingEntity>> _buildingMap;

}
