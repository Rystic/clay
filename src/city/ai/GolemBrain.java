package city.ai;

import java.util.ArrayList;
import java.util.List;

import main.ClayConstants;
import screens.CityScreen;
import xml.BehaviorData;
import city.ai.objects.Behavior;
import city.ai.objects.Item;
import city.ai.util.BehaviorTriple;
import city.entities.BuildingEntity;
import city.entities.GolemEntity;

public class GolemBrain
{
	/*
	 * TODO Some things to consider
	 * 
	 * low mana low clay
	 */

	public static BehaviorTriple think(GolemEntity golem_)
	{
		List<BehaviorTriple> behaviors = new ArrayList<BehaviorTriple>();
		BehaviorTriple bestBehavior = null;
		BehaviorTriple currentBehavior = null;

		CityScreen screen = (CityScreen) golem_.getHomeScreen();
		List<Item> heldItems = golem_.getCopyOfHeldItems();
		if (heldItems.size() > 0)
		{
			currentBehavior = new BehaviorTriple(golem_, new Behavior(BehaviorData
					.getTask(ClayConstants.PERSONAL_BEHAVIOR_STORE_ITEMS)), Integer.MAX_VALUE);
		}
		if (canCreateGolem(golem_, screen))
		{
			currentBehavior = new BehaviorTriple(golem_, new Behavior(
					(BehaviorData.getTask(ClayConstants.PERSONAL_BEHAVIOR_BUILD_CLAY_GOLEM)),
					"sculptors-studio"), 100);
		}
		
		return bestBehavior;
	}
	
	public static boolean canCreateGolem(GolemEntity golem_, CityScreen screen_)
	{
		BuildingEntity[][] tiles = screen_.getModel().getTileValues();
		int houseCount = 0;
		for (int i = 0; i < tiles.length; i++)
		{
			for (int j = 0; j < tiles[0].length; j++)
			{
				if (tiles[i][j] != null && tiles[i][j].isHouse())
					houseCount++;
			}
		}
		return screen_.getModel().getGolemCount() < houseCount;
	}
}
