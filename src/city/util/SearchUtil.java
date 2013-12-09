package city.util;

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

import main.ClayConstants;
import screens.AbstractScreen;
import screens.CityScreen;
import city.ai.objects.Item;
import city.entities.AbstractEntity;
import city.entities.BuildingEntity;
import city.entities.GolemEntity;

public class SearchUtil
{
	public static Queue<Point> searchIt(AbstractEntity entity_, AbstractScreen abstractScreen, Object... params_)
	{
		Set<Point> closedSet = new HashSet<Point>();
		Queue<Point> openQueue = new ArrayBlockingQueue<Point>(256);
		Map<Point, Point> cameFrom = new HashMap<Point, Point>();
		Map<Point, Integer> nodeWeight = new HashMap<Point, Integer>();

		BuildingEntity tiles_[][] = ((CityScreen) abstractScreen).getModel()
				.getTileValues();

		Point start = new Point(entity_.getGridX(), entity_.getGridY());

		openQueue.add(new Point(entity_.getGridX(), entity_.getGridY()));
		nodeWeight.put(start, new Integer(0));

		int currentWeight = 0;
		int bestGoalWeight = -1;
		Set<Point> adjacentNodes = new HashSet<Point>();
		List<Point> goalPoints = new ArrayList<Point>();

		int searchType = Integer.parseInt(params_[0].toString());

		while (!openQueue.isEmpty())
		{
			Point current = openQueue.poll();
			currentWeight = nodeWeight.get(current);

			closedSet.add(current);

			if (bestGoalWeight != -1 && currentWeight >= bestGoalWeight)
				continue;

			if (tiles_[current.x][current.y] != null)
			{
				if (!tiles_[current.x][current.y].isPassable())
					continue;
				boolean isGoal = false;

				BuildingEntity tile = tiles_[current.x][current.y];

				if (searchType == ClayConstants.SEARCH_ENTITY)
				{
					isGoal = tile.equals(params_[1]);
				}
				else if (searchType == ClayConstants.SEARCH_GENERIC_BUILDING
						|| searchType == ClayConstants.SEARCH_GENERIC_BUILDING_GOAL_ONLY)
				{
					isGoal = tile.getBuildingTag().equals(params_[1])
							&& !tile.isInUse();
				}
				else if (searchType == ClayConstants.SEARCH_STORAGE)
				{
					isGoal = tile.isStorageAvailable();
				}
				else if (searchType == ClayConstants.SEARCH_ITEM_GOAL_ONLY)
				{
					isGoal = tile.isHolding((Item)params_[1]);
				}
				else if (searchType == ClayConstants.SEARCH_CLAIMED_ITEMS)
				{
					BuildingEntity claimedBuilding = ((GolemEntity)entity_).getClaimedBuilding();
					isGoal = false;;
					for (Item item : claimedBuilding.getCopyOfHeldItems())
					{
						if (tile.isHolding(item))
						{
							isGoal = true;
							break;
						}
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
			return resultQueue;

		List<Point> bestPath = new ArrayList<Point>();

		if (searchType == ClayConstants.SEARCH_GENERIC_BUILDING_GOAL_ONLY || searchType == ClayConstants.SEARCH_ITEM_GOAL_ONLY)
		{
			resultQueue.add(finalGoal);
			return resultQueue;
		}

		for (; finalGoal != null; finalGoal = cameFrom.get(finalGoal))
		{
			bestPath.add(new Point(finalGoal.x * TILE_X, finalGoal.y * TILE_Y));
		}

		Collections.reverse(bestPath);
		resultQueue.addAll(bestPath);
		return resultQueue;
	}

	private static boolean isHorizontalMoveValid(int direction_, Point p_, BuildingEntity tiles_[][])
	{
		if (tiles_[p_.x + direction_][p_.y - 1] == null)
			return false;
		if (!tiles_[p_.x + direction_][p_.y - 1].isSupportBlock())
			return false;
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
		if (!tiles_[p_.x][p_.y].isScalable())
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
		if (tiles_[p_.x + direction_][p_.y - 1].isScalable())
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
			if (!tiles_[p_.x][p_.y].isScalable())
				return false;
			if (!tiles_[p_.x][p_.y].isSupportBlock())
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
			if (tiles_[p_.x][p_.y + direction_].isScalable())
				return true;
		}
		return false;
	}

	private static final int DIR_LEFT = -1;
	private static final int DIR_RIGHT = 1;
	private static final int DIR_UP = 1;
	private static final int DIR_DOWN = -1;

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

}
