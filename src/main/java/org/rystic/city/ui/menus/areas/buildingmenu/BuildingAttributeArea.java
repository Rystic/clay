package org.rystic.city.ui.menus.areas.buildingmenu;

import java.awt.Color;

import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.util.FieldParser;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.ImageComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.screens.AbstractScreen;
import org.rystic.screens.CityScreen;

public class BuildingAttributeArea extends AbstractArea
{
	public BuildingAttributeArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_selectedBuilding = -1;
	}

	@Override
	public void update()
	{
		int selectedBuilding = ((CityScreen) _homeScreen).getModel()
				.getSelectedBuilding();
		if (selectedBuilding != _selectedBuilding)
		{
			_components.clear();
			// _components.add(new HorizontalLineComponent(57, 2));
			// _components.add(new TextComponent(5, 58, "Building Description",
			// ClayConstants.M_AREA_HEADER_COLOR));
			// _components
			// .add(new TextComponent(5, 61, BuildingData.getBuildingById(
			// selectedBuilding).getBuildingDescription()));
			_components.add(new TextComponent(5, 97,
					"Press TAB to view building info.", Color.green));
			// _components.add(new TextComponent(5, 97,
			// "Press 'L' to view Lore.",
			// Color.green));
			int increment = 5;
			GenericBuilding building = BuildingData
					.getBuildingById(selectedBuilding);
			if (building.isSupport())
			{
				addBullet(increment);
				_components.add(new TextComponent(8, increment,
						"Can support structures."));
			}
			else
			{
				addBullet(increment);
				_components.add(new TextComponent(8, increment,
						"Cannot support structures."));
			}
			increment += 3;
			if (building.isHouse())
			{
				addBullet(increment);
				_components.add(new TextComponent(8, increment,
						"Can be used for repairs by clay golems."));
				increment += 3;
			}
			if (building.isStorage())
			{
				addBullet(increment);
				_components.add(new TextComponent(8, increment,
						"Storage capacity of " + building.getStorageCapacity()
								+ ".", Color.yellow));
				increment += 3;
			}
		}
	}
	
	private void addBullet(int increment_)
	{
		_components.add(new ImageComponent(0, increment_ + 3, 28, 28, FieldParser.parseTexture("menuBullet.png")));

	}

	private int _selectedBuilding;

}
