package city.controllers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.ClayConstants;
import models.CityModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import screens.AbstractScreen;
import city.generics.GenericBuilding;
import city.generics.data.BuildingData;
import city.generics.entities.BuildingEntity;
import city.processes.AbstractProcess;

public class BuildingPlacementController extends AbstractProcess
{
	public BuildingPlacementController(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_tileValues = _model.getTileValues();
		_recentlyCreatedClayBlocks = new ArrayList<Point>();
	}

	@Override
	public void execute()
	{

		boolean leftClick = Mouse.isButtonDown(0);

		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
				|| Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			if (leftClick)
				destroyBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
			else
				highlightBuildingForDeletion(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);

		}
		else if (leftClick)
		{
			placeBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
		}
		else
		{
			_recentlyCreatedClayBlocks.clear();
			if (Mouse.isButtonDown(1))
				copyBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
		}

	}

	private void placeBuilding(int x_, int y_)
	{
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
				building = BuildingData
						.getBuildingByTag(ClayConstants.DEFAULT_TILE_TYPE);
				_recentlyCreatedClayBlocks.add(location);
			}
			if (building.isValidLocation(
					location,
					_tileValues,
					building.isSupport()))
			{
				try
				{
					building.placeBuilding(location, _tileValues, _homeScreen);
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
					building.deleteBuilding();
				else
					building.markForDeletion(true);
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

	private CityModel _model;

}
