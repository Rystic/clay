package city.ui.menus.areas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.Color;

import main.ClayConstants;
import models.CityModel;
import screens.AbstractScreen;
import city.generics.GenericItem;
import city.generics.data.ItemData;
import city.ui.menus.components.AdjustItemRatioButton.DecreaseItemRatioButton;
import city.ui.menus.components.AdjustItemRatioButton.IncreaseItemRatioButton;
import city.ui.menus.components.ImageComponent;
import city.ui.menus.components.TextComponent;

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
		_components.add(new TextComponent(5, 5, "Mana Family",
				ClayConstants.M_AREA_HEADER_COLOR));
		int increment = 12;
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

			IncreaseItemRatioButton increaseButton = new IncreaseItemRatioButton(
					85, increment + 5, 20, 20, _model, item.getItemTag());
			DecreaseItemRatioButton decreaseButton = new DecreaseItemRatioButton(
					93, increment + 5, 20, 20, _model, item.getItemTag());
			_components.add(increaseButton);
			_components.add(decreaseButton);

			increment += 10;
		}
		_components.add(new TextComponent(5, increment - 2, "Clay Family",
				ClayConstants.M_CLAY_RELATED));
		increment += 5;
		for (GenericItem item : clayItems)
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

			IncreaseItemRatioButton increaseButton = new IncreaseItemRatioButton(
					85, increment + 5, 20, 20, _model, item.getItemTag());
			DecreaseItemRatioButton decreaseButton = new DecreaseItemRatioButton(
					93, increment + 5, 20, 20, _model, item.getItemTag());
			_components.add(increaseButton);
			_components.add(decreaseButton);

			increment += 10;
		}
	}

	@Override
	public void update()
	{
		Set<String> items = _itemToCurrentStockLabel.keySet();
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

			Map<String, Integer> itemStock = _model.getCurrentItemInventory().get(
					familyName);
			String itemPercentage;
			if (currentItemRatios != null)
				itemPercentage = currentItemRatios.get(itemTag) != null ? "("
						+ currentItemRatios.get(itemTag) + "%)" : "(0%)";
			else
				itemPercentage = "";
			if (itemStock != null)
			{
				Object itemCount = itemStock.get(itemTag);
				currentStock.setText((itemCount == null ? "0" : String
						.valueOf(itemCount)) + " " + itemPercentage);
			}
			else
				currentStock.setText("0 " + itemPercentage);

			Map<String, Integer> desiredFamilyItemCount = _model.getDesiredItemInventory().get(familyName);
			int desiredCount = 0;
			if (desiredFamilyItemCount != null)
			{
				if (desiredFamilyItemCount.get(itemTag) != null)
					desiredCount = desiredFamilyItemCount.get(itemTag);
			}
			desiredStock.setText(desiredCount + " (" + desiredItemRatios.get(itemTag) + "%)");
			
			Integer itemCurrentRatio = currentItemRatios != null ? currentItemRatios.get(itemTag) : null;
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
		// _components.clear();
		// if (_firstArea)
		// {
		// TextComponent title = new TextComponent(60, _startingY - 50,
		// "Resource Management");
		// title.setColor(Color.yellow);
		// _components.add(title);
		// }
		// Map<String, Integer> desiredRatios = _model.getItemRatios().get(
		// _familyName);
		// TextComponent header = new TextComponent(20, _startingY - 100);
		// header.setColor(Color.yellow);
		// _components.add(header);
		//
		// int yPos = _startingY - 100;
		//
		// Map<String, Integer> itemFamily = _model.getItemInventory().get(
		// _familyName);
		// Map<String, Integer> currentRatios =
		// _model.getCurrentItemRatios().get(
		// _familyName);
		// int totalItems = 0;
		// for (String key : desiredRatios.keySet())
		// {
		// Integer itemCount = itemFamily == null ? null : itemFamily.get(key);
		// if (itemCount == null)
		// itemCount = 0;
		// Integer itemCurrentRatio = currentRatios == null ? null :
		// currentRatios
		// .get(key);
		// if (itemCurrentRatio == null)
		// itemCurrentRatio = 0;
		// GenericItem item = ItemData.getItem(key);
		// ImageComponent resourceIcon = new ImageComponent(5, yPos - 60, 30,
		// 30, item.getTexture());
		// TextComponent resourceName = new TextComponent(30, yPos - 30,
		// item.getItemName() + " (" + itemCount + ")");
		// yPos -= 30;
		// TextComponent resourceRatio = new TextComponent(30, yPos - 30,
		// itemCurrentRatio + "%/" + desiredRatios.get(key).toString()
		// + "% (desired)");
		// int diff = desiredRatios.get(key) - itemCurrentRatio;
		// if (diff >= 5)
		// resourceRatio.setColor(Color.red);
		// else if (diff <= -5)
		// resourceRatio.setColor(Color.green);
		// _components.add(resourceIcon);
		// _components.add(resourceName);
		// _components.add(resourceRatio);
		// if (!item.isFamilyHead())
		// {
		// IncreaseItemRatioButton itemIncrease = new IncreaseItemRatioButton(
		// 185, yPos - 50, 20, 20, _model, key);
		// DecreaseItemRatioButton itemDecrease = new DecreaseItemRatioButton(
		// 210, yPos - 50, 20, 20, _model, key);
		// _components.add(itemIncrease);
		// _components.add(itemDecrease);
		// }
		// yPos -= 40;
		// totalItems += itemCount;
		// }
		// header.setText(_familyName + " Family (" + totalItems + ")");
	}

	private CityModel _model;

	private Map<String, TextComponent> _itemToCurrentStockLabel;
	private Map<String, TextComponent> _itemToDesiredStockLabel;

}
