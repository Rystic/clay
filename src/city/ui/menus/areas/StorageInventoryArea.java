package city.ui.menus.areas;

import java.util.Map;

import city.ui.menus.components.TextComponent;
import models.CityModel;
import screens.AbstractScreen;

public class StorageInventoryArea extends AbstractArea
{
	public StorageInventoryArea(AbstractScreen homeScreen_, int yPos_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();
		_yPos = yPos_;
	}

	@Override
	public void update()
	{
		_components.clear();
		Map<String, Integer> itemInventory = _model.getItemInventory();
		int yDiff = 0;
		for (String key : itemInventory.keySet())
		{
			_components.add(new TextComponent(50, _yPos
					- yDiff, key + " --- " + itemInventory.get(key)));
			yDiff -= 20;
		}
	}
	
	private CityModel _model;
	private int _yPos;
}
