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
import city.ai.Item;
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
		if (commandAndParams[0].equals(ClayConstants.BEHAVIOR_COMMAND_SEEK))
		{
			AbstractEntity entity = (AbstractEntity) params_[Integer
					.parseInt(commandAndParams[1])];
			if (executingEntity_.getPoint().equals(entity.getPoint()))
				executingEntity_.instructionComplete();
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
		}
		if (commandAndParams[0]
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_GENERIC_BUILDING))
		{
			String tag = (String) params_[Integer.parseInt(commandAndParams[1])];
			BuildingEntity entityAtPoint = model.getTileValue(
					executingEntity_.getGridX(),
					executingEntity_.getGridY());
			if (entityAtPoint != null
					&& entityAtPoint.getBuildingTag().equals(tag))
			{
				executingEntity_.instructionComplete();
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
		}
		else if (commandAndParams[0]
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_STORAGE))
		{
			BuildingEntity currentBuilding = executingEntity_.getModel()
					.getTileValue(
							executingEntity_.getGridX(),
							executingEntity_.getGridY());
			if (currentBuilding != null && currentBuilding.isStorageAvailable())
				executingEntity_.instructionComplete();
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
		}
		if (commandAndParams[0]
				.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_BUILDING))
		{
			String tag = (String) params_[Integer.parseInt(commandAndParams[1])];
			BuildingEntity entityAtPoint = model.getTileValue(
					executingEntity_.getGridX(),
					executingEntity_.getGridY());
			if (entityAtPoint != null
					&& entityAtPoint.getBuildingTag().equals(tag))
			{
				executingEntity_.instructionComplete();
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
		}

		else if (commandAndParams[0]
				.equals(ClayConstants.BEHAVIOR_COMMAND_BUILD))
			complete = _build(
					executingEntity_,
					model,
					commandAndParams,
					params_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_GOLEM))
			complete = _createGolem(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_HIDE))
			complete = _hide(executingEntity_, model, commandAndParams);

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
			executingEntity_.instructionComplete();
		}
		else
			executingEntity_.setTickAndRate(entity.getBuildTime(), 1);
		return false;
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
