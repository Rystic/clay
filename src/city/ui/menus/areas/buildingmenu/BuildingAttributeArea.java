package city.ui.menus.areas.buildingmenu;

import main.ClayConstants;

import org.newdawn.slick.Color;

import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.GenericBuilding;
import city.generics.data.BuildingData;
import city.ui.menus.areas.AbstractArea;
import city.ui.menus.components.HorizontalLineComponent;
import city.ui.menus.components.TextComponent;

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
				_components.add(new TextComponent(5, increment,
						"Can support structures."));
			}
			else
			{
				_components.add(new TextComponent(5, increment,
						"Cannot support structures."));
			}
			increment += 3;
			if (building.isHouse())
			{
				_components.add(new TextComponent(5, increment,
						"Can be used for repairs by clay golems."));
				increment += 3;
			}
			if (building.isStorage())
			{
				_components.add(new TextComponent(5, increment,
						"Storage capacity of " + building.getStorageCapacity()
								+ ".", Color.yellow));
				increment += 3;
			}
		}
	}

	private int _selectedBuilding;

}
