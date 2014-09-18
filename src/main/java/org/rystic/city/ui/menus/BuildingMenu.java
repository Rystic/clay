package org.rystic.city.ui.menus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.rystic.city.ui.menus.areas.buildingmenu.BuildResourceCostArea;
import org.rystic.city.ui.menus.areas.buildingmenu.BuildingAttributeArea;
import org.rystic.city.ui.menus.areas.buildingmenu.BuildingDescriptionArea;
import org.rystic.city.ui.menus.areas.buildingmenu.BuildingPatternArea;
import org.rystic.city.ui.menus.areas.buildingmenu.BuildingSelectionArea;
import org.rystic.city.ui.menus.areas.buildingmenu.CategoryDescriptionArea;
import org.rystic.city.ui.menus.areas.buildingmenu.CategorySelectionArea;
import org.rystic.screens.AbstractScreen;

public class BuildingMenu extends AbstractMenu
{
	public BuildingMenu(AbstractScreen homeScreen_)
	{
		super(homeScreen_, "Building Menu");

		_buildingInfo = false;

		_categorySelectionArea = new CategorySelectionArea(_homeScreen,
				DEFAULT_CATEGORY_LABELS, DEFAULT_HOTKEYS, this);
		_categoryDescriptionArea = new CategoryDescriptionArea(_homeScreen,
				DEFAULT_HOTKEYS);
		_buildingSelectionArea = new BuildingSelectionArea(_homeScreen,
				DEFAULT_HOTKEYS);

		_buildingPatternArea = new BuildingPatternArea(_homeScreen);
		_buildingResourceCostArea = new BuildResourceCostArea(_homeScreen);
		_buildingDescriptionArea = new BuildingDescriptionArea(_homeScreen);

		_buildingAttributeArea = new BuildingAttributeArea(_homeScreen);

		_areas.add(_categorySelectionArea);
		_areas.add(_categoryDescriptionArea);
		_areas.add(_buildingSelectionArea);

		_hotKeys.addAll(DEFAULT_HOTKEYS.keySet());
		_hotKeys.add(Keyboard.KEY_TAB);
	}

	@Override
	public void handleKeyEvent(Integer key_)
	{
		if (Keyboard.KEY_TAB == key_)
		{
			if (!_buildingAttribute)
				_buildingInfo = !_buildingInfo;
			if (_buildingInfo)
			{
				_areas.remove(_categorySelectionArea);
				_areas.remove(_categoryDescriptionArea);
				_areas.remove(_buildingSelectionArea);
				_areas.remove(_buildingAttributeArea);

				_areas.add(_buildingPatternArea);
				_areas.add(_buildingResourceCostArea);
				_areas.add(_buildingDescriptionArea);
				_buildingAttribute = false;
			}
			else
			{
				_areas.add(_categorySelectionArea);
				_areas.add(_categoryDescriptionArea);
				_areas.add(_buildingSelectionArea);

				_areas.remove(_buildingPatternArea);
				_areas.remove(_buildingResourceCostArea);
				_areas.remove(_buildingDescriptionArea);

			}
		}
		else
		{
			if (!_buildingInfo)
			{
				_categorySelectionArea.updateSelection(key_);
				_categoryDescriptionArea.updateSelection(key_);
				_buildingSelectionArea.updateCategorySelection(key_);
			}
			else if (Keyboard.KEY_A == key_ && !_buildingAttribute)
			{
				_areas.remove(_buildingPatternArea);
				_areas.remove(_buildingResourceCostArea);
				_areas.remove(_buildingDescriptionArea);
				_areas.add(_buildingAttributeArea);
				_buildingAttribute = true;
			}
		}
	}

	@Override
	public void handleMouseWheel(boolean upwardScroll_)
	{
		if (upwardScroll_)
			_buildingSelectionArea.updateBuildingSelection(-1);
		else
			_buildingSelectionArea.updateBuildingSelection(1);
	}

	private static Map<String, String> DEFAULT_CATEGORY_LABELS = new LinkedHashMap<String, String>();
	private static Map<Integer, String> DEFAULT_HOTKEYS = new HashMap<Integer, String>();

	static
	{
		DEFAULT_CATEGORY_LABELS.put("(A) Architecture", "Architecture");
		DEFAULT_CATEGORY_LABELS.put("(S) Basic Needs", "Basic Needs");
		DEFAULT_CATEGORY_LABELS.put("(C) Conversions", "Conversions");
		DEFAULT_CATEGORY_LABELS.put("(R) Culture", "Culture");
		DEFAULT_CATEGORY_LABELS.put("(E) Mana Pool", "Mana Pool");

		DEFAULT_HOTKEYS.put(Keyboard.KEY_A, "Architecture");
		DEFAULT_HOTKEYS.put(Keyboard.KEY_S, "Basic Needs");
		DEFAULT_HOTKEYS.put(Keyboard.KEY_C, "Conversions");
		DEFAULT_HOTKEYS.put(Keyboard.KEY_R, "Culture");
		DEFAULT_HOTKEYS.put(Keyboard.KEY_E, "Mana Pool");
	}

	private BuildingSelectionArea _buildingSelectionArea;
	private CategorySelectionArea _categorySelectionArea;
	private CategoryDescriptionArea _categoryDescriptionArea;

	private BuildingPatternArea _buildingPatternArea;
	private BuildResourceCostArea _buildingResourceCostArea;
	private BuildingDescriptionArea _buildingDescriptionArea;

	private BuildingAttributeArea _buildingAttributeArea;

	private boolean _buildingInfo;
	private boolean _buildingAttribute;
}
