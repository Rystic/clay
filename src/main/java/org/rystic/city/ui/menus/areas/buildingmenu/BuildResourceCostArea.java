package org.rystic.city.ui.menus.areas.buildingmenu;

import java.util.HashMap;
import java.util.Map;

import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.GenericItem;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.data.ItemData;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.HorizontalLineComponent;
import org.rystic.city.ui.menus.components.ImageComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

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
