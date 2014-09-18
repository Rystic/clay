package org.rystic.city.ui.menus.areas.buildingmenu;

import java.util.Map;

import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.ui.menus.BuildingMenu;
import org.rystic.city.ui.menus.areas.AbstractArea;
import org.rystic.city.ui.menus.components.AbstractButton;
import org.rystic.city.ui.menus.components.AbstractComponent;
import org.rystic.city.ui.menus.components.HorizontalLineComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class CategorySelectionArea extends AbstractArea
{
	public CategorySelectionArea(AbstractScreen homeScreen_,
			Map<String, String> categoryLabels_,
			Map<Integer, String> categoryHotkeys_, final BuildingMenu menu_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();

		_categoryLabels = categoryLabels_;
		_categoryHotkeys = categoryHotkeys_;
		_components.add(new TextComponent(5, 5, HEADER_STRING,
				ClayConstants.M_AREA_HEADER_COLOR));
		int increment = 8;
		for (String label : _categoryLabels.keySet())
		{
			final TextComponent component = new TextComponent(7, increment, label,
					ClayConstants.M_UNHIGHLIGHTED_COLOR);
			AbstractButton button = new AbstractButton(7, increment + 2, 255, 20){

				@Override
				public void clicked()
				{
					String newCategory = _categoryLabels.get(component.getText());
					for (Integer key : _categoryHotkeys.keySet())
					{
						if (_categoryHotkeys.get(key).equals(newCategory))
						{
							menu_.handleKeyEvent(key);
							break;
						}
					}
				}};
				_components.add(button);
			_components.add(component);
			increment += 3;
		}
		_components.add(new HorizontalLineComponent(increment + 1, 2));
		_category = ClayConstants.DEFAULT_BUILDING_MENU_CATEGORY;
		_buildingIdentifier = -1;
		updateHighlight(_category);
	}

	public void updateSelection(int key_)
	{
		String newCategory = _categoryHotkeys.get(key_);
		if (newCategory == null)
			return;
		updateHighlight(newCategory);
	}
	
	private void updateHighlight(String category_)
	{
		for (AbstractComponent component : _components)
		{
			if (component instanceof TextComponent)
			{
				TextComponent textComponent = (TextComponent) component;
				if (textComponent.getText().equals(HEADER_STRING))
					continue;
				textComponent
						.setColor(_categoryLabels.get(textComponent.getText())
								.equals(category_) ? ClayConstants.M_HIGHLIGHTED_COLOR : ClayConstants.M_UNHIGHLIGHTED_COLOR);
			}
		}
	}

	@Override
	public void update()
	{
		if (_model.getSelectedBuilding() != _buildingIdentifier)
		{
			_buildingIdentifier = _model.getSelectedBuilding();
			String category = BuildingData.getBuildingById(_buildingIdentifier)
					.getMenuCategory();
			if (!category.equals(_category))
			{
				_category = BuildingData.getBuildingById(_buildingIdentifier)
						.getMenuCategory();
				updateHighlight(_category);
			}
		}
	}

	private CityModel _model;

	private Map<String, String> _categoryLabels;
	private Map<Integer, String> _categoryHotkeys;

	private String _category;
	private int _buildingIdentifier;

	private static final String HEADER_STRING = "Building Categories";
}
