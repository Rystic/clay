package city.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.ClayConstants;
import models.CityModel;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import screens.AbstractScreen;
import city.ai.objects.Behavior;
import city.ai.util.BehaviorTriple;
import city.entities.GolemEntity;
import city.processes.AbstractProcess;
import city.util.MapUpdateEvent;

public class GolemBehaviorProcess extends AbstractProcess implements
		EventSubscriber<MapUpdateEvent>
{
	public GolemBehaviorProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
		_unassignedBehaviorList = new ArrayList<Behavior>();
		_inProgressBehaviorList = new ArrayList<Behavior>();
		_unreachableBehaviorList = new ArrayList<Behavior>();
		_noMaterialsBehaviorList = new ArrayList<Behavior>();
		_noAvailableGolemsBehaviorList = new ArrayList<Behavior>();
		_noStorageAvailable = new ArrayList<Behavior>();
		_model = (CityModel) homeScreen_.getModel();
		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void execute()
	{
		List<Behavior> toBeAssigned = new ArrayList<Behavior>();
		toBeAssigned.addAll(_unassignedBehaviorList);

		if (!toBeAssigned.isEmpty())
		{
			List<BehaviorTriple> behaviorScores = new ArrayList<BehaviorTriple>();
			for (Behavior behavior : toBeAssigned)
			{
				if (_clearInvalid)
					behavior.clearInvalidEntities();
				int required = behavior.requiredConditionsSatisfied(_model);
				if (required != ClayConstants.BEHAVIOR_PASSED)
				{
					if (required == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
						_noStorageAvailable.add(behavior);
				}
				else if (behavior.allGolemsInvalid(_golemList))
					_unreachableBehaviorList.add(behavior);
				else
				{
					for (GolemEntity golem : _golemList)
					{
						if (golem.isActive() || behavior.isInvalid(golem))
							continue;
						if (behavior.allGolemsInvalid(_golemList))
							_unreachableBehaviorList.add(behavior);
						else
							behaviorScores.add(new BehaviorTriple(golem,
									behavior, behavior
											.calculateBehaviorWeight(golem)));
					}
				}
			}
			for (GolemEntity golem : _golemList)
			{
				Random random = new Random();
				int addPersonalTask = random.nextInt(100);
			}
			if (behaviorScores.isEmpty())
			{
				_noAvailableGolemsBehaviorList.addAll(toBeAssigned);
			}
			else
			{
				BehaviorTriple[] scores = new BehaviorTriple[behaviorScores
						.size()];
				behaviorScores.toArray(scores);
				BehaviorTriple[] topScores = sort(scores);
				List<GolemEntity> invalidGolems = new ArrayList<GolemEntity>();
				List<Behavior> invalidBehaviors = new ArrayList<Behavior>();
				for (BehaviorTriple triple : topScores)
				{
					if (!invalidGolems.contains(triple._golem)
							&& !invalidBehaviors.contains(triple._behavior))
					{
						triple._golem.setBehavior(triple._behavior);
						setBehaviorInProgess(triple._behavior);
						invalidGolems.add(triple._golem);
						invalidBehaviors.add(triple._behavior);
					}
				}
			}
			_clearInvalid = false;
		}
		
		_unassignedBehaviorList.removeAll(_unreachableBehaviorList);
		_unassignedBehaviorList.removeAll(_inProgressBehaviorList);
		_unassignedBehaviorList.removeAll(_noMaterialsBehaviorList);
		_unassignedBehaviorList.removeAll(_noAvailableGolemsBehaviorList);

		for (GolemEntity golem : _golemList)
		{
			golem.calculateBehavior();
		}
	}

	public void behaviorComplete(Behavior Behavior_)
	{
		_inProgressBehaviorList.remove(Behavior_);
		if (!_noAvailableGolemsBehaviorList.isEmpty())
		{
			_unassignedBehaviorList.addAll(_noAvailableGolemsBehaviorList);
			_noAvailableGolemsBehaviorList.clear();
		}
	}

	public void behaviorFailed(Behavior behavior_, int reason_)
	{
		_inProgressBehaviorList.remove(behavior_);
		if (behavior_.isPersonalTask())
			return;
		if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
		{
			_noMaterialsBehaviorList.add(behavior_);
		}
		else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
		{
			if (behavior_.allGolemsInvalid(_golemList))
				_unreachableBehaviorList.add(behavior_);
			else
				_unassignedBehaviorList.add(behavior_);
		}
		else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
		{
			_noStorageAvailable.add(behavior_);
		}
		_unassignedBehaviorList.addAll(_noAvailableGolemsBehaviorList);
		_noAvailableGolemsBehaviorList.clear();
	}

	public void queueBehavior(Behavior behavior_)
	{
		behavior_.setBehaviorProcess(this);
		_unassignedBehaviorList.add(behavior_);
	}

	public void setBehaviorInProgess(Behavior behavior_)
	{
		behavior_.setBehaviorProcess(this);
		_inProgressBehaviorList.add(behavior_);
	}

	public int getBehaviorCount(String behaviorTag_)
	{
		int count = 0;
		for (Behavior behavior : _inProgressBehaviorList)
		{
			if (behavior.getBehavior().getBehaviorTag().equals(behaviorTag_))
				count++;
		}
		return count;
	}

	public BehaviorTriple[] sort(BehaviorTriple[] values)
	{
		BehaviorTriple[] returnValues = values;
		quicksort(returnValues, 0, values.length - 1);
		return returnValues;
	}

	private void quicksort(BehaviorTriple[] values, int low, int high)
	{
		int i = low, j = high;
		int pivot = values[low + (high - low) / 2]._score;

		while (i <= j)
		{
			while (values[i]._score > pivot)
			{
				i++;
			}
			while (values[j]._score < pivot)
			{
				j--;
			}
			if (i <= j)
			{
				exchange(values, i, j);
				i++;
				j--;
			}
		}
		if (low < j)
			quicksort(values, low, j);
		if (i < high)
			quicksort(values, i, high);
	}

	private void exchange(BehaviorTriple[] values, int i, int j)
	{
		BehaviorTriple temp = values[i];
		values[i] = values[j];
		values[j] = temp;
	}

	@Override
	public void onEvent(MapUpdateEvent event_)
	{
		// TODO make sure it's the home screen
		_clearInvalid = true;
		_unassignedBehaviorList.addAll(_unreachableBehaviorList);
		_unreachableBehaviorList.clear();
	}


	private List<GolemEntity> _golemList;

	private List<Behavior> _unassignedBehaviorList;
	private List<Behavior> _inProgressBehaviorList;
	private List<Behavior> _unreachableBehaviorList;
	private List<Behavior> _noMaterialsBehaviorList;
	private List<Behavior> _noAvailableGolemsBehaviorList;
	private List<Behavior> _noStorageAvailable;

	private CityModel _model;

	private boolean _clearInvalid;

}
