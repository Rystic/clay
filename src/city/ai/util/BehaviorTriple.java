package city.ai.util;

import city.ai.objects.Behavior;
import city.entities.GolemEntity;

public 	class BehaviorTriple
{
	public BehaviorTriple(GolemEntity golem_, Behavior behavior_, int score_)
	{
		_golem = golem_;
		_behavior = behavior_;
		_weight = score_;
	}
	
	public GolemEntity _golem;
	public Behavior _behavior;
	public int _weight;
}