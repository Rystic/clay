package city.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.ClayConstants;
import models.CityModel;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import screens.AbstractScreen;
import city.ai.GolemBrain;
import city.ai.util.BehaviorTriple;
import city.ai.util.BehaviorTripleQuickSort;
import city.generics.data.BehaviorData;
import city.generics.entities.GolemEntity;
import city.generics.objects.Behavior;
import city.util.MapUpdateEvent;

public class GolemBehaviorProcess extends AbstractProcess implements
		EventSubscriber<MapUpdateEvent>
{
	public GolemBehaviorProcess(AbstractScreen homeScreen_)
	{
		super(homeScreen_);
		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
		_inactiveGolems = new ArrayList<GolemEntity>();
		_unassignedBehaviors = new ArrayList<Behavior>();
		_inProgressBehaviors = new ArrayList<Behavior>();
		_unreachableBehaviors = new ArrayList<Behavior>();
		_noMaterials = new ArrayList<Behavior>();
		_noAvailableGolems = new ArrayList<Behavior>();
		_noStorageAvailable = new ArrayList<Behavior>();
		_noUnoccupiedBuildings = new ArrayList<Behavior>();
		EventBus.subscribe(MapUpdateEvent.class, this);
	}

	@Override
	public void execute()
	{
		_inactiveGolems.clear();
		for (GolemEntity golem : _golemList)
		{
			if (golem.isActive())
				golem.executeBehavior();
			else
				_inactiveGolems.add(golem);
		}
		if (_inactiveGolems.size() > 0)
			calculateBehavior();
	}

	private void calculateBehavior()
	{
		List<Behavior> toBeAssigned = new ArrayList<Behavior>();
		toBeAssigned.addAll(_unassignedBehaviors);
		List<BehaviorTriple> behaviorScores = new ArrayList<BehaviorTriple>();
		if (!toBeAssigned.isEmpty())
		{
			for (Behavior behavior : toBeAssigned)
			{
				if (_clearInvalid)
					behavior.clearInvalidEntities();
				for (GolemEntity golem : _inactiveGolems)
				{
					if (behavior.isInvalid(golem))
						continue;
					BehaviorTriple triple = new BehaviorTriple(golem, behavior,
							behavior.calculateBehaviorWeight(golem)
									+ behavior.getAddedWeight());
					if (triple._weight <= 0)
						triple._behavior.requiredFailed(golem);
					else
						behaviorScores.add(triple);
				}
				if (behavior.allGolemsInvalid(_golemList))
					_unreachableBehaviors.add(behavior);
			}
		}
		toBeAssigned.removeAll(_unreachableBehaviors);

		for (GolemEntity golem : _inactiveGolems)
		{
			BehaviorTriple golemNeededBehavior = GolemBrain
					.calculateBestBehavior(
							golem,
							BehaviorData.getNeededBehaviors(),
							true);
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
									BehaviorData.getWantedBehaviors(),
									false);
					if (golemWantedBehavior != null)
					{
						String behaviorTag = golemWantedBehavior._behavior
								.getBehaviorTag();
						golemWantedBehavior._behavior.setBehaviorProcess(this);
						behaviorScores.add(golemWantedBehavior);
						golemWantedBehavior._weight += golem
								.getPersonalBehaviorWeight(behaviorTag);
						golem.addPersonalBehaviorWeight(behaviorTag);
					}
				}
			}
		}
		if (!behaviorScores.isEmpty())
		{
			BehaviorTriple[] scores = new BehaviorTriple[behaviorScores.size()];
			behaviorScores.toArray(scores);
			BehaviorTriple[] topScores = BehaviorTripleQuickSort.sort(scores);
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
						triple._behavior.setBehaviorProcess(this);
						_inProgressBehaviors.add(triple._behavior);
						invalidGolems.add(triple._golem);
						invalidBehaviors.add(triple._behavior);
					}
					else if (!triple._behavior.isPersonalTask())
					{
						if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
						{
							_noMaterials.add(triple._behavior);
							invalidBehaviors.add(triple._behavior);
						}
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
						{
							_noStorageAvailable.add(triple._behavior);
							invalidBehaviors.add(triple._behavior);
						}
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
						{
							triple._behavior.requiredFailed(triple._golem);
							if (triple._behavior.allGolemsInvalid(_golemList))
							{
								_unreachableBehaviors.add(triple._behavior);
								invalidBehaviors.add(triple._behavior);
							}
						}
						else if (requiredComplete == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
						{
							_unreachableBehaviors.add(triple._behavior);
							invalidBehaviors.add(triple._behavior);
						}
					}
				}
			}
		}
		_clearInvalid = false;

		_unassignedBehaviors.removeAll(_unreachableBehaviors);
		_unassignedBehaviors.removeAll(_inProgressBehaviors);
		_unassignedBehaviors.removeAll(_noMaterials);
		_unassignedBehaviors.removeAll(_noStorageAvailable);

		for (Behavior behavior : _unassignedBehaviors)
		{
			behavior.increaseAddedWeight(ClayConstants.ADDED_WEIGHT_INCREASE);
		}
		_noAvailableGolems.addAll(_unassignedBehaviors);
		_unassignedBehaviors.clear();
	}

	public void behaviorComplete(Behavior behavior_)
	{
		_inProgressBehaviors.remove(behavior_);
		if (behavior_.getAssigningBuilding() != null)
			behavior_.getAssigningBuilding().removeActiveBehavior(behavior_);
		if (!_noAvailableGolems.isEmpty())
		{
			_unassignedBehaviors.addAll(_noAvailableGolems);
			_noAvailableGolems.clear();
		}
		if (!_noUnoccupiedBuildings.isEmpty())
		{
			_unassignedBehaviors.addAll(_noUnoccupiedBuildings);
			_noUnoccupiedBuildings.clear();
		}
	}

	public void behaviorFailed(Behavior behavior_, int reason_)
	{
		if (!behavior_.isPersonalTask())
		{
			if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
			{
				_noMaterials.add(behavior_);
			}
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
			{
				if (behavior_.allGolemsInvalid(_golemList))
					_unreachableBehaviors.add(behavior_);
				else
					_unassignedBehaviors.add(behavior_);
			}
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
			{
				_noStorageAvailable.add(behavior_);
			}
			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM
					|| reason_ == ClayConstants.BEHAVIOR_FAILED_OBSOLETE)
			{
				_unassignedBehaviors.remove(behavior_);
				_inProgressBehaviors.remove(behavior_);
				_unreachableBehaviors.remove(behavior_);
				_noAvailableGolems.remove(behavior_);
				_noMaterials.remove(behavior_);
				_noStorageAvailable.remove(behavior_);
			}

			if (reason_ == ClayConstants.BEHAVIOR_FAILED_BUILDING_OCCUPIED)
			{
				_noUnoccupiedBuildings.add(behavior_);
			}
			else
			{
				_unassignedBehaviors.addAll(_noUnoccupiedBuildings);
				_noUnoccupiedBuildings.clear();
			}
		}
		// TODO if there's ever a failure task for a building being interrupted
		// mid-task, it needs to clear no unoccupied buildings
		_inProgressBehaviors.remove(behavior_);
		_unassignedBehaviors.addAll(_noAvailableGolems);
		_noAvailableGolems.clear();
	}

	public void queueBehavior(Behavior behavior_)
	{
		behavior_.setBehaviorProcess(this);
		_unassignedBehaviors.add(behavior_);
	}

	public int getBehaviorCount(String behaviorTag_)
	{
		int count = 0;
		for (GolemEntity golem : _golemList)
		{
			Behavior behavior = golem.getCurrentBehavior();
			if (behavior != null
					&& behavior.getBehaviorTag().equals(behaviorTag_))
				count++;
		}
		return count;
	}

	@Override
	public void onEvent(MapUpdateEvent event_)
	{
		// TODO make sure it's the home screen
		if (event_.getPoints() != null)
		{
			for (GolemEntity golem : _golemList)
			{
				golem.recalculatePathIfNecessary(event_.getPoints());
			}
			_clearInvalid = true;
			_unassignedBehaviors.addAll(_unreachableBehaviors);
			_unreachableBehaviors.clear();
		}

		if (event_.isItemUpdate())
		{
			_unassignedBehaviors.addAll(_noMaterials);
			_noMaterials.clear();
		}

		if (event_.isStorageAvailable())
		{
			_unassignedBehaviors.addAll(_noStorageAvailable);
			_noStorageAvailable.clear();
		}
	}

	private Random _random = new Random();

	private List<GolemEntity> _golemList;
	private List<GolemEntity> _inactiveGolems;

	private volatile List<Behavior> _unassignedBehaviors;
	private List<Behavior> _inProgressBehaviors;
	private List<Behavior> _unreachableBehaviors;
	private List<Behavior> _noMaterials;
	private List<Behavior> _noAvailableGolems;
	private List<Behavior> _noStorageAvailable;
	private List<Behavior> _noUnoccupiedBuildings;

	private boolean _clearInvalid;

}
