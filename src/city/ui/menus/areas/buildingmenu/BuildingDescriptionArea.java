package city.ui.menus.areas.buildingmenu;

import org.newdawn.slick.Color;

import main.ClayConstants;
import screens.AbstractScreen;
import screens.CityScreen;
import city.generics.data.BuildingData;
import city.ui.menus.areas.AbstractArea;
import city.ui.menus.components.HorizontalLineComponent;
import city.ui.menus.components.TextComponent;

public class BuildingDescriptionArea extends AbstractArea
{
	public BuildingDescriptionArea(AbstractScreen homeScreen_)
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
			_components.add(new HorizontalLineComponent(57, 2));
			_components.add(new TextComponent(5, 58, "Building Description",
					ClayConstants.M_AREA_HEADER_COLOR));
			_components
					.add(new TextComponent(5, 61, BuildingData.getBuildingById(
							selectedBuilding).getBuildingDescription()));
			_components.add(new TextComponent(5, 91,
					"Press TAB to view building menu.", Color.green));
			_components.add(new TextComponent(5, 94,
					"Press 'A' to view Attributes.", Color.green));
			_components.add(new TextComponent(5, 97, "Press 'L' to view Lore.",
					Color.green));
		}
	}

	private int _selectedBuilding;

}
