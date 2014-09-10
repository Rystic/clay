package city.generics.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import main.ClayConstants;
import models.CityModel;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.newdawn.slick.opengl.Texture;

import screens.AbstractScreen;
import city.generics.GenericBuilding;
import city.generics.data.BehaviorData;
import city.generics.data.BuildingData;
import city.generics.objects.Behavior;
import city.generics.objects.Item;
import city.processes.BuildingTickProcess;
import city.processes.GolemBehaviorProcess;
import city.processes.HeatTickProcess;
import city.processes.StorageInventoryProcess;
import city.util.MapUpdateEvent;

public class BuildingEntity extends AbstractEntity implements
		EventSubscriber<MapUpdateEvent>
{
	public BuildingEntity(GenericBuilding building_, Point point_,
			AbstractScreen homeScreen_, String position_)
	{
		super(point_, homeScreen_);
		_building = building_;
		_point = point_;
		_claimedItems = new ArrayList<Item>();
		_model = homeScreen_.getModel();
		_position = position_;
		_buildTime = _building.getBuildTime();
		_built = _buildTime == 0;
		_tickReset = false;
		_texture = _building.getTexture(
				_building.calculateTexture(this),
				_position);
		_tickTime = _building.getTickStart();
		_isBase = _position.equals(ClayConstants.DEFAULT_BUILDING_POSITION);
		_activeBehaviors = new ArrayList<Behavior>();

		if (_built
				&& _isBase
				&& _building.getBuildingTag().equals(
						ClayConstants.DEFAULT_TILE_TYPE))
			((CityModel) _model).addToBuildingMap(this);

		_markedForDeletion = false;
		_heat = 0;

		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void onEvent(MapUpdateEvent update)
	{
		if (update.getHomeScreen().equals(_homeScreen))
		{
			if (!_building.getTransform().isEmpty())
			{
				GenericBuilding newBuilding = _building.transform(
						new Point(getGridX(), getGridY()),
						((CityModel) _model).getTileValues());
				if (!newBuilding.equals(_building))
				{
					_building = newBuilding;
					if (_building.getBuildTime() == 0)
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
			_tickReset = _building.tickReset(this);
		_state = _building.calculateTexture(this);
		_texture = _building.getTexture(_state, _position);
	}

	public Texture getTexture()
	{
		if (!_built)
			return BuildingData._unbuiltTexture;
		return _texture;
	}

	public int getBuildingIdentifier()
	{
		return _building.getBuildingIdentifier();
	}

	public int getBuildTime()
	{
		return _buildTime;
	}

	public String getBuildingName()
	{
		return _building.getBuildingName();
	}

	public String getBuildingTag()
	{
		return _building.getBuildingTag();
	}

	public boolean isSupportBlock()
	{
		return _building.isSupport();
	}

	public boolean isUpwardScalable()
	{
		return isBuilt() && _building.getScalableUpwards(_position);
	}

	public boolean isDownwardScalable()
	{
		return isBuilt() && _building.getScalableDownards(_position);
	}

	public boolean isDiagonalScalable()
	{
		return isBuilt() && _building.getScalableDiagonal(_position);
	}

	public boolean isPassable()
	{
		return _building.isPassable();
	}

	public boolean isInUse()
	{
		return _claimingGolem != null;
	}

	public boolean isValid()
	{
		return _building.isValid(this);
	}

	public boolean isNatural()
	{
		return _building.isNatural();
	}

	public String getExtraWeight()
	{
		return _building.getExtraWeightConditions();
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
		if (_allBuildingTiles != null && tileIsBuilt && firstSearch_)
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
		return _building.isHouse();
	}

	public boolean isBaseTile()
	{
		return _isBase;
	}

	public boolean isStorageAvailable()
	{
		return _building.isStorage()
				&& _building.getStorageCapacity() > _heldItems.size();
	}

	public void constructionComplete()
	{
		_built = true;
		if (_tickTime > 0)
			((BuildingTickProcess) _homeScreen
					.getProcess(BuildingTickProcess.class)).register(this);

		List<Point> point = new ArrayList<Point>();
		point.add(getPoint());

		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(ClayConstants.EVENT_MAP_UPDATE, point);
		if (isStorageAvailable())
			map.put(ClayConstants.EVENT_STORAGE_AVAILABLE_UPDATE, true);
		if (_isBase
				&& !_building.getBuildingTag().equals(
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
				_building.tickFinished(this);
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
		_tickTime = _building.getTickStart();
		_tickReset = false;
		calculateTexture();
	}

	public List<Item> getClaimedItems()
	{
		return _claimedItems;
	}

	public String getConstructionItems()
	{
		return _building.getConstructionItems();
	}

	public void setAllTiles(List<BuildingEntity> allBuildingTiles_)
	{
		_allBuildingTiles = allBuildingTiles_;
	}

	public void markForDeletion(boolean first_)
	{
		if (_markedForDeletion)
			return;
		_markedForDeletion = true;
		if (!first_)
			return;
		if (_allBuildingTiles == null)
		{
			_allBuildingTiles = new ArrayList<BuildingEntity>();
			_allBuildingTiles.add(this);
		}
		BuildingTickProcess tickProcess = (BuildingTickProcess) _homeScreen
				.getProcess(BuildingTickProcess.class);
		for (BuildingEntity building : _allBuildingTiles)
		{
			building.releaseAll();
			tickProcess.unregister(building);
			building.markForDeletion(false);
		}
		GolemBehaviorProcess behaviorProcess = (GolemBehaviorProcess) _homeScreen
				.getProcess(GolemBehaviorProcess.class);
		behaviorProcess.queueBehavior(new Behavior(BehaviorData
				.getBehavior("deconstruct-building"), this));
	}

	public void deleteBuilding()
	{
		removeBuilding(false);
	}

	public void deconstructBuilding()
	{
		removeBuilding(true);
	}

	public String getBuildingPatternForTile()
	{
		return _building.getValidPlacementMap().get(_position);
	}

	private void removeBuilding(boolean deconstruct_)
	{
		//TODO cancel active behaviors
		BuildingTickProcess behaviorTickProcess = (BuildingTickProcess) _homeScreen
				.getProcess(BuildingTickProcess.class);
		HeatTickProcess heatTickProcess = (HeatTickProcess) _homeScreen
				.getProcess(HeatTickProcess.class);
		CityModel model = (CityModel) _homeScreen.getModel();
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
		if (_allBuildingTiles == null)
		{
			_allBuildingTiles = new ArrayList<BuildingEntity>();
			_allBuildingTiles.add(this);
		}

		for (BuildingEntity building : _allBuildingTiles)
		{
			building.releaseAll();
			behaviorTickProcess.unregister(building);
			heatTickProcess.unregister(building);
			if (deconstruct_)
				model.deconstructTile(building.getGridX(), building.getGridY());
			else
				model.clearTile(building.getGridX(), building.getGridY());
		}

	}

	public void claimedItemDestroyed()
	{
		for (Item item : _claimedItems)
		{
			item.release();
		}
		_claimingGolem
				.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM);
	}

	public void releaseItems()
	{
		for (Item item : _claimedItems)
		{
			item.release();
		}
		_claimedItems.clear();
	}

	public void releaseAll()
	{
		for (Item item : _claimedItems)
		{
			item.release();
		}

		boolean golemFailed = false;
		for (Behavior behavior : _activeBehaviors)
		{
			if (behavior.getAssignedGolem() != null
					&& behavior.getAssignedGolem().equals(_claimingGolem))
				golemFailed = true;
			behavior.obsolete();
		}
		_activeBehaviors.clear();

		if (_claimingGolem != null && !golemFailed)
		{
			_claimingGolem
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_OBSOLETE);
		}

		_claimedItems.clear();
		_claimingGolem = null;

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
	}

	public void removeActiveBehavior(Behavior behavior_)
	{
		_activeBehaviors.remove(behavior_);
	}

	public boolean hasActiveBehavior()
	{
		return isBaseTile() && _activeBehaviors.size() > 0;
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
		if (_building.isStorage() && !isStorageAvailable())
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
		if (_building.isStorage() && !isStorageAvailable())
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
		if (_building.isStorage() && !isStorageAvailable())
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
		if (_allBuildingTiles == null)
			setHighlightedForDeletion(true);
		else
		{
			for (BuildingEntity building : _allBuildingTiles)
			{
				building.setHighlightedForDeletion(true);
			}
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
		if (_allBuildingTiles == null)
			openSet.add(this);
		else
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
					- (isBuilt ? _building.getHeatResistance() : 0);
			if (addedHeat <= (isBuilt ? _building.getInsulation() : 0))
				continue;
			building.addHeat(addedHeat);
			if (x - 1 >= 0 && tiles[x - 1][y] != null  && !openSet.contains(tiles[x - 1][y]))
			{
				degreeFromSource.put(tiles[x - 1][y], prevDegree + 1);
				rawHeat.put(tiles[x - 1][y], heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x - 1][y]);

			}
			if (x + 1 < tiles.length && tiles[x + 1][y] != null  && !openSet.contains(tiles[x + 1][y]))
			{
				degreeFromSource.put(tiles[x + 1][y], prevDegree + 1);
				rawHeat.put(tiles[x + 1][y], heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x + 1][y]);
			}
			if (y - 1 >= 0 && tiles[x][y - 1] != null  && !openSet.contains(tiles[x][y - 1]))
			{
				degreeFromSource.put(tiles[x][y - 1], prevDegree + 1);
				rawHeat.put(tiles[x][y - 1], heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x][y - 1]);
			}
			if (y + 1 < tiles[0].length && tiles[x][y + 1] != null && !openSet.contains(tiles[x][y + 1]))
			{
				degreeFromSource.put(tiles[x][y + 1], prevDegree + 1);
				rawHeat.put(tiles[x][y + 1], heat - (isBuilt ? building.getHeatAbsorb() : 0));
				openSet.add(tiles[x][y + 1]);
			}
		}
	}

	private void addHeat(int heat_)
	{
		if (isNatural()) return;
		((HeatTickProcess) _homeScreen.getProcess(HeatTickProcess.class))
				.register(this);
		if (isOverheated())
		{
			_heatDamage += heat_;
			if (_heatDamage > _building.getThirdHeadThreshold())
				deleteBuilding();
		}
		_heat += heat_;
	}
	
	public int getCoolingRate()
	{
		return _building.getCoolingRate();
	}

	public void heatTick()
	{
		if (_heat > _heatDamage)
			_heat--;
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
		return _heat >= _building.getFirstHeatThreshold();
	}

	public int getHeatAbsorb()
	{
		return _building.getHeatAbsorb();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof BuildingEntity))
			return false;
		BuildingEntity entity = (BuildingEntity) o;
		if (entity.getPoint().equals(_point))
		{
			 if (entity.getBuildingIdentifier() == _building
						.getBuildingIdentifier())
			 {
				 return true;
			 }
			 else
			 {
				 System.out.println("Mapping Error. Expecting " + entity.getBuildingName() + " on tile " + entity.getPoint() + ". Instead, found " + _building.getBuildingName());
			 }
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return _building.hashCode();
	}

	private GenericBuilding _building;

	private List<BuildingEntity> _allBuildingTiles;

	private List<Behavior> _activeBehaviors;

	private final Point _point;

	private Texture _texture;

	private GolemEntity _claimingGolem;

	private List<Item> _claimedItems;

	private String _position;
	private String _state;

	private int _buildTime;
	private int _tickTime;
	private int _heat;
	private int _heatDamage;

	private boolean _built;
	private boolean _tickReset;
	private boolean _isBase;
	private boolean _isHighlighted;
	private boolean _markedForDeletion;

}
