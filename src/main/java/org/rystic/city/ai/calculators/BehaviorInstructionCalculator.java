package org.rystic.city.ai.calculators;

import java.awt.Point;
import java.util.List;
import java.util.Queue;

import org.rystic.city.entities.AbstractEntity;
import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.GolemData;
import org.rystic.city.generics.data.ItemData;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.generics.objects.Item;
import org.rystic.city.processes.GolemBehaviorProcess;
import org.rystic.city.util.SearchUtil;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;

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

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_CLAY_GOLEM))
			complete = _createGolem(
					executingEntity_,
					model,
					ClayConstants.GOLEM_CLAY);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_PEARLCLAY_GOLEM))
			complete = _createGolem(
					executingEntity_,
					model,
					ClayConstants.GOLEM_PEARLCLAY);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_STONEWARE_GOLEM))
			complete = _createGolem(
					executingEntity_,
					model,
					ClayConstants.GOLEM_STONEWARE);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_EARTHENWARE_GOLEM))
			complete = _createGolem(
					executingEntity_,
					model,
					ClayConstants.GOLEM_EARTHENWARE);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_CREATE_WARRENS_GOLEM))
			complete = _createGolem(
					executingEntity_,
					model,
					ClayConstants.GOLEM_WARRENS);
		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_DECONSTRUCT))
			complete = _deconstruct(
					executingEntity_,
					commandAndParams,
					behaviorParams_);
		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_GENERATE_HEAT))
			complete = _generateHeat(
					executingEntity_,
					model,
					commandAndParams,
					behaviorParams_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_HARVEST_ITEMS_ON_BUILDING))
			complete = _harvestItemsOnBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_HARVEST_ITEMS_ON_CLAIMED_BUILDING))
			complete = _harvestItemsOnClaimedBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_);
		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_HIDE))
			complete = _hide(executingEntity_);
		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_KILL_GOLEM))
			complete = _killGolem(executingEntity_);
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

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_REPAIR_HEAT_DAMAGE))
			complete = _repairHeatDamage(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK))
			complete = _seek(
					executingEntity_,
					commandAndParams,
					behaviorParams_);
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_CLAIMED_BUILDING))
			complete = _seekClaimedBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_CONSTRUCTION_BUILDING))
			complete = _seekConstructionBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_);

		else if (com.equals(ClayConstants.BEHAVIOR_COMMAND_SEEK_ENTITIES))
			complete = _seekEntities(
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
			complete = _seekStorage(executingEntity_, model);

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
				passed = ClayConstants.BEHAVIOR_FAILED_ASSIGNING_BUILDING_CLAIMED;
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
		else if (com
				.equals(ClayConstants.BEHAVIOR_COMMAND_SET_CONSTRUCTION_BUILDING))
			passed = _setConstructionBuilding(
					executingEntity_,
					commandAndParams,
					behaviorParams_) ? ClayConstants.BEHAVIOR_PASSED : ClayConstants.BEHAVIOR_FAILED_NO_PATH;
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
		BuildingEntity building = ((BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]);
		BuildingEntity constructionBuilding = building
				.getConstructionBuilding();
		if (building.getConstructionItems().isEmpty())
			return ClayConstants.BEHAVIOR_PASSED;
		String[] neededItems = building.getConstructionItems().split(",");
		for (int i = 0; i < neededItems.length; i++)
		{
			Item searchItem = new Item(ItemData.getItem(neededItems[i]));
			if (constructionBuilding.isHolding(searchItem))
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
					constructionBuilding,
					building.getHomeScreen(),
					searchItem);
			int pathStatus = SearchUtil.getPathStatus(path);
			if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
			{
				if (!itemExists(model_, searchItem))
				{
					building.releaseClaimedItems();
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
					return ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS;
				}
				else
				{
					building.releaseClaimedItems();
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
					building.releaseClaimedItems();
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS);
					return ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS;
				}
				else
				{
					building.releaseClaimedItems();
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
		BuildingEntity building = ((BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]).getConstructionBuilding();
		building.consumeClaimed();
		return true;
	}

	private static boolean _createGolem(GolemEntity executingEntity_, CityModel model_, String type_)
	{
		model_.addGolem(
				GolemData.getGolem(type_),
				executingEntity_.getX(),
				executingEntity_.getY(),
				executingEntity_);
		return true;
	}

	private static boolean _deconstruct(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity entity = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		if (executingEntity_.isTickComplete())
		{
			entity.deconstructBuilding();
			return true;
		}
		else
			executingEntity_.setTickAndRate(entity.getBuildTime(), 1);
		return false;
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

		return constructionItems.contains(items.get(0).getTag()); // TODO one of
																	// the spots
																	// to fix if
																	// golems
																	// want to
																	// hold
																	// multiple
																	// items.

	}

	private static boolean _generateHeat(GolemEntity executingEntity_, CityModel model_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = executingEntity_.getClaimedBuilding();
		int heat = (Integer) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		building.permeateHeat(heat);
		return true;
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

	private static boolean _harvestItemsOnClaimedBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = executingEntity_.getClaimedBuilding();
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

	private static boolean _killGolem(GolemEntity executingEntity_)
	{
		executingEntity_.getModel().removeGolem(executingEntity_);
		return true;
	}

	private static boolean _repairHeatDamage(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity building = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		building.repairHeatDamage();
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

	private static boolean _seekClaimedBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity entity = executingEntity_.getClaimedBuilding();
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

	private static boolean _seekConstructionBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity entity = ((BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]).getConstructionBuilding();
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

	@SuppressWarnings("unchecked")
	private static boolean _seekEntities(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		List<BuildingEntity> entities = ((List<BuildingEntity>) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]);
		for (BuildingEntity entity : entities)
		{
			if (executingEntity_.getPoint().equals(entity.getPoint()))
				return true;
		}
		Queue<Point> path = SearchUtil.searchBuildingEntitiesGoalOnly(
				executingEntity_,
				executingEntity_.getHomeScreen(),
				entities);
		int pathStatus = SearchUtil.getPathStatus(path);
		if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
			executingEntity_.behaviorFailed(pathStatus);
		else
			executingEntity_.addMoveInstructions(path);
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
		BuildingEntity building = ((BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])]);
		BuildingEntity constructionBuilding = building
				.getConstructionBuilding();
		if (building.getConstructionItems().isEmpty())
			return true;

		if (constructionBuilding == null)
		{
			executingEntity_
					.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			return false;
		}
		for (Item item : building.getClaimedItems())
		{
			if (constructionBuilding.isHolding(item))
				continue;

			if (executingEntity_.isHolding(item))
			{
				if (executingEntity_.getPoint().equals(
						constructionBuilding.getPoint()))
				{
					constructionBuilding.generate(item);
					executingEntity_.consume(item);
					continue;
				}
				Queue<Point> path = SearchUtil.searchBuildingEntity(
						executingEntity_,
						executingEntity_.getHomeScreen(),
						constructionBuilding);
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
			if (path.isEmpty() && !constructionBuilding.isHolding(item))
				executingEntity_
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_PATH);
			else
				executingEntity_.addMoveInstructions(path);
			return false;
		}
		return true;
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

	private static boolean _seekStorage(GolemEntity executingEntity_, CityModel model_)
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
			{
				if (!storageExists(model_))
					executingEntity_
							.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_NO_STORAGE);
				else
					executingEntity_.behaviorFailed(pathStatus);
			}
			else
			{
				executingEntity_.addMoveInstructions(path);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static boolean _setConstructionBuilding(GolemEntity executingEntity_, String[] commandAndParams_, Object[] behaviorParams_)
	{
		BuildingEntity entity = (BuildingEntity) behaviorParams_[Integer
				.parseInt(commandAndParams_[1])];
		List<BuildingEntity> searchEntities = (List<BuildingEntity>) behaviorParams_[Integer
				.parseInt(commandAndParams_[2])];
		Queue<Point> path = SearchUtil.searchBuildingEntitiesGoalOnly(
				executingEntity_,
				executingEntity_.getHomeScreen(),
				searchEntities);
		int pathStatus = SearchUtil.getPathStatus(path);
		if (pathStatus != ClayConstants.BEHAVIOR_PASSED)
		{
			executingEntity_.behaviorFailed(pathStatus);
			return false;
		}
		else
		{
			Point buildingPoint = path.poll();
			BuildingEntity constructionBuilding = ((CityModel) executingEntity_
					.getHomeScreen().getModel()).getTileValue(
					buildingPoint.x,
					buildingPoint.y);
			entity.setConstructionBuilding(constructionBuilding);
		}
		return true;
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
		executingEntity_
				.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM);
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
