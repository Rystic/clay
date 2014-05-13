package city.ui.menus.areas;

import java.util.Map;

import models.CityModel;
import screens.AbstractScreen;
import city.generics.GenericItem;
import city.generics.data.ItemData;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

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
			GenericItem item = ItemData.getItem(key);
			_components.add(new ImageComponent(20, _yPos - yDiff - 10, 30, 30, item.getTexture()));
			_components.add(new TextComponent(50, _yPos
					- yDiff + 15, item.getItemName() + " - " + itemInventory.get(key)));
			yDiff += 30;
		}
	}
	
	private CityModel _model;
	private int _yPos;
}
