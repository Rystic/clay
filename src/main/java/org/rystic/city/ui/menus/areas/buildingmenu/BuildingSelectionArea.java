package org.rystic.city.ui.menus.areas.buildingmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.AbstractButton;
import org.rystic.city.ui.menus.components.SelectBuildingButton;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class BuildingSelectionArea extends AbstractArea
{
	public BuildingSelectionArea(AbstractScreen homeScreen_,
			Map<Integer, String> categoryHotkeys_)
	{
		super(homeScreen_);
		_categoryHotkeys = categoryHotkeys_;
		_doCategoryUpdate = true;
		_doBuildingSelectionUpdate = true;
		_labels = new ArrayList<TextComponent>();
		_model = (CityModel) _homeScreen.getModel();

		_buildings = BuildingData
				.getBuildingsInCategory(ClayConstants.DEFAULT_BUILDING_MENU_CATEGORY);
		_category = ClayConstants.DEFAULT_BUILDING_MENU_CATEGORY;
		_buildingListPointer = 0;
	}

	public void updateCategorySelection(int key_)
	{
		String newCategory = _categoryHotkeys.get(key_);
		if (newCategory == null || newCategory.equals(_category))
			return;
		_buildings = BuildingData.getBuildingsInCategory(newCategory);
		_category = newCategory;
		_doCategoryUpdate = true;
		_buildingListPointer = 0;
	}

	public void updateBuildingSelection(int direction_)
	{
		_buildingListPointer += direction_;
		if (_buildingListPointer < 0)
			_buildingListPointer = _buildings.size() - 1;
		else if (_buildingListPointer == _buildings.size())
			_buildingListPointer = 0;
		_doBuildingSelectionUpdate = true;
	}

	@Override
	public void update()
	{
		if (!_doBuildingSelectionUpdate && !_doCategoryUpdate)
		{
			if (_model.getSelectedBuilding() != _buildings.get(
					_buildingListPointer).getBuildingIdentifier())
			{
				GenericBuilding building = BuildingData.getBuildingById(_model
						.getSelectedBuilding());
				if (!_buildings.contains(building))
				{
					_category = building.getMenuCategory();
					_buildings = BuildingData.getBuildingsInCategory(_category);
					_doCategoryUpdate = true;
				}
				else
					_doBuildingSelectionUpdate = true;
				for (int i = 0; i < _buildings.size(); i++)
				{
					if (_buildings.get(i).getBuildingIdentifier() == building
							.getBuildingIdentifier())
					{
						_buildingListPointer = i;
						break;
					}
				}
			}
		}
		if (_doCategoryUpdate)
		{
			_components.clear();
			_labels.clear();
			_components.add(new TextComponent(5, 39, "Buildings",
					ClayConstants.M_AREA_HEADER_COLOR));
			int increment = 46;
			for (final GenericBuilding building : _buildings)
			{
				SelectBuildingButton buildingButton = new SelectBuildingButton(
						5, increment, 32, 32, building)
				{
					@Override
					public void clicked()
					{
						for (int i = 0; i < _buildings.size(); i++)
						{
							if (_buildings.get(i).equals(building))
								_buildingListPointer = i;
							_doBuildingSelectionUpdate = true;
						}
					}
				};
				buildingButton.setDrawRatio(1.0f);
				_components.add(buildingButton);
				TextComponent textComponent = new TextComponent(20,
						increment - 3, building.getBuildingName());
				AbstractButton textButton = new AbstractButton(5, increment,
						255, 20)
				{

					@Override
					public void clicked()
					{
						for (int i = 0; i < _buildings.size(); i++)
						{
							if (_buildings.get(i).equals(building))
								_buildingListPointer = i;
							_doBuildingSelectionUpdate = true;
						}
					}
				};
				_labels.add(textComponent);
				_components.add(textButton);
				_components.add(textComponent);

				increment += 5;
			}
			_doBuildingSelectionUpdate = true;
			_doCategoryUpdate = false;
		}
		if (_doBuildingSelectionUpdate)
		{
			CityModel model = ((CityScreen) _homeScreen).getModel();
			model.setSelectedBuilding(_buildings.get(_buildingListPointer)
					.getBuildingIdentifier());
			for (int i = 0; i < _labels.size(); i++)
			{
				if (i == _buildingListPointer)
					_labels.get(i).setColor(ClayConstants.M_HIGHLIGHTED_COLOR);
				else
					_labels.get(i)
							.setColor(ClayConstants.M_UNHIGHLIGHTED_COLOR);
			}
			_doBuildingSelectionUpdate = false;
		}
	}

	private CityModel _model;

	private Map<Integer, String> _categoryHotkeys;

	private List<GenericBuilding> _buildings;
	private List<TextComponent> _labels;

	private String _category;
	private int _buildingListPointer;
	private boolean _doCategoryUpdate;
	private boolean _doBuildingSelectionUpdate;
}
