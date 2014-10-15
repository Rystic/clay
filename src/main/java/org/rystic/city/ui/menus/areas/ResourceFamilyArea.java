package org.rystic.city.ui.menus.areas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.Color;
import org.rystic.city.generics.GenericItem;
import org.rystic.city.generics.data.ItemData;
import org.rystic.city.ui.menus.components.ImageComponent;
import org.rystic.city.ui.menus.components.TextComponent;
import org.rystic.city.ui.menus.components.AdjustItemRatioButton.DecreaseItemRatioButton;
import org.rystic.city.ui.menus.components.AdjustItemRatioButton.IncreaseItemRatioButton;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class ResourceFamilyArea extends AbstractArea
{
	public ResourceFamilyArea(AbstractScreen homeScreen_)
	{
		super(homeScreen_);

		_model = (CityModel) _homeScreen.getModel();

		_itemToCurrentStockLabel = new HashMap<String, TextComponent>();
		_itemToDesiredStockLabel = new HashMap<String, TextComponent>();

		Set<String> allItems = ItemData.getAllItemTags();
		List<GenericItem> manaItems = new ArrayList<GenericItem>();
		List<GenericItem> clayItems = new ArrayList<GenericItem>();

		for (String itemTag : allItems)
		{
			GenericItem item = ItemData.getItem(itemTag);
			String itemFamily = item.getItemFamily();
			if (itemFamily.equals(ClayConstants.ITEM_FAMILY_MANA))
				manaItems.add(item);
			else if (itemFamily.equals(ClayConstants.ITEM_FAMILY_CLAY))
				clayItems.add(item);
			else
				System.out.println("Item with tag " + itemTag
						+ " read with unknown family.");
		}
		_manaHeader = new TextComponent(5, 5, "Mana Family",
				ClayConstants.M_AREA_HEADER_COLOR);
		_components.add(_manaHeader);
		int increment = 12;
		boolean first = true;
		for (GenericItem item : manaItems)
		{
			ImageComponent icon = new ImageComponent(7, increment, 25, 25,
					item.getTexture());
			icon.setDrawRatio(.80f);
			_components.add(icon);

			_components.add(new TextComponent(6, increment - 3, "(   ) "
					+ item.getItemName(), ClayConstants.M_AREA_HEADER_COLOR));
			_components.add(new TextComponent(7, increment, "Current Stock: "));
			_components.add(new TextComponent(7, increment + 3,
					"Desired Stock: "));

			TextComponent currentStock = new TextComponent(48, increment);
			TextComponent desiredStock = new TextComponent(48, increment + 3);
			_itemToCurrentStockLabel.put(item.getItemTag(), currentStock);
			_itemToDesiredStockLabel.put(item.getItemTag(), desiredStock);
			_components.add(currentStock);
			_components.add(desiredStock);

			if (first)
				first = false;
			else
			{
				IncreaseItemRatioButton increaseButton = new IncreaseItemRatioButton(
						82, increment + 5, 20, 20, _model, item.getItemTag());
				DecreaseItemRatioButton decreaseButton = new DecreaseItemRatioButton(
						90, increment + 5, 20, 20, _model, item.getItemTag());
				_components.add(increaseButton);
				_components.add(decreaseButton);
			}
			increment += 10;
		}
		_clayHeader = new TextComponent(5, increment - 2, "Clay Family",
				ClayConstants.M_CLAY_RELATED);
		_components.add(_clayHeader);
		increment += 5;
		first = true;
		for (GenericItem item : clayItems)
		{
			ImageComponent icon = new ImageComponent(7, increment, 25, 25,
					item.getTexture());
			icon.setDrawRatio(.80f);
			_components.add(icon);

			_components.add(new TextComponent(6, increment - 3, "(   ) "
					+ item.getItemName(), ClayConstants.M_CLAY_RELATED));
			_components.add(new TextComponent(7, increment, "Current Stock: "));
			_components.add(new TextComponent(7, increment + 3,
					"Desired Stock: "));

			TextComponent currentStock = new TextComponent(48, increment);
			TextComponent desiredStock = new TextComponent(48, increment + 3);
			_itemToCurrentStockLabel.put(item.getItemTag(), currentStock);
			_itemToDesiredStockLabel.put(item.getItemTag(), desiredStock);
			_components.add(currentStock);
			_components.add(desiredStock);

			if (first)
				first = false;
			else
			{
				IncreaseItemRatioButton increaseButton = new IncreaseItemRatioButton(
						82, increment + 5, 20, 20, _model, item.getItemTag());
				DecreaseItemRatioButton decreaseButton = new DecreaseItemRatioButton(
						90, increment + 5, 20, 20, _model, item.getItemTag());
				_components.add(increaseButton);
				_components.add(decreaseButton);
			}

			increment += 10;
		}
	}

	@Override
	public void update()
	{
		Set<String> items = _itemToCurrentStockLabel.keySet();
		int manaTotalItems = 0;
		int clayTotalItems = 0;
		for (String itemTag : items)
		{
			GenericItem item = ItemData.getItem(itemTag);
			TextComponent currentStock = _itemToCurrentStockLabel.get(itemTag);
			TextComponent desiredStock = _itemToDesiredStockLabel.get(itemTag);

			String familyName = item.getItemFamily();

			Map<String, Integer> currentItemRatios = _model
					.getCurrentItemRatios().get(familyName);
			Map<String, Integer> desiredItemRatios = _model
					.getDesiredItemRatios().get(familyName);

			Map<String, Integer> itemStock = _model.getCurrentItemInventory()
					.get(familyName);
			String itemPercentage;
			if (currentItemRatios != null)
				itemPercentage = currentItemRatios.get(itemTag) != null ? "("
						+ currentItemRatios.get(itemTag) + "%)" : "(0%)";
			else
				itemPercentage = "(0%)";
			if (itemStock != null)
			{
				Integer itemCount = itemStock.get(itemTag);
				if (itemCount == null) itemCount = 0;
				currentStock.setText(itemCount + " " + itemPercentage);
				if (familyName.equals(ClayConstants.ITEM_FAMILY_MANA))
					manaTotalItems += itemCount;
				else if (familyName.equals(ClayConstants.ITEM_FAMILY_CLAY))
					clayTotalItems += itemCount;
			}
			else
				currentStock.setText("0 " + itemPercentage);

			Map<String, Integer> desiredFamilyItemCount = _model
					.getDesiredItemInventory().get(familyName);
			int desiredCount = 0;
			if (desiredFamilyItemCount != null)
			{
				if (desiredFamilyItemCount.get(itemTag) != null)
					desiredCount = desiredFamilyItemCount.get(itemTag);
			}
			desiredStock.setText(desiredCount + " ("
					+ desiredItemRatios.get(itemTag) + "%)");

			Integer itemCurrentRatio = currentItemRatios != null ? currentItemRatios
					.get(itemTag) : null;
			if (itemCurrentRatio == null)
				itemCurrentRatio = 0;
			int diff = desiredItemRatios.get(itemTag) - itemCurrentRatio;
			if (diff >= 5)
				currentStock.setColor(Color.red);
			else if (diff <= -5)
				currentStock.setColor(Color.green);
			else
				currentStock.setColor(Color.white);
		}
		_manaHeader.setText("Mana Family (" + manaTotalItems + " total items)");
		_clayHeader.setText("Clay Family (" + clayTotalItems + " total items)");
	}

	private CityModel _model;

	private Map<String, TextComponent> _itemToCurrentStockLabel;
	private Map<String, TextComponent> _itemToDesiredStockLabel;

	private TextComponent _manaHeader;
	private TextComponent _clayHeader;

}
