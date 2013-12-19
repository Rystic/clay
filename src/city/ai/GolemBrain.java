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

	public static BehaviorTriple calculateBestBehavior(GolemEntity golem_, List<GenericBehavior> behaviors_)
	{
		// TODO don't spin on this
		Behavior bestBehavior = null;
		int bestWeight = Integer.MIN_VALUE;
		for (GenericBehavior neededBehavior : behaviors_)
		{
			int weight = BehaviorWeightCalculator.calculate(
					golem_,
					neededBehavior.getWeightConditions(),
					null);
			if (weight != Integer.MIN_VALUE)
			{
				if (weight > bestWeight)
				{
					bestBehavior = new Behavior(neededBehavior,
							neededBehavior.getDefaultParams());
					bestWeight = weight;
				}
			}
		}
		if (bestBehavior != null)
		{
			return new BehaviorTriple(golem_, bestBehavior, bestWeight);
		}
		return null;
	}

}
