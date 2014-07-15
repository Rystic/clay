package city.ai.calculators;

import java.awt.Point;
import java.util.List;
import java.util.Queue;

import main.ClayConstants;
import screens.CityScreen;
import city.generics.data.BuildingData;
import city.generics.entities.BuildingEntity;
import city.generics.entities.GolemEntity;
import city.util.SearchUtil;

public class BehaviorWeightCalculator
{

	public static int calculate(GolemEntity golem_, String weightConditions_, Object[] params_)
	{
		int finalWeight = 0;
		String[] weightConditions = weightConditions_.split(",");

		for (String weightCondition : weightConditions)
		{
			if (weightCondition.equals(ClayConstants.WC_CLOSEST_TO_POINT))
			{
				BuildingEntity entity = (BuildingEntity) params_[0];
				Queue<Point> path = SearchUtil.searchBuildingEntity(
						golem_,
						golem_.getHomeScreen(),
						entity);
				if (path.size() == 0)
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				finalWeight += 50 - path.size();
				if (finalWeight <= 0)
					finalWeight = 1;
			}
			else if (weightCondition.equals(ClayConstants.WC_LOW_MANA))
			{
				if (golem_.isLowMana())
				{
					finalWeight += (int) (300 - golem_.getMana());
					if (finalWeight <= 0)
						finalWeight = 1;
				}
			}
			else if (weightCondition.equals(ClayConstants.WC_LOW_CLAY))
			{
				if (golem_.isLowClay())
				{
					finalWeight += (int) (300 - golem_.getClay());
					if (finalWeight <= 0)
						finalWeight = 1;
				}
			}
			else if (weightCondition
					.equals(ClayConstants.WC_CAN_BUILD_CLAY_GOLEM))
			{
				CityScreen screen = (CityScreen) golem_.getHomeScreen();
				BuildingEntity[][] tiles = screen.getModel().getTileValues();
				int houseCount = 0;
				for (int i = 0; i < tiles.length; i++)
				{
					for (int j = 0; j < tiles[0].length; j++)
					{
						if (tiles[i][j] != null && tiles[i][j].isHouse()
								&& tiles[i][j].isBuilt())
							houseCount++;
					}
				}
				if (screen.getModel().getGolemCount() >= houseCount)
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				finalWeight += 50 + ((houseCount - screen.getModel()
						.getGolemCount()) * 10);
				if (golem_.getPsychology() == ClayConstants.PSYCHOLOGY_INFLUENTIAL)
				{
					finalWeight += 10 * (golem_.getIntensity() + 1);
					int personality = golem_.getPersonality();
					int personalityCount = 0;
					int otherPersonalityCount = 0;
					List<GolemEntity> cityGolems = golem_.getModel()
							.getGolems();
					for (GolemEntity golem : cityGolems)
					{
						if (golem.getPersonality() == personality)
						{
							personalityCount++;
							continue;
						}
						if (personality == ClayConstants.PERSONALITY_CREATIVE
								&& golem.getPersonality() == ClayConstants.PERSONALITY_AMBITIOUS)
						{
							otherPersonalityCount += 2;
							continue;
						}
						if (personality == ClayConstants.PERSONALITY_AMBITIOUS
								&& golem.getPersonality() == ClayConstants.PERSONALITY_CREATIVE)
						{
							otherPersonalityCount += 3;
							continue;
						}
						if (personality != ClayConstants.PERSONALITY_NONE)
						{
							otherPersonalityCount++;
							continue;
						}
					}
					int result = otherPersonalityCount - personalityCount;
					if (result > 0)
					{
						double multiplier = 1 + ((double) (golem_
								.getIntensity() + 1) / 10);
						finalWeight *= multiplier;
						finalWeight += 5 * result;
					}
				}
				if (finalWeight <= 0)
					finalWeight = 1;
			}
			else if (weightCondition
					.equals(ClayConstants.WC_CAN_BUILD_PEARLCLAY_GOLEM))
			{
				CityScreen screen = (CityScreen) golem_.getHomeScreen();
				int pearlclayCount = 0;
				List<GolemEntity> golems = screen.getModel().getGolems();
				for (GolemEntity entity : golems)
				{
					if (entity.getGolemTag().equals(
							ClayConstants.GOLEM_PEARLCLAY))
						pearlclayCount++;
				}
				Integer buildingId = BuildingData.getBuildingByTag(
						"mana-battery").getBuildingIdentifier();
				List<BuildingEntity> manaBatteries = screen.getModel()
						.getBuildingMap().get(buildingId);
				if (manaBatteries == null)
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				if (pearlclayCount < manaBatteries.size())
					finalWeight += 1000;
				else
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
			}
			else if (weightCondition
					.equals(ClayConstants.WC_CAN_BUILD_STONEWARE_GOLEM))
			{
				CityScreen screen = (CityScreen) golem_.getHomeScreen();
				BuildingEntity[][] tiles = screen.getModel().getTileValues();
				int houseCount = 0;
				for (int i = 0; i < tiles.length; i++)
				{
					for (int j = 0; j < tiles[0].length; j++)
					{
						if (tiles[i][j] != null && tiles[i][j].isHouse()
								&& tiles[i][j].isBuilt())
							houseCount++;
					}
				}
				if (screen.getModel().getGolemCount() >= houseCount)
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				if (golem_.getPersonality() == ClayConstants.PERSONALITY_AMBITIOUS)
				{
					finalWeight += 150 + ((houseCount - screen.getModel()
							.getGolemCount()) * 10);
					finalWeight = golem_.getPsychology() == ClayConstants.PSYCHOLOGY_INFLUENTIAL ? finalWeight * 4 : finalWeight;
				}
				else if (golem_.getPersonality() == ClayConstants.PERSONALITY_CREATIVE)
					finalWeight =  Integer.MIN_VALUE;
				else
				{
					finalWeight += 25 + ((houseCount - screen.getModel()
							.getGolemCount()) * 10);
				}
				if (finalWeight <= 0)
					finalWeight = 1;
			}
			else if (weightCondition.equals(ClayConstants.WC_HOLDING_ITEM))
			{
				if (golem_.getCopyOfHeldItems().isEmpty())
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				finalWeight = Integer.MAX_VALUE;
				break;
			}
			else if (weightCondition.equals(ClayConstants.WC_CALC_BUILDING))
			{
				BuildingEntity entity = (BuildingEntity) params_[0];
				String extraWeight = entity.getExtraWeight();
				if (!extraWeight.isEmpty())
				{
					String[] conditions = extraWeight.split(",");
					for (String condition : conditions)
					{
						String[] conditionAndParams = condition.split(":");
						Integer multiplier = Integer
								.parseInt(conditionAndParams[1]);
						boolean passed = false;
						if (conditionAndParams[0]
								.equals(ClayConstants.WC_LOW_MANA)
								&& golem_.isLowMana())
							passed = true;
						else if (conditionAndParams[0]
								.equals(ClayConstants.WC_LOW_CLAY)
								&& golem_.isLowClay())
							passed = true;
						else if (conditionAndParams[0]
								.equals(ClayConstants.WC_NO_STORAGE))
							passed = SearchUtil.getPathStatus(SearchUtil
									.searchStorage(
											golem_,
											golem_.getHomeScreen())) == ClayConstants.BEHAVIOR_PASSED;
						if (passed)
						{
							finalWeight *= multiplier;
						}
					}
				}
			}
		}
		if (finalWeight <= 0)
			return Integer.MIN_VALUE;
		return finalWeight;
	}
}
