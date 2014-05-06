package city.processes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.CityModel;
import screens.AbstractScreen;
import city.generics.entities.BuildingEntity;
import city.generics.objects.Item;

public class StorageInventoryProcess extends AbstractProcess
{
	public StorageInventoryProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_itemInventory = new HashMap<String, Integer>();
		_updateInventory = false;
	}

	@Override
	public void execute()
	{
		if (_updateInventory)
		{
			_updateInventory = false;
			_itemInventory.clear();
			BuildingEntity[][] buildings = _model.getTileValues();
			for (int i = 0; i < buildings.length; i++)
			{
				for (int j = 0; j < buildings[0].length; j++)
				{
					BuildingEntity building = buildings[i][j];
					if (building == null) continue;
					List<Item> items = building.getCopyOfHeldItems();
					for (Item item : items)
					{
						if (item.getItemIdentifier() != Item.NO_IDENTIFIER) continue;
						String tag = item.getTag();
						Integer count = _itemInventory.get(tag);
						if (count == null)
							_itemInventory.put(tag, new Integer(1));
						else
							_itemInventory.put(tag, count + 1);
					}
				}
			}
			for (String key : _itemInventory.keySet())
			{
				System.out.println(key + "   " + _itemInventory.get(key));
			}
		}
	}

	public void requestInventoryUpdate()
	{
		_updateInventory = true;
	}

	private CityModel _model;
	private Map<String, Integer> _itemInventory;
	private boolean _updateInventory;
}