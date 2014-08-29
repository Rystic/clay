package city.ui.menus.areas.buildingmenu;

import java.util.HashMap;
import java.util.Map;

import main.ClayConstants;
import models.CityModel;
import screens.AbstractScreen;
import city.generics.GenericBuilding;
import city.generics.GenericItem;
import city.generics.data.BuildingData;
import city.generics.data.ItemData;
import city.ui.menus.areas.AbstractArea;
import city.ui.menus.components.HorizontalLineComponent;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

public class BuildResourceCostArea extends AbstractArea
{
	public BuildResourceCostArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();
		_selectedBuilding = -1;
	}

	@Override
	public void update()
	{
		if (_selectedBuilding != _model.getSelectedBuilding())
		{
			_components.clear();

			_selectedBuilding = _model.getSelectedBuilding();
			GenericBuilding building = BuildingData
					.getBuildingById(_selectedBuilding);
			String constructionItems = building.getConstructionItems();

			_components.add(new HorizontalLineComponent(30, 2));
			_components.add(new TextComponent(
					5,
					31,
					"Required Resources",
					ClayConstants.M_AREA_HEADER_COLOR));
			Map<String, Integer> itemCounts = new HashMap<String, Integer>();
			for (String item : constructionItems.split(","))
			{
				Integer count = itemCounts.get(item);
				if (count == null)
					count = 1;
				else
					count++;
				itemCounts.put(item, count);
			}
			int segments = building.getValidPlacementMap().keySet().size();
			int increment = 35;
			for (String itemTag : itemCounts.keySet())
			{
				if (itemTag.isEmpty()) continue;
				GenericItem item = ItemData.getItem(itemTag);
				_components.add(new ImageComponent(5, increment + 3	, 25, 25, item.getTexture()));
				_components.add(new TextComponent(15, increment, "x" + (itemCounts.get(itemTag) * segments) + "   " + item.getItemName()));
				increment += 3;
			}
		}

	}

	private CityModel _model;
	private int _selectedBuilding;
}
