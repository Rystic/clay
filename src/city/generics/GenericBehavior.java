package city.generics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import main.ClayConstants;
import models.CityModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xml.ItemData;
import city.ai.objects.Item;
import city.entities.AbstractEntity;
import city.entities.BuildingEntity;
import city.entities.GolemEntity;
import city.util.SearchUtil;

public class GenericBehavior
{
	public GenericBehavior(Node node_)
	{
		Element eElement = (Element) node_;
		_behaviorName = eElement.getAttribute("BehaviorName");
		_behaviorTag = eElement.getAttribute("BehaviorTag");
		_code = eElement.getAttribute("code");
		_require = eElement.getAttribute("require");
		_weight = eElement.getAttribute("weight");
		_golemCost = eElement.getAttribute("golemCost");
		_isPersonalTask = eElement.getAttribute("personalTask").isEmpty() ? false : Boolean
				.parseBoolean(eElement.getAttribute("personalTask"));
		_limit = eElement.getAttribute("limit").isEmpty() ? -1 : Integer
				.parseInt(eElement.getAttribute("limit"));

		if (_isPersonalTask)
			_personalTasks.add(this);
	}

	public void executeBehavior(GolemEntity executingEntity_, String currentCommand_, Object[] params_)
	{
		CityModel model = (CityModel) executingEntity_.getModel();
		String[] commandAndParams = currentCommand_.split(":");
		String com = commandAndParams[0];
		boolean complete = false;

		if (com.equals(ClayConstants.BEHAVIOR_COMMAND_BUILD))
			complete = _build(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_BUILDING))
			complete = _claimBuilding(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_ITEMS))
			complete = _claimItems(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_GOLEM))
			complete = _createGolem(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_HIDE))
			complete = _hide(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK))
			complete = _seek(executingEntity_, model, commandAndParams, params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_CLAIMED_ITEMS))
			complete = _seekClaimedItems(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_GENERIC_BUILDING))
			complete = _seekGenericBuilding(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_STORAGE))
			complete = _seekStorage(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SHOW))
			complete = _show(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_STORE_ALL))
			complete = _storeAll(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_STORE_ITEM))
			complete = _storeItem(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_TAKE_ITEM))
			complete = _takeItem(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_TICK))
			complete = _tick(executingEntity_, model, commandAndParams);

		if (complete)
			executingEntity_.instructionComplete();
	}

	private boolean _build(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		BuildingEntity entity = (BuildingEntity) params_[Integer
				.parseInt(commandParams_[1])];
		if (executingEntity_.isTickComplete())
		{
			entity.constructionComplete();
			return true;
		}
		else
			executingEntity_.setTickAndRate(entity.getBuildTime(), 1);
		return false;
	}

	private boolean _claimBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		String tag = (String) ((BuildingEntity)params_[Integer.parseInt(commandParams_[1])]).getBuildingTag();
		Queue<Point> path = SearchUtil.searchIt(
				executingEntity_,
				executingEntity_.getHomeScreen(),
				ClayConstants.SEARCH_GENERIC_BUILDING_GOAL_ONLY,
				tag);
		if (path.isEmpty())
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			return false;
		}
		Point point = path.poll();
		BuildingEntity claimableBuilding = model_
				.getTileValue(point.x, point.y);
		executingEntity_.setClaimedBuilding(claimableBuilding);
		claimableBuilding.setClaimingGolem(executingEntity_);

		return true;
	}

	private boolean _claimItems(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		BuildingEntity claimedBuilding = executingEntity_.getClaimedBuilding();
		if (claimedBuilding == null)
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			return false;
		}
		for (int i = 1; i < commandParams_.length; i++)
		{
			Item searchItem = new Item(ItemData.getItem(commandParams_[i]));
			Queue<Point> path = SearchUtil.searchIt(
					claimedBuilding,
					claimedBuilding.getHomeScreen(),
					ClayConstants.SEARCH_ITEM_GOAL_ONLY,
					searchItem);
			if (path.isEmpty())
			{
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
				return false;
			}
			Point buildingPoint = path.poll();
			BuildingEntity holdingBuilding = model_.getTileValue(
					(int) buildingPoint.getX(),
					(int) buildingPoint.getY());
			Item item = holdingBuilding.getItem(searchItem);
			claimedBuilding.claimItem(item);
		}
		return true;
	}

	private boolean _createGolem(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		model_.addGolem(executingEntity_.getX(), executingEntity_.getY());
		return true;
	}

	private boolean _hide(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		executingEntity_.setVisible(false);
		return true;
	}

	private boolean _seek(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		AbstractEntity entity = (AbstractEntity) params_[Integer
				.parseInt(commandParams_[1])];
		if (executingEntity_.getPoint().equals(entity.getPoint()))
			return true;
		else
		{
			Queue<Point> path = SearchUtil.searchIt(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					ClayConstants.SEARCH_ENTITY,
					entity);
			if (path.isEmpty())
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
		}
		return false;
	}

	private boolean _seekClaimedItems(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		BuildingEntity claimedBuilding = executingEntity_.getClaimedBuilding();
		if (claimedBuilding == null)
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			return false;
		}
		for (Item item : claimedBuilding.getClaimedItems())
		{
			if (claimedBuilding.isHolding(item))
				continue;

			if (executingEntity_.isHolding(item))
			{
				if (executingEntity_.getPoint().equals(
						claimedBuilding.getPoint()))
				{
					executingEntity_.consume(item);
					claimedBuilding.generate(item);
					continue;
				}
				Queue<Point> path = SearchUtil.searchIt(
						executingEntity_,
						executingEntity_.getHomeScreen(),
						ClayConstants.SEARCH_ENTITY,
						claimedBuilding);
				if (path.isEmpty())
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
				else
					executingEntity_.addMoveInstructions(path);
				return false;
			}

			BuildingEntity entityAtPoint = model_.getTileValue(
					executingEntity_.getGridX(),
					executingEntity_.getGridY());

			if (entityAtPoint.isHolding(item))
			{
				entityAtPoint.consume(item);
				executingEntity_.generate(item);
				return false;
			}

			Queue<Point> path = SearchUtil.searchIt(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					ClayConstants.SEARCH_CLAIMED_ITEMS);
			if (path.isEmpty())
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
			return false;
		}
		return true;
	}

	private boolean _seekGenericBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		String tag = (String) params_[Integer.parseInt(commandParams_[1])];
		BuildingEntity entityAtPoint = model_.getTileValue(
				executingEntity_.getGridX(),
				executingEntity_.getGridY());
		if (entityAtPoint != null && entityAtPoint.getBuildingTag().equals(tag))
		{
			return true;
		}
		else
		{
			Queue<Point> path = SearchUtil.searchIt(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					ClayConstants.SEARCH_GENERIC_BUILDING,
					tag);
			if (path.isEmpty())
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
		}
		return false;
	}

	private boolean _seekStorage(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		BuildingEntity currentBuilding = executingEntity_.getModel()
				.getTileValue(
						executingEntity_.getGridX(),
						executingEntity_.getGridY());
		if (currentBuilding != null && currentBuilding.isStorageAvailable())
			return true;
		else
		{
			Queue<Point> path = SearchUtil.searchIt(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					ClayConstants.SEARCH_STORAGE);
			if (path.isEmpty())
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
		}
		return false;
	}

	private boolean _show(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		executingEntity_.setVisible(true);
		return true;
	}

	private boolean _storeAll(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		BuildingEntity storage = executingEntity_.getModel().getTileValue(
				executingEntity_.getGridX(),
				executingEntity_.getGridY());
		List<Item> items = executingEntity_.getCopyOfHeldItems();
		for (Item item : items)
		{
			if (storage.isStorageAvailable())
			{
				executingEntity_.consume(item);
				storage.generate(item);
			}
			else
				break;
		}
		return true;
	}

	private boolean _storeItem(GolemEntity executingEntity_, CityModel model_, String[] params)
	{
		Item item = new Item(ItemData.getItem(params[1]));
		BuildingEntity storage = executingEntity_.getModel().getTileValue(
				executingEntity_.getGridX(),
				executingEntity_.getGridY());
		if (executingEntity_.isHolding(item))
		{
			executingEntity_.consume(item);
			storage.generate(item);
			return true;
		}
		else
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_STORAGE);
		}
		return false;
	}

	private boolean _takeItem(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		Item item = new Item(ItemData.getItem(commandParams_[1]));
		AbstractEntity takenFrom = (AbstractEntity) params_[Integer
				.parseInt(commandParams_[2])];
		if (takenFrom.isHolding(item))
		{
			takenFrom.consume(item);
			executingEntity_.generate(item);
			return true;
		}
		else
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM);
		}
		return false;
	}

	private boolean _tick(GolemEntity executingEntity_, CityModel model_, String[] params)
	{
		Integer tick = Integer.parseInt(params[1]);
		if (executingEntity_.isTickComplete())
			return true;
		else
			executingEntity_.setTickAndRate(tick, 1);
		return false;
	}

	public int calculateRequired(CityModel model_, Object[] params_)
	{
		String[] require = _require.split(",");
		for (String requireCondition : require)
		{
			if (requireCondition.equals(ClayConstants.BEHAVIOR_REQUIRE_STORAGE))
			{
				AbstractEntity entity = (AbstractEntity) params_[0];
				Queue<Point> queue = SearchUtil.searchIt(
						entity,
						entity.getHomeScreen(),
						ClayConstants.SEARCH_STORAGE);
				if (queue.isEmpty())
					return ClayConstants.BEHAVIOR_FAILED_NO_STORAGE;
			}
		}
		return ClayConstants.BEHAVIOR_PASSED;
	}

	public int calculateBehaviorWeight(GolemEntity golem_, Object[] params_)
	{
		int finalWeight = 0;
		String[] weightConditions = _weight.split(",");

		for (String weightCondition : weightConditions)
		{
			if (weightCondition.equals(ClayConstants.WC_CLOSEST_TO_PINT))
			{
				AbstractEntity entity = (AbstractEntity) params_[0];
				Point entityPoint = entity.getPoint();
				Point golemPoint = golem_.getPoint();
				int xWeight = 3 * Math.abs(entityPoint.x - golemPoint.x);
				int yWeight = 8 * Math.abs(entityPoint.y - golemPoint.y);
				finalWeight += 100 - (xWeight + yWeight);
			}
		}
		return finalWeight;
	}

	public void calculateGolemCost(GolemEntity golem_)
	{
		String[] golemCost = _golemCost.split(",");
	}

	public int getLimit()
	{
		return _limit;
	}

	public String getBehaviorName()
	{
		return _behaviorName;
	}

	public String getBehaviorTag()
	{
		return _behaviorTag;
	}

	public String getCode()
	{
		return _code;
	}

	public Boolean isPersonalTask()
	{
		return _isPersonalTask;
	}

	public static List<GenericBehavior> getPersonalTasks()
	{
		return _personalTasks;
	}

	private static final List<GenericBehavior> _personalTasks = new ArrayList<GenericBehavior>();

	private final String _behaviorName;
	private final String _behaviorTag;
	private final String _code;
	private final String _require;
	private final String _weight;
	private final String _golemCost;
	private final Integer _limit;
	private final Boolean _isPersonalTask;
}
