package city.ui.menus.components;

import java.util.Map;

import main.ClayConstants;
import models.CityModel;

import org.newdawn.slick.opengl.Texture;

import city.generics.GenericItem;
import city.generics.data.ItemData;
import city.generics.util.FieldParser;

public class AdjustItemRatioButton extends AbstractButton
{
	protected AdjustItemRatioButton(int x_, int y_, int width_, int height_,
			CityModel model_, String item_, boolean increaseValue_)
	{
		super(x_, y_, width_, height_);
		_item = item_;
		_increaseValue = increaseValue_;
		_model = model_;
		setTexture(increaseValue_ ? _plusIcon : _minusIcon);
		setDrawRatio(.77f);
	}

	@Override
	public void clicked()
	{
		GenericItem item = ItemData.getItem(_item);
		Map<String, Integer> ratios = _model.getItemRatios().get(
				item.getItemFamily());
		Integer percentage = ratios.get(_item);
		if (_increaseValue)
			percentage += ClayConstants.ITEM_RATIO_INCREMENT;
		else
			percentage -= ClayConstants.ITEM_RATIO_INCREMENT;
		if (percentage < 0 || percentage > 100)
			return;
		if (adjustParent(item, ratios))
		{
			ratios.put(_item, percentage);
		}

	}

	private boolean adjustParent(GenericItem item_, Map<String, Integer> ratios_)
	{
		String parentTag = item_.getParentTag();
		GenericItem parentItem = ItemData.getItem(parentTag);
		if (_increaseValue)
		{
			if (parentItem != null && parentItem.getParentTag().isEmpty())
			{
				Integer parentRatio = ratios_.get(parentTag);
				if (parentRatio < ClayConstants.ITEM_RATIO_INCREMENT)
					return false;
				parentRatio -= ClayConstants.ITEM_RATIO_INCREMENT;
				ratios_.put(parentTag, parentRatio);
				return true;
			}
			else
			{
				if (parentItem != null)
				{
					boolean parentDecrease = adjustParent(parentItem, ratios_);
					if (parentDecrease)
						return true;
				}
				Integer itemRatio = ratios_.get(item_.getItemTag());
				if (itemRatio < ClayConstants.ITEM_RATIO_INCREMENT)
					return false;
				itemRatio -= ClayConstants.ITEM_RATIO_INCREMENT;
				ratios_.put(item_.getItemTag(), itemRatio);
				return true;
			}
		}
		else
		{
			if (parentItem != null && parentItem.getParentTag().isEmpty())
			{
				Integer parentRatio = ratios_.get(parentTag);
				if (parentRatio + ClayConstants.ITEM_RATIO_INCREMENT > 100)
					return false;
				parentRatio += ClayConstants.ITEM_RATIO_INCREMENT;
				ratios_.put(parentTag, parentRatio);
				return true;
			}
			else
			{
				if (parentItem != null)
				{
					boolean parentIncrease = adjustParent(parentItem, ratios_);
					if (parentIncrease)
						return true;
				}
				Integer itemRatio = ratios_.get(item_.getItemTag());
				if (itemRatio + ClayConstants.ITEM_RATIO_INCREMENT > 100)
					return false;
				itemRatio += ClayConstants.ITEM_RATIO_INCREMENT;
				ratios_.put(item_.getItemTag(), itemRatio);
				return true;
			}
		}
	}

	public static class IncreaseItemRatioButton extends AdjustItemRatioButton
	{
		public IncreaseItemRatioButton(int x_, int y_, int width_, int height_,
				CityModel model_, String item_)
		{
			super(x_, y_, width_, height_, model_, item_, true);
		}

	}

	public static class DecreaseItemRatioButton extends AdjustItemRatioButton
	{
		public DecreaseItemRatioButton(int x_, int y_, int width_, int height_,
				CityModel model_, String item_)
		{
			super(x_, y_, width_, height_, model_, item_, false);
		}

	}

	private static Texture _plusIcon = FieldParser.parseTexture("plusIcon.png");
	private static Texture _minusIcon = FieldParser
			.parseTexture("minusIcon.png");

	private CityModel _model;
	private String _item;

	private boolean _increaseValue;
}
