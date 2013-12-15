package city.ai.calculators;

import java.awt.Point;
import java.util.List;
import java.util.Queue;

import main.ClayConstants;
import models.CityModel;
import city.ai.objects.Item;
import city.entities.AbstractEntity;
import city.entities.BuildingEntity;
import city.entities.GolemEntity;
import city.util.SearchUtil;
import data.GolemData;
import data.ItemData;

public class BehaviorInstructionCalculator
{
	public static void executeBehavior(GolemEntity executingEntity_, String currentCommand_, Object[] params_)
	{
		CityModel model = (CityModel) executingEntity_.getModel();
		String[] commandAndParams = currentCommand_.split(":");
		String com = commandAndParams[0];
		boolean complete = false;

		if (com.equals(ClayConstants.BEHAVIOR_COMMAND_ADD_CLAY))
			complete = _addClay(executingEntity_, model, commandAndParams);

		if (com.equals(ClayConstants.BEHAVIOR_COMMAND_ADD_MANA))
			complete = _addMana(executingEntity_, model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_BUILD))
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

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CONSUME_CLAIMED))
			complete = _consumeClaimed(
					executingEntity_,
					model,
					commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_GOLEM))
			complete = _createClayGolem(
					executingEntity_,
					model,
					commandAndParams);

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

	private static boolean _addClay(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		executingEntity_.adjustClay(Double.parseDouble(commandParams_[1]));
		return true;
	}

	private static boolean _addMana(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		model_.addMana(Integer.parseInt(commandParams_[1]));
		return true;
	}

	private static boolean _build(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
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

	private static boolean _claimBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
	{
		String tag = (String) params_[Integer.parseInt(commandParams_[1])];
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

	private static boolean _claimItems(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
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
			if (claimedBuilding.claimHeldItem(searchItem))
				continue;
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

	private static boolean _consumeClaimed(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		BuildingEntity claimedBuilding = executingEntity_.getClaimedBuilding();
		claimedBuilding.consumeClaimed();
		return true;
	}

	private static boolean _createClayGolem(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		model_.addGolem(
				GolemData.getGolem(ClayConstants.GOLEM_CLAY),
				executingEntity_.getX(),
				executingEntity_.getY());
		return true;
	}

	private static boolean _hide(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		executingEntity_.setVisible(false);
		return true;
	}

	private static boolean _seek(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
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

	private static boolean _seekClaimedItems(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
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
					ClayConstants.SEARCH_CLAIMED_ITEM,
					item);
			if (path.isEmpty())
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
			return false;
		}
		return true;
	}

	private static boolean _seekGenericBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
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

	private static boolean _seekStorage(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
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

	private static boolean _show(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
	{
		executingEntity_.setVisible(true);
		return true;
	}

	private static boolean _storeAll(GolemEntity executingEntity_, CityModel model_, String[] commandParams_)
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

	private static boolean _storeItem(GolemEntity executingEntity_, CityModel model_, String[] params)
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

	private static boolean _takeItem(GolemEntity executingEntity_, CityModel model_, String[] commandParams_, Object[] params_)
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

	private static boolean _tick(GolemEntity executingEntity_, CityModel model_, String[] params)
	{
		Integer tick = Integer.parseInt(params[1]);
		if (executingEntity_.isTickComplete())
			return true;
		else
			executingEntity_.setTickAndRate(tick, 1);
		return false;
	}

}
