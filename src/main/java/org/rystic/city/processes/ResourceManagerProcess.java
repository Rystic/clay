package org.rystic.city.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rystic.city.entities.building.BuildingEntity;
import org.rystic.city.generics.GenericBuilding;
import org.rystic.city.generics.GenericConversion;
import org.rystic.city.generics.data.BehaviorData;
import org.rystic.city.generics.data.BuildingData;
import org.rystic.city.generics.data.ConversionData;
import org.rystic.city.generics.objects.Behavior;
import org.rystic.city.generics.objects.ConversionBehavior;
import org.rystic.main.ClayConstants;
import org.rystic.models.CityModel;
import org.rystic.screens.AbstractScreen;

public class ResourceManagerProcess extends AbstractProcess
{
	public ResourceManagerProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_model = (CityModel) homeScreen_.getModel();
		_requiresUpdate = false;
	}

	@Override
	public void execute()
	{
		if (_requiresUpdate)
		{
			Map<String, Map<String, Integer>> itemInventory = _model
					.getCurrentItemInventory();
			Map<String, Map<String, Integer>> itemRatios = _model
					.getDesiredItemRatios();
			Map<String, Map<String, Integer>> currentItemRatios = new HashMap<String, Map<String, Integer>>();
			List<String> underQuotaItems = new ArrayList<String>();
			for (String familyName : itemInventory.keySet())
			{
				Map<String, Integer> currentFamilyRatios = new HashMap<String, Integer>();
				Map<String, Integer> desiredFamilyInventory = new HashMap<String, Integer>();
				Map<String, Integer> familyRatios = itemRatios.get(familyName);
				Integer total = 0;
				Map<String, Integer> familyInventory = itemInventory
						.get(familyName);
				for (String item : familyInventory.keySet())
				{
					total += familyInventory.get(item);
				}
				for (String item : familyRatios.keySet())
				{
					desiredFamilyInventory.put(
							item,
							(int) (((double) _model.getDesiredItemRatios()
									.get(familyName).get(item) / 100)
									* total));
					if (!familyInventory.containsKey(item)
							&& familyRatios.get(item) > 0)
					{
						underQuotaItems.add(item);
						continue;
					}
					Double itemCount = familyInventory.get(item) != null ? familyInventory
							.get(item).doubleValue() : 0D;
					Double percentage = 100 * (double) (itemCount / total);
					currentFamilyRatios.put(item, percentage.intValue());
					if (percentage < familyRatios.get(item))
						underQuotaItems.add(item);
				}
				currentItemRatios.put(familyName, currentFamilyRatios);
				for (String underQuotaItem : underQuotaItems)
				{
					GenericConversion conversion = ConversionData
							.getConversion(underQuotaItem);
					if (conversion == null)
						continue;
					String[] inputList = conversion.getConversionInput().split(
							",");
					Map<String, Integer> inputCountMap = new HashMap<String, Integer>();
					for (String input : inputList)
					{
						Integer count = inputCountMap.get(input);
						if (count == null)
							count = new Integer(0);
						count++;
						inputCountMap.put(input, count);
					}
					if (!inputItemsExist(itemInventory, inputCountMap))
						continue;
					GenericBuilding building = BuildingData
							.getBuildingByTag(conversion
									.getConversionBuilding());
					List<BuildingEntity> buildings = _model.getBuildingMap()
							.get(building.getBuildingIdentifier());
					if (buildings == null || buildings.size() == 0)
						continue;
					for (BuildingEntity conversionBuilding : buildings)
					{
						if (conversionBuilding.isBuilt()
								&& !conversionBuilding.hasActiveConversion(underQuotaItem) && conversionBuilding.getCopyOfHeldItems().isEmpty())
						{
							Object[] conversionParams = new Object[4];
							conversionParams[0] = conversionBuilding;
							conversionParams[1] = conversion.getConversionInput();
							conversionParams[2] = conversion.getConverstionOutput();
							conversionParams[3] = conversion.getHeat();
							Behavior conversionBehavior = new ConversionBehavior(underQuotaItem,
									BehaviorData
											.getBehavior(ClayConstants.BEHAVIOR_CONVERSION),
									conversionParams);
							conversionBehavior
									.setAssigningBuilding(conversionBuilding);
							conversionBuilding
									.addActiveBehavior(conversionBehavior);
							GolemBehaviorProcess behaviorProcess = ((GolemBehaviorProcess) _homeScreen
									.getProcess(GolemBehaviorProcess.class));
							behaviorProcess.queueBehavior(conversionBehavior);
						}
					}

				}
				_model.getDesiredItemInventory().put(
						familyName,
						desiredFamilyInventory);
			}
			_model.setCurrentItemRatios(currentItemRatios);
		}
	}

	private boolean inputItemsExist(Map<String, Map<String, Integer>> itemInventory_, Map<String, Integer> inputCountMap_)
	{
		boolean passed = true;
		inputItems: for (String inputName : inputCountMap_.keySet())
		{
			for (String familyName : itemInventory_.keySet())
			{
				Map<String, Integer> familyInventory = itemInventory_
						.get(familyName);
				if (familyInventory.containsKey(inputName))
				{
					if (familyInventory.get(inputName) >= inputCountMap_
							.get(inputName))
						continue inputItems;
				}
			}
			passed = false;
			break;
		}
		return passed;
	}

	public void requiresUpdate()
	{
		_requiresUpdate = true;
	}

	private boolean _requiresUpdate;

	private CityModel _model;
}
