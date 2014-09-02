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
			int addedWeight = 0;
			if (weightCondition.equals(ClayConstants.WC_CAN_BUILD_CLAY_GOLEM))
				addedWeight = _canBuildClayGolem(golem_, params_);
			else if (weightCondition
					.equals(ClayConstants.WC_CAN_BUILD_PEARLCLAY_GOLEM))
				addedWeight = _canBuildPearlclayGolem(golem_, params_);
			else if (weightCondition
					.equals(ClayConstants.WC_CAN_BUILD_STONEWARE_GOLEM))
				addedWeight = _canBuildStonewareGolem(golem_, params_);
			else if (weightCondition.equals(ClayConstants.WC_CLOSEST_TO_POINT))
				addedWeight = _closestToPoint(golem_, params_);
			else if (weightCondition.equals(ClayConstants.WC_HOLDING_ITEM))
				addedWeight = _holdingItem(golem_);
			else if (weightCondition.equals(ClayConstants.WC_LOW_MANA))
				addedWeight = _lowMana(golem_);
			else if (weightCondition.equals(ClayConstants.WC_LOW_CLAY))
				addedWeight = _lowClay(golem_);
			else if (weightCondition.equals(ClayConstants.WC_CALC_BUILDING))
				addedWeight = _calculateExtraBuildingWeight(golem_, params_);
			if (addedWeight == Integer.MIN_VALUE)
				return addedWeight;
			finalWeight += addedWeight;
		}
		if (finalWeight <= 0)
			return Integer.MIN_VALUE;
		return finalWeight;
	}

	private static int _canBuildClayGolem(GolemEntity golem_, Object[] params_)
	{
		int weight = 0;
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
			return Integer.MIN_VALUE;
		}
		weight += 50 + ((houseCount - screen.getModel().getGolemCount()) * 10);
		if (golem_.getPsychology() == ClayConstants.PSYCHOLOGY_INFLUENTIAL)
		{
			weight += 10 * (golem_.getGolemanity() + 1);
			int personality = golem_.getPersonality();
			int personalityCount = 0;
			int otherPersonalityCount = 0;
			List<GolemEntity> cityGolems = golem_.getModel().getGolems();
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
				double multiplier = 1 + ((double) (golem_.getGolemanity() + 1) / 10);
				weight *= multiplier;
				weight += 5 * result;
			}
		}
		if (weight <= 0)
			weight = 1;
		return weight;
	}

	private static int _calculateExtraBuildingWeight(GolemEntity golem_, Object[] params_)
	{
		int weight = 0;
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
					weight *= multiplier;
			}
		}
		return weight;
	}
	private static int _canBuildPearlclayGolem(GolemEntity golem_, Object[] params_)
	{
		int weight = 0;
		CityScreen screen = (CityScreen) golem_.getHomeScreen();
		int pearlclayCount = 0;
		List<GolemEntity> golems = screen.getModel().getGolems();
		for (GolemEntity entity : golems)
		{
			if (entity.getGolemTag().equals(ClayConstants.GOLEM_PEARLCLAY))
				pearlclayCount++;
		}
		Integer buildingId = BuildingData.getBuildingByTag("mana-battery")
				.getBuildingIdentifier();
		List<BuildingEntity> manaBatteries = screen.getModel().getBuildingMap()
				.get(buildingId);
		if (manaBatteries == null || pearlclayCount > manaBatteries.size())
			weight = Integer.MIN_VALUE;
		else
		{
			int weightMultiplier = 1;
			if (golem_.getPersonality() == ClayConstants.PERSONALITY_CREATIVE)
				weightMultiplier += 2;
			weight = (manaBatteries.size() - pearlclayCount) * 10
					* weightMultiplier;
		}
		return weight;
	}

	private static int _canBuildStonewareGolem(GolemEntity golem_, Object[] params_)
	{
		int weight = 0;
		CityScreen screen = (CityScreen) golem_.getHomeScreen();
		int stonewareCount = 0;
		List<GolemEntity> golems = screen.getModel().getGolems();
		for (GolemEntity entity : golems)
		{
			if (entity.getGolemTag().equals(ClayConstants.GOLEM_STONEWARE))
				stonewareCount++;
		}
		Integer buildingId = BuildingData.getBuildingByTag("kiln")
				.getBuildingIdentifier();
		List<BuildingEntity> kilns = screen.getModel().getBuildingMap()
				.get(buildingId);
		if (kilns == null || stonewareCount > kilns.size() * 2)
			weight = Integer.MIN_VALUE;
		else
		{
			int weightMultiplier = 1;
			if (golem_.getPersonality() == ClayConstants.PERSONALITY_AMBITIOUS)
				weightMultiplier += 3;
			weight = ((kilns.size() * 2) - stonewareCount) * 10
					* weightMultiplier;
		}
		return weight;
	}

	private static int _closestToPoint(GolemEntity golem_, Object[] params_)
	{
		int weight = 0;
		BuildingEntity entity = (BuildingEntity) params_[0];
		Queue<Point> path = SearchUtil.searchBuildingEntity(
				golem_,
				golem_.getHomeScreen(),
				entity);
		if (path.size() == 0)
			return Integer.MIN_VALUE;
		weight += 50 - path.size();
		if (weight <= 0)
			weight = 1;
		return weight;
	}

	private static int _holdingItem(GolemEntity golem_)
	{
		if (golem_.getCopyOfHeldItems().isEmpty())
			return Integer.MIN_VALUE;
		return Integer.MAX_VALUE;
	}

	private static int _lowClay(GolemEntity golem_)
	{
		int weight = 0;
		if (golem_.isLowClay())
		{
			weight += (int) (golem_.getMaximumClay() - golem_.getClay());
			if (weight <= 0)
				weight = 1;
		}
		return weight;
	}

	private static int _lowMana(GolemEntity golem_)
	{
		int weight = 0;
		if (golem_.isLowMana())
		{
			weight += (int) (golem_.getMaximumMana() - golem_.getMana());
			if (weight <= 0)
				weight = 1;
		}
		return weight;
	}

}
