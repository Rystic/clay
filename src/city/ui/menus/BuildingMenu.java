package city.ui.menus;

import screens.AbstractScreen;
import city.generics.data.BuildingData;
import city.ui.components.SelectBuildingButton;
import city.ui.events.InterfaceKeyEvent;
import city.ui.events.InterfaceMouseEvent;

public class BuildingMenu extends AbstractMenu
{
	public BuildingMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		SelectBuildingButton houseButton = new SelectBuildingButton(115, 750, 75, 75, BuildingData.getBuildingByTag("crucible"));
		houseButton.setDrawRatio(.77f);
		_components.add(houseButton);
	}
	
	@Override
	public void handleKeyEvent(InterfaceKeyEvent event_)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void handleMouseEvent(InterfaceMouseEvent event_)
	{
		// TODO Auto-generated method stub
	}
}
