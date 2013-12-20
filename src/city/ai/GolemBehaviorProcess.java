package city.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import data.BehaviorData;

public class GolemBehaviorProcess extends AbstractProcess implements
		EventSubscriber<MapUpdateEvent>
{
	public GolemBehaviorProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
		_unassignedBehaviorList = new HashSet<Behavior>();
		_inProgressBehaviorList = new HashSet<Behavior>();
		_unreachableBehaviorList = new HashSet<Behavior>();
		_noMaterialsBehaviorList = new HashSet<Behavior>();
		_noAvailableGolemsBehaviorList = new HashSet<Behavior>();
		_noStorageAvailable = new HashSet<Behavior>();
		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void execute()
	{
		List<Behavior> toBeAssigned = new ArrayList<Behavior>();
		toBeAssigned.addAll(_unassignedBehaviorList);

		List<BehaviorTriple> behaviorScores = new ArrayList<BehaviorTriple>();
		if (!toBeAssigned.isEmpty())
		{
			for (Behavior behavior : toBeAssigned)
			{
				if (_clearInvalid)
					behavior.clearInvalidEntities();
				if (behavior.allGolemsInvalid(_golemList))
					_unreachableBehaviorList.add(behavior);
				else
				{
					for (GolemEntity golem : _golemList)
					{
						if (golem.isActive() || behavior.isInvalid(golem))
							continue;
						behaviorScores.add(new BehaviorTriple(golem, behavior,
								behavior.calculateBehaviorWeight(golem)
										+ behavior.getAddedWeight()));
					}
				}
			}
		}
		for (GolemEntity golem : _golemList)
		{
			if (golem.isActive())
				continue;

			BehaviorTriple golemNeededBehavior = GolemBrain
					.calculateBestBehavior(
							golem,
							BehaviorData.getNeededBehaviors());
			if (golemNeededBehavior != null)
			{
				golemNeededBehavior._behavior.setBehaviorProcess(this);
				behaviorScores.add(golemNeededBehavior);
			}
			else
			{
				int addPersonalTask = _random.nextInt(100);
				if (addPersonalTask > golem.getPersonalBehaviorChance())
				{
					BehaviorTriple golemWantedBehavior = GolemBrain
							.calculateBestBehavior(
									golem,
									BehaviorData.getWantedBehaviors());
					if (golemWantedBehavior != null)
					{
						golemWantedBehavior._behavior.setBehaviorProcess(this);
						behaviorScores.add(golemWantedBehavior);
					}
				}
			}
		}
		if (behaviorScores.isEmpty())
		{
			_noAvailableGolemsBehaviorList.addAll(toBeAssigned);
			for (Behavior behavior : toBeAssigned)
			{
				behavior.increaseAddedWeight(5);
			}
		}
		else
		{
			BehaviorTriple[] scores = new BehaviorTriple[behaviorScores.size()];
			behaviorScores.toArray(scores);
			BehaviorTriple[] topScores = sort(scores);
			List<GolemEntity> invalidGolems = new ArrayList<GolemEntity>();
			List<Behavior> invalidBehaviors = new ArrayList<Behavior>();
			for (BehaviorTriple triple : topScores)
			{
				if (!invalidGolems.contains(triple._golem)
						&& !invalidBehaviors.contains(triple._behavior))
				{
					int requiredComplete = triple._behavior
							.calculateRequired(triple._golem);
					if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
					{
						triple._golem.setBehavior(triple._behavior);
						setBehaviorInProgess(triple._behavior);
						invalidGolems.add(triple._golem);
						invalidBehaviors.add(triple._behavior);
					}
					else
						handleFailedBehavior(triple._behavior, requiredComplete);
				}
			}
			_clearInvalid = false;
		}

		_unassignedBehaviorList.removeAll(_unreachableBehaviorList);
		_unassignedBehaviorList.removeAll(_inProgressBehaviorList);
		_unassignedBehaviorList.removeAll(_noMaterialsBehaviorList);
		_unassignedBehaviorList.removeAll(_noAvailableGolemsBehaviorList);
		//TODO add no storage list
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
		if (!behavior_.isPersonalTask())
			handleFailedBehavior(behavior_, reason_);
		_unassignedBehaviorList.addAll(_noAvailableGolemsBehaviorList);
		_noAvailableGolemsBehaviorList.clear();
	}

	private void handleFailedBehavior(Behavior behavior_, int reason_)
	{
		_inProgressBehaviorList.remove(behavior_);
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
		if (event_.getPoints() != null)
		{
			for (GolemEntity golem : _golemList)
			{
				golem.recalculatePathIfNecessary(event_.getPoints());
			}
		}
		if (event_.isItemUpdate())
		{
			_unassignedBehaviorList.addAll(_noMaterialsBehaviorList);
			_noMaterialsBehaviorList.clear();
		}
	}

	private Random _random = new Random();

	private List<GolemEntity> _golemList;

	private Set<Behavior> _unassignedBehaviorList;
	private Set<Behavior> _inProgressBehaviorList;
	private Set<Behavior> _unreachableBehaviorList;
	private Set<Behavior> _noMaterialsBehaviorList;
	private Set<Behavior> _noAvailableGolemsBehaviorList;
	private Set<Behavior> _noStorageAvailable;

	private boolean _clearInvalid;

}
