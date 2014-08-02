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
			title.setColor(Color.yellow);
			_components.add(title);
		}
		Map<String, Integer> desiredRatios = _model.getItemRatios().get(
				_familyName);
		TextComponent header = new TextComponent(20, _startingY - 100);
		header.setColor(Color.yellow);
		_components.add(header);

		int yPos = _startingY - 100;

		Map<String, Integer> itemFamily = _model.getItemInventory().get(
				_familyName);
		Map<String, Integer> currentRatios = _model.getCurrentItemRatios().get(
				_familyName);
		int totalItems = 0;
		for (String key : desiredRatios.keySet())
		{
			Integer itemCount = itemFamily == null ? null : itemFamily.get(key);
			if (itemCount == null)
				itemCount = 0;
			Integer itemCurrentRatio = currentRatios == null ? null : currentRatios
					.get(key);
			if (itemCurrentRatio == null)
				itemCurrentRatio = 0;
			GenericItem item = ItemData.getItem(key);
			ImageComponent resourceIcon = new ImageComponent(5, yPos - 60, 30,
					30, item.getTexture());
			TextComponent resourceName = new TextComponent(30, yPos - 30,
					item.getItemName() + " (" + itemCount + ")");
			yPos -= 30;
			TextComponent resourceRatio = new TextComponent(30, yPos - 30,
					itemCurrentRatio + "%/" + desiredRatios.get(key).toString()
							+ "% (desired)");
			int diff = desiredRatios.get(key) - itemCurrentRatio;
			if (diff >= 5)
				resourceRatio.setColor(Color.red);
			else if (diff <= -5)
				resourceRatio.setColor(Color.green);
			_components.add(resourceIcon);
			_components.add(resourceName);
			_components.add(resourceRatio);
			if (!item.isFamilyHead())
			{
				IncreaseItemRatioButton itemIncrease = new IncreaseItemRatioButton(
						185, yPos - 50, 20, 20, _model, key);
				DecreaseItemRatioButton itemDecrease = new DecreaseItemRatioButton(
						210, yPos - 50, 20, 20, _model, key);
				_components.add(itemIncrease);
				_components.add(itemDecrease);
			}
			yPos -= 40;
			totalItems += itemCount;
		}
		header.setText(_familyName + " Family (" + totalItems + ")");
	}

	private CityModel _model;
	private String _familyName;

	private int _startingY;

	private boolean _firstArea;
}
