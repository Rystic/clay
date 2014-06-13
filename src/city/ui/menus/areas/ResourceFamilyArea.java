package city.ui.menus.areas;

import java.util.Map;

import models.CityModel;

import org.newdawn.slick.Color;

import screens.AbstractScreen;
import city.generics.GenericItem;
import city.generics.data.ItemData;
import city.ui.menus.components.AdjustItemRatioButton.DecreaseItemRatioButton;
import city.ui.menus.components.AdjustItemRatioButton.IncreaseItemRatioButton;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

public class ResourceFamilyArea extends AbstractArea
{
	public ResourceFamilyArea(AbstractScreen homeScreen_, String familyName_,
			int yPos_, boolean firstArea_)
	{
		super(homeScreen_);
		_model = (CityModel) _homeScreen.getModel();
		_familyName = familyName_;
		_startingY = yPos_;
		_firstArea = firstArea_;
	}

	@Override
	public void update()
	{
		_components.clear();
		if (_firstArea)
		{
			TextComponent title = new TextComponent(60, _startingY - 50,
					"Resource Management");
			title.setTextColor(Color.yellow);
			_components.add(title);
		}
		Map<String, Integer> ratios = _model.getItemRatios().get(_familyName);
		TextComponent header = new TextComponent(20, _startingY - 100,
				_familyName + " Family");
		header.setTextColor(Color.yellow);
		_components.add(header);
		int yPos = _startingY - 100;
		for (String key : ratios.keySet())
		{
			Map<String, Integer> itemFamily = _model.getItemInventory().get(
					_familyName);
			Integer count = itemFamily == null ? null : itemFamily.get(key);
			if (count == null)
				count = 0;
			GenericItem item = ItemData.getItem(key);
			ImageComponent resourceIcon = new ImageComponent(15, yPos - 60, 30,
					30, item.getTexture());
			TextComponent resourceName = new TextComponent(45, yPos - 30,
					item.getItemName() + " (" + count + ")");
			TextComponent resourceRatio = new TextComponent(215, yPos - 30,
					ratios.get(key).toString() + "%");
			_components.add(resourceIcon);
			_components.add(resourceName);
			_components.add(resourceRatio);
			if (!item.isFamilyHead())
			{
				IncreaseItemRatioButton itemIncrease = new IncreaseItemRatioButton(
						255, yPos - 50, 20, 20, _model, key);
				DecreaseItemRatioButton itemDecrease = new DecreaseItemRatioButton(
						275, yPos - 50, 20, 20, _model, key);
				_components.add(itemIncrease);
				_components.add(itemDecrease);
			}
			yPos -= 35;
		}
	}

	private CityModel _model;
	private String _familyName;

	private int _startingY;

	private boolean _firstArea;
}
