package org.rystic.city.ui.menus.areas.buildingmenu;

import org.newdawn.slick.Color;
import org.rystic.city.generics.GenericBehavior;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class BuildingInformationArea extends AbstractArea
{
	public BuildingInformationArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_yPos = yPos_;
		_selectedBuilding = -1;
		_buildingInformationLabel = new TextComponent(65, _yPos + 40, "Building Information");
		_buildingInformationLabel.setColor(Color.yellow);
	}

	@Override
	public void update()
	{
		int selectedBuilding = ((CityScreen) _homeScreen).getModel()
				.getSelectedBuilding();
		if (selectedBuilding != _selectedBuilding)
		{
			_components.clear();
			_components.add(_buildingInformationLabel);
			_selectedBuilding = selectedBuilding;
			GenericBuilding building = BuildingData
					.getBuildingById(selectedBuilding);
			int elements = 0;
			int xPos = 20;
			int yDiff = 20;
			if (building.isHouse())
			{
				_components.add(new TextComponent(xPos, _yPos
						- (elements * yDiff), "Can be used for repairs."));
				elements++;
			}
			if (!building.isSupport())
			{
				_components.add(new TextComponent(xPos, _yPos
						- (elements * yDiff),
						"Cannot support buildings above it."));
				elements++;
			}
			if (building.isStorage())
			{
				_components.add(new TextComponent(xPos, _yPos
						- (elements * yDiff), "Can store up to "
						+ building.getStorageCapacity() + " items."));
				elements++;
			}

			for (GenericBehavior behavior : BehaviorData.getAllBeahviors())
			{
				for (Object o : behavior.getDefaultParams())
				{
					if (o.toString().contains(building.getBuildingTag()))
					{
						_components.add(new TextComponent(xPos, _yPos
								- (elements * yDiff), "Used in "
								+ behavior.getBehaviorName() + "."));
						elements++;
					}
				}

			}
		}
	}
	
	private TextComponent _buildingInformationLabel;

	private int _selectedBuilding;

	private int _yPos;

}
