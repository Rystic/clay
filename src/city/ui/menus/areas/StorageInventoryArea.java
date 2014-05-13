package city.ui.menus.areas;

import java.util.Map;

import org.newdawn.slick.Color;

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
		_storageMenuLabel = new TextComponent(70, _yPos + 25,
				"Storage Information");
		_storageMenuLabel.setTextColor(Color.yellow);
		_noItemsLabel = new TextComponent(72, _yPos - 15,
				"No items in storage.");
		_noItemsLabel.setTextColor(Color.red);

	}

	@Override
	public void update()
	{
		_components.clear();
		_components.add(_storageMenuLabel);
		Map<String, Integer> itemInventory = _model.getItemInventory();
		if (itemInventory.keySet().isEmpty())
			_components.add(_noItemsLabel);

		int yDiff = 0;
		for (String key : itemInventory.keySet())
		{
			GenericItem item = ItemData.getItem(key);
			_components.add(new ImageComponent(20, _yPos - yDiff - 40, 30, 30,
					item.getTexture()));
			_components.add(new TextComponent(50, _yPos - yDiff - 15, item
					.getItemName() + " - " + itemInventory.get(key)));
			yDiff += 30;
		}
	}

	private TextComponent _storageMenuLabel;
	private TextComponent _noItemsLabel;
	private CityModel _model;
	private int _yPos;
}
