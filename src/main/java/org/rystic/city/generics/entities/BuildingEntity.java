package org.rystic.city.generics.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.newdawn.slick.opengl.Texture;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.generics.objects.ConversionBehavior;
import org.rystic.city.generics.objects.Item;
import org.rystic.city.processes.BuildingTickProcess;
import org.rystic.city.processes.GolemBehaviorProcess;
import org.rystic.city.processes.HeatTickProcess;
import org.rystic.city.processes.StorageInventoryProcess;
import org.rystic.city.processes.TrafficProcess;
import org.rystic.city.util.MapUpdateEvent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class BuildingEntity extends AbstractEntity implements
		EventSubscriber<MapUpdateEvent>
{
	public BuildingEntity(GenericBuilding building_, Point point_,
			AbstractScreen homeScreen_, String position_)
	{
		super(point_, homeScreen_);
		_genericBuilding = building_;
		_point = point_;
		_claimedItems = new ArrayList<Item>();
		_model = homeScreen_.getModel();
		_position = position_;
		_buildTime = _genericBuilding.getBuildTime();
		_built = _buildTime == 0;
		_tickReset = false;
		_texture = _genericBuilding.getTexture(
				_genericBuilding.calculateTexture(this),
				_position);
		_tickTime = _genericBuilding.getTickStart();
		_isBase = _position.equals(ClayConstants.DEFAULT_BUILDING_POSITION);
		_activeBehaviors = new ArrayList<Behavior>();
		_activeConversions = new HashSet<String>();
		_allBuildingTiles = new ArrayList<BuildingEntity>();

		if (_built
				&& _isBase
				&& !_genericBuilding.getBuildingTag().equals(
						ClayConstants.DEFAULT_TILE_TYPE))
			((CityModel) _model).addToBuildingMap(this);

		_markedForDeletion = false;
		_heat = 0;
		_traffic = 0;

		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void onEvent(MapUpdateEvent update)
	{
		if (update.getHomeScreen().equals(_homeScreen))
		{
			if (!_genericBuilding.getTransform().isEmpty())
			{
				GenericBuilding newBuilding = _genericBuilding.transform(
						new Point(getGridX(), getGridY()),
						((CityModel) _model).getTileValues());
				if (!newBuilding.equals(_genericBuilding))
				{
					_genericBuilding = newBuilding;
					if (_genericBuilding.getBuildTime() == 0)
						constructionComplete();
					List<Point> point = new ArrayList<Point>();
					point.add(getPoint());
					Map<Integer, Object> map = new HashMap<Integer, Object>();
					map.put(ClayConstants.EVENT_MAP_UPDATE, point);
					EventBus.publish(new MapUpdateEvent(_homeScreen, map));
				}
			}
			calculateTexture();
		}
	}

	@Override
	public void calculateTexture()
	{
		if (_tickTime == 0)
			_tickReset = _genericBuilding.tickReset(this);
		_state = _genericBuilding.calculateTexture(this);
		_texture = _genericBuilding.getTexture(_state, _position);
	}

	public Texture getTexture()
	{
		if (!_built)
		{
			if (!isSupportBlock())
			{
				if (_genericBuilding.getBuildingTag().equals(
						ClayConstants.FOUNDATION_BLOCK))
					return BuildingData._clayBlockFoundationTexture;
				else
					return BuildingData._constructionNonSupportTexture;
			}
			else
				return BuildingData._constructionTexture;
		}
		return _texture;
	}

	public int getBuildingIdentifier()
	{
		return _genericBuilding.getBuildingIdentifier();
	}

	public int getBuildTime()
	{
		return _buildTime;
	}

	public String getBuildingName()
	{
		return _genericBuilding.getBuildingName();
	}

	public String getBuildingTag()
	{
		return _genericBuilding.getBuildingTag();
	}

	public boolean isSupportBlock()
	{
		return _genericBuilding.isSupport();
	}

	public boolean isBridge()
	{
		return isBuilt() && _genericBuilding.isBridge();
	}

	public boolean isUpwardScalable()
	{
		return isBuilt() && _genericBuilding.getScalableUpwards(_position);
	}

	public boolean isDownwardScalable()
	{
		return isBuilt() && _genericBuilding.getScalableDownards(_position);
	}

	public boolean isDiagonalScalable()
	{
		return isBuilt() && _genericBuilding.getScalableDiagonal(_position);
	}

	public String getPosition()
	{
		return _position;
	}

	public boolean isPassable()
	{
		return _genericBuilding.isPassable();
	}

	public boolean isInUse()
	{
		return _claimingGolem != null;
	}

	public boolean isNatural()
	{
		return _genericBuilding.isNatural();
	}

	public String getExtraWeight()
	{
		return _genericBuilding.getExtraWeightConditions();
	}

	public String getState()
	{
		return _state;
	}

	public boolean isBuilt()
	{
		return isBuilt(true);
	}

	private boolean isBuilt(boolean firstSearch_)
	{
		boolean tileIsBuilt = _built;
		if (_allBuildingTiles.size() > 1 && tileIsBuilt && firstSearch_)
		{
			for (BuildingEntity building : _allBuildingTiles)
			{
				if (building.equals(this))
					continue;
				if (!building.isBuilt(false))
					return false;
			}
		}
		return tileIsBuilt;
	}

	public boolean isHouse()
	{
		return _genericBuilding.isHouse();
	}

	public boolean isBaseTile()
	{
		return _isBase;
	}

	public boolean isStorageAvailable()
	{
		return _genericBuilding.isStorage()
				&& _genericBuilding.getStorageCapacity() > _heldItems.size();
	}

	public void constructionComplete()
	{
		_built = true;
		if (_tickTime > 0)
			((BuildingTickProcess) _homeScreen
					.getProcess(BuildingTickProcess.class)).register(this);

		((TrafficProcess) _homeScreen.getProcess(TrafficProcess.class))
				.doUpdate();

		List<Point> point = new ArrayList<Point>();
		point.add(getPoint());

		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(ClayConstants.EVENT_MAP_UPDATE, point);
		if (isStorageAvailable())
			map.put(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE, true);
		if (_isBase
				&& !_genericBuilding.getBuildingTag().equals(
						ClayConstants.DEFAULT_TILE_TYPE))
			((CityModel) _model).addToBuildingMap(this);

		EventBus.publish(new MapUpdateEvent(_homeScreen, map));
	}

	public void tick()
	{
		if (_tickTime > 0)
		{
			_tickTime--;
			if (_tickTime == 0
					&& _position
							.equals(ClayConstants.DEFAULT_BUILDING_POSITION))
				_genericBuilding.tickFinished(this);
		}
	}

	public boolean isTickFinished()
	{
		return _tickTime == 0;
	}

	public boolean shouldTickReset()
	{
		return _tickReset;
	}

	public void tickReset()
	{
		_tickTime = _genericBuilding.getTickStart();
		_tickReset = false;
		calculateTexture();
	}

	public List<Item> getClaimedItems()
	{
		return _claimedItems;
	}

	public String getConstructionItems()
	{
		return _genericBuilding.getConstructionItems();
	}

	public void setAllTiles(List<BuildingEntity> allBuildingTiles_)
	{
		_allBuildingTiles = allBuildingTiles_;
	}

	public String getBuildingPatternForTile()
	{
		return _genericBuilding.getValidPlacementMap().get(_position);
	}

	public void markForDeletion()
	{
		if (_markedForDeletion)
			return;
		_markedForDeletion = true;
		for (BuildingEntity building : _allBuildingTiles)
		{
			building.markForDeletion();
		}
		releaseAll();
		GolemBehaviorProcess behaviorProcess = (GolemBehaviorProcess) _homeScreen
				.getProcess(GolemBehaviorProcess.class);
		behaviorProcess.queueBehavior(new Behavior(BehaviorData
				.getBehavior("deconstruct-building"), this));
	}

	public void deleteBuilding()
	{
		if (!_genericBuilding.getBuildingTag().equals(
				ClayConstants.DEFAULT_TILE_TYPE))
		{
			for (BuildingEntity entity_ : _allBuildingTiles)
				entity_.removeBuilding(false);
		}
	}

	public void deconstructBuilding()
	{
		if (!_genericBuilding.getBuildingTag().equals(
				ClayConstants.DEFAULT_TILE_TYPE))
		{
			for (BuildingEntity entity_ : _allBuildingTiles)
			{
				entity_.removeBuilding(true);
			}
		}
	}

	private void removeBuilding(boolean deconstruct_)
	{
		unregisterAll();
		releaseAll();
		CityModel model = (CityModel) _homeScreen.getModel();
		if (deconstruct_)
			model.deconstructTile(getGridX(), getGridY());
		else
			model.clearTile(getGridX(), getGridY());
	}

	private void unregisterAll()
	{
		BuildingTickProcess behaviorTickProcess = (BuildingTickProcess) _homeScreen
				.getProcess(BuildingTickProcess.class);
		HeatTickProcess heatTickProcess = (HeatTickProcess) _homeScreen
				.getProcess(HeatTickProcess.class);
		behaviorTickProcess.unregister(this);
		heatTickProcess.unregister(this);
	}

	private void releaseAll()
	{
		if (_heldItems.size() > 0)
		{
			for (Item item : _heldItems)
			{
				if (item.getClaimingBuilding() != null
						&& !item.getClaimingBuilding().equals(this))
					item.getClaimingBuilding().claimedItemDestroyed();
			}
			((StorageInventoryProcess) _homeScreen
					.getProcess(StorageInventoryProcess.class))
					.requestInventoryUpdate();
		}

		releaseClaimedItems();

		for (Behavior behavior : _activeBehaviors)
		{
			behavior.obsolete();
		}

		_claimingGolem = null;

		_activeBehaviors.clear();
		_activeConversions.clear();
		// TODO make sure active conversions map is clearing.
	}

	public void releaseClaimedItems()
	{
		for (Item item : _claimedItems)
		{
			item.release();
		}
		_claimedItems.clear();
	}

	public void claimedItemDestroyed()
	{
		for (Item item : _claimedItems)
		{
			item.release();
		}
		if (_claimingGolem != null)
			_claimingGolem
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM);
		else
		{
			System.out.println("Why is the claiming golem null?");
		}
	}

	public void setClaimingGolem(GolemEntity claimingGolem_)
	{
		_claimingGolem = claimingGolem_;
	}

	public Item getItem(Item item_)
	{
		for (Item item : _heldItems)
		{
			if (item.equals(item_))
			{
				return item;
			}
		}
		return null;
	}

	public void claimItem(Item item_)
	{
		item_.setClaimingBuilding(this);
		item_.setItemIdentifier(_claimedItems.size());
		_claimedItems.add(item_);
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
	}

	public void consumeClaimed()
	{
		consume(_claimedItems);
		_claimedItems.clear();
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
	}

	public boolean claimHeldItem(Item item_)
	{
		for (Item item : _heldItems)
		{
			if (item.equals(item_))
			{
				claimItem(item);
				((StorageInventoryProcess) _homeScreen
						.getProcess(StorageInventoryProcess.class))
						.requestInventoryUpdate();
				return true;
			}
		}
		return false;
	}

	public void addActiveBehavior(Behavior behavior_)
	{
		_activeBehaviors.add(behavior_);
		if (behavior_ instanceof ConversionBehavior)
			_activeConversions.add(((ConversionBehavior) behavior_)
					.getConversionKey());
	}

	public void removeActiveBehavior(Behavior behavior_)
	{
		if (!_activeBehaviors.contains(behavior_))
			System.out
					.println("Building trying to remove active behavior it doesn't have.");
		_activeBehaviors.remove(behavior_);
		if (behavior_ instanceof ConversionBehavior)
			_activeConversions.remove(((ConversionBehavior) behavior_)
					.getConversionKey());
	}

	public boolean hasActiveConversion(String conversionKey_)
	{
		return _activeConversions.contains(conversionKey_);
	}

	@Override
	public void generate(Item item_)
	{
		super.generate(item_);
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
	}

	@Override
	public void generate(List<Item> items_)
	{
		super.generate(items_);
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
	}

	@Override
	public boolean consume(Item item_)
	{
		boolean consume;
		if (_genericBuilding.isStorage() && !isStorageAvailable())
		{
			consume = super.consume(item_);
			if (isStorageAvailable())
			{
				Map<Integer, Object> map = new HashMap<Integer, Object>();
				map.put(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE, true);
				EventBus.publish(new MapUpdateEvent(_homeScreen, map));
			}
			removeObsoleteHarvestTasks();
			((StorageInventoryProcess) _homeScreen
					.getProcess(StorageInventoryProcess.class))
					.requestInventoryUpdate();
			return consume;
		}
		consume = super.consume(item_);
		removeObsoleteHarvestTasks();
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
		return consume;
	}

	@Override
	public boolean consume(List<Item> items_)
	{
		boolean consumed;
		if (_genericBuilding.isStorage() && !isStorageAvailable())
		{
			consumed = super.consume(items_);
			if (isStorageAvailable())
			{
				Map<Integer, Object> map = new HashMap<Integer, Object>();
				map.put(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE, true);
				EventBus.publish(new MapUpdateEvent(_homeScreen, map));
			}
			removeObsoleteHarvestTasks();
			((StorageInventoryProcess) _homeScreen
					.getProcess(StorageInventoryProcess.class))
					.requestInventoryUpdate();
			return consumed;
		}
		consumed = super.consume(items_);
		removeObsoleteHarvestTasks();
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
		return consumed;
	}

	@Override
	public List<Item> consumeAllItemType(Item item_)
	{
		List<Item> consumed;
		if (_genericBuilding.isStorage() && !isStorageAvailable())
		{
			consumed = super.consumeAllItemType(item_);
			if (isStorageAvailable())
			{
				Map<Integer, Object> map = new HashMap<Integer, Object>();
				map.put(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE, true);
				EventBus.publish(new MapUpdateEvent(_homeScreen, map));
			}
			removeObsoleteHarvestTasks();
			((StorageInventoryProcess) _homeScreen
					.getProcess(StorageInventoryProcess.class))
					.requestInventoryUpdate();
			return consumed;
		}
		consumed = super.consumeAllItemType(item_);
		removeObsoleteHarvestTasks();
		((StorageInventoryProcess) _homeScreen
				.getProcess(StorageInventoryProcess.class))
				.requestInventoryUpdate();
		return consumed;
	}

	public void removeObsoleteHarvestTasks()
	{
		List<Behavior> obsoleteBehaviors = new ArrayList<Behavior>();
		for (Behavior behavior : _activeBehaviors)
		{
			if (behavior.checkIfHarvestObsolete())
			{
				behavior.obsolete();
				obsoleteBehaviors.add(behavior);
			}
		}
		_activeBehaviors.removeAll(obsoleteBehaviors);
	}

	public void highlightAllForDeletion()
	{
		for (BuildingEntity building : _allBuildingTiles)
		{
			building.setHighlightedForDeletion(true);
		}
	}

	public void setHighlightedForDeletion(boolean isHighlighted_)
	{
		_isHighlighted = isHighlighted_;
	}

	public boolean isHighlightedForDeletion()
	{
		return _markedForDeletion || _isHighlighted;
	}

	public void permeateHeat(int heat_)
	{
		Queue<BuildingEntity> openSet = new ArrayBlockingQueue<BuildingEntity>(
				256);
		Set<BuildingEntity> closedSet = new HashSet<BuildingEntity>();
		Map<BuildingEntity, Integer> degreeFromSource = new HashMap<BuildingEntity, Integer>();
		Map<BuildingEntity, Integer> rawHeat = new HashMap<BuildingEntity, Integer>();
		openSet.addAll(_allBuildingTiles);
		for (BuildingEntity building : openSet)
		{
			degreeFromSource.put(building, 0);
			rawHeat.put(building, heat_);
		}

		BuildingEntity[][] tiles = ((CityModel) _model).getTileValues();
		while (!openSet.isEmpty())
		{
			BuildingEntity building = openSet.poll();
			if (openSet.contains(building) || closedSet.contains(building))
				continue;
			closedSet.add(building);
			int x = building.getGridX();
			int y = building.getGridY();
			int prevDegree = degreeFromSource.get(building);
			int heat = rawHeat.get(building);
			boolean isBuilt = building.isBuilt();
			int addedHeat = (heat / (prevDegree + 1))
					- (isBuilt ? _genericBuilding.getHeatResistance() : 0);
			if (addedHeat <= (isBuilt ? _genericBuilding.getInsulation() : 0))
				continue;
			building.addHeat(addedHeat);
			if (x - 1 >= 0 && tiles[x - 1][y] != null
					&& !openSet.contains(tiles[x - 1][y]))
			{
				degreeFromSource.put(tiles[x - 1][y], prevDegree + 1);
				rawHeat.put(
						tiles[x - 1][y],
						heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x - 1][y]);

			}
			if (x + 1 < tiles.length && tiles[x + 1][y] != null
					&& !openSet.contains(tiles[x + 1][y]))
			{
				degreeFromSource.put(tiles[x + 1][y], prevDegree + 1);
				rawHeat.put(
						tiles[x + 1][y],
						heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x + 1][y]);
			}
			if (y - 1 >= 0 && tiles[x][y - 1] != null
					&& !openSet.contains(tiles[x][y - 1]))
			{
				degreeFromSource.put(tiles[x][y - 1], prevDegree + 1);
				rawHeat.put(
						tiles[x][y - 1],
						heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x][y - 1]);
			}
			if (y + 1 < tiles[0].length && tiles[x][y + 1] != null
					&& !openSet.contains(tiles[x][y + 1]))
			{
				degreeFromSource.put(tiles[x][y + 1], prevDegree + 1);
				rawHeat.put(
						tiles[x][y + 1],
						heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x][y + 1]);
			}
		}
	}

	private void addHeat(int heat_)
	{
		if (isNatural())
			return;
		((HeatTickProcess) _homeScreen.getProcess(HeatTickProcess.class))
				.register(this);
		if (isOverheated()
				&& !_genericBuilding.getBuildingTag().equals(
						ClayConstants.DEFAULT_TILE_TYPE))
		{
			_heatDamage += heat_;
			if (_heatDamage > _genericBuilding.getThirdHeadThreshold())
			{
				deleteBuilding();
				return;
			}
			boolean activeHeatDamageRepair = false;
			for (Behavior behavior : _activeBehaviors)
			{
				if (behavior.getBehaviorTag().equals("repair-heat-damage"))
				{
					activeHeatDamageRepair = true;
					break;
				}
			}
			if (!activeHeatDamageRepair)
			{
				GolemBehaviorProcess gbp = (GolemBehaviorProcess) _homeScreen
						.getProcess(GolemBehaviorProcess.class);
				Behavior behavior = new Behavior(
						BehaviorData.getBehavior("repair-heat-damage"), this);
				behavior.setAssigningBuilding(this);
				_activeBehaviors.add(behavior);
				gbp.queueBehavior(behavior);
			}
		}
		_heat += heat_;
	}

	public int getCoolingRate()
	{
		return _genericBuilding.getCoolingRate();
	}

	public void heatTick()
	{
		if (_heat > _heatDamage)
			_heat--;
	}

	public void repairHeatDamage()
	{
		_heatDamage = 0;
	}

	public boolean isHeatedExcludeHeatDamage()
	{
		return _heat > _heatDamage;
	}

	public boolean isHeatedIncludeHeatDamage()
	{
		return _heat > 0;
	}

	public boolean isOverheated()
	{
		return _heat >= _genericBuilding.getFirstHeatThreshold();
	}

	public int getHeatAbsorb()
	{
		return _genericBuilding.getHeatAbsorb();
	}

	public void setConstructionBuilding(BuildingEntity constructionBuilding_)
	{
		_constructionBuilding = constructionBuilding_;
	}

	public BuildingEntity getConstructionBuilding()
	{
		return _constructionBuilding;
	}

	public void addTraffic(int traffic_)
	{
		_traffic += traffic_;
	}

	public void clearTraffic()
	{
		_traffic = 0;
	}

	public int getNetTrafficWeight()
	{
		return _traffic - (_built ? _genericBuilding.getTrafficThreshold() : 0);
	}

	public int getTraffic()
	{
		return _traffic;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof BuildingEntity))
			return false;
		BuildingEntity entity = (BuildingEntity) o;
		if (entity.getPoint().equals(_point))
		{
			if (entity.getBuildingIdentifier() == _genericBuilding
					.getBuildingIdentifier())
			{
				return true;
			}
			else
			{
				// System.out.println("Mapping Error. Expecting " +
				// entity.getBuildingName() + " on tile " + entity.getPoint() +
				// ". Instead, found " + _building.getBuildingName());
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return _genericBuilding.hashCode();
	}

	private GenericBuilding _genericBuilding;

	private List<BuildingEntity> _allBuildingTiles;

	private List<Behavior> _activeBehaviors;

	private Set<String> _activeConversions;

	private final Point _point;

	private Texture _texture;

	private GolemEntity _claimingGolem;

	private List<Item> _claimedItems;

	private BuildingEntity _constructionBuilding;

	private String _position;
	private String _state;

	private int _buildTime;
	private int _tickTime;
	private int _heat;
	private int _heatDamage;
	private int _traffic;

	private boolean _built;
	private boolean _tickReset;
	private boolean _isBase;
	private boolean _isHighlighted;
	private boolean _markedForDeletion;

}
