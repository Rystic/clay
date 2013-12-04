package city.ai;

import city.entities.GolemEntity;

public 	class BehaviorTriple
{
	public BehaviorTriple(GolemEntity golem_, Behavior behavior_, int score_)
	{
		_golem = golem_;
		_behavior = behavior_;
		_score = score_;
	}

	public GolemEntity getGolem()
	{
		return _golem;
	}

	public Behavior getBehavior()
	{
		return _behavior;
	}
	
	public int getScore()
	{
		return _score;
	}
	
	GolemEntity _golem;
	Behavior _behavior;
	int _score;
}