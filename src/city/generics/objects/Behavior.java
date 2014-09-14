package city.generics.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.ClayConstants;
import city.ai.calculators.BehaviorInstructionCalculator;
import city.ai.calculators.BehaviorWeightCalculator;
import city.generics.GenericBehavior;
import city.generics.data.ItemData;
import city.generics.entities.BuildingEntity;
import city.generics.entities.GolemEntity;
import city.processes.GolemBehaviorProcess;

public class Behavior
{
	public Behavior(GenericBehavior behavior_, Object... params_)
	{
		_behavior = behavior_;
		_params = params_;
		_invalidEntities = new HashSet<GolemEntity>();
		_requiredCompleted = _behavior.getRequired().isEmpty();
		_isHarvestTask = _behavior.getBehaviorTag().equals(
				ClayConstants.BEHAVIOR_HARVEST);
		_itemDescription = behavior_.calculateBehaviorDescription(params_);
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
		if (getLimit() != ClayConstants.NO_LIMIT)
		{
			boolean limitReached = _behaviorProcess
					.getBehaviorCount(getBehaviorTag()) >= getLimit();
			if (limitReached)
				return ClayConstants.BEHAVIOR_FAILED_LIMIT_REACHED;
		}
		if (_behavior.getRequired().isEmpty())
			return ClayConstants.BEHAVIOR_PASSED;
		int passed = ClayConstants.BEHAVIOR_PASSED;
		for (String command : _behavior.getRequired().split(","))
		{
			if (passed != ClayConstants.BEHAVIOR_PASSED)
				break;
			passed = BehaviorInstructionCalculator.executeRequired(
					golem_,
					command,
					_params);
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
		if (_assigningBuilding != null && _assigningBuilding.isInUse()) return Integer.MIN_VALUE;
		return BehaviorWeightCalculator.calculate(
				golem_,
				_behavior.getWeightConditions(),
				_params);
	}

	public void complete(GolemEntity golem_)
	{
		_assignedGolem = null;
		_behavior.calculateGolemCost(golem_);
		if (_behaviorProcess == null)
			return;
		_behaviorProcess.behaviorComplete(this);
	}

	public void failed(GolemEntity _failedGolem, int reason_)
	{
		_assignedGolem = null;
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

	public int getLimit()
	{
		return _behavior.getLimit();
	}

	public boolean isPersonalTask()
	{
		return _behavior.isPersonalBehavior();
	}

	public boolean isHarvestTask()
	{
		return _isHarvestTask;
	}

	public void increaseAddedWeight(int addedWeight_)
	{
		if (_addedWeight >= ClayConstants.ADDED_WEIGHT_CAP)
			return;
		_addedWeight += addedWeight_;
		if (_addedWeight > ClayConstants.ADDED_WEIGHT_CAP)
			_addedWeight = ClayConstants.ADDED_WEIGHT_CAP;
	}

	public int getAddedWeight()
	{
		return _addedWeight;
	}

	public String getBehaviorTag()
	{
		return _behavior.getBehaviorTag();
	}

	public String getBehaviorDescription()
	{
		return _itemDescription;
	}

	public boolean isRequiredCompleted()
	{
		return _requiredCompleted;
	}

	public void setAssigningBuilding(BuildingEntity assigningBuilding_)
	{
		_assigningBuilding = assigningBuilding_;
	}
	
	public void setAssigningGolem(GolemEntity assigningGolem_)
	{
		_assigningGolem = assigningGolem_;
	}

	public void setAssignedGolem(GolemEntity assignedGolem_)
	{
		_assignedGolem = assignedGolem_;
	}

	public BuildingEntity getAssigningBuilding()
	{
		return _assigningBuilding;
	}
	
	public GolemEntity getAssigningGolem()
	{
		return _assigningGolem;
	}

	public GolemEntity getAssignedGolem()
	{
		return _assignedGolem;
	}

	public boolean checkIfHarvestObsolete()
	{
		if (!_isHarvestTask)
			return false;
		Item item = new Item(ItemData.getItem((String) _params[1]));
		return !_assigningBuilding.isHolding(item)
				&& (_assignedGolem == null || (_assignedGolem != null && !_assignedGolem
						.isHolding(item)));
	}

	public void obsolete()
	{
		try
		{
			if (_assignedGolem != null)
			{
				if (!this.equals(_assignedGolem.getCurrentBehavior()))
				{
					throw new Exception(
							"Obsolete behavior trying to cancel a different behavior.");
				}
				_assignedGolem
						.behaviorFailed(ClayConstants.BEHAVIOR_FAILED_OBSOLETE);
			}
			else
				_behaviorProcess.behaviorFailed(
						this,
						ClayConstants.BEHAVIOR_FAILED_OBSOLETE);
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Behavior))
			return false;
		Behavior behavior = (Behavior) obj;
		if (_assigningBuilding != null)
		{
			if (behavior.getAssigningBuilding() == null || !_assigningBuilding.equals(behavior.getAssigningBuilding()))
				return false;
		}
		else if (_assigningBuilding == null
				&& behavior.getAssigningBuilding() != null)
			return false;
		return _params.equals(behavior.getParams())
				&& _behavior.equals(behavior._behavior);
	}

	private GenericBehavior _behavior;

	private BuildingEntity _assigningBuilding;
	private GolemEntity _assigningGolem;
	private GolemEntity _assignedGolem;

	private Set<GolemEntity> _invalidEntities;

	private GolemBehaviorProcess _behaviorProcess;

	private Object[] _params;

	private String _itemDescription;

	private int _addedWeight;

	private boolean _requiredCompleted;
	private boolean _isHarvestTask;

}
