package city.ui.menus;

import models.CityModel;
import screens.AbstractScreen;
import city.ui.menus.areas.ResourceFamilyArea;

public class ResourceManagementMenu extends AbstractMenu
{
	public ResourceManagementMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		CityModel model = (CityModel) homeScreen_.getModel();
		int yPos = 900;
		boolean first = true;
		for (String key : model.getItemRatios().keySet())
		{
			_areas.add(new ResourceFamilyArea(homeScreen_, key, yPos, first));
			yPos -= 200;
			first = false;
		}
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{
	}

	@Override
	public void handleMouseWheel(boolean upwardScroll_)
	{
	}
}
