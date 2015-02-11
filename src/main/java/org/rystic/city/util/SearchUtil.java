package org.rystic.city.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.rystic.city.entities.AbstractEntity;
import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.entities.golem.GolemEntity;
import org.rystic.city.generics.objects.Item;
import org.rystic.main.ClayConstants;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class SearchUtil
{
	public static Queue<Point> searchBuildingEntity(AbstractEntity entity_, AbstractScreen abstractScreen_, BuildingEntity buildingEntity_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_ENTITY,
				buildingEntity_);
		return path;
	}
	
	public static Queue<Point> searchBuildingEntitiesGoalOnly(AbstractEntity entity_, AbstractScreen abstractScreen_, List<BuildingEntity> buildingEntities_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_ENTITIES_GOAL_ONLY,
				buildingEntities_);
		return path;
	}

	public static Queue<Point> searchGenericBuilding(AbstractEntity entity_, AbstractScreen abstractScreen_, String buildingTag_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_GENERIC_BUILDING,
				buildingTag_);
		return path;
	}

	public static Queue<Point> searchEmptyGenericBuildingGoalOnly(AbstractEntity entity_, AbstractScreen abstractScreen_, String buildingTag_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_EMPTY_GENERIC_BUILDING_GOAL_ONLY,
				buildingTag_);
		return path;
	}

	public static Queue<Point> searchGenericBuildingGoalOnly(AbstractEntity entity_, AbstractScreen abstractScreen_, String buildingTag_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_GENERIC_BUILDING_GOAL_ONLY,
				buildingTag_);
		return path;
	}

	public static Queue<Point> searchStorage(AbstractEntity entity_, AbstractScreen abstractScreen_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_STORAGE);
		return path;
	}

	public static Queue<Point> searchClaimedItem(AbstractEntity entity_, AbstractScreen abstractScreen_, Item item_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_CLAIMED_ITEM,
				item_);
		return path;
	}

	public static Queue<Point> searchItemGoalOnly(AbstractEntity entity_, AbstractScreen abstractScreen_, Item item_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_ITEM_GOAL_ONLY,
				item_);
		return path;
	}

	public static Queue<Point> searchHouseGoalOnly(AbstractEntity entity_, AbstractScreen abstractScreen_)
	{
		Queue<Point> path = SearchUtil.search(
				entity_,
				entity_.getHomeScreen(),
				ClayConstants.SEARCH_HOUSE_GOAL_ONLY);
		return path;
	}

	@SuppressWarnings("unchecked")
	private static Queue<Point> search(AbstractEntity entity_, AbstractScreen abstractScreen_, Object... params_)
	{
		Set<Point> closedSet = new HashSet<Point>();
		Queue<Point> openQueue = new ArrayBlockingQueue<Point>(256);
		Map<Point, Point> cameFrom = new HashMap<Point, Point>();
		Map<Point, Integer> nodeWeight = new HashMap<Point, Integer>();

		BuildingEntity tiles_[][] = ((CityScreen) abstractScreen_).getModel()
				.getTileValues();

		Point start = new Point(entity_.getGridX(), entity_.getGridY());

		openQueue.add(new Point(entity_.getGridX(), entity_.getGridY()));
		nodeWeight.put(start, new Integer(0));

		int currentWeight = 0;
		int bestGoalWeight = -1;
		Set<Point> adjacentNodes = new HashSet<Point>();
		List<Point> goalPoints = new ArrayList<Point>();

		int searchType = Integer.parseInt(params_[0].toString());

		boolean existsButOccupied = false;

		while (!openQueue.isEmpty())
		{
			Point current = openQueue.poll();
			currentWeight = nodeWeight.get(current);

			closedSet.add(current);

			if (bestGoalWeight != -1 && currentWeight >= bestGoalWeight)
				continue;
			boolean startTile = current.x == start.x && current.y == start.y;
			if (tiles_[current.x][current.y] != null)
			{
				if (!tiles_[current.x][current.y].isPassable() && !startTile)
					continue;
				boolean isGoal = false;

				BuildingEntity tile = tiles_[current.x][current.y];

				if (searchType == ClayConstants.SEARCH_ENTITY)
				{
					isGoal = tile.equals(params_[1]);
				}
				else if (searchType == ClayConstants.SEARCH_ENTITIES_GOAL_ONLY)
				{
					isGoal = ((List<BuildingEntity>)params_[1]).contains(tile);
				}
				else if (searchType == ClayConstants.SEARCH_GENERIC_BUILDING
						|| searchType == ClayConstants.SEARCH_GENERIC_BUILDING_GOAL_ONLY)
				{
					isGoal = tile.getBuildingTag().equals(params_[1])
							&& tile.isBuilt() && tile.isBaseTile();
					if (isGoal && tile.isInUse())
					{
						isGoal = false;
						existsButOccupied = true;
					}
				}
				else if (searchType == ClayConstants.SEARCH_EMPTY_GENERIC_BUILDING_GOAL_ONLY)
				{
					isGoal = tile.getBuildingTag().equals(params_[1])
							&& tile.isBuilt() && tile.isBaseTile()
							&& tile.getCopyOfHeldItems().size() == 0;
					if (isGoal && tile.isInUse())
					{
						isGoal = false;
						existsButOccupied = true;
					}
				}
				else if (searchType == ClayConstants.SEARCH_STORAGE)
				{
					isGoal = tile.isStorageAvailable() && tile.isBuilt()
							&& tile.isBaseTile();
				}
				else if (searchType == ClayConstants.SEARCH_ITEM_GOAL_ONLY)
				{
					isGoal = tile.isHolding((Item) params_[1]);
				}
				else if (searchType == ClayConstants.SEARCH_CLAIMED_ITEM)
				{
					BuildingEntity claimedBuilding = ((GolemEntity) entity_)
							.getClaimedBuilding();
					isGoal = tile.isHolding((Item) params_[1])
							&& !tile.equals(claimedBuilding);
				}
				else if (searchType == ClayConstants.SEARCH_HOUSE_GOAL_ONLY)
				{
					isGoal = tile.isBuilt() && tile.isHouse();
					if (isGoal && tile.isInUse())
					{
						isGoal = false;
						existsButOccupied = true;
					}
				}

				if (isGoal)
				{
					goalPoints.add(current);
					if (bestGoalWeight == -1 || currentWeight < bestGoalWeight)
						bestGoalWeight = currentWeight;
					continue;
				}
			}

			adjacentNodes.clear();

			if (current.x > 0)
			{
				if (isHorizontalMoveValid(DIR_LEFT, current, tiles_))
					adjacentNodes.add(new Point(current.x - 1, current.y));
				else if (isDownwardDiagonalMoveValid(DIR_LEFT, current, tiles_))
					adjacentNodes.add(new Point(current.x - 1, current.y - 1));
			}

			if (current.x < tiles_.length - 1)
			{
				if (isHorizontalMoveValid(DIR_RIGHT, current, tiles_))
					adjacentNodes.add(new Point(current.x + 1, current.y));
				else if (isDownwardDiagonalMoveValid(DIR_RIGHT, current, tiles_))
					adjacentNodes.add(new Point(current.x + 1, current.y - 1));
			}

			if (current.y < tiles_[0].length)
			{
				if (isVerticalMoveValid(DIR_UP, current, tiles_))
					adjacentNodes.add(new Point(current.x, current.y + 1));
				else
				{
					if (isUpwardDiagonalMoveValid(DIR_LEFT, current, tiles_))
						adjacentNodes.add(new Point(current.x - 1,
								current.y + 1));
					if (isUpwardDiagonalMoveValid(DIR_RIGHT, current, tiles_))
						adjacentNodes.add(new Point(current.x + 1,
								current.y + 1));
				}
			}

			if (current.y > 0)
			{
				if (isVerticalMoveValid(DIR_DOWN, current, tiles_))
					adjacentNodes.add(new Point(current.x, current.y - 1));
			}

			for (Point node : adjacentNodes)
			{
				if (!closedSet.contains(node) && !openQueue.contains(node))
				{
					nodeWeight.put(node, currentWeight + 1);
					cameFrom.put(node, current);
					openQueue.add(node);
				}
				else if (currentWeight < nodeWeight.get(node))
				{
					nodeWeight.put(node, currentWeight + 1);
					cameFrom.put(node, current);
				}
			}
		}

		int finalWeight = -1;
		Point finalGoal = null;
		// TODO set in-use
		for (Point path : goalPoints)
		{
			int goalWeight = nodeWeight.get(path);
			if (goalWeight < finalWeight || finalWeight == -1)
			{
				finalGoal = path;
				finalWeight = goalWeight;
			}
		}
		Queue<Point> resultQueue = new ArrayBlockingQueue<Point>(256);
		if (finalGoal == null)
		{
			if (existsButOccupied)
				return EXISTS_BUT_IN_USE;
			return resultQueue;
		}

		List<Point> bestPath = new ArrayList<Point>();

		if (searchType == ClayConstants.SEARCH_GENERIC_BUILDING_GOAL_ONLY
				|| searchType == ClayConstants.SEARCH_ITEM_GOAL_ONLY
				|| searchType == ClayConstants.SEARCH_HOUSE_GOAL_ONLY
				|| searchType == ClayConstants.SEARCH_ENTITIES_GOAL_ONLY)
		{
			resultQueue.add(finalGoal);
			return resultQueue;
		}

		if (cameFrom.get(finalGoal) == null)
			bestPath.add(new Point(finalGoal.x * TILE_X, finalGoal.y * TILE_Y));
		else
		{
			for (; cameFrom.get(finalGoal) != null; finalGoal = cameFrom
					.get(finalGoal))
			{
				bestPath.add(new Point(finalGoal.x * TILE_X, finalGoal.y
						* TILE_Y));
			}
		}

		Collections.reverse(bestPath);
		resultQueue.addAll(bestPath);
		return resultQueue;
	}

	private static boolean isHorizontalMoveValid(int direction_, Point p_, BuildingEntity tiles_[][])
	{
		if (tiles_[p_.x + direction_][p_.y] == null
				|| !tiles_[p_.x + direction_][p_.y].isBridge())
		{
			if (tiles_[p_.x + direction_][p_.y - 1] == null)
				return false;
			if (!tiles_[p_.x + direction_][p_.y - 1].isSupportBlock())
				return false;
		}
		if (tiles_[p_.x + direction_][p_.y] == null)
			return true;
		if (tiles_[p_.x + direction_][p_.y].isPassable())
			return true;
		return false;
	}

	private static boolean isUpwardDiagonalMoveValid(int direction_, Point p_, BuildingEntity tiles_[][])
	{
		if (tiles_[p_.x][p_.y] == null)
			return false;
		if (!tiles_[p_.x][p_.y].isBuilt())
			return false;
		if (!tiles_[p_.x][p_.y].isDiagonalScalable())
			return false;
		if (tiles_[p_.x + direction_][p_.y] == null)
			return false;
		if (tiles_[p_.x + direction_][p_.y].isSupportBlock())
		{
			if (tiles_[p_.x + direction_][p_.y + DIR_UP] == null)
				return true;
			if (tiles_[p_.x + direction_][p_.y + DIR_UP].isPassable())
				return true;
		}
		return false;
	}

	private static boolean isDownwardDiagonalMoveValid(int direction_, Point p_, BuildingEntity tiles_[][])
	{
		if (tiles_[p_.x + direction_][p_.y - 1] == null)
			return false;
		if (!tiles_[p_.x + direction_][p_.y - 1].isBuilt())
			return false;
		if (tiles_[p_.x + direction_][p_.y - 1].isDiagonalScalable())
			return true;
		return false;
	}

	private static boolean isVerticalMoveValid(int direction_, Point p_, BuildingEntity tiles_[][])
	{
		if (direction_ == DIR_UP)
		{
			if (tiles_[p_.x][p_.y] == null)
				return false;
			if (!tiles_[p_.x][p_.y].isBuilt())
				return false;
			if (!tiles_[p_.x][p_.y].isUpwardScalable())
				return false;
			if (!tiles_[p_.x][p_.y].isSupportBlock())
				return false;
			if (p_.y + direction_ > tiles_[0].length - 1)
				return false;
			if (tiles_[p_.x][p_.y + direction_] == null)
				return true;
			if (!tiles_[p_.x][p_.y + direction_].isPassable())
				return false;
			return true;
		}
		else
		{
			if (tiles_[p_.x][p_.y + direction_] == null)
				return false;
			if (!tiles_[p_.x][p_.y + direction_].isBuilt())
				return false;
			if (tiles_[p_.x][p_.y + direction_].isDownwardScalable())
				return true;
		}
		return false;
	}

	public static int getPathStatus(Queue<Point> path_)
	{
		if (path_.isEmpty())
			return ClayConstants.BEHAVIOR_FAILED_NO_PATH;
		if (path_.equals(EXISTS_BUT_IN_USE))
			return ClayConstants.BEHAVIOR_FAILED_NO_UNOCCUPIED_GENERIC_BUILDING;
		return ClayConstants.BEHAVIOR_PASSED;
	}

	public static final Queue<Point> EXISTS_BUT_IN_USE = new ArrayBlockingQueue<Point>(
			256);
	static
	{
		EXISTS_BUT_IN_USE.add(new Point(-1, -1));
	}

	private static final int DIR_LEFT = -1;
	private static final int DIR_RIGHT = 1;
	private static final int DIR_UP = 1;
	private static final int DIR_DOWN = -1;

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

}
