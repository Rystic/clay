package city.ai;

import java.util.List;

import city.ai.calculators.BehaviorWeightCalculator;
import city.ai.objects.Behavior;
import city.ai.util.BehaviorTriple;
import city.entities.GolemEntity;
import city.generics.GenericBehavior;

public class GolemBrain
{
	/*
	 * TODO Some things to consider
	 * 
	 * low mana low clay
	 */

	public static BehaviorTriple calculateBestBehavior(GolemEntity golem_, List<GenericBehavior> behaviors_, boolean isNeededBehavior_)
	{
		// TODO don't spin on this
		Behavior bestBehavior = null;
		int bestWeight = Integer.MIN_VALUE;
		for (GenericBehavior behavior : behaviors_)
		{
			int weight = BehaviorWeightCalculator.calculate(
					golem_,
					behavior.getWeightConditions(),
					null);
			if (weight != Integer.MIN_VALUE)
			{
				if (weight > bestWeight)
				{
					bestBehavior = new Behavior(behavior,
							behavior.getDefaultParams());
					bestWeight = weight;
				}
			}
		}
		if (bestBehavior != null)
		{
			return new BehaviorTriple(golem_, bestBehavior, isNeededBehavior_ ? Integer.MAX_VALUE : bestWeight);
		}
		return null;
	}

}
