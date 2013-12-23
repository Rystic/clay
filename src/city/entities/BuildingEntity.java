package city.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ClayConstants;
import models.CityModel;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.newdawn.slick.opengl.Texture;

import screens.AbstractScreen;
import city.ai.objects.Item;
import city.generics.GenericBuilding;
import city.processes.BuildingTickProcess;
import city.util.MapUpdateEvent;
import data.BuildingData;

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
		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void onEvent(MapUpdateEvent update)
	{
		if (update.getHomeScreen().equals(_homeScreen))
		{
			if (!_building.getTransform().isEmpty())
			{
				GenericBuilding newBuilding = _building.transform(this);
				if (newBuilding != null)
				{
					_building = newBuilding;
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
		_texture = _building.getTexture(
				_building.calculateTexture(this),
				_position);
	}

	public Texture getTexture()
	{
		if (!_built)
			return BuildingData._unbuiltTexture;
		return _texture;
	}

	public int getIdentifier()
	{
		return _building.getBuildingIdentifier();
	}

	public int getBuildTime()
	{
		return _buildTime;
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
		return _building.getScalableUpwards(_position);
	}

	public boolean isDownwardScalable()
	{
		return _building.getScalableDownards(_position);
	}

	public boolean isDiagonalScalable()
	{
		return _building.getScalableDiagonal(_position);
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

	public boolean isBuilt()
	{
		boolean tileIsBuilt = _built;
		if (_allBuildingTiles != null && tileIsBuilt && _isBase)
		{
			for (BuildingEntity building : _allBuildingTiles)
			{
				if (building.equals(this)) continue;
				if (!building.isBuilt())
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

	public void consumeClaimed()
	{
		consume(_claimedItems);
		_claimedItems.clear();
	}

	public void release()
	{
		for (Item item : _claimedItems)
		{
			item.release();
		}
		_claimedItems.clear();
		_claimingGolem = null;
	}

	public void claimItem(Item item_)
	{
		item_.setClaimingBuilding(this);
		item_.setItemIdentifier(_claimedItems.size());
		_claimedItems.add(item_);
	}

	public boolean claimHeldItem(Item item_)
	{
		for (Item item : _heldItems)
		{
			if (item.equals(item_))
			{
				claimItem(item);
				return true;
			}
		}
		return false;
	}

	public List<Item> getClaimedItems()
	{
		return _claimedItems;
	}

	public void setAllTiles(List<BuildingEntity> allBuildingTiles_)
	{
		_allBuildingTiles = allBuildingTiles_;
	}

	public void deleteBuilding()
	{
		BuildingTickProcess process = (BuildingTickProcess) _homeScreen
				.getProcess(BuildingTickProcess.class);
		CityModel model = (CityModel) _homeScreen.getModel();
		if (_allBuildingTiles == null)
		{
			_allBuildingTiles = new ArrayList<BuildingEntity>();
			_allBuildingTiles.add(this);
		}

		for (BuildingEntity building : _allBuildingTiles)
		{
			building.release();
			process.unregister(building);
			model.clearTile(building.getGridX(), building.getGridY());
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof BuildingEntity))
			return false;
		BuildingEntity entity = (BuildingEntity) o;
		return entity.getPoint().equals(_point)
				&& entity.getIdentifier() == _building.getBuildingIdentifier();
	}

	@Override
	public int hashCode()
	{
		return _building.hashCode();
	}

	private GenericBuilding _building;

	private List<BuildingEntity> _allBuildingTiles;

	private final Point _point;

	private Texture _texture;

	private GolemEntity _claimingGolem;

	private List<Item> _claimedItems;

	private String _position;

	private int _buildTime;
	private int _tickTime;

	private boolean _built;
	private boolean _tickReset;
	private boolean _isBase;

}
