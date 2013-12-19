package city.ai.calculators;

import java.awt.Point;
import java.util.Queue;

import main.ClayConstants;
import screens.CityScreen;
import city.entities.AbstractEntity;
import city.entities.BuildingEntity;
import city.entities.GolemEntity;
import city.util.SearchUtil;

public class BehaviorWeightCalculator
{

	public static int calculate(GolemEntity golem_, String weightConditions_, Object[] params_)
	{
		int finalWeight = Integer.MIN_VALUE;
		String[] weightConditions = weightConditions_.split(",");
		
		for (String weightCondition : weightConditions)
		{
			if (weightCondition.equals(ClayConstants.WC_CLOSEST_TO_PINT))
			{
				AbstractEntity entity = (AbstractEntity) params_[0];

				Queue<Point> path = SearchUtil.search(
						golem_,
						golem_.getHomeScreen(),
						ClayConstants.SEARCH_ENTITY,
						entity);
				if (path.size() == 0)
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				finalWeight += 50 - path.size();
			}
			if (weightCondition.equals(ClayConstants.WC_LOW_MANA))
			{
				if (golem_.isLowMana())
					finalWeight += (int) (175 - golem_.getMana());
			}
			if (weightCondition.equals(ClayConstants.WC_LOW_CLAY))
			{
				if (golem_.isLowClay())
					finalWeight += (int) (175 - golem_.getClay());
			}
			if (weightCondition.equals(ClayConstants.WC_CAN_BUILD_GOLEM))
			{
				CityScreen screen = (CityScreen) golem_.getHomeScreen();
				BuildingEntity[][] tiles = screen.getModel().getTileValues();
				int houseCount = 0;
				for (int i = 0; i < tiles.length; i++)
				{
					for (int j = 0; j < tiles[0].length; j++)
					{
						if (tiles[i][j] != null && tiles[i][j].isHouse())
							houseCount++;
					}
				}
				if (screen.getModel().getGolemCount() >= houseCount)
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				finalWeight += 50;
			}
			if (weightCondition.equals(ClayConstants.WC_HOLDING_ITEM))
			{
				if (golem_.getCopyOfHeldItems().isEmpty())
				{
					finalWeight = Integer.MIN_VALUE;
					break;
				}
				finalWeight = Integer.MAX_VALUE;
				break;
			}
		}
		return finalWeight;
	}
}
