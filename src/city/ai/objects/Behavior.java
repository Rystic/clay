package city.ai.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.ClayConstants;
import city.ai.GolemBehaviorProcess;
import city.ai.calculators.BehaviorInstructionCalculator;
import city.ai.calculators.BehaviorWeightCalculator;
import city.entities.GolemEntity;
import city.generics.GenericBehavior;

public class Behavior
{
	public Behavior(GenericBehavior behavior_, Object... params_)
	{
		_behavior = behavior_;
		_params = params_;
		_invalidEntities = new HashSet<GolemEntity>();
		_requiredCompleted = _behavior.getRequired().isEmpty();
	}

	public List<String> getCommands()
	{
		List<String> commands = new ArrayList<String>();
		for (String command : _behavior.getCode().split(","))
		{
			commands.add(command);
		}
		return commands;
	}

	public void executeBehavior(GolemEntity executingEntity_, String currentCommand_)
	{
		BehaviorInstructionCalculator.executeBehavior(
				executingEntity_,
				currentCommand_,
				_params);
	}
	
	public int calculateRequired(GolemEntity golem_)
	{
		if (_behavior.getRequired().isEmpty()) return ClayConstants.BEHAVIOR_PASSED;
		int passed = ClayConstants.BEHAVIOR_PASSED;
		for (String command : _behavior.getRequired().split(","))
		{
			if (passed != ClayConstants.BEHAVIOR_PASSED) break;
			passed = BehaviorInstructionCalculator.executeRequired(golem_, command, _params);
		}
		return passed;
	}

	public void setBehaviorProcess(GolemBehaviorProcess behaviorProcess_)
	{
		_behaviorProcess = behaviorProcess_;
	}

	public boolean isInvalid(GolemEntity golem_)
	{
		return _invalidEntities.contains(golem_);
	}

	public boolean allGolemsInvalid(List<GolemEntity> allGolems_)
	{
		return _invalidEntities.containsAll(allGolems_);
	}

	public void clearInvalidEntities()
	{
		_invalidEntities.clear();
	}

	public int calculateBehaviorWeight(GolemEntity golem_)
	{
		if (_behavior.getLimit() > 0)
		{
			int count = _behaviorProcess.getBehaviorCount(_behavior
					.getBehaviorTag());
			if (count >= _behavior.getLimit())
				return Integer.MIN_VALUE;
		}
		return BehaviorWeightCalculator.calculate(
				golem_,
				_behavior.getWeightConditions(),
				_params);
	}

	public void complete(GolemEntity golem_)
	{
		_behavior.calculateGolemCost(golem_);
		if (_behaviorProcess == null)
			return;
		_behaviorProcess.behaviorComplete(this);
	}

	public void failed(GolemEntity _failedGolem, int reason_)
	{
		if (_behaviorProcess == null)
			return;
		_invalidEntities.add(_failedGolem);
		_behaviorProcess.behaviorFailed(this, reason_);
	}
	
	public void requiredFailed(GolemEntity _failedGolem)
	{
		_invalidEntities.add(_failedGolem);
	}

	public GolemBehaviorProcess getBehaviorProcess()
	{
		return _behaviorProcess;
	}

	public Object[] getParams()
	{
		return _params;
	}

	public GenericBehavior getBehavior()
	{
		return _behavior;
	}

	public int getLimit()
	{
		return _behavior.getLimit();
	}

	public boolean isPersonalTask()
	{
		return _behavior.isPersonalBehavior();
	}

	public void increaseAddedWeight(int addedWeight_)
	{
		_addedWeight += addedWeight_;
	}

	public int getAddedWeight()
	{
		return _addedWeight;
	}
	
	public boolean isRequiredCompleted()
	{
		return _requiredCompleted;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Behavior))
			return false;
		Behavior behavior = (Behavior) obj;
		return _params.equals(behavior.getParams())
				&& _behavior.equals(behavior.getBehavior());
	}

	private GenericBehavior _behavior;

	private Set<GolemEntity> _invalidEntities;

	private GolemBehaviorProcess _behaviorProcess;

	private Object[] _params;

	private boolean _requiredCompleted;
	private int _addedWeight;

}
