package city.controllers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.ClayConstants;
import models.CityModel;

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
		if (Mouse.isButtonDown(0))
			placeBuilding(Mouse.getX() / TILE_X, Mouse.getY() / TILE_Y);
		else
			_recentlyCreatedClayBlocks.clear();
	}

	private void placeBuilding(int x_, int y_)
	{
		Map<Integer, List<BuildingEntity>> buildingMap = _model
				.getBuildingMap();
		if (x_ >= ClayConstants.DEFAULT_MAP_WIDTH / ClayConstants.TILE_X)
			return;
		if (y_ > 0)
		{
			GenericBuilding building = BuildingData.getBuildingById(_model
					.getSelectedBuilding());
			Point location = new Point(x_, y_);
			if (_tileValues[location.x][location.y] == null || _recentlyCreatedClayBlocks.contains(location))
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
				if (_tileValues[x_][y_] != null)
				{
					if (buildingMap.get(_tileValues[x_][y_].getIdentifier()) != null)
					{
						buildingMap.get(_tileValues[x_][y_].getIdentifier())
								.remove(_tileValues[x_][y_]);
					}
					else
						return;
				}
				try
				{
					building.placeBuilding(location, _tileValues, _homeScreen);
				} catch (Exception e_)
				{
					e_.printStackTrace();
					return;
				}
				List<BuildingEntity> buildingList = buildingMap.get(building
						.getBuildingIdentifier());
				if (buildingList == null)
				{
					buildingList = new ArrayList<BuildingEntity>();
					buildingMap.put(
							building.getBuildingIdentifier(),
							buildingList);
				}
				buildingList.add(_tileValues[x_][y_]);
			}
		}
	}

	private static final int TILE_X = ClayConstants.TILE_X;
	private static final int TILE_Y = ClayConstants.TILE_Y;

	private BuildingEntity[][] _tileValues;
	private List<Point> _recentlyCreatedClayBlocks;

	private CityModel _model;

}
