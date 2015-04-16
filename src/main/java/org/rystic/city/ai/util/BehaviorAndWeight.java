package org.rystic.city.ai.util;

import org.rystic.city.generics.objects.Behavior;

public 	class BehaviorAndWeight
{
	public BehaviorAndWeight(Behavior behavior_, int score_)
	{
		_behavior = behavior_;
		_weight = score_;
	}

	public Behavior _behavior;
	public int _weight;
}