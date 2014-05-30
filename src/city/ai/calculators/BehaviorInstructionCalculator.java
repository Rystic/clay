package city.ai.calculators;

import java.awt.Point;
import java.util.List;
import java.util.Queue;

import main.ClayConstants;
import models.CityModel;
import city.generics.data.BehaviorData;
import city.generics.data.GolemData;
import city.generics.data.ItemData;
import city.generics.entities.AbstractEntity;
import city.generics.entities.BuildingEntity;
import city.generics.entities.GolemEntity;
import city.generics.objects.Behavior;
import city.generics.objects.Item;
import city.processes.GolemBehaviorProcess;
import city.util.SearchUtil;

public class BehaviorInstructionCalculator
{
	public static void executeBehavior(GolemEntity executingEntity_, String currentCommand_, Object[] behaviorParams_)
	{
		CityModel model = (CityModel) executingEntity_.getModel();
		String[] commandAndParams = currentCommand_.split(":");
		String com = commandAndParams[0];
		boolean complete = false;

		if (com.equals(ClayConstants.BEHAVIOR_COMMAND_ADD_CLAY))
			complete = _addClay(executingEntity_, commandAndParams);

		if (com.equals(ClayConstants.BEHAVIOR_COMMAND_ADD_MANA))
			complete = _addMana(model, commandAndParams);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_BUILD))
			complete = _build(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CONSUME_CLAIMED))
			complete = _consumeClaimed(executingEntity_);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CONSUME_CLAIMED_CONSTRUCTION))
			complete = _consumeConstructionClaimed(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_GOLEM))
			complete = _createClayGolem(executingEntity_, model);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_HARVEST_ITEMS_ON_BUILDING))
			complete = _harvestItemsOnBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_);
		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_HIDE))
			complete = _hide(executingEntity_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_PRODUCE_ITEMS_ON_BUILDING))
			complete = _produceItemsOnBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_PRODUCE_ITEM_ON_GOLEM))
			complete = _produceItemOnGolem(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK))
			complete = _seek(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_CLAIMED_ITEMS))
			complete = _seekClaimedItems(executingEntity_, model);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_CLAIMED_CONSTRUCTION_ITEMS))
			complete = _seekClaimedConstructionItems(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_GENERIC_BUILDING))
			complete = _seekGenericBuilding(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_STORAGE))
			complete = _seekStorage(executingEntity_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SHOW))
			complete = _show(executingEntity_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_STORE_ALL))
			complete = _storeAll(executingEntity_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_STORE_ITEM))
			complete = _storeItem(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_TAKE_ITEM))
			complete = _takeItem(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_TICK))
			complete = _tick(executingEntity_, commandAndParams);

		if (complete)
			executingEntity_.instructionComplete();
	}

	public static int executeRequired(GolemEntity executingEntity_, String currentCommand_, Object[] behaviorParams_)
	{
		CityModel model = (CityModel) executingEntity_.getModel();
		String[] commandAndParams = currentCommand_.split(":");
		String com = commandAndParams[0];
		int passed = ClayConstants.BEHAVIOR_PASSED;
		if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_BUILDING))

		{
			if (!_claimBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_))
				passed = ClayConstants.BEHAVIOR_FAILED_NO_PATH;
		}
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_EMPTY_GENERIC_BUILDING))
		{
			passed = _claimGenericBuilding(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);
		}
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_GENERIC_BUILDING))
		{
			passed = _claimGenericBuilding(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);
		}
		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_HOUSE))

		{
			passed = _claimHouse(executingEntity_, model);
		}

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_ITEMS))
		{
			passed = _claimItems(executingEntity_, model, commandAndParams);
		}
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_STORAGE_EXISTS_FROM_ENTITY))
		{
			if (!_storageExistsFromEntity(commandAndParams, behaviorParams_))
			{
				if (!storageExists(model))
					passed = ClayConstants.BEHAVIOR_FAILED_NO_STORAGE;
				else
					passed = ClayConstants.BEHAVIOR_FAILED_NO_PATH;
			}
		}
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_CONSTRUCTION_ITEMS))
			passed = _claimConstructionItems(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CLAIM_PARAMETER_ITEMS))
			passed = _claimParameterItems(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_ENTITY_NOT_HOLDING_ITEM))
			passed = _entityNotHoldingItem(executingEntity_) ? ClayConstants.BEHAVIOR_PASSED : ClayConstants.BEHAVIOR_FAILED_INVALID_GOLEM;
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_ENTITY_NOT_HOLDING_UNNECESSARY_ITEM))
			passed = _entityNotHoldingItemConstruction(
					executingEntity_,
					commandAndParams,
					behaviorParams_) ? ClayConstants.BEHAVIOR_PASSED : ClayConstants.BEHAVIOR_FAILED_INVALID_GOLEM;
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_STORAGE_EXISTS_FROM_GOLEM))
		{
			if (!_storageExistsFromGolem(executingEntity_))
			{
				if (!storageExists(model))
					passed = ClayConstants.BEHAVIOR_FAILED_NO_STORAGE;
				else
					passed = ClayConstants.BEHAVIOR_FAILED_NO_PATH;
			}
		}

		return passed;
	}

	private static boolean _addClay(GolemEntity executingEntity_, String[] commandAndParams_)
	{
		executingEntity_.adjustClay(Double.parseDouble(commandAndParams_[1]));
		return true;
	}

	private static boolean _addMana(CityModel model_, String[] commandAndParams_)
	{
		model_.addMana(Integer.parseInt(commandAndParams_[1]));
		return true;
	}

	private static boolean _build(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity entity = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		if (executingEntity_.isTickComplete())
		{
			entity.constructionComplete();
			return true;
		}
		else
			executingEntity_.setTickAndRate(entity.getBuildTime(), 1);
		return false;
	}

	private static boolean _claimBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		if (building.isInUse())
			return false;
		building.setClaimingGolem(executingEntity_);
		executingEntity_.setClaimedBuilding(building);
		return true;
	}

	private static int _claimEmptyGenericBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		String tag = (String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		Queue<Point> path = SearchUtil.searchEmptyGenericBuildingGoalOnly(
				executingEntity_,
				executingEntity_.getHomeScreen(),
				tag);
		int pathStatus = SearchUtil.getPathStatus(path);
		if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
		{
			executingEntity_.behaviorFailed(pathStatus);
			return pathStatus;
		}
		Point point = path.poll();
		BuildingEntity claimableBuilding = model_
				.getTileValue(point.x, point.y);
		executingEntity_.setClaimedBuilding(claimableBuilding);
		claimableBuilding.setClaimingGolem(executingEntity_);

		return ClayConstants.BEHAVIOR_PASSED;
	}

	private static int _claimGenericBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		String tag = (String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		Queue<Point> path = SearchUtil.searchGenericBuildingGoalOnly(
				executingEntity_,
				executingEntity_.getHomeScreen(),
				tag);
		int pathStatus = SearchUtil.getPathStatus(path);
		if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
		{
			executingEntity_.behaviorFailed(pathStatus);
			return pathStatus;
		}
		Point point = path.poll();
		BuildingEntity claimableBuilding = model_
				.getTileValue(point.x, point.y);
		executingEntity_.setClaimedBuilding(claimableBuilding);
		claimableBuilding.setClaimingGolem(executingEntity_);

		return ClayConstants.BEHAVIOR_PASSED;
	}

	private static int _claimHouse(GolemEntity executingEntity_, CityModel model_)
	{
		Queue<Point> path = SearchUtil.searchHouseGoalOnly(
				executingEntity_,
				executingEntity_.getHomeScreen());
		int pathStatus = SearchUtil.getPathStatus(path);
		if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
		{
			executingEntity_.behaviorFailed(pathStatus);
			return pathStatus;
		}
		Point point = path.poll();
		BuildingEntity claimableBuilding = model_
				.getTileValue(point.x, point.y);
		executingEntity_.setClaimedBuilding(claimableBuilding);
		claimableBuilding.setClaimingGolem(executingEntity_);

		return ClayConstants.BEHAVIOR_PASSED;
	}

	private static int _claimConstructionItems(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		if (building.getConstructionItems().isEmpty())
			return ClayConstants.BEHAVIOR_PASSED;
		String[] neededItems = building.getConstructionItems().split(",");
		for (int i = 0; i < neededItems.length; i++)
		{
			Item searchItem = new Item(ItemData.getItem(neededItems[i]));
			if (building.isHolding(searchItem))
			{
				building.claimHeldItem(searchItem);
				continue;
			}
			if (executingEntity_.isHolding(searchItem))
			{
				executingEntity_.claimItemForBuilding(building, searchItem);
				continue;
			}
			Queue<Point> path = SearchUtil.searchItemGoalOnly(
					building,
					building.getHomeScreen(),
					searchItem);
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
			{
				if (!itemExists(model_, searchItem))
				{
					building.releaseItems();
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
					return ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS;
				}
				else
				{
					building.releaseItems();
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
					return ClayConstants.BEHAVIOR_FAILED_NO_PATH;
				}
			}
			Point buildingPoint = path.poll();
			BuildingEntity holdingBuilding = model_.getTileValue(
					(int) buildingPoint.getX(),
					(int) buildingPoint.getY());
			Item item = holdingBuilding.getItem(searchItem);
			building.claimItem(item);
		}
		return ClayConstants.BEHAVIOR_PASSED;
	}

	private static int _claimParameterItems(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = executingEntity_.getClaimedBuilding();
		String[] neededItems = ((String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]).split(",");
		for (int i = 0; i < neededItems.length; i++)
		{
			Item searchItem = new Item(ItemData.getItem(neededItems[i]));
			if (building.isHolding(searchItem))
			{
				building.claimHeldItem(searchItem);
				continue;
			}
			if (executingEntity_.isHolding(searchItem))
			{
				executingEntity_.claimItemForBuilding(building, searchItem);
				continue;
			}
			Queue<Point> path = SearchUtil.searchItemGoalOnly(
					building,
					building.getHomeScreen(),
					searchItem);
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
			{
				if (!itemExists(model_, searchItem))
				{
					building.releaseItems();
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
					return ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS;
				}
				else
				{
					building.releaseItems();
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
					return ClayConstants.BEHAVIOR_FAILED_NO_PATH;
				}
			}
			Point buildingPoint = path.poll();
			BuildingEntity holdingBuilding = model_.getTileValue(
					(int) buildingPoint.getX(),
					(int) buildingPoint.getY());
			Item item = holdingBuilding.getItem(searchItem);
			building.claimItem(item);
		}
		return ClayConstants.BEHAVIOR_PASSED;
	}

	private static int _claimItems(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_)
	{
		BuildingEntity claimedBuilding = executingEntity_.getClaimedBuilding();
		if (claimedBuilding == null)
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			return ClayConstants.BEHAVIOR_FAILED_NO_PATH;
		}
		for (int i = 1; i < commandAndParams_.length; i++)
		{
			Item searchItem = new Item(ItemData.getItem(commandAndParams_[i]));
			if (claimedBuilding.isHolding(searchItem))
			{
				claimedBuilding.claimHeldItem(searchItem);
				continue;
			}
			if (executingEntity_.isHolding(searchItem))
			{
				executingEntity_.claimItemForBuilding(
						claimedBuilding,
						searchItem);
				continue;
			}
			Queue<Point> path = SearchUtil.searchItemGoalOnly(
					claimedBuilding,
					claimedBuilding.getHomeScreen(),
					searchItem);
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
			{
				if (!itemExists(model_, searchItem))
				{
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
					return ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS;
				}
				else
				{
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
					return ClayConstants.BEHAVIOR_FAILED_NO_PATH;
				}
			}
			Point buildingPoint = path.poll();
			BuildingEntity holdingBuilding = model_.getTileValue(
					(int) buildingPoint.getX(),
					(int) buildingPoint.getY());
			Item item = holdingBuilding.getItem(searchItem);
			claimedBuilding.claimItem(item);
		}
		return ClayConstants.BEHAVIOR_PASSED;
	}

	private static boolean _consumeClaimed(GolemEntity executingEntity_)
	{
		BuildingEntity claimedBuilding = executingEntity_.getClaimedBuilding();
		claimedBuilding.consumeClaimed();
		return true;
	}

	private static boolean _consumeConstructionClaimed(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		building.consumeClaimed();
		return true;
	}

	private static boolean _createClayGolem(GolemEntity executingEntity_, CityModel model_)
	{
		model_.addGolem(
				GolemData.getGolem(ClayConstants.GOLEM_CLAY),
				executingEntity_.getX(),
				executingEntity_.getY());
		return true;
	}

	private static boolean _entityNotHoldingItem(GolemEntity executingEntity_)
	{
		return executingEntity_.getCopyOfHeldItems().isEmpty();
	}

	private static boolean _entityNotHoldingItemConstruction(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];

		List<Item> items = executingEntity_.getCopyOfHeldItems();
		String constructionItems = building.getConstructionItems();
		if (items.isEmpty() || constructionItems.isEmpty())
			return true;

		return constructionItems.contains(items.get(0).getTag());

	}

	private static boolean _harvestItemsOnBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) building
				.getHomeScreen().getProcess(GolemBehaviorProcess.class));
		for (Item item : building.getCopyOfHeldItems())
		{
			Behavior behavior = new Behavior(
					BehaviorData.getBehavior(ClayConstants.BEHAVIOR_HARVEST),
					building, item.getTag());
			behavior.setAssigningBuilding(building);
			building.addActiveBehavior(behavior);
			behaviorProcess.queueBehavior(behavior);
		}
		return true;
	}

	private static boolean _hide(GolemEntity executingEntity_)
	{
		executingEntity_.setVisible(false);
		return true;
	}

	private static boolean _produceItemsOnBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		String[] items = ((String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]).split(",");
		BuildingEntity building = executingEntity_.getClaimedBuilding();
		for (String item : items)
		{
			building.generate(new Item(ItemData.getItem(item)));
		}
		return true;
	}

	private static boolean _produceItemOnGolem(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		Item item = new Item(ItemData.getItem((String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]));
		executingEntity_.generate(item);
		return true;
	}

	private static boolean _seek(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity entity = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		if (executingEntity_.getPoint().equals(entity.getPoint()))
			return true;
		else
		{
			Queue<Point> path = SearchUtil.searchBuildingEntity(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					entity);
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
				executingEntity_.behaviorFailed(pathStatus);
			else
				executingEntity_.addMoveInstructions(path);
		}
		return false;
	}

	private static boolean _seekClaimedItems(GolemEntity executingEntity_, CityModel model_)
	{
		return _seekItems(
				executingEntity_,
				executingEntity_.getClaimedBuilding(),
				model_);
	}

	private static boolean _seekClaimedConstructionItems(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		if (building.getConstructionItems().isEmpty())
			return true;
		return _seekItems(executingEntity_, building, model_);
	}

	private static boolean _seekItems(GolemEntity executingEntity_, BuildingEntity building_, CityModel model_)
	{
		if (building_ == null)
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			return false;
		}
		for (Item item : building_.getClaimedItems())
		{
			if (building_.isHolding(item))
				continue;

			if (executingEntity_.isHolding(item))
			{
				if (executingEntity_.getPoint().equals(building_.getPoint()))
				{
					building_.generate(item);
					executingEntity_.consume(item);
					continue;
				}
				Queue<Point> path = SearchUtil.searchBuildingEntity(
						executingEntity_,
						executingEntity_.getHomeScreen(),
						building_);
				int pathStatus = SearchUtil.getPathStatus(path);
				if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
					executingEntity_.behaviorFailed(pathStatus);
				else
					executingEntity_.addMoveInstructions(path);
				return false;
			}

			BuildingEntity entityAtPoint = model_.getTileValue(
					executingEntity_.getGridX(),
					executingEntity_.getGridY());

			if (entityAtPoint.isHolding(item))
			{
				executingEntity_.generate(item);
				entityAtPoint.consume(item);
				return false;
			}

			Queue<Point> path = SearchUtil.searchClaimedItem(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					item);
			if (path.isEmpty() && !building_.isHolding(item))
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
			return false;
		}
		return true;
	}

	private static boolean _seekGenericBuilding(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		String tag = (String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		BuildingEntity entityAtPoint = model_.getTileValue(
				executingEntity_.getGridX(),
				executingEntity_.getGridY());
		if (entityAtPoint != null && entityAtPoint.getBuildingTag().equals(tag))
		{
			return true;
		}
		else
		{
			Queue<Point> path = SearchUtil.searchGenericBuilding(
					executingEntity_,
					executingEntity_.getHomeScreen(),
					tag);
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
				executingEntity_.behaviorFailed(pathStatus);
			else
				executingEntity_.addMoveInstructions(path);
		}
		return false;
	}

	private static boolean _seekStorage(GolemEntity executingEntity_)
	{
		BuildingEntity currentBuilding = executingEntity_.getModel()
				.getTileValue(
						executingEntity_.getGridX(),
						executingEntity_.getGridY());
		if (currentBuilding != null && currentBuilding.isStorageAvailable())
			return true;
		else
		{
			Queue<Point> path = SearchUtil.searchStorage(
					executingEntity_,
					executingEntity_.getHomeScreen());
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
				executingEntity_.behaviorFailed(pathStatus);
			else
				executingEntity_.addMoveInstructions(path);
		}
		return false;
	}

	private static boolean _show(GolemEntity executingEntity_)
	{
		executingEntity_.setVisible(true);
		return true;
	}

	private static boolean _storageExistsFromEntity(String[] commandAndParams_, Object[] behaviorParams_)
	{
		AbstractEntity entity = (AbstractEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		Queue<Point> path = SearchUtil.searchStorage(
				entity,
				entity.getHomeScreen());
		return path.size() > 0;
	}

	private static boolean _storageExistsFromGolem(GolemEntity executingEntity_)
	{
		Queue<Point> path = SearchUtil.searchStorage(
				executingEntity_,
				executingEntity_.getHomeScreen());
		return path.size() > 0;
	}

	private static boolean _storeAll(GolemEntity executingEntity_)
	{
		BuildingEntity storage = executingEntity_.getModel().getTileValue(
				executingEntity_.getGridX(),
				executingEntity_.getGridY());
		List<Item> items = executingEntity_.getCopyOfHeldItems();
		for (Item item : items)
		{
			if (storage.isStorageAvailable())
			{
				storage.generate(item);
				executingEntity_.consume(item);
			}
			else
				break;
		}
		return true;
	}

	private static boolean _storeItem(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		Item item = new Item(ItemData.getItem((String) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]));
		BuildingEntity storage = executingEntity_.getModel().getTileValue(
				executingEntity_.getGridX(),
				executingEntity_.getGridY());
		if (executingEntity_.isHolding(item))
		{
			storage.generate(item);
			executingEntity_.consume(item);
			return true;
		}
		else
		{
			if (!storageExists(model_))
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_STORAGE);
			else
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
		}
		return false;
	}

	private static boolean _takeItem(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		AbstractEntity takenFrom = (AbstractEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		Item item = new Item(ItemData.getItem((String) behaviorParams_[Integer
				.parseInt(commandAndParams_[2])]));
		if (takenFrom.isHolding(item))
		{
			executingEntity_.generate(item);
			takenFrom.consume(item);
			return true;
		}
		else
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM);
		}
		return false;
	}

	private static boolean _tick(GolemEntity executingEntity_, String[] commandAndParams_)
	{
		Integer tick = Integer.parseInt(commandAndParams_[1]);
		if (executingEntity_.isTickComplete())
			return true;
		else
			executingEntity_.setTickAndRate(tick, 1);
		return false;
	}

	// Utility Functions

	private static boolean storageExists(CityModel model_)
	{
		BuildingEntity[][] tiles = model_.getTileValues();
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[0].length; j++)
			{
				if (tiles[i][j] != null && tiles[i][j].isStorageAvailable())
					return true;
			}
		}
		return false;
	}

	private static boolean itemExists(CityModel model_, Item item_)
	{
		BuildingEntity[][] tiles = model_.getTileValues();
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[0].length; j++)
			{
				if (tiles[i][j] != null && tiles[i][j].isHolding(item_))
					return true;
			}
		}
		return false;
	}

}
