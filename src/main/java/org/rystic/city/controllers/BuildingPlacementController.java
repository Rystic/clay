package org.rystic.city.controllers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.entities.BuildingEntity;
import org.rystic.city.processes.AbstractProcess;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class BuildingPlacementController extends AbstractProcess
{
	public BuildingPlacementController(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_tileValues = _model.getTileValues();
		_recentlyCreatedClayBlocks = new ArrayList<Point>();
		_leftClickReleased = true;
	}

	@Override
	public void execute()
	{

		boolean leftClick = Mouse.isButtonDown(0);

		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
				|| Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			if (_leftClickReleased && leftClick)
				destroyBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
			else
			{
				if (!leftClick)
					_leftClickReleased = true;
				highlightBuildingForDeletion(
						Mouse.getX() / TILE_X,
						Mouse.getY() / TILE_Y);
			}

		}
		else if (leftClick)
		{
			placeBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
		}
		else
		{
			_leftClickReleased = true;
			_recentlyCreatedClayBlocks.clear();
			if (Mouse.isButtonDown(1))
				copyBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
		}

	}

	private void placeBuilding(int x_, int y_)
	{
		_leftClickReleased = false;
		if (x_ >= ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X)
			return;
		if (y_ > 0)
		{
			GenericBuilding building = BuildingData.getBuildingById(_model
					.getSelectedBuilding());
			Point location = new Point(x_, y_);
			if (_tileValues[location.x][location.y] == null
					|| _recentlyCreatedClayBlocks.contains(location))
			{

				if ((location.y > 0
						&& _tileValues[location.x][location.y - 1] != null && _tileValues[location.x][location.y - 1]
						.isNatural())
						|| (location.y > 1
								&& _tileValues[location.x][location.y - 2] != null && _tileValues[location.x][location.y - 2]
								.isNatural())
						|| (location.y > 2
								&& _tileValues[location.x][location.y - 3] != null && _tileValues[location.x][location.y - 3]
								.isNatural())
						|| (location.y > 3
								&& _tileValues[location.x][location.y - 4] != null && _tileValues[location.x][location.y - 4]
								.isNatural()))
					building = BuildingData
							.getBuildingByTag(ClayConstants.DEFAULT_TILE_TYPE);
				else
					building = BuildingData
							.getBuildingByTag(ClayConstants.FOUNDATION_BLOCK);
				_recentlyCreatedClayBlocks.add(location);
			}
			if (building.isValidLocation(
					location,
					_tileValues,
					building.isSupport()))
			{
				try
				{
					building.placeBuilding(
							location,
							_tileValues,
							_homeScreen,
							false);
				} catch (Exception e_)
				{
					e_.printStackTrace();
					return;
				}
			}
		}
	}

	private void copyBuilding(int x_, int y_)
	{
		if (x_ >= ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X)
			return;
		if (y_ > 0)
		{
			BuildingEntity building = _tileValues[x_][y_];
			if (building != null && !building.isNatural())
			{
				_model.setSelectedBuilding(building.getBuildingIdentifier());
			}
		}
	}

	private void destroyBuilding(int x_, int y_)
	{
		_leftClickReleased = false;
		if (x_ >= ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X)
			return;
		if (y_ > 0)
		{
			BuildingEntity building = _tileValues[x_][y_];
			if (building != null
					&& !building.isNatural()
					&& !building.getBuildingTag().equals(
							ClayConstants.DEFAULT_TILE_TYPE))
			{
				if (!building.isBuilt())
					building.deconstructBuilding();
				else
					building.markForDeletion();
			}
		}
	}

	private void highlightBuildingForDeletion(int x_, int y_)
	{
		if (x_ >= ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X)
			return;
		if (y_ > 0)
		{
			BuildingEntity building = _tileValues[x_][y_];
			if (building != null
					&& !building.isNatural()
					&& !building.getBuildingTag().equals(
							ClayConstants.DEFAULT_TILE_TYPE))
			{
				building.highlightAllForDeletion();
			}
		}
	}

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

	private BuildingEntity[][] _tileValues;
	private List<Point> _recentlyCreatedClayBlocks;
	private boolean _leftClickReleased;

	private CityModel _model;

}
