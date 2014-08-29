package city.ui.menus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import screens.AbstractScreen;
import city.ui.menus.areas.buildingmenu.BuildResourceCostArea;
import city.ui.menus.areas.buildingmenu.BuildingDescriptionArea;
import city.ui.menus.areas.buildingmenu.BuildingPatternArea;
import city.ui.menus.areas.buildingmenu.BuildingSelectionArea;
import city.ui.menus.areas.buildingmenu.CategoryDescriptionArea;
import city.ui.menus.areas.buildingmenu.CategorySelectionArea;

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
			_buildingInfo = !_buildingInfo;
			if (_buildingInfo)
			{
				_areas.remove(_categorySelectionArea);
				_areas.remove(_categoryDescriptionArea);
				_areas.remove(_buildingSelectionArea);

				_areas.add(_buildingPatternArea);
				_areas.add(_buildingResourceCostArea);
				_areas.add(_buildingDescriptionArea);
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
			_categorySelectionArea.updateSelection(key_);
			_categoryDescriptionArea.updateSelection(key_);
			_buildingSelectionArea.updateCategorySelection(key_);
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

	private boolean _buildingInfo;
}
