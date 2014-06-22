package models;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.ClayConstants;
import screens.AbstractScreen;
import city.generics.GenericBuilding;
import city.generics.GenericGolem;
import city.generics.GenericItem;
import city.generics.data.BuildingData;
import city.generics.data.ItemData;
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
		_itemInventory = new HashMap<String, Map<String, Integer>>();
		_itemRatios = new HashMap<String, Map<String, Integer>>();
		_currentItemRatios = new HashMap<String, Map<String, Integer>>();
		initItems();
	}

	private void initItems()
	{
		Set<String> items = ItemData.getAllItemTags();
		for (String itemTag : items)
		{
			GenericItem item = ItemData.getItem(itemTag);
			Map<String, Integer> familyMap = _itemRatios.get(item
					.getItemFamily());
			if (familyMap == null)
			{
				familyMap = new HashMap<String, Integer>();
				_itemRatios.put(item.getItemFamily(), familyMap);
			}
			if (item.isFamilyHead())
				familyMap.put(itemTag, 100);
			else
				familyMap.put(itemTag, 0);
		}
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
		return _buildingMap.get(building_.getBuildingIdentifier()) != null
				&& !_buildingMap.get(building_.getBuildingIdentifier())
						.isEmpty();
	}

	public int buildingCount(GenericBuilding building_)
	{
		return _buildingMap.get(building_.getBuildingIdentifier()) == null ? 0 : _buildingMap
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
		BuildingEntity building = _tileValues[x_][y_];
		if (building != null
				&& !building.getBuildingTag().equals(
						ClayConstants.DEFAULT_TILE_TYPE))
		{
			Integer identifier = building.getIdentifier();
			_buildingMap.get(identifier).remove(building);
		}
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

	public void setSelectedMenu(AbstractMenu selectedMenu_)
	{
		_selectedMenu = selectedMenu_;
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

	public void updateItemInventory(Map<String, Map<String, Integer>> itemInventory_)
	{
		_itemInventory = itemInventory_;
	}

	public Map<String, Map<String, Integer>> getItemInventory()
	{
		return _itemInventory;
	}

	public Map<String, Map<String, Integer>> getItemRatios()
	{
		return _itemRatios;
	}

	public Map<String, Map<String, Integer>> getCurrentItemRatios()
	{
		return _currentItemRatios;
	}

	public void setCurrentItemRatios(Map<String, Map<String, Integer>> currentItemRatios_)
	{
		_currentItemRatios = currentItemRatios_;
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

	private int _xTileCount;
	private int _yTileCount;

	private Map<String, Map<String, Integer>> _itemInventory;
	private Map<String, Map<String, Integer>> _itemRatios;
	private Map<String, Map<String, Integer>> _currentItemRatios;

	private Map<Integer, List<BuildingEntity>> _buildingMap;

}
