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
		_requiresUpdate = true;
		_firstArea = firstArea_;
	}
	
	public void requiresUpdate()
	{
		_requiresUpdate = true;
	}

	@Override
	public void update()
	{
		if (_requiresUpdate)
		{
			_components.clear();
			if (_firstArea)
			{
				TextComponent title = new TextComponent(60, _startingY - 50, "Resource Management");
				title.setTextColor(Color.yellow);
				_components.add(title);
			}
			Map<String, Integer> ratios = _model.getItemRatios().get(_familyName);
			TextComponent header = new TextComponent(40, _startingY - 100, _familyName
					+ " Family");
			header.setTextColor(Color.yellow);
			_components.add(header);
			int yPos = _startingY - 100;
			for (String key : ratios.keySet())
			{
				GenericItem item = ItemData.getItem(key);
				ImageComponent resourceIcon = new ImageComponent(30, yPos - 60, 30, 30,
						item.getTexture());
				TextComponent resourceName = new TextComponent(60, yPos - 30,
						item.getItemName());
				TextComponent resourceRatio = new TextComponent(205, yPos - 30,
						ratios.get(key).toString() + "%");
				_components.add(resourceIcon);
				_components.add(resourceName);
				_components.add(resourceRatio);
				if (!item.isFamilyHead()){
					IncreaseItemRatioButton itemIncrease = new IncreaseItemRatioButton(255, yPos - 50, 20, 20, _model, key, this);
					DecreaseItemRatioButton itemDecrease = new DecreaseItemRatioButton(275, yPos - 50, 20, 20, _model, key, this);
					_components.add(itemIncrease);
					_components.add(itemDecrease);
				}
				yPos -= 35;
			}
			_requiresUpdate = false;
		}
	}

	private CityModel _model;
	private String _familyName;
	
	private int _startingY;
	
	private boolean _requiresUpdate;
	private boolean _firstArea;
}
