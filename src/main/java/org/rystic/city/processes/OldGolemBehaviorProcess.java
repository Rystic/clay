package org.rystic.city.processes;


public class OldGolemBehaviorProcess
{
//	public OldGolemBehaviorProcess(AbstractScreen homeScreen_)
//	{
//		super(homeScreen_);
//		_golemList = ((CityModel) homeScreen_.getModel()).getGolems();
//		_inactiveGolems = new ArrayList<GolemEntity>();
//
//		_noUnoccupiedGenericBuildingMap = new HashMap<String, List<Behavior>>();
//		_assigningBuildingClaimedMap = new HashMap<BuildingEntity, List<Behavior>>();
//		_unassignedBehaviors = new ArrayList<Behavior>();
//		_inProgressBehaviors = new ArrayList<Behavior>();
//		_unreachableBehaviors = new ArrayList<Behavior>();
//		_noMaterials = new ArrayList<Behavior>();
//		_noAvailableGolems = new ArrayList<Behavior>();
//		_noStorageAvailable = new ArrayList<Behavior>();
//
//		_neededBehaviorsGolems = new ArrayList<GolemEntity>();
//
//		_failedBehaviors = new ArrayBlockingQueue<FinishedBehavior>(256);
//		_completedBehaviors = new ArrayBlockingQueue<FinishedBehavior>(256);
//		_mapEvents = new ArrayBlockingQueue<MapUpdateEvent>(256);
//		_toBeAssignedQueue = new ArrayBlockingQueue<>(256);
//
//		EventBus.subscribe(MapUpdateEvent.class, this);
//	}
//
//	@Override
//	public void execute()
//	{
//		_inactiveGolems.clear();
//		for (GolemEntity golem : _golemList)
//		{
//			if (golem.isActive())
//				golem.executeBehavior();
//			else
//				_inactiveGolems.add(golem);
//		}
//		if (!_mapEvents.isEmpty())
//			handleMapEvents();
//		if (_inactiveGolems.size() > 0)
//			calculateBehavior();
//		if (!_failedBehaviors.isEmpty())
//		{
//			handleFailedBehaviors();
//			removeFailedBehaviorsFromUnassigned();
//		}
//		if (!_completedBehaviors.isEmpty())
//			handleCompletedBehaviors();
//		removeHandledBehaviorsFromUnassgined();
//		// a thread for calculate behavior process would require the following
//		// things:
//		// a list for currently unassigned behaviors.
//		// a list for currently inactive golems.
//	}
//
//	private void calculateBehavior()
//	{
//		List<BehaviorTriple> behaviorScores = new ArrayList<BehaviorTriple>();
//		for (GolemEntity golem : _inactiveGolems)
//		{
//			calculateNeededBehaviors(golem);
//			if (!golem.isActive())
//			{
//				int addPersonalTask = _random.nextInt(100);
//				if (addPersonalTask < golem.getPersonalBehaviorChance())
//				{
//					BehaviorTriple golemWantedBehavior = GolemBrain
//							.calculateBestBehavior(
//									golem,
//									BehaviorData.getWantedBehaviors(),
//									false);
//					if (golemWantedBehavior != null)
//					{
//						String behaviorTag = golemWantedBehavior._behavior
//								.getBehaviorTag();
//						golemWantedBehavior._behavior
//								.setBehaviorProcess(GolemBehaviorProcess.this);
//						golemWantedBehavior._behavior.setAssignedGolem(golem);
//						golemWantedBehavior._weight += golem
//								.getPersonalBehaviorWeight(behaviorTag);
//						golem.addPersonalBehaviorWeight(behaviorTag);
//						behaviorScores.add(golemWantedBehavior);
//					}
//				}
//			}
//		}
//
//		_inactiveGolems.removeAll(_neededBehaviorsGolems);
//		_neededBehaviorsGolems.clear();
//		if (_inactiveGolems.isEmpty())
//			return;
//
//		_unassignedBehaviors.addAll(_noAvailableGolems);
//		_noAvailableGolems.clear();
//		_toBeAssignedQueue.addAll(_unassignedBehaviors);
//		if (_toBeAssignedQueue.size() > 50)
//			System.out.println("50+ tasks: " + _toBeAssignedQueue.size());
//		if (_toBeAssignedQueue.size() > 100)
//			System.out.println("100+ tasks: " + _toBeAssignedQueue.size());
//
//		while (!_toBeAssignedQueue.isEmpty())
//		{
//			Behavior behavior = _toBeAssignedQueue.poll();
//			if (_clearInvalid)
//				behavior.clearInvalidEntities();
//			if (behavior.isAssigningBuildingClaimed()
//					&& !behavior.isHarvestBehavior())
//			{
//				// System.out.println("occupado " +
//				// behavior.getBehaviorTag());
//				behaviorFailed(
//						behavior,
//						ClayConstants.BEHAVIOR_FAILED_ASSIGNING_BUILDING_CLAIMED);
//				continue;
//			}
//			for (GolemEntity golem : _inactiveGolems)
//			{
//				if (behavior.isInvalid(golem))
//					continue;
//				BehaviorTriple triple = new BehaviorTriple(golem, behavior,
//						behavior.calculateBehaviorWeight(golem)
//								+ behavior.getAddedWeight());
//				if (triple._weight <= 0)
//					triple._behavior.requiredFailed(golem);
//				else
//					behaviorScores.add(triple);
//			}
//			if (behavior.allGolemsInvalid(_golemList))
//				behaviorFailed(behavior, ClayConstants.BEHAVIOR_FAILED_NO_PATH);
//		}
//
//		if (!behaviorScores.isEmpty())
//		{
//			BehaviorTriple[] scores = new BehaviorTriple[behaviorScores.size()];
//			behaviorScores.toArray(scores);
//			BehaviorTriple[] topScores = BehaviorTripleQuickSort.sort(scores);
//			List<GolemEntity> invalidGolems = new ArrayList<GolemEntity>();
//			List<Behavior> invalidBehaviors = new ArrayList<Behavior>();
//			for (BehaviorTriple triple : topScores)
//			{
//				// buildings shouldn't have claimed items for multiple tasks. On a construct task, the building that needs to be built should claim the items.
//				if (!invalidGolems.contains(triple._golem)
//						&& !invalidBehaviors.contains(triple._behavior))
//				{
//					int requiredComplete = triple._behavior
//							.calculateRequired(triple._golem);
//					if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
//					{
//						triple._golem.setBehavior(triple._behavior);
//						triple._behavior
//								.setBehaviorProcess(GolemBehaviorProcess.this);
//						triple._behavior.setAssignedGolem(triple._golem);
//						_inProgressBehaviors.add(triple._behavior);
//						invalidGolems.add(triple._golem);
//						invalidBehaviors.add(triple._behavior);
//					}
//					else
//					{
//						if (!triple._behavior.isPersonalTask())
//						{
//							if (requiredComplete != ClayConstants.BEHAVIOR_FAILED_NO_PATH
//									|| !triple._behavior
//											.allGolemsInvalid(_golemList))
//							{
//								triple._behavior.requiredFailed(triple._golem);
//								behaviorFailed(
//										triple._behavior,
//										requiredComplete);
//								invalidBehaviors.add(triple._behavior);
//							}
//						}
//						else
//							behaviorFailed(triple._behavior, requiredComplete);
//					}
//				}
//			}
//		}
//		_clearInvalid = false;
//	}
//
//	private void calculateNeededBehaviors(GolemEntity golem_)
//	{
//		BehaviorTriple triple = GolemBrain.calculateBestBehavior(
//				golem_,
//				golem_.getNeededBehaviors(),
//				true);
//		if (triple != null)
//		{
//			triple._behavior.setBehaviorProcess(GolemBehaviorProcess.this);
//			triple._behavior.setAssignedGolem(golem_);
//			int requiredComplete = triple._behavior
//					.calculateRequired(triple._golem);
//			if (requiredComplete == ClayConstants.BEHAVIOR_PASSED)
//			{
//				_inProgressBehaviors.add(triple._behavior);
//				golem_.setBehavior(triple._behavior);
//				_neededBehaviorsGolems.add(golem_);
//			}
//			else
//				behaviorFailed(triple._behavior, requiredComplete);
//		}
//	}
//
//	public void queueBehavior(Behavior behavior_)
//	{
//		behavior_.setBehaviorProcess(this);
//		_unassignedBehaviors.add(behavior_);
//	}
//
//	public int getBehaviorCount(String behaviorTag_)
//	{
//		int count = 0;
//		for (GolemEntity golem : _golemList)
//		{
//			Behavior behavior = golem.getCurrentBehavior();
//			if (behavior != null
//					&& behavior.getBehaviorTag().equals(behaviorTag_))
//				count++;
//		}
//		return count;
//	}
//
//	public void behaviorComplete(Behavior behavior_)
//	{
//		_completedBehaviors.add(new FinishedBehavior(behavior_,
//				ClayConstants.BEHAVIOR_PASSED));
//	}
//
//	private void handleCompletedBehaviors()
//	{
//		while (!_completedBehaviors.isEmpty())
//		{
//			FinishedBehavior completedBehavior = _completedBehaviors.poll();
//			updateCompletedBehaviorLists(completedBehavior._behavior);
//		}
//	}
//
//	private void updateCompletedBehaviorLists(Behavior behavior_)
//	{
//		_inProgressBehaviors.remove(behavior_);
//		if (behavior_.getAssigningBuilding() != null)
//		{
//			behavior_.getAssigningBuilding().removeActiveBehavior(behavior_);
//		}
//		clearGolemsNoUnoccupiedBuildingsBehaviors();
//	}
//
//	public void behaviorFailed(Behavior behavior_, int reason_)
//	{
//		_failedBehaviors.add(new FinishedBehavior(behavior_, reason_));
//	}
//
//	private void handleFailedBehaviors()
//	{
//		while (!_failedBehaviors.isEmpty())
//		{
//			FinishedBehavior failedBehavior = _failedBehaviors.poll();
//			updateFailedBehaviorLists(
//					failedBehavior._behavior,
//					failedBehavior._reason);
//		}
//	}
//
//	private void updateFailedBehaviorLists(Behavior behavior_, int reason_)
//	{
//		if (!behavior_.isPersonalTask())
//		{
//			if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
//			{
//				if (behavior_.allGolemsInvalid(_golemList))
//					_unreachableBehaviors.add(behavior_);
//				else
//					_unassignedBehaviors.add(behavior_);
//			}
//			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
//			{
//				_noMaterials.add(behavior_);
//			}
//			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
//			{
//				_noStorageAvailable.add(behavior_);
//			}
//			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_MISSING_ITEM
//					|| reason_ == ClayConstants.BEHAVIOR_FAILED_OBSOLETE)
//			{
//				if (!behavior_.isHarvestBehavior())
//					System.out.println(behavior_.getBehaviorTag() + " "
//							+ reason_);
//				_unassignedBehaviors.remove(behavior_);
//				_inProgressBehaviors.remove(behavior_);
//				_unreachableBehaviors.remove(behavior_);
//				_noAvailableGolems.remove(behavior_);
//				_noMaterials.remove(behavior_);
//				_noStorageAvailable.remove(behavior_);
//				if (behavior_.getAssigningBuilding() != null)
//				{
//					List<Behavior> assingningBuildingBehaviors = _assigningBuildingClaimedMap
//							.get(behavior_.getAssigningBuilding());
//					if (assingningBuildingBehaviors != null)
//						assingningBuildingBehaviors.remove(behavior_);
//					List<Behavior> noUnoccupiedGenericBuildingBehaviors = _noUnoccupiedGenericBuildingMap
//							.get(behavior_.getAssigningBuilding()
//									.getBuildingTag());
//					if (noUnoccupiedGenericBuildingBehaviors != null)
//						noUnoccupiedGenericBuildingBehaviors.remove(behavior_);
//
//				}
//			}
//			if (reason_ == ClayConstants.BEHAVIOR_FAILED_ASSIGNING_BUILDING_CLAIMED)
//			{
//				BuildingEntity assigningBuilding = behavior_
//						.getAssigningBuilding();
//				List<Behavior> assigningBuildingBehaviors = _assigningBuildingClaimedMap
//						.get(assigningBuilding);
//				if (assigningBuildingBehaviors == null)
//				{
//					assigningBuildingBehaviors = new ArrayList<Behavior>();
//					_assigningBuildingClaimedMap.put(
//							assigningBuilding,
//							assigningBuildingBehaviors);
//				}
//				assigningBuildingBehaviors.add(behavior_);
//			}
//		}
//		else
//		{
//			if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_PATH)
//				behavior_.getAssignedGolem().addUnreachableBehavior(behavior_);
//			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_MATERIALS)
//				behavior_.getAssignedGolem().addNoMaterialsBehavior(behavior_);
//			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_NO_STORAGE)
//				behavior_.getAssignedGolem().addNoStorageAvailableBehavior(
//						behavior_);
//			else if (reason_ == ClayConstants.BEHAVIOR_FAILED_ASSIGNING_BUILDING_CLAIMED)
//				behavior_.getAssignedGolem().addNoUnoccupiedBuildingsBehavior(
//						behavior_);
//		}
//		_inProgressBehaviors.remove(behavior_);
//	}
//
//	@Override
//	public void onEvent(MapUpdateEvent event_)
//	{
//		_mapEvents.add(event_);
//	}
//
//	private void handleMapEvents()
//	{
//		while (!_mapEvents.isEmpty())
//		{
//			updateMapEventLists(_mapEvents.poll());
//		}
//	}
//
//	private void updateMapEventLists(MapUpdateEvent event_)
//	{
//		// TODO make sure it's the home screen
//		// TODO main thread is going to have to clear invalid golem personal
//		// behaviors.
//		if (event_.getPoints() != null)
//		{
//			for (GolemEntity golem : _golemList)
//			{
//				golem.recalculatePathIfNecessary(event_.getPoints());
//			}
//			_clearInvalid = true;
//			_unassignedBehaviors.addAll(_unreachableBehaviors);
//			_unreachableBehaviors.clear();
//			clearGolemsUnreachableBehaviors();
//		}
//
//		if (event_.isItemUpdate())
//		{
//			_unassignedBehaviors.addAll(_noMaterials);
//			_noMaterials.clear();
//			clearGolemsNoMaterials();
//			_clearInvalid = true;
//		}
//
//		if (event_.isStorageAvailable())
//		{
//			_unassignedBehaviors.addAll(_noStorageAvailable);
//			_noStorageAvailable.clear();
//			clearGolemsNoStorageAvailableBehaviors();
//		}
//		if (event_.getUnclaimedBuilding() != null)
//		{
//			List<Behavior> validBehaviors = _assigningBuildingClaimedMap
//					.get(event_.getUnclaimedBuilding());
//			if (validBehaviors != null)
//			{
//				_unassignedBehaviors.addAll(validBehaviors);
//				_assigningBuildingClaimedMap.remove(event_
//						.getUnclaimedBuilding());
//			}
//			else
//			{
//				validBehaviors = _noUnoccupiedGenericBuildingMap.get(event_
//						.getUnclaimedBuilding().getBuildingTag());
//				if (validBehaviors != null)
//				{
//					_unassignedBehaviors.addAll(validBehaviors);
//					_noUnoccupiedGenericBuildingMap.remove(event_
//							.getUnclaimedBuilding());
//				}
//			}
//		}
//	}
//
//	private void clearGolemsUnreachableBehaviors()
//	{
//		for (GolemEntity golem : _golemList)
//		{
//			golem.clearUnreachableBehaviors();
//		}
//	}
//
//	private void clearGolemsNoMaterials()
//	{
//		for (GolemEntity golem : _golemList)
//		{
//			golem.clearNoMaterialsBehaviors();
//		}
//	}
//
//	private void clearGolemsNoStorageAvailableBehaviors()
//	{
//		for (GolemEntity golem : _golemList)
//		{
//			golem.clearNoStorageAvailableBehaviors();
//		}
//	}
//
//	private void clearGolemsNoUnoccupiedBuildingsBehaviors()
//	{
//		for (GolemEntity golem : _golemList)
//		{
//			golem.clearNoUnoccupiedBuildingsBehaviors();
//		}
//	}
//
//	private void removeHandledBehaviorsFromUnassgined()
//	{
//		_unassignedBehaviors.removeAll(_inProgressBehaviors);
//		for (Behavior behavior : _unassignedBehaviors)
//		{
//			if (behavior != null)
//				behavior.increaseAddedWeight(ClayConstants.ADDED_WEIGHT_INCREASE);
//		}
//		_noAvailableGolems.addAll(_unassignedBehaviors);
//		_unassignedBehaviors.clear();
//	}
//
//	private void removeFailedBehaviorsFromUnassigned()
//	{
//		_unassignedBehaviors.removeAll(_unreachableBehaviors);
//		_unassignedBehaviors.removeAll(_noMaterials);
//		_unassignedBehaviors.removeAll(_noStorageAvailable);
//
//		for (BuildingEntity key : _assigningBuildingClaimedMap.keySet())
//		{
//			_unassignedBehaviors.removeAll(_assigningBuildingClaimedMap
//					.get(key));
//		}
//
//		for (String key : _noUnoccupiedGenericBuildingMap.keySet())
//		{
//			_unassignedBehaviors.removeAll(_noUnoccupiedGenericBuildingMap
//					.get(key));
//		}
//	}
//
//	private class FinishedBehavior
//	{
//		public FinishedBehavior(Behavior behavior_, int reason_)
//		{
//			_behavior = behavior_;
//			_reason = reason_;
//		}
//
//		Behavior _behavior;
//		int _reason;
//	}
//
//	private Random _random = new Random();
//
//	private List<GolemEntity> _golemList;
//	private List<GolemEntity> _inactiveGolems;
//
//	private Queue<FinishedBehavior> _failedBehaviors;
//	private Queue<FinishedBehavior> _completedBehaviors;
//	private Queue<MapUpdateEvent> _mapEvents;
//
//	private Queue<Behavior> _toBeAssignedQueue;
//
//	private volatile List<Behavior> _unassignedBehaviors;
//	private List<Behavior> _inProgressBehaviors;
//	private List<Behavior> _unreachableBehaviors;
//	private List<Behavior> _noMaterials;
//	private List<Behavior> _noAvailableGolems;
//	private List<Behavior> _noStorageAvailable;
//
//	private List<GolemEntity> _neededBehaviorsGolems;
//
//	private Map<String, List<Behavior>> _noUnoccupiedGenericBuildingMap;
//	private Map<BuildingEntity, List<Behavior>> _assigningBuildingClaimedMap;
//
//	private boolean _clearInvalid;

}
